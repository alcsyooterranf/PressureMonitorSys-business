package org.pms.infrastructure.adapter.repository.clickhouse;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.pms.infrastructure.mapper.po.DeviceDataPO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * ClickHouse数据批量写入缓冲器
 * 
 * 为什么需要缓冲器?
 * - ClickHouse批量写入性能极高,但单条写入性能一般
 * - 通过缓冲器收集数据,达到一定数量或时间后批量写入
 * - 可以将写入性能提升100倍以上
 * 
 * 工作原理:
 * 1. 数据先放入内存缓冲队列
 * 2. 当缓冲区达到batch-size或超过flush-interval时间,触发批量写入
 * 3. 使用定时任务定期刷新缓冲区
 * 
 * 注意事项:
 * - 数据在缓冲区期间可能丢失(如果服务器宕机)
 * - 如果对数据一致性要求极高,可以考虑先写MySQL再异步写ClickHouse
 * 
 * @author zeal
 * @since 2024-11-24
 */
@Slf4j
@Component
public class ClickHouseDataBuffer {

    @Resource
    private ClickHouseDataReportRepository clickHouseRepository;

    /**
     * 批量写入大小(从配置文件读取)
     * 达到这个数量就触发写入
     * 建议值: 1000-10000
     */
    @Value("${clickhouse.batch-size:1000}")
    private int batchSize;

    /**
     * 刷新间隔(毫秒)
     * 即使没达到batch-size,也会定时刷新
     * 建议值: 3000-10000
     */
    @Value("${clickhouse.flush-interval:5000}")
    private long flushInterval;

    /**
     * 缓冲区容量
     * 超过这个容量会阻塞,防止内存溢出
     */
    @Value("${clickhouse.buffer-capacity:10000}")
    private int bufferCapacity;

    /**
     * 阻塞队列,用于存储待写入的数据
     * 
     * BlockingQueue特点:
     * - 线程安全,多线程环境下可以安全使用
     * - 当队列满时,put()会阻塞,直到有空间
     * - 当队列空时,take()会阻塞,直到有数据
     */
    private BlockingQueue<DeviceDataPO> buffer;

    /**
     * 定时任务调度器
     * 用于定期刷新缓冲区
     */
    private ScheduledExecutorService scheduler;

    /**
     * 刷新任务的Future对象
     * 用于取消定时任务
     */
    private ScheduledFuture<?> flushTask;

    /**
     * 初始化方法
     * @PostConstruct注解表示在Bean创建后自动执行
     */
    @PostConstruct
    public void init() {
        // 创建阻塞队列
        buffer = new LinkedBlockingQueue<>(bufferCapacity);

        // 创建单线程的定时任务调度器
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "ClickHouse-Flush-Thread");
            thread.setDaemon(true);  // 设置为守护线程,JVM退出时自动结束
            return thread;
        });

        // 启动定时刷新任务
        // scheduleAtFixedRate: 固定频率执行
        // initialDelay: 初始延迟(第一次执行前等待的时间)
        // period: 执行间隔
        flushTask = scheduler.scheduleAtFixedRate(
                this::flush,           // 要执行的任务
                flushInterval,         // 初始延迟
                flushInterval,         // 执行间隔
                TimeUnit.MILLISECONDS  // 时间单位
        );

        log.info("ClickHouse数据缓冲器启动成功: batchSize={}, flushInterval={}ms, bufferCapacity={}",
                batchSize, flushInterval, bufferCapacity);
    }

    /**
     * 销毁方法
     * @PreDestroy注解表示在Bean销毁前自动执行
     */
    @PreDestroy
    public void destroy() {
        log.info("ClickHouse数据缓冲器正在关闭...");

        // 取消定时任务
        if (flushTask != null) {
            flushTask.cancel(false);
        }

        // 关闭调度器
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                // 等待最多10秒让任务完成
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // 刷新剩余数据
        flush();

        log.info("ClickHouse数据缓冲器已关闭");
    }

    /**
     * 添加数据到缓冲区
     * 
     * 这个方法会被多个线程并发调用(每次设备上报数据都会调用)
     * 所以必须是线程安全的
     * 
     * @param data 监控数据
     */
    public void add(DeviceDataPO data) {
        if (data == null) {
            return;
        }

        try {
            // offer方法尝试添加数据,如果队列满了会立即返回false
            // 这里使用带超时的offer,等待最多1秒
            boolean success = buffer.offer(data, 1, TimeUnit.SECONDS);

            if (!success) {
                // 如果添加失败(队列满了),记录警告日志
                log.warn("ClickHouse缓冲区已满,数据添加失败: deviceSN={}", data.getDeviceSN());
                // 可以考虑直接写入,或者丢弃数据
                // 这里选择强制刷新缓冲区,然后重试
                flush();
                buffer.offer(data, 1, TimeUnit.SECONDS);
            }

            // 如果缓冲区达到批量大小,立即刷新
            if (buffer.size() >= batchSize) {
                flush();
            }
        } catch (InterruptedException e) {
            log.error("添加数据到ClickHouse缓冲区被中断", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 刷新缓冲区,批量写入ClickHouse
     * 
     * 这个方法会被定时任务调用,也会在缓冲区满时手动调用
     * 使用synchronized确保同一时间只有一个线程在刷新
     */
    public synchronized void flush() {
        // 如果缓冲区为空,直接返回
        if (buffer.isEmpty()) {
            return;
        }

        // 创建临时列表存储要写入的数据
        List<DeviceDataPO> batch = new ArrayList<>();

        // drainTo方法会将队列中的数据转移到batch列表
        // 最多转移batchSize条数据
        // 这个操作是原子的,线程安全
        int count = buffer.drainTo(batch, batchSize);

        if (count == 0) {
            return;
        }

        try {
            // 批量写入ClickHouse
            clickHouseRepository.batchInsertMonitorData(batch);
            log.debug("ClickHouse缓冲区刷新成功,写入数量: {}", count);
        } catch (Exception e) {
            log.error("ClickHouse缓冲区刷新失败,数据量: {}", count, e);

            // 写入失败的处理策略:
            // 1. 重新放回队列(可能导致数据重复)
            // 2. 写入失败日志文件(可以后续手动恢复)
            // 3. 直接丢弃(如果对数据完整性要求不高)
            // 这里选择策略2: 记录错误日志
            log.error("失败的数据: {}", batch);

            // 也可以选择重新放回队列
            // buffer.addAll(batch);
        }
    }

    /**
     * 获取当前缓冲区大小
     * 用于监控
     */
    public int getBufferSize() {
        return buffer.size();
    }

    /**
     * 获取缓冲区容量
     */
    public int getBufferCapacity() {
        return bufferCapacity;
    }

    /**
     * 获取缓冲区使用率
     */
    public double getBufferUsageRate() {
        return (double) buffer.size() / bufferCapacity;
    }
}

