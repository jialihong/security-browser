package com.jiaxh.security.browser.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiaxh.security.browser.support.SimpleResponse;
import com.jiaxh.security.core.properties.LoginType;
import com.jiaxh.security.core.properties.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义失败处理器
 * implements AuthenticationFailureHandler
 * extends SimpleUrlAuthenticationFailureHandler
 * @Auther: jiaxh
 * @Date: 2019/5/8 16:29
 */
@Component("jiaAuthenticationFailureHandler")
public class JiaAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        logger.info("登录失败...");
        //配置文件中的LoginType为json时，使用自定义的登录失败处理器
        if(LoginType.JSON.equals(securityProperties.getBrowser().getLoginType())){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json;charset=UTF-8");
//            response.getWriter().write(objectMapper.writeValueAsString(exception));
            response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponse(exception.getMessage())));
        }else{
            //配置文件中的LoginType为redirect时,使用spring security默认的登录失败处理器
            super.onAuthenticationFailure(request,response,exception);
        }

    }
}
