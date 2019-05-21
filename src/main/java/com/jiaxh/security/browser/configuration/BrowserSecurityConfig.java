package com.jiaxh.security.browser.configuration;

import com.jiaxh.security.core.properties.SecurityProperties;
import com.jiaxh.security.core.validate.code.ValidateCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * WebSecurityConfigurerAdapter 是Spring Security提供的一个安全适配器类
 * @Auther: jiaxh
 * @Date: 2019/5/6 10:42
 */
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 自定义登录成功处理器
     */
    @Autowired
    private AuthenticationSuccessHandler jiaAuthenticationSuccessHandler;

    /**
     * 自定义登录失败处理器
     */
    @Autowired
    private AuthenticationFailureHandler jiaAuthenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
//        validateCodeFilter.setAuthenticationFailureHandler(jiaAuthenticationFailureHandler);
//        validateCodeFilter.setSecurityProperties(securityProperties);
//        validateCodeFilter.afterPropertiesSet();

        //使用原生的http请求
        //http.httpBasic()
        http
                //将自定义的验证码校验的过滤器设置到UsernamePasswordAuthenticationFilter之前
//                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                //使用自定义的登录页面的url，如果不加该方法则使用security默认的登录页
//                .loginPage("/jia-login.html")
                .loginPage("/authentication/require")
                //登录页提交时的url，向该url提交时，使用UsernamePasswordAuthenticationFilter验证用户名密码是否正确
                .loginProcessingUrl("/authentication/form")
                //使用自定义的成功处理器
                .successHandler(jiaAuthenticationSuccessHandler)
//                //使用自定义的失败处理器
                .failureHandler(jiaAuthenticationFailureHandler)
                .and()
                .authorizeRequests()
                //访问以下url时不需要认证，否则会报 重定向次数过多 的错误
                .antMatchers("/authentication/require",securityProperties.getBrowser().getLoginPage(),"/code/image")
                .permitAll()
                .anyRequest()
                .authenticated()
        .and()
         //关闭csrf验证 跨站请求伪造防护
        .csrf().disable();
    }
}
