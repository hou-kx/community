package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //Test方式也使用main中的配置类
public class SensitiveTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void setSensitiveFilterTest(){
        String text = "小明喜欢赌博，一天天的在学校学校装逼，真是特么的无语！你觉得呢?";
        System.out.println(text);
        System.out.println(sensitiveFilter.filter(text));
        System.out.println("");
        System.out.println("=======================================================");
        System.out.println("");
        text = "小明喜欢￥赌￥博￥，一天天的在学校学校@装@逼@，真是☆特☆么☆的☆无☆语☆！你觉得呢?";
        System.out.println(text);
        System.out.println(sensitiveFilter.filter(text));
    }
}
