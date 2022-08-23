package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

public class CookieUtil implements CommunityConstant {
    /**
     * 根据名称获取 cookie 中的值
     *
     * @param request
     * @param name
     * @return
     */
    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException(PARAM_EMPTY_MSG);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return "";
        }
//        // 找到对应 cookie
//        for (Cookie c : cookies) {
//            if (c.getName().equals(name)) {
//                return c.getValue();
//            }
//        }
//        return null;

        // lambda 表达式
        // .get() If a value is present in this Optional, returns the value, otherwise throws NoSuchElementException.
        // 如果没有则会自动抛出 NoSuchElementException 异常 throw new NoSuchElementException("No value present");
        Optional<Cookie> ticket = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(name)).findFirst();
        return ticket.map(Cookie::getValue).orElse("");
        // return ticket.getValue();
    }
}
