# DDD架构重构方案

## 一、当前架构问题分析

### 1.1 核心问题
**API模型渗透到领域层和基础设施层**，违反了DDD的依赖倒置原则。

### 1.2 具体问题表现

#### 问题1：Domain层直接依赖API层的DTO
**位置：** `business-domain/src/main/java/org/pms/domain/`

**问题代码：**
- `CommandTaskService.java` 使用 `CommandTaskCreateReq`, `CommandTaskSendReq`
- `CommandMetaService.java` 使用 `CommandMetaInsertReq`, `CommandMetaUpdateReq`
- `DeviceService.java` 使用 `DeviceInsertReq`, `DeviceUpdateReq`
- `PipelineService.java` 使用 `PipelineInsertReq`, `PipelineUpdateReq`
- `RbacService.java` 使用 `UserCreateReq`, `UserUpdateReq`

**违反原则：** 领域层不应该知道外部API的存在，应该使用自己的领域模型。

#### 问题2：Infrastructure层直接依赖API层的DTO
**位置：** `business-infrastructure/src/main/java/org/pms/infrastructure/`

**问题代码：**
- `CommandMetaConverter.java` 转换 `CommandMetaInsertReq` → `CommandMetaPO`
- `DeviceConverter.java` 转换 `DeviceInsertReq` → `DevicePO`
- `PipelineConverter.java` 转换 `PipelineInsertReq` → `PipelinePO`
- `AuthConverter.java` 转换 `UserCreateReq` → `UserPO`
- `CommandMetaRepository.java` 直接使用 `CommandMetaInsertReq`, `CommandMetaUpdateReq`
- `PipelineRepository.java` 直接使用 `PipelineInsertReq`, `PipelineUpdateReq`

**违反原则：** 基础设施层应该只依赖领域层，不应该知道API层的存在。

#### 问题3：Application层职责混乱
**位置：** `business-application/src/main/java/org/pms/application/`

**问题代码：**
- `ApiToDomainConverter.java` 只转换了设备数据，其他模块缺失
- 查询服务接口返回API层的View对象（这个是合理的）
- 缺少统一的DTO转换层

**违反原则：** Application层应该负责协调领域层和外部层，进行DTO转换。

---

## 二、符合DDD的正确架构

### 2.1 依赖关系（从外到内）
```
Trigger层 → Application层 → Domain层 ← Infrastructure层
   ↓            ↓              ↓            ↓
  API层       API层         Types层      Types层
```

### 2.2 各层职责

#### API层 (business-api)
- **职责：** 定义对外发布的接口契约（DTO、Facade接口）
- **依赖：** business-types
- **被依赖：** Trigger层、Application层

#### Trigger层 (business-trigger)
- **职责：** HTTP/RPC接口实现，接收外部请求
- **依赖：** Application层、API层
- **转换：** API DTO → Application Command/Query

#### Application层 (business-application)
- **职责：** 用例编排、DTO转换、事务控制
- **依赖：** Domain层、API层（仅用于DTO转换）
- **转换：** 
  - 入参：API DTO → Domain Entity/ValueObject
  - 出参：Domain Entity → API DTO

#### Domain层 (business-domain)
- **职责：** 核心业务逻辑、领域模型、领域服务
- **依赖：** Types层（仅依赖异常和常量）
- **不依赖：** API层、Infrastructure层

#### Infrastructure层 (business-infrastructure)
- **职责：** 持久化、外部服务调用
- **依赖：** Domain层、Application层（仅查询服务接口）、Types层
- **不依赖：** API层

---

## 三、重构方案

### 3.1 重构步骤概览
1. **在Domain层创建领域命令对象（Command）**
2. **在Application层创建DTO转换器**
3. **修改Domain层服务接口，使用领域命令对象**
4. **修改Infrastructure层，移除对API层的依赖**
5. **修改Application层，添加DTO转换逻辑**
6. **修改Trigger层，调用Application层**
7. **删除Domain层对API层的依赖**
8. **验证编译和测试**

### 3.2 详细重构方案

#### 步骤1：创建领域命令对象
**位置：** `business-domain/src/main/java/org/pms/domain/*/model/command/`

