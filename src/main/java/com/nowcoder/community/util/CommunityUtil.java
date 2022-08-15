package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CommunityUtil {
    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    // MD5加密 加密，加密不能解密，输入进来的：密码 + sault
    public static String md5 (String key) {
        if(StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

}
