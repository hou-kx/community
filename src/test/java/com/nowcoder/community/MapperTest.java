package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.*;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;

//@RunWith(Spring.class) 这个Idea自导Junit 环境自己加上容易出现导错org.junit.jupiter.api.Test包的问题
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //Test方式也使用main中的配置类
public class MapperTest implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testMapper() {
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
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost post : list) {
            System.out.println(post);
        }

        int total = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(total);
    }

    @Test
    public void inserTicketTest() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(250);
        loginTicket.setTicket("ticket Test");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));  // 10min  outtime
        System.out.println(loginTicketMapper.insertLoginTicket(loginTicket));
    }

    @Test
    public void selectTicketTest() {
        System.out.println(loginTicketMapper.selectByTicket("ticket Test").toString());
    }

    @Test
    public void updateTicketTest() {
        System.out.println("#####  before change  #########");
        System.out.println(loginTicketMapper.selectByTicket("ticket Test").toString());
        System.out.println("#####  changed  #########");

        System.out.println(loginTicketMapper.updateStatus("ticket Test", 1));

        System.out.println(loginTicketMapper.selectByTicket("ticket Test").toString());
    }

    @Test
    public void updateLoginTicketTest() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket("4ca2066fa35449348d66455144ba3d76");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + DEFAULT_EXPIRED_SECONDS * 1000L));
        System.out.println((loginTicketMapper.updateLoginTicket(loginTicket)));
    }

    @Test
    public void selectDiscussPostTest() {
        int id = 249;
        System.out.println(discussPostMapper.selectDiscussPostById(id).toString());
    }

    @Test
    public void selectLetterUnreadCountTest() {
        int userId = 111;
        String conversationId = "111_131";
        int offset = 0;
        int limit = 20;

        int unreadCount = messageMapper.selectLetterUnreadCount(userId, conversationId);
        int allUnreadCount = messageMapper.selectLetterUnreadCount(userId, null);
        System.out.println("unread count :" + unreadCount);
        System.out.println("all unread count :" + allUnreadCount);
        System.out.println("===========letter=================");

        List<Message> letters = messageMapper.selectLetters(conversationId, offset, limit);
        for (Message m : letters) System.out.println(m.toString());
        int letterCount = messageMapper.selectLetterCount(conversationId);
        System.out.println("letterCount : " + letterCount);
        System.out.println("==========conversations====================");

        List<Message> conversations = messageMapper.selectConversations(userId, offset, limit);
        for (Message m : conversations) System.out.println(m.toString());
        int conversationCount = messageMapper.selectConversationCount(userId);
        System.out.println("conversationCount : " + conversationCount);

    }
}
