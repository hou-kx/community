package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，代替 Session 对象
 * 本地线程变量  ThreadLocal
 * ThreadLocal 中填充的的是当前线程的变量，该变量对其他线程而言是封闭且隔离的;
 * ThreadLocal 为变量在每个线程中创建了一个副本，这样每个线程都可以访问自己内部的副本变量。
 * <p>
 * 1、避免多次传递，打破层次间的约束。
 * 2、线程间数据隔离
 * 3、进行事务操作，用于存储线程事务信息。
 * 4、数据库连接，Session会话管理。
 */
@Component
public class HostHolder {
    /**
     * 线程隔离，==> 以线程为 key 存取值得
     * final 被定义后只是不能被重新赋值，不代表它不能改变本身的值，这里 users 的方法都是允许调用的
     * 只是不能再将这个list变量指向其他的List实例化对象了，即不能再出现 list = new ArrayList(); 的代码
     */
    private final ThreadLocal<User> users = new ThreadLocal<>();

    /**
     * Sets the current thread's copy of this thread-local variable to the specified value.
     *
     * @param user 存放当前登录用户数据
     */
    public void setUser(User user) {
        users.set(user);
    }

    /**
     * Returns the value in the current thread's copy of this thread-local variable.
     *
     * @return User
     */
    public User getUser() {
        return users.get();
    }

    /**
     * Removes the current thread's value for this thread-local variable
     */
    public void clear() {
        users.remove();
    }
}