**需要创建的Command对象：**
```
domain/
├── command/model/command/
│   ├── CreateCommandMetaCommand.java
│   ├── UpdateCommandMetaCommand.java
│   ├── CreateCommandTaskCommand.java
│   └── SendCommandCommand.java
├── terminal/model/command/
│   ├── CreateDeviceCommand.java
│   ├── UpdateDeviceCommand.java
│   ├── CreatePipelineCommand.java
│   └── UpdatePipelineCommand.java
└── rbac/model/command/
    ├── CreateUserCommand.java
    └── UpdateUserCommand.java
```

**示例：CreateCommandMetaCommand.java**
```java
package org.pms.domain.command.model.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建指令元数据命令（领域层）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommandMetaCommand {
    private Long pipelineId;
    private String serviceIdentifier;
    private String name;
    private String payloadSchema;
    private String remark;
}
```

#### 步骤2：在Application层创建DTO转换器
**位置：** `business-application/src/main/java/org/pms/application/converter/`

**需要创建的Converter：**
```
application/converter/
├── CommandDtoConverter.java      # 指令相关DTO转换
├── TerminalDtoConverter.java     # 设备/管道DTO转换
├── RbacDtoConverter.java         # 用户/角色DTO转换
└── ApiToDomainConverter.java     # 已存在，补充完善
```

**示例：CommandDtoConverter.java**
```java
package org.pms.application.converter;

import org.pms.api.dto.req.*;
import org.pms.domain.command.model.command.*;
import org.springframework.stereotype.Component;

/**
 * 指令模块DTO转换器（Application层）
 * 职责：API DTO ↔ Domain Command
 */
@Component
public class CommandDtoConverter {

    // API DTO → Domain Command
    public CreateCommandMetaCommand toCreateCommand(CommandMetaInsertReq req) {
        return CreateCommandMetaCommand.builder()
            .pipelineId(req.getPipelineId())
            .serviceIdentifier(req.getServiceIdentifier())
            .name(req.getName())
            .payloadSchema(req.getPayloadSchema())
            .remark(req.getRemark())
            .build();
    }

    public UpdateCommandMetaCommand toUpdateCommand(CommandMetaUpdateReq req) {
        return UpdateCommandMetaCommand.builder()
            .id(req.getId())
            .pipelineId(req.getPipelineId())
            .serviceIdentifier(req.getServiceIdentifier())
            .name(req.getName())
            .payloadSchema(req.getPayloadSchema())
            .remark(req.getRemark())
            .build();
    }

    // 其他转换方法...
}
```

#### 步骤3：修改Domain层服务接口
**修改前：**
```java
// ICommandMetaService.java
void insertCommandMeta(CommandMetaInsertReq commandMetaInsertReq);
void updateCommandMeta(CommandMetaUpdateReq commandMetaUpdateReq);
```

**修改后：**
```java
// ICommandMetaService.java
void insertCommandMeta(CreateCommandMetaCommand command);
void updateCommandMeta(UpdateCommandMetaCommand command);
```

#### 步骤4：修改Infrastructure层
**修改前：**
```java
// CommandMetaConverter.java (Infrastructure层)
@Mapper(componentModel = "spring")
public abstract class CommandMetaConverter {
    public abstract CommandMetaPO insertReq2PO(CommandMetaInsertReq req);
}

// CommandMetaRepository.java
void insertCommandMeta(CommandMetaInsertReq commandMetaInsertReq);
```

**修改后：**
```java
// CommandMetaConverter.java (Infrastructure层)
@Mapper(componentModel = "spring")
public abstract class CommandMetaConverter {
    // 只负责 Entity ↔ PO 转换
    public abstract CommandMetaPO entity2PO(CommandMetaEntity entity);
    public abstract CommandMetaEntity po2Entity(CommandMetaPO po);
}

// CommandMetaRepository.java
void insertCommandMeta(CommandMetaEntity entity);
```

#### 步骤5：修改Application层Facade
**修改前：**
```java
// 直接传递API DTO到Domain层
@Override
public Response<Void> insertCommandMeta(CommandMetaInsertReq req) {
    commandMetaService.insertCommandMeta(req);  // ❌ 直接传递
    return Response.success();
}
```

**修改后：**
```java
// 在Application层进行DTO转换
@Resource
private CommandDtoConverter commandDtoConverter;

@Override
public Response<Void> insertCommandMeta(CommandMetaInsertReq req) {
    // 1. API DTO → Domain Command
    CreateCommandMetaCommand command = commandDtoConverter.toCreateCommand(req);

    // 2. 调用Domain层服务
    commandMetaService.insertCommandMeta(command);

    return Response.success();
}
```

