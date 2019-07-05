package com.jiaxh.security.browser.controller;

import com.jiaxh.security.browser.support.SimpleResponse;
import com.jiaxh.security.browser.support.SocialUserInfo;
import com.jiaxh.security.core.properties.SecurityProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理不同类型的请求
 * 当需要身份认证时跳转到这里
 * @Auther: jiaxh
 * @Date: 2019/5/7 14:54
 */
@RestController
public class BrowserSecurityController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private RequestCache requestCache = new HttpSessionRequestCache();

    //页面跳转
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private ProviderSignInUtils providerSignInUtils;

    @RequestMapping("/authentication/require")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public SimpleResponse requireAuthentication(HttpServletRequest request, HttpServletResponse response) throws Exception{

        final SavedRequest savedRequest = requestCache.getRequest(request, response);

        if(savedRequest != null){
            final String redirectUrl = savedRequest.getRedirectUrl();
            logger.info("引发跳转的请求为："+redirectUrl);
            if(StringUtils.endsWithIgnoreCase(redirectUrl,".html")){
                //重定向到不同的页面
                redirectStrategy.sendRedirect(request,response,securityProperties.getBrowser().getLoginPage());
            }
        }

        return new SimpleResponse("访问的服务需要身份认证，请引导用户到登录页面");
    }

    @GetMapping("/social/user")
    public SocialUserInfo getSocialUserInfo(HttpServletRequest request){
        SocialUserInfo socialUserInfo = new SocialUserInfo();
        //注入providerSignInUtils，从session中获取connection信息,从而获取用户在服务提供商（即第三方）的信息
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
        socialUserInfo.setProviderId(connection.getKey().getProviderId());
        socialUserInfo.setProviderUserId(connection.getKey().getProviderUserId());
        socialUserInfo.setNickname(connection.getDisplayName());
        socialUserInfo.setHeadImg(connection.getImageUrl());
        return socialUserInfo;
    }
}
