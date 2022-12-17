package com.nowcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {

    private final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {

    }

    /**
     * 用户【1.2.3.4】，在【00:00：00】，访问了【com.nowcoder.community.service.xxx()】.
     * 这里使用静态方法，获取，当前的请求详情，最终转换为 HttpServletRequest 对象，获取到当前的ip地址
     * 然后通过被调用的连接点获取到当前记录日志所运行的组件类，以及当前方法
     */
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String ip = "0.0.0.0";
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            ip = request.getRemoteHost();
        }
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        logger.info(String.format("用户[%s]，在[%s]，访问了[%s].", ip, now, target));
    }

}
