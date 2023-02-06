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
    //关注者、关注目标
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";
    // 登录验证码
    private static final String PREFIX_KAPTCHA = "kaptcha";
    // 登录凭证
    private static final String PREFIX_TICKET = "ticket";
    // 用户信息
    private static final String PREFIX_USER = "user";

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

    /**
     * 某个用户关注的实体 k - v
     * followee:userId:entityType -> zset(entityId, now)
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某个实体拥有的粉丝
     * follower:entityType:entityId -> zset(userId, now)
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 获取验证码 key -> kaptcha:owner (UUID)
     */
    public static String getPrefixKaptcha(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 获取登录凭证 Key -> ticket:userId
     */
    public static String getPrefixTicket(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 用户信息 Key -> user:userId
     */
    public static String getPrefixUser(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
