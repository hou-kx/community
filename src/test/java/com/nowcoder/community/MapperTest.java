package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;
import java.util.Date;
import java.util.List;

//@RunWith(Spring.class) 这个Idea自导Junit 环境自己加上容易出现导错org.junit.jupiter.api.Test包的问题
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //Test方式也使用main中的配置类
public class MapperTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testMapper(){
        User user = userMapper.selectById(150);
        System.out.println(user);

        user = userMapper.selectByName("guanyu");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder102@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("甜包子");
        user.setPassword("123456");
        user.setEmail("tianbaozi@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");   //0-1000
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser() {
        int rows = userMapper.updatePassword(150, "521521");
        System.out.println(rows);


        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/520.png");
        System.out.println(rows);

        rows = userMapper.updateStatus(150, 2);
        System.out.println(rows);
    }

    @Test
    public void testDiscussPost() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0,10);
        for(DiscussPost post : list) {
            System.out.println(post);
        }

        int total = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(total);
    }
}
