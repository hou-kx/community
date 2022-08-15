package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sun.java2d.pipe.SpanShapeRenderer;

import java.security.PublicKey;
import java.util.Map;

@Controller
/**
 * 这里不配置  RequestMapping，则直接访问就是类中方法
 */
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "site/login"; // 在login 界面进行模板使用的声明，即可
    }

    /**
     * 1、页面传来的数据只要和user的属性对应SpringMVC就会自动装配好注入给user对象
     * 2、并且 SpringMVC 也可自动装配方法中其它变量到 Model  中，如本方法中 user 装配到 model 中, 也可以如下主动装配到model中
     *
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        model.addAttribute(user);   // 主动装配user
        Map<String, Object> map = userService.register(user);
        // 这里判断是否新增了一行，
        if ((int) map.getOrDefault("registerMsg", 0) > 0) {
            model.addAttribute("msg", "注册成功，已经向您的邮箱发送激活邮件! \ncode:" + map.getOrDefault("registerMsg", 0).toString());
            model.addAttribute("target", "/index"); // 当前跳转页面
            return "site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "site/register";
        }
    }

    @RequestMapping(path = "activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model,
                             @PathVariable("userId") int userId,
                             @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，您的账户可正常使用！");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，您的账户已激活！");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "site/operate-result";
    }
}
