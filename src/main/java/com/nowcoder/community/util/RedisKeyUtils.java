package com.nowcoder.community.util;

public class RedisKeyUtils {
    private static final String SPLIT = ":";
    // 评论，帖子，回复等实体的赞，key 的前缀 prefix
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    /**
     * discuss:entity:entityType:entityId -> set(userId1, userId2, ···)
     * Redis 中存储 所有点赞用户 Id 的集合
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
}
