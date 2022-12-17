package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞操作
     */
    public void like(int userId, int entityType, int entityId) {
        // 1、首先获取，Redis 存储的 Key
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType, entityId);
        // 2、然后判断当前用户是否已经点赞，若已点赞取消点赞，否则将 userId 加入 set 中
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if (isMember) {
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        } else {
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }
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
}
