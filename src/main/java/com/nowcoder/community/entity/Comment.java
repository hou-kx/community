package com.nowcoder.community.entity;

import java.util.Date;

public class Comment {
    // 评论Id
    private int id;
    // 发布评论的用户id
    private int userId;
    // 评论的类型，对题目，帖子，视频等等类型
    private int entityType;
    // 当前评论类型下的实体Id，比如具体额那个题目或者帖子等
    private int entityId;
    // 评论的时候，有回复某个人，这个用 target 指向某个人
    private int targetId;
    // 目标内容
    private String content;
    // 当前评论的状态
    private int status;
    // 创建时间
    private Date CreateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", targetId=" + targetId +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", CreateTime=" + CreateTime +
                '}';
    }
}
