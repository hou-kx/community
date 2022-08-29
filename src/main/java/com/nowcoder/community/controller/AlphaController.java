package com.nowcoder.community.controller;

import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

@Controller
@RequestMapping("/alpha")   //给这个类取一个访问名
public class AlphaController {

    //    @Autowired   //filed注入，不建议使用
    //    private AlphaService alphaService;
    private final AlphaService alphaService;

    @Autowired //构造器注入 或者Setter注入合适
    public AlphaController(AlphaService alphaService) {
        this.alphaService = alphaService;
    }

    @RequestMapping("/hello") //方法访问名
    @ResponseBody //返回类型
    public String sayHello() {
        return "Hello Houkx Quincy!";
    }

    @ResponseBody
    @RequestMapping("/Data")
    public String getData() {
        return alphaService.find();
    }

    // SpringMVC获得请求、响应对象，实际封装了，了解
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        // 我们可以通过response 对象向浏览器返回任何数据这里就不用设置返回值了
        // 在这个方法中加以声明，Dispatcher，在调用这个方法的时候自动传给你

        // 获取请求数据
        // 下面两行实际是请求第一行的数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());

        // 使用一个迭代器获取，请求头的内容 key-value，请求头的数据，消息头
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        //请求体，业务数据，包含参数
        System.out.println(request.getParameter("code"));

        // 返回响应数据,首先设置返回的数据类型和字符集编码方式
        response.setContentType("text/html;charset=utf-8");
        try ( //直接在try的括号里定义流，在try结束的时候就自动调用close
              PrintWriter writer = response.getWriter(); //获取链接的输入流，相当获得服务器写回浏览器的权限
        ) {// 因为可能找不到句柄的错误，所以圈起来
            writer.write("<h1>甜包子!</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get请求

    // path = /Students?current=1&limit=20   当前页、每页显示的数目
    @RequestMapping(path = "/students", method = RequestMethod.GET)    //这里指定了路径，请求方式，上面的方式都是未指定
    @ResponseBody   //表示返回的是字符串
    public String getStudent(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,    //这里只要形参同名就能捕获到，加上@RequestParam指定参数对应
            @RequestParam(name = "limit", required = false, defaultValue = "20") int limit        //require表示是否必须有参，若无设置默认参数
    ) {
        System.out.println("current:" + current);
        System.out.println("limit" + limit);
        return "Some 甜包子";
    }

    // 获取一个学生的信息 path = /student/123  // 123具体的学生 {}框起来的是变量
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)  //因为这个时候设置的路径参数，若没传参，会匹配不到页面
    @ResponseBody
    public String getStudent(
            @PathVariable(name = "id") int id       //获取路径中的参数
    ) {
        System.out.println("id: " + id);
        return "a 甜包子";
    }

    // post请求提交表单
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age) {    //只要名称对应，传过来就可以使用
        System.out.println(name);
        System.out.println(age);
        return "Success!";
    }

    // 响应 HTML数据  1
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "甜包子");
        mav.addObject("age", 24);
        mav.setViewName("/demo/view");   // 因为Thymeleaf 默认是导出的是HTML 这里实际上是 view.html
        return mav;
    }

    // 响应HTML数据 2
    @RequestMapping(path = "/school", method = RequestMethod.GET)  // 不加@ResponseBody 默认是HTML文件，加上表示字符串
    public String getSchool(Model model) { // Dispatcher 持有Model的引用，是个Bean，往里头装收据
        model.addAttribute("name", "东林");
        model.addAttribute("age", 70);
        return "demo/view"; // 返回的 View 的路径 String  Thymeleaf 将 model和view模板生成HTML
    }

    // 响应异步请求 Json数据 当前网页不刷新，悄悄的访问数据库
    // java 对象返回给浏览器，而浏览器是JS来解析，这就需用Json这种独立的具体格式字符串衔接，跨语言用的比较多
    // Java对象 -> Json 字符串 -> JS对象
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody   // Dispatcher 调用这里 注解是返回字符串，方法是Map则return 的结果就是一个Json
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "甜包子");
        emp.put("age", 24);
        emp.put("salary", 8000.00);
        return emp;
    }

    // 多个数据的情况
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody   // Dispatcher 调用这里 注解是返回字符串，方法是Map则return 的结果就是一个Json
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "甜包子");
        emp.put("age", 24);
        emp.put("salary", 8000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "小棉袄");
        emp.put("age", 24);
        emp.put("salary", 9000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "憨批");
        emp.put("age", 24);
        emp.put("salary", 8000.00);
        list.add(emp);

        return list;
    }

    // cookie， 返回的实在响应的  head
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody   // json 字符串
    public String setCookie(HttpServletResponse response) {
        // 创建 cookie， 存到response 中响应浏览器时，返回给浏览器
        // new Cookie 创建必须传入参数，每个cookie对象只能存一对 key value 并且都需要是字符串
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置cookie 生效范围、生存时间
        cookie.setPath("/communiyt/alpha");
        cookie.setMaxAge(60 * 10);  // 默认存内存，关闭浏览器就没了，设置生存时间则存到硬盘里，直到超时失活

        // 发送cookie
        response.addCookie(cookie);

        return "set cookie!";
        //  调试结果 在response header 中  Set-Cookie: code=995ad461e07e439fb4558adb4675a785; Max-Age=600; Expires=Tue, 16-Aug-2022 14:14:12 GMT; Path=/communiyt/alpha
    }

    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody   // json 字符串
    public String getCookie() {

        // System.out.println(Arrays.toString(request.getCookies()));

        return "get Cookie!";
        // 调试结果，在response header 中:
    }

    // session 示例
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        // 在 spring mvc 中，自动创建注入  和model类似 注入进来就可以用了
        // session 可以存储任意类型数据
        session.setAttribute("code", CommunityUtil.generateUUID());
        session.setAttribute("name", "W-A");
        session.setAttribute("age", 60);
        return "set Session";
        // 调试结果 在response header 中 Set-Cookie: JSESSIONID=656DDC10A65E0C29F8F5189C0A274432; Path=/community; HttpOnly
        // max-age 没有就是存在内存里，关闭浏览器就没有了  jsessionID  就是 session ID
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.toString());
        System.out.println(session.getAttributeNames());
        System.out.println("code: " + session.getAttribute("code") + "\tname: " + session.getAttribute("name") + "\tage: " + session.getAttribute("age"));
        return "get session";
    }

    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody  // 表示这返回的不是网页而是字符串
    public String ajaxTest(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0, "AJAX-操作成功", null);
    }
}
