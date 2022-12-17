package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    /**
     * 这里配置类 Bean ，使用 Redis key 为 String； key 为 Object
     * 因为 Redis 也是数据库，所以使用也需要建立连接，所以这里需要连接工厂类来实现，建立数据库连接
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory); // 此时有了连接数据库的能力

        // 这里实际主要配置的序列化的方式，程序中是 Java 的数据，存到 Redis 中，要把 Java 对象保存起来实际就是对其进行序列化
        // 1、首先对 Redis 中的 Key 进行序列化
        template.setKeySerializer(RedisSerializer.string());

        // 2、value 的序列化方式
        template.setValueSerializer(RedisSerializer.json());

        // 3、hash 的 key 序列化
        template.setHashKeySerializer(RedisSerializer.string());

        // 4、hash 的 value 的序列化
        template.setHashValueSerializer(RedisSerializer.json());

        // 5、设置完毕，调用下面方法进行生效
        template.afterPropertiesSet();

        return template;
    }
}
