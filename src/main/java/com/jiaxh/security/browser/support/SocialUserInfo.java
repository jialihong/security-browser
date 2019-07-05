package com.jiaxh.security.browser.support;

/**
 * @Auther: jiaxh
 * @Date: 2019/7/5 15:52
 */
public class SocialUserInfo {

    /**
     * 正在进行社交登录的用户所属的第三方
     */
    private String providerId;

    /**
     * 用户在第三方社交平台的id，即openId
     */
    private String providerUserId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String headImg;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }
}
