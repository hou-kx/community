package com.nowcoder.community.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TestService {
    public TestService() {
        System.out.println("TestService!");
    }

    @PostConstruct
    public void init() {
        System.out.println("初始化 TestService！");
    }
}
