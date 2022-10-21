package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service    //注解表示提供业务的类
//@Scope("prototype") //Spring 中的bean都是单例的,通过scope更改 prototype每次调用都产生一个新的实例 还有 Request、Session、Global-Session//
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaService() {     //构造函数必须没有显式返回类型,默认无参的构造方法
        System.out.println("实例化AlphaService");
    }

    @PostConstruct //方法注解，表示这个方法在构造方法之后调用
    public void init() {
        System.out.println("初始化AlphaService");
    }

    @PreDestroy //在bean 销毁之前调用
    public void destory() {
        System.out.println("销毁AlphaService");
    }

    public String find() {
        return alphaDao.select();
    }

    /**
     * 使用注解的方式，是下面的方法成为一个整体，一旦出现问题，下边的DML就会回滚不保存执行
     * isolation = Isolation.READ_COMMITTED    设置隔离级别：常用的隔离级别 read_commit、repeatable_read
     * propagation = Propagation.REQUIRED  传播机制  相互调用的时候 事务是否起效
     * 传播机制：
     * required        支持外部事物调用者，如果不存在则创建新的事务
     * requires_new    创建新事务，并暂停外部事务
     * nested          若存在外部事物，则嵌套在外部事务中执行，若外部不存在就和 required 一致
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object transactionalSave1() {
        // 新增用户
        User user = new User();
        user.setUsername("alphaName");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@Gmail.com");
        user.setHeaderUrl("http://image.nowcode.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("hello");
        post.setContent("新人报道！");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("asd");

        return "OK";
    }

    /**
     * @return
     */
    public Object transactionalSave2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                // 新增用户
                User user = new User();
                user.setUsername("alphaName");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("alpha@Gmail.com");
                user.setHeaderUrl("http://image.nowcode.com/head/99t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                // 新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("hello");
                post.setContent("新人报道！");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("asd");

                return "OK";
            }
        });
    }
}
