package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

// 注解这是一个Bean MyBatis 中使用Mapper来标识这是一个Bean
//@Repository
@Mapper
public interface UserMapper {
    // 接口只需要定义方法,要想实现则需要提供配置文件，里面编写了SQL语句，MyBits通过配置文件，底层自动来生成实现类
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    // 修改了的行数
    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