#### 步骤6：修改Domain层服务实现
**修改前：**
```java
@Override
public void insertCommandMeta(CommandMetaInsertReq req) {
    // 校验
    commandMetaValidationService.validatePayloadSchema(req.getPayloadSchema());

    // 保存
    commandMetaRepository.insertCommandMeta(req);
}
```

**修改后：**
```java
@Override
public void insertCommandMeta(CreateCommandMetaCommand command) {
    // 1. 校验
    commandMetaValidationService.validatePayloadSchema(command.getPayloadSchema());

    // 2. 构建领域实体
    CommandMetaEntity entity = CommandMetaEntity.builder()
        .pipelineId(command.getPipelineId())
        .serviceIdentifier(command.getServiceIdentifier())
        .name(command.getName())
        .payloadSchema(command.getPayloadSchema())
        .status(StatusVO.UNVERIFIED)
        .remark(command.getRemark())
        .build();

    // 3. 保存
    commandMetaRepository.insertCommandMeta(entity);
}
```

---

## 四、重构优先级

### 高优先级（核心业务）
1. **指令模块** (command)
   - CreateCommandMetaCommand, UpdateCommandMetaCommand
   - CreateCommandTaskCommand, SendCommandCommand

2. **设备管理模块** (terminal)
   - CreateDeviceCommand, UpdateDeviceCommand
   - CreatePipelineCommand, UpdatePipelineCommand

### 中优先级
3. **用户权限模块** (rbac)
   - CreateUserCommand, UpdateUserCommand

### 低优先级（查询不需要重构）
4. **查询服务** - 保持现状
   - 查询服务返回API层的View对象是合理的
   - Infrastructure层实现Application层的查询接口也是合理的

---

## 五、重构后的依赖关系

### 5.1 正确的依赖图
```
┌─────────────────────────────────────────────────────────┐
│                     Trigger层                            │
│  (HTTP/RPC接口，依赖Application层和API层)                 │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│                  Application层                           │
│  - Facade实现：接收API DTO，转换为Domain Command         │
│  - 查询服务接口：返回API View                            │
│  - DTO转换器：API DTO ↔ Domain Command/Entity           │
│  依赖：Domain层、API层                                   │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│                    Domain层                              │
│  - 领域模型：Entity、ValueObject、Aggregate              │
│  - 领域服务：业务逻辑                                     │
│  - 领域命令：Command对象                                  │
│  - 仓储接口：Repository接口                               │
│  依赖：Types层（仅异常和常量）                            │
│  ❌ 不依赖：API层、Infrastructure层                      │
└────────────────────┬────────────────────────────────────┘
                     ↑
                     │
┌─────────────────────────────────────────────────────────┐
│                Infrastructure层                          │
│  - 仓储实现：Repository实现                               │
│  - 数据转换：Entity ↔ PO                                 │
│  - 外部服务：RPC、消息队列等                              │
│  - 查询服务实现：实现Application层的查询接口              │
│  依赖：Domain层、Application层（查询接口）、Types层       │
│  ❌ 不依赖：API层                                        │
└─────────────────────────────────────────────────────────┘
```

### 5.2 模块依赖配置（pom.xml）

**business-domain/pom.xml** - 移除API依赖
```xml
<dependencies>
    <!-- ❌ 删除这个依赖 -->
    <!-- <dependency>
        <groupId>org.pms</groupId>
        <artifactId>business-api</artifactId>
    </dependency> -->

    <!-- ✅ 只保留types依赖 -->
    <dependency>
        <groupId>org.pms</groupId>
        <artifactId>business-types</artifactId>
    </dependency>
</dependencies>
```

**business-infrastructure/pom.xml** - 移除API依赖
```xml
<dependencies>
    <!-- ❌ 删除这个依赖 -->
    <!-- <dependency>
        <groupId>org.pms</groupId>
        <artifactId>business-api</artifactId>
    </dependency> -->

    <!-- ✅ 保留domain和application依赖 -->
    <dependency>
        <groupId>org.pms</groupId>
        <artifactId>business-domain</artifactId>
    </dependency>
    <dependency>
        <groupId>org.pms</groupId>
        <artifactId>business-application</artifactId>
    </dependency>
</dependencies>
```

---

## 六、重构检查清单

### 6.1 Domain层检查
- [ ] 所有Service接口使用Command对象，不使用API DTO
- [ ] 所有Repository接口使用Entity对象，不使用API DTO
- [ ] pom.xml中移除business-api依赖
- [ ] 编译通过

