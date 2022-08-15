package com.nowcoder.community;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // 程序的入口用这个注解，开启自动配置，内容主要做一些框架的配置
//配置数据库加密注解
@EnableEncryptableProperties
public class CommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}
