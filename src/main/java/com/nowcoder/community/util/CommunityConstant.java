package com.nowcoder.community.util;

/**
 * 一些公用的状态声明
 */
public interface CommunityConstant {
    /**
     * 0-激活成功；1-重复激活；2-激活失败
     */
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAILURE = 2;
    /**
     * 默认登录凭证存活时间； 勾选记住我的登录凭证存活时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    int REMEMBER_ME_EXPIRED_SECONDS = 3600 * 24 * 10;

    String PARAM_EMPTY_MSG = "Parameter is empty!";

    /**
     * 实体类型：1-帖子 ; 2-评论；3-人(用户)
     */
    int ENTITY_TYPE_POST = 1;
    int ENTITY_TYPE_COMMENT = 2;
    int ENTITY_TYPE_USER = 3;

    /**
     * 未读消息 0；已读消息 1；删除消息 2；
     */
    int MESSAGE_STATUS_UNREAD = 0;
    int MESSAGE_STATUS_READ = 1;
    int MESSAGE_STATUS_DELETED = 2;
}
