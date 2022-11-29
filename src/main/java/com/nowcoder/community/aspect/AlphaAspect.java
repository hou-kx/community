package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component    // 注释掉后，spring 就不会加载
//@Aspect
public class AlphaAspect {

    /**
     * 使用 @Pointcut 注解，指定生效的业务组件的范围，execution 是其的一个关键字，第一个参数表示所有的返回值
     * 第二个参数中的第一个 * 表示 service 下所有的 service 第二个 * 表示组件中所有的方法
     * 括号中的 .. 表示所有的参数
     * 以上都可以指定对应的特定值
     */
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {

    }

    /**
     * 1、连接点的一开始 @Before
     * 2、连接点之后运行 @After
     * 3、返回之后运行 @AfterReturning
     * 4、抛出异常的时候运行 @AfterReturning
     * 5、在之前之后都执行 @Around
     */
    @Before("pointcut()")
    public void before() {
        System.out.println("before!");
    }

    @After("pointcut()")
    public void after() {
        System.out.println("after");
    }

    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("afterReturning");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    /**
     * 这里实际上就是，joinPoint 就是值得连接点，或者说组件执行到的位置（方法），这里使用了一种代理的方式进行执行
     * joinPoint.proceed() 即执行目标的方法函数，会有返回值，则这里返回即可
     * around（） 当本 aspect 通过代理实现的时候这里，实际就是通过，调用.proceed() 代理的调用原来目标对象的方法，
     * 而在调用的前后执行这里的一个操作！
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        Object obj = joinPoint.proceed();
        System.out.println("around after");
        return obj;
    }

}
