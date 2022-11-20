package com.nowcoder.community.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

//@Service
// 在类上带上 Component 的注解，则 SpringBoot 启动自动装配加载
public class TestService {
    public TestService() {
        System.out.println("TestService!");
    }

    @PostConstruct
    public void init() {
        System.out.println("初始化 TestService！");
    }
}
