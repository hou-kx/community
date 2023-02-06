package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.service.UserServiceV1;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserServiceV1 userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 在请求的一开始就获取 cookie 中的 ticket 去查询 user 登录状态
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // return HandlerInterceptor.super.preHandle(request, response, handler);
        // 封装一个从 request 中获取 cookie 的方法。util.CookieUtil.getValue
        String ticket = CookieUtil.getValue(request, "ticket");
        if (StringUtils.isNotBlank(ticket)) {
            // 不为空，则查询 user 登录凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 判断凭证状态、时效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询本次请求持有用户，
                User user = userService.findUserById(loginTicket.getUserId());
                /**
                 * 封装一个存放 登录用户信息 的工具方法。因为服务器对于一个浏览器的请求都是单独线程来处理的，
                 * 使用 ThreadLocal 为当前线程存储线程独有的变量，相当于对当前请求的浏览器存一个独有变量
                 * 当前请求处理完，服务器做出响应之后，则线程销毁
                 */
                hostHolder.setUser(user);
            }
        }

        return true;
    }

    /**
     * 模板引擎调用之前 存入 model 中
     * <p>
     * model是 "模型" 的意思，是MVC架构中的 "M" 部分，是用来传输数据的。
     * ModelAndView 可以理解成MVC架构中的 "M"、"V"，其中包含 Model 和 view
     * <p>
     * Model只是用来传输数据的，并不会进行业务的寻址。
     * ModelAndView 却是可以进行业务寻址的，就是设置对应的要请求的静态文件，这里的静态文件指的是类似jsp的文件。
     * <p>
     * Model是每一次请求自动创建，但是ModelAndView 是需要我们自己去new的
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 请求结束，清理掉相关的 TheadLocal 数据
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        hostHolder.clear();
    }
}
