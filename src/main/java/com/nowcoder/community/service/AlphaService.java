package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.security.PublicKey;

@Service    //注解表示提供业务的类
//@Scope("prototype") //Spring 中的bean都是单例的,通过scope更改 prototype每次调用都产生一个新的实例 还有 Request、Session、Global-Session//
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public AlphaService() {     //构造函数必须没有显式返回类型,默认无参的构造方法
        System.out.println("实例化AlphaService");
    }

    @PostConstruct //方法注解，表示这个方法在构造方法之后调用
    public void init() {
        System.out.println("初始化AlphaService");
    }

    @PreDestroy //在bean 销毁之前调用
    public void destory() {
        System.out.println("销毁AlphaService");
    }

    public String find(){
        return alphaDao.select();
    }
}
