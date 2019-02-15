package com.example.wechatpay.config.redisson;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import org.springframework.util.ClassUtils;

import java.io.IOException;

/**
 * @Description Redisson配置
 * @Author blake
 * @Date 2018/12/10 下午6:10
 * @Version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redisson")
//@EnableRedissonHttpSession
public class RedissonConfig {

    private static final String CODEC = "org.redisson.codec.JsonJacksonCodec";

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "密码")
    private String password;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient initRedisClient() throws IOException {

        // Config config = Config.fromYAML(new File("config-file.yaml"));

        Config config = new Config();
        config.useSingleServer().setAddress(address).setPassword(password);

        Codec codec = new JsonJacksonCodec(ClassUtils.getDefaultClassLoader());
        config.setCodec(codec);

        return Redisson.create(config);
    }

    public static class Initializer extends AbstractHttpSessionApplicationInitializer {

        public Initializer() {
            super(RedissonConfig.class);
        }
    }

}
