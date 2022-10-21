package com.nowcoder.community;

import com.nowcoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //Test方式也使用main中的配置类
public class TransactionTests {
    @Autowired
    private AlphaService alphaService;

    @Test
    public void testTransactionSave1(){
        Object obj = alphaService.transactionalSave1();
        System.out.println(obj);
    }

    @Test
    public void testTransactionSave2(){
        Object obj = alphaService.transactionalSave2();
        System.out.println(obj);
    }
}
