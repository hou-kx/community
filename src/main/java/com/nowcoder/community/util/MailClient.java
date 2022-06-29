package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {
    // 创建一个日志对象，以当前的类命名的
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender javaMailSender;

    // from to content;
    // 从 application.properties 注入变量，spring.mail.username=community.4616@foxmail.com
    @Value("${spring.mail.username}")
    private String from;

    // 实现发送邮件的功能 收件人 主题 内容
    public void sendMail (String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage); // Spring帮助创建mimeMessage的帮助类
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true 表示支持html
            javaMailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            // e.printStackTrace();
            logger.error("Send mail fail" + e.getMessage());
        }

    }
}
