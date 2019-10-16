package org.easyblog.bean;

import java.io.Serializable;
import java.util.Date;

public class UserAttention implements Serializable {

    private static final long serialVersionUID = -3911799858589965008L;

    private Integer id;

    private Integer userId;

    private Integer attentionId;

    private Date attentionTime;

    public UserAttention() {
    }

    public UserAttention( Integer userId, Integer attentionId, Date attentionTime) {
        this.userId = userId;
        this.attentionId = attentionId;
        this.attentionTime = attentionTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAttentionId() {
        return attentionId;
    }

    public void setAttentionId(Integer attentionId) {
        this.attentionId = attentionId;
    }

    public Date getAttentionTime() {
        return attentionTime;
    }

    public void setAttentionTime(Date attentionTime) {
        this.attentionTime = attentionTime;
    }

    @Override
    public String toString() {
        return "UserAttention{" +
                "id=" + id +
                ", userId=" + userId +
                ", attentionId=" + attentionId +
                ", attentionTime=" + attentionTime +
                '}';
    }
}