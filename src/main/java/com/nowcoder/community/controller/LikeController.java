package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId) {
        // 1、首先获得当前登录用户，可以用拦截器拒绝未登录用户操作，后续， Spring Security 统一管理
        User user = hostHolder.getUser();
        // 2、实现点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        // 3、点赞结果，数量、状态
        Long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        // 4、将点赞结果封装到 map 中并传回 JSON 字符串
        Map<String, Object> res = new HashMap<>();
        res.put("likeCount", likeCount);
        res.put("likeStatus", likeStatus);

        return CommunityUtil.getJSONString(0, null, res);
    }
}
