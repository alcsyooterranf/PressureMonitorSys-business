package org.pms.config;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信公众号配置类
 * 
 * 微信公众号开发流程:
 * 1. 在微信公众平台(https://mp.weixin.qq.com)注册公众号
 * 2. 获取AppID和AppSecret
 * 3. 配置服务器URL和Token(用于接收微信消息)
 * 4. 申请模板消息权限,创建消息模板
 * 5. 用户关注公众号后,获取用户的OpenID
 * 6. 使用OpenID发送模板消息
 * 
 * @author zeal
 * @since 2024-11-24
 */
@Configuration
public class WeChatConfig {

    /**
     * 微信公众号AppID
     * 在微信公众平台 -> 开发 -> 基本配置 中获取
     */
    @Value("${wechat.mp.app-id}")
    private String appId;

    /**
     * 微信公众号AppSecret
     * 在微信公众平台 -> 开发 -> 基本配置 中获取
     */
    @Value("${wechat.mp.secret}")
    private String secret;

    /**
     * 微信公众号Token
     * 自己设置,用于验证消息来自微信服务器
     */
    @Value("${wechat.mp.token}")
    private String token;

    /**
     * 微信公众号AESKey(消息加密密钥)
     * 在微信公众平台 -> 开发 -> 基本配置 中设置
     * 如果不使用加密模式,可以不配置
     */
    @Value("${wechat.mp.aes-key:}")
    private String aesKey;

    /**
     * 创建微信公众号配置存储对象
     * 
     * WxMpConfigStorage用于存储微信公众号的配置信息
     * 包括AppID、AppSecret、Token等
     */
    @Bean
    public WxMpConfigStorage wxMpConfigStorage() {
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(appId);
        config.setSecret(secret);
        config.setToken(token);
        config.setAesKey(aesKey);
        return config;
    }

    /**
     * 创建微信公众号服务对象
     * 
     * WxMpService是微信公众号SDK的核心接口
     * 提供了发送消息、管理用户、菜单管理等功能
     * 
     * 常用方法:
     * - getTemplateMsgService(): 获取模板消息服务
     * - getUserService(): 获取用户管理服务
     * - getMenuService(): 获取菜单管理服务
     */
    @Bean
    public WxMpService wxMpService(WxMpConfigStorage configStorage) {
        WxMpService service = new WxMpServiceImpl();
        service.setWxMpConfigStorage(configStorage);
        return service;
    }
}

