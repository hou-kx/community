package com.nowcoder.community.util;

/**
 * 这个类用于返回，redis 中的 key
 */
public class RedisKeyUtils {
    private static final String SPLIT = ":";
    // 评论，帖子，回复等实体的赞，key 的前缀 prefix
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    // 记录用户收到的赞
    private static final String PREFIX_USER_LIKE = "like:user";

    /**
     * discuss:entity:entityType:entityId -> set(userId1, userId2, ···)
     * Redis 中存储 所有点赞用户 Id 的集合
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
    /**
     * 某个用户接收到的赞：user-discuss-key
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }
}