### 6.2 Infrastructure层检查
- [ ] 所有Converter只转换Entity ↔ PO
- [ ] 所有Repository实现使用Entity对象
- [ ] pom.xml中移除business-api依赖
- [ ] 编译通过

### 6.3 Application层检查
- [ ] 创建完整的DTO转换器
- [ ] Facade实现中进行DTO转换
- [ ] 查询服务接口返回API View（保持不变）
- [ ] 编译通过

### 6.4 整体检查
- [ ] 所有模块编译通过
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 功能测试通过

---

## 七、重构收益

### 7.1 架构收益
1. **依赖方向正确**：符合DDD的依赖倒置原则
2. **领域层纯净**：Domain层不依赖外部API，易于测试和维护
3. **职责清晰**：各层职责明确，边界清晰
4. **可扩展性强**：API变更不影响Domain层

### 7.2 代码质量收益
1. **可测试性**：Domain层可以独立测试，不需要Mock API对象
2. **可维护性**：修改API不需要修改Domain层
3. **可复用性**：Domain层可以被多个API版本复用
4. **类型安全**：Command对象提供更强的类型约束

### 7.3 团队协作收益
1. **并行开发**：API设计和Domain开发可以并行
2. **职责分离**：前端团队关注API，后端团队关注Domain
3. **版本管理**：API版本升级不影响Domain层

---

## 八、实施建议

### 8.1 渐进式重构
**不要一次性重构所有模块**，建议按以下顺序：

1. **第一阶段**：重构指令模块（command）
   - 创建Command对象
   - 修改Service接口
   - 修改Repository接口
   - 验证功能

2. **第二阶段**：重构设备管理模块（terminal）
   - 创建Command对象
   - 修改Service接口
   - 修改Repository接口
   - 验证功能

3. **第三阶段**：重构用户权限模块（rbac）
   - 创建Command对象
   - 修改Service接口
   - 修改Repository接口
   - 验证功能

4. **第四阶段**：移除依赖
   - 删除Domain层的API依赖
   - 删除Infrastructure层的API依赖
   - 全量测试

### 8.2 风险控制
1. **保留旧代码**：重构时先创建新方法，旧方法标记@Deprecated
2. **增量测试**：每个模块重构后立即测试
3. **代码审查**：重构代码需要团队审查
4. **回滚方案**：使用Git分支，确保可以快速回滚

### 8.3 团队协作
1. **技术分享**：向团队讲解DDD架构原则
2. **代码示例**：提供标准的Command对象和Converter示例
3. **文档更新**：更新开发规范文档
4. **持续改进**：定期Review架构，持续优化

---

## 九、常见问题FAQ

### Q1: 为什么查询服务可以返回API View对象？
**A:** 查询服务是CQRS模式中的Query端，可以直接返回前端需要的DTO，不需要经过Domain层。这是合理的架构设计。

### Q2: Infrastructure层为什么可以依赖Application层？
**A:** Infrastructure层实现Application层定义的查询服务接口，这是依赖倒置原则的体现。接口在Application层定义，实现在Infrastructure层。

### Q3: Command对象和API DTO有什么区别？
**A:**
- **API DTO**：面向外部接口，字段名和结构由API契约决定
- **Command对象**：面向领域模型，字段名和结构由业务语义决定
- **转换职责**：Application层负责两者之间的转换

### Q4: 是否所有操作都需要Command对象？
**A:**
- **写操作**（增删改）：需要Command对象
- **查询操作**：不需要，直接返回View对象即可

### Q5: 重构会影响现有功能吗？
**A:** 如果按照渐进式重构方案，先创建新方法，旧方法保留，不会影响现有功能。等新方法验证通过后，再删除旧方法。

---

## 十、总结

当前项目的核心问题是**API模型渗透到领域层和基础设施层**，违反了DDD的依赖倒置原则。

**重构核心思路：**
1. 在Domain层创建Command对象，替代API DTO
2. 在Application层创建DTO转换器，负责API DTO ↔ Domain Command转换
3. 修改Domain层和Infrastructure层，移除对API层的依赖
4. 保持查询服务返回API View对象（CQRS模式）

**重构后的架构：**
- Domain层纯净，只依赖Types层
- Infrastructure层只依赖Domain层和Application层（查询接口）
- Application层负责DTO转换和用例编排
- 符合DDD的依赖倒置原则

**建议采用渐进式重构**，先重构核心模块，验证通过后再推广到其他模块。


