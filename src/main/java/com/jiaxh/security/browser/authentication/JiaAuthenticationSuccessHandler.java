package com.jiaxh.security.browser.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiaxh.security.core.properties.LoginType;
import com.jiaxh.security.core.properties.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * implements AuthenticationSuccessHandler
 * extends SavedRequestAwareAuthenticationSuccessHandler
 * @Auther: jiaxh
 * @Date: 2019/5/8 15:54
 */
@Component("jiaAuthenticationSuccessHandler")
public class JiaAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;
    /**
     * 登录成功后将用户信息以json格式传送给前端
     * @param request
     * @param response
     * @param authentication 是security的一个核心类，将UserDetails的信息包装起来
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("登录成功......");
        //配置文件中的logintype为json时，使用自定义的登录成功处理器
        if(LoginType.JSON.equals(securityProperties.getBrowser().getLoginType())){
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(authentication));
        } else{
            //配置文件中的logintype为跳转时，使用spring security默认的登录成功处理器
            super.onAuthenticationSuccess(request,response,authentication);
        }

    }
}
