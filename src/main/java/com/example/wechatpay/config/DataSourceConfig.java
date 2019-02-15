package com.example.wechatpay.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置数据库连接池参数
 */
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.parameters}")
    private String parameters;

    /**
     * 创建数据库的时候使用下面这句SQL
     * 使得整个数据库所有表都使用utf8mb4字符集
     * create database xxx CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
     */
    @Bean
    public HikariDataSource hikariDataSource(DataSourceProperties properties) {
        String url = properties.getUrl() + parameters;
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPassword(properties.getPassword());
        hikariConfig.setUsername(properties.getUsername());
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setMaximumPoolSize(32);
        hikariConfig.setIdleTimeout(300000);
        hikariConfig.setDriverClassName(properties.getDriverClassName());
        hikariConfig.setConnectionInitSql("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci");
        hikariConfig.setPoolName("HikariConfig");
        return new HikariDataSource(hikariConfig);
    }
}
