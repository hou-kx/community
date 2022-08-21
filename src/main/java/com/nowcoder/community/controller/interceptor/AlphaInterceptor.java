package com.nowcoder.community.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component      // 声明 交给 spring 容器来管理
public class AlphaInterceptor implements HandlerInterceptor {
    // 接口 有三个方法，定义范围为 default 即 可以实现该方法，也可不实现

    private static final Logger logger = LoggerFactory.getLogger(AlphaInterceptor.class);

    /**
     * 在 controller 之前执行
     *
     * @param request
     * @param response
     * @param handler
     * @return false-取消请求，不往下执行，一般使用true-继续执行。
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // return HandlerInterceptor.super.preHandle(request, response, handler);

        logger.debug("preHandle: " + handler.toString());
        return true;
    }

    /**
     * 在 controller 之后执行， 主要的逻辑已经完成，下一步就是显示，加载模板引擎
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        logger.debug("postHandle: " + handler.toString());
    }

    /**
     * 在 TemplateEngine 之后执行
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        logger.debug("after Completion: " + handler.toString());
    }
}
