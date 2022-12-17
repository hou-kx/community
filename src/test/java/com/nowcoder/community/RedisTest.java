package com.nowcoder.community;

import io.lettuce.core.cluster.event.TopologyRefreshEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //Test方式也使用main中的配置类
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey, 1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHashes() {
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey, "username", "zhang san");
        redisTemplate.opsForHash().put(redisKey, "age", 11);
        redisTemplate.opsForHash().put(redisKey, "sex", "girl");
        System.out.println("username: " + redisTemplate.opsForHash().get(redisKey, "username"));
        System.out.println("keys: " + redisTemplate.opsForHash().keys(redisKey));
        System.out.println("values: " + redisTemplate.opsForHash().values(redisKey));
        System.out.println("entries: " + redisTemplate.opsForHash().entries(redisKey));
    }

    @Test
    public void testList() {
        String redisKey = "test:ids";
        redisTemplate.opsForList().leftPush(redisKey, 100);
        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 2));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 3));

        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
    }

    @Test
    public void testSet() {
        String redisKey = "test:teachers";
        redisTemplate.opsForSet().add(redisKey, "法外狂徒", "四大天王", "百变星君", "葫芦娃");

        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testSortSet() {
        String redisKey = "test:students";

        redisTemplate.opsForZSet().add(redisKey, "aaa", 10);
        redisTemplate.opsForZSet().add(redisKey, "bbb", 20);
        redisTemplate.opsForZSet().add(redisKey, "ccc", 30);
        redisTemplate.opsForZSet().add(redisKey, "ddd", 40);
        redisTemplate.opsForZSet().add(redisKey, "eee", 50);
        redisTemplate.opsForZSet().add(redisKey, "fff", 60);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println("ccc score: " + redisTemplate.opsForZSet().score(redisKey, "ccc"));
        System.out.println("rank: " + redisTemplate.opsForZSet().rank(redisKey, "eee"));
        System.out.println("reverseRank: " + redisTemplate.opsForZSet().reverseRank(redisKey, "eee"));
        System.out.println("range 0-3: " + redisTemplate.opsForZSet().range(redisKey, 0, 3));
        System.out.println("reverseRange 0-3: " + redisTemplate.opsForZSet().reverseRange(redisKey, 0, 3));
    }

    @Test
    public void testCommand() {
        System.out.println(redisTemplate.keys("*"));
    }

    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
         // BoundHashOperations; BoundListOperations; BoundSetOperations; BoundZSetOperations;
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    @Test
    public void testTransactional(){
        Object obj = redisTemplate.execute(new SessionCallback(){
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:trans";
                // 启动事务
                operations.multi();

                operations.opsForSet().add(redisKey, "123");
                operations.opsForSet().add(redisKey, "456");
                operations.opsForSet().add(redisKey, "789");

                // 此时事务还没有提交，因而查询不到数据
                System.out.println(operations.opsForSet().members(redisKey));
                // 提交事务
                return operations.exec();
            }
        });
        System.out.println(obj);
    }
}
