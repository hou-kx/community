package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)   // 默认所有的路径， 仅仅限制 controller
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    /**
     * 统一处理 Controller 异常
     * 注解 @ExceptionHandle 修饰方法，在Controller出现异常后调用，指定处理为 Exception 的异常
     */
    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 对处理 请求 Controller 错误进行处理
        // 1、 记录下日志
        logger.error("服务器发生异常！" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        // 2、根据请求头分辨是否是异步请求，从而返回的是字符串还是 html 网页
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            response.setContentType("application/plain;charset=utf-8"); // 设置返回的字符串格式，字符编码，plain 表示普通字符串，前端解析，需要加上 $.parseJSON(str)
            PrintWriter printWriter = response.getWriter(); // 这里获取请求返回的写 IO， 输出流
            printWriter.write(CommunityUtil.getJSONString(1, "服务器异常！"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error"); // 直接跳转到错误页面，request.getContextPath()获得所请的路径
        }

        // System.out.println(
        //                 "Scheme: " + request.getScheme() +
        //                 "\nServerName: " + request.getServerName() +
        //                 "\nServerPort: " + request.getServerPort() +
        //                 "\nContextPath: " + request.getContextPath());
        //    Scheme: http
        //    ServerName: localhost
        //    ServerPort: 8090
        //    ContextPath: /community
    }
}
