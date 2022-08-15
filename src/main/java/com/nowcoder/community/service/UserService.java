package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.port}")
    private String serverPort;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("Parameter is invalid！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "The account is null!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "The passWord is null!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "The e-mail is null!");
            return map;
        }
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "Account already exists!");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "Email has been registered!");
            return map;
        }

        // 验证完毕，开始注册
        // 密码加密，生成salt
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getPassword()));
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        /** 这里mybatis 插入之后会自动回填用户ID, mybatis.configuration.useGeneratedKeys=true */
        int resCode = userMapper.insertUser(user);
        map.put("registerMsg", resCode);

        // 激活邮件
        // 创建 thymleaf context对象携带变量
        org.thymeleaf.context.Context context = new Context();
        context.setVariable("email", user.getEmail());
        // url : http://localhost:8080/community/activation/101/activateCode
        String url = domain + serverPort + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        // 加载 thymeleaf 模板网页
        String content = templateEngine.process("mail/activation", context);
        // 发送激活邮件
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT; // 重复激活
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

}
