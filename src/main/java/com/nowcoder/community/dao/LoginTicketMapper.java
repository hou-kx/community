package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {
    // 增删改的方法一般返回的都是整数

    /**
     * // @Insert({"", "", ""})    @Update    @Select  自动多个字符串进行拼接，
     * 每个字符串末尾最好加上一个空格，这样拼接的时候不会出问题
     * // @options 来配置声明SQL的一些机制，这里配置自动生成主键 注入给字段 "id"
     */
    @Insert({
            "insert into login_ticket(user_id, ticket, status, expired) ",
            "values(#{userId}, #{ticket}, #{status}, #{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 登录重置登录凭证
     * "<script> "
     * "<if test = "a != b"> "
     * "and ``` = #{```} "
     * "</if> "
     * "</script>"
     *
     * prefix:在trim标签内sql语句加上前缀。
     * suffix:在trim标签内sql语句加上后缀。
     * prefixOverrides:指定去除多余的前缀内容
     * suffixOverrides:指定去除多余的后缀内容，如：suffixOverrides=","，去除trim标签内sql语句多余的后缀","。
     *
     * @param loginTicket
     * @return
     */
    @Update({
            "<script>",
            "update login_ticket",
            "<trim prefix = 'set' suffixOverrides = ','> ",
                "status = #{status}, ",
                "<if test=\"expired != null\"> ",
                    "expired = #{expired}, ",
                "</if> ",
            "</trim> ",
            "where 1=1 ",
            "and ticket = #{ticket} ",
            "</script>"
    })
    int updateLoginTicket(LoginTicket loginTicket);

    /**
     * 整张表围绕 ticket 建立，发送给浏览器保存 （cookie）
     */
    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    /**
     * 退出，凭证失效
     * 使用 if 动态
     */
    @Update({
            "update login_ticket set status = #{status} where 1=1 ",
            "and ticket = #{ticket} ",
    })
    int updateStatus(String ticket, int status);
}
