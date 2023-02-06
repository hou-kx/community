package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.RedisKeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceV1 extends UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    /**
     * 通过 Id 查询用户
     */
    public User findUserById(int id) {
        // return userMapper.selectById(id);
        User user = findUserByCache(id);
        if (user == null) {
            user = initUserCache(id);
        }
        return user;
    }


    /**
     * 登录创建登录凭证，密码验证的时候也需要进行 MD5 加密之后验证
     */
    @Override
    public Map<String, Object> login(String username, String password, String ticket, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        // 1. 表单验证
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        // 2. 账号验证，获取到，用户名对应的信息
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }
        if (user.getStatus() != 1) {     // 未激活，包含以后 status 扩展: 删除、冻结啊等等状态
            map.put("usernameMsg", "该账户未激活！");
            return map;
        }

        // 3. 密码验证, md5 加密
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码错误！");
            return map;
        }

        // 4. 执行到这，登录就没问题了  生成/修改 登录凭证, 若当前ticket已经存在，则直接使用，若不是同个账号则创建新的ticket
        LoginTicket loginTicket = StringUtils.isBlank(ticket) ? null : findLoginTicket(ticket);
        if (loginTicket == null || loginTicket.getUserId() != user.getId()) {
            loginTicket = new LoginTicket();
            loginTicket.setUserId(user.getId());
            loginTicket.setTicket(CommunityUtil.generateUUID());
            loginTicket.setStatus(0);
            loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
        } else {
            loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
            loginTicket.setStatus(0);
        }
        // 5. 存储 ticket 到redis 中去，这里直接存储为字符串，自动进行序列化
        String ticketKey = RedisKeyUtils.getPrefixTicket(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, loginTicket, expiredSeconds * 1000L, TimeUnit.SECONDS);
        // 6.
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 退出账户，登录凭证失效
     */
    @Override
    public void logout(String ticket) {
        String ticketKey = RedisKeyUtils.getPrefixTicket(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);   // 登录凭证设为 1 无效。
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
    }

    /**
     * 查询 LoginTicket
     */
    @Override
    public LoginTicket findLoginTicket(String ticket) {
        String ticketKey = RedisKeyUtils.getPrefixTicket(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    /**
     * 激活账号
     */
    @Override
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT; // 重复激活
        } else if (user.getActivationCode().equals(code)) {
            int count = userMapper.updateStatus(userId, 1);
            // 为了保持 MySQL 和 Redis 数据一致，修改的时候直接删除 Redis 上的缓存的用户信息
            if (count > 0) {
                deleteUserCache(userId);
            }
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 更新用户头像
     *
     * @param userId    改用户的 userId
     * @param headerUrl 修改后的头像地址
     * @return 收到影响的行数
     */
    @Override
    public int updateHeader(int userId, String headerUrl) {
        int count = userMapper.updateHeader(userId, headerUrl);
        // 为了保持 MySQL 和 Redis 数据一致，修改的时候直接删除 Redis 上的缓存的用户信息
        if (count > 0) {
            deleteUserCache(userId);
        }
        return count;
    }

    /**
     * 修改密码
     *
     * @param userId   用户Id
     * @param password 用户新密码
     * @param salt     密码加盐
     * @return 成功返回 1
     */
    @Override
    public int updatePassword(int userId, String password, String salt) {
        password = CommunityUtil.md5(password + salt);
        int count = userMapper.updatePassword(userId, password);
        // 为了保持 MySQL 和 Redis 数据一致，修改的时候直接删除 Redis 上的缓存的用户信息
        if (count > 0) {
            deleteUserCache(userId);
        }
        return count;
    }

    /**
     * 查缓存
     */
    private User findUserByCache(int userId) {
        String userKey = RedisKeyUtils.getPrefixUser(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    /**
     * 初始化缓存
     */
    private User initUserCache(int userId) {
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtils.getPrefixUser(userId);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    /**
     * 数据变更时清除缓存
     */
    private void deleteUserCache(int userId) {
        String userKey = RedisKeyUtils.getPrefixUser(userId);
        redisTemplate.delete(userKey);
    }
}
