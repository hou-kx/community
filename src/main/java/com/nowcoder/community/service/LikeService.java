package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞操作
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
//        // 1、首先获取，Redis 存储的 Key
//        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
//        // 2、然后判断当前用户是否已经点赞，若已点赞取消点赞，否则将 userId 加入 set 中
//        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if (isMember) {
//            redisTemplate.opsForSet().remove(entityLikeKey, userId);
//        } else {
//            redisTemplate.opsForSet().add(entityLikeKey, userId);
//        }
        // 2023-01-31 houkx 重构 点赞的时候并统计更新被点赞人的点赞数，这里使用事务进行完整性约束
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // return null;
                //1. 先获取，点赞相关的key，被点赞内容实体、被点赞用户
                String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtils.getUserLikeKey(entityUserId);

                //2. 在启动事务之前先进行查询，是否已经点赞
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                //3. 启动事务
                operations.multi();
                //4. 事务中处理操作
                if (isMember) {
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                // 5. 结束事务
                return operations.exec();
            }
        });
    }

    /**
     * 查询实体点赞的数量值
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询某人是否给当前实体点过赞, 1-是， 0-否
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    /**
     * 查询用户获得点赞数量
     */
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtils.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }
}
