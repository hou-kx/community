package com.nowcoder.community.util;

/**
 * 一些公用的状态声明
 */
public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;
    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;
    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;
    /**
     * 默认登录凭证存活时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    /**
     * 勾选记住我转态，登录凭证存活时间
     */
    int REMEMBER_ME_EXPIRED_SECONDS = 3600 * 24 * 10;
}
