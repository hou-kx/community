package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

// 表明这是个配置类
@Configuration
public class KaptchaConfig {

    // 声明一个bean由spring 装配、管理， 三方库的核心对象接口
    @Bean
    public Producer KaptchaProducer() {
        // 这里实际上就是存的一些  key-value 值，即是配置文件，如本文的  CommunityApplication.properties 一样， 这里直接就在这里设置，未再创建 properties 文件
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");   // 验证码图片宽、高
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");     // 字体颜色、大小
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");    // 设置字符库
        properties.setProperty("kaptcha.textproducer.char.length", "4");    // 设置验证码长度
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise"); // 设置无噪声

        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);   // para: java.util.Properties properties 实际就是一堆 key-value 的配置文件
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
