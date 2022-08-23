package com.nowcoder.community.service;

import com.nowcoder.community.controller.LoginController;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import javafx.scene.chart.Axis;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    // 继承 静态变量的接口
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${community.path.domain}${server.port}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LoginTicketMapper ticketMapper;

    private String getDomain() {

        final Logger logger = LoggerFactory.getLogger(LoginController.class);
        String hostAddress = "";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("获取 hostAddress 失败！" + e.getMessage());
        }
        if (StringUtils.isBlank(hostAddress)) {
            return domain;
        }
        return hostAddress + ":";
    }

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
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
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
        // url : http://localhost:8090/community/activation/101/activateCode
        // String url = this.getDomain() + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
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

    /**
     * 登录创建登录凭证，密码验证的时候也需要进行 MD5 加密之后验证
     */
    public Map<String, Object> login(String username, String password, String ticket, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        // 1. 表单验证
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        // 2. 账号验证，获取到，用户名对应的信息
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }
        if (user.getStatus() != 1) {     // 未激活，包含以后 status 扩展: 删除、冻结啊等等状态
            map.put("usernameMsg", "该账户未激活！");
            return map;
        }

        // 3. 密码验证, md5 加密
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码错误！");
            return map;
        }

        // 4. 执行到这，登录就没问题了  生成/修改 登录凭证
        LoginTicket loginTicket = StringUtils.isBlank(ticket) ? null : ticketMapper.selectByTicket(ticket);
        if (loginTicket == null) {
            loginTicket = new LoginTicket();
            loginTicket.setUserId(user.getId());
            loginTicket.setTicket(CommunityUtil.generateUUID());
            loginTicket.setStatus(0);
            loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
            ticketMapper.insertLoginTicket(loginTicket);
        } else {
            loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
            loginTicket.setStatus(0);
            ticketMapper.updateLoginTicket(loginTicket);
        }
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 退出账户，登录凭证失效
     */
    public void logout(String ticket) {
        ticketMapper.updateStatus(ticket, 1);   // 登录凭证设为 1 无效。
    }

    /**
     * 强制退出所有账户，登录凭证失效，如修改密码后
     */
    public int logoutById(int userId) {
        return ticketMapper.updateStatusById(userId, 1);   // 登录凭证设为 1 无效。
    }

    /**
     * 查询 LoginTicket
     *
     * @param ticket
     * @return
     */
    public LoginTicket findLoginTicket(String ticket) {
        return ticketMapper.selectByTicket(ticket);
    }

    /**
     * 更新用户头像
     *
     * @param userId    改用户的 userId
     * @param headerUrl 修改后的头像地址
     * @return 收到影响的行数
     */
    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }

    /**
     * 修改密码
     *
     * @param userId   用户Id
     * @param password 用户新密码
     * @param salt     密码加盐
     * @return 成功返回 1
     */
    public int updatePassword(int userId, String password, String salt) {
        password = CommunityUtil.md5(password + salt);
        return userMapper.updatePassword(userId, password);
    }
}
