package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.xml.transform.Templates;
import java.security.PublicKey;

//@RunWith(Spring.class) 这个Idea自导Junit 环境自己加上容易出现导错org.junit.jupiter.api.Test包的问题
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //Test方式也使用main中的配置类
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail() {
        mailClient.sendMail("houkx.mail@qq.com", "testText Mail", "The first e-Mail !");
    }

    // templeaf
    @Test
    public void testHtmlMail() {
        Context context = new Context();    // 传给模板的对象
        context.setVariable("username", "Quincy");

        // 主动调用 templeaf 模板引擎 指定版本位置, 以及传参变量对象 返回html的网页字符串数据
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("houkx.mail@qq.com", "test HTML Mail", content);
    }
}
