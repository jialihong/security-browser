package com.jiaxh.security.browser.configuration;

import com.jiaxh.security.core.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import com.jiaxh.security.core.properties.SecurityProperties;
import com.jiaxh.security.core.validate.code.ValidateCodeFilter;
import com.jiaxh.security.core.validate.code.sms.SmsCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

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

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        //系统启动时自动创建表  创建一次即可
//        tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //配置图形验证码校验的filter
        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        validateCodeFilter.setAuthenticationFailureHandler(jiaAuthenticationFailureHandler);
        validateCodeFilter.setSecurityProperties(securityProperties);
        validateCodeFilter.afterPropertiesSet();

        //配置短信验证码校验的filter
        SmsCodeFilter smsCodeFilter = new SmsCodeFilter();
        smsCodeFilter.setAuthenticationFailureHandler(jiaAuthenticationFailureHandler);
        smsCodeFilter.setSecurityProperties(securityProperties);
        smsCodeFilter.afterPropertiesSet();

        //使用原生的http请求
        //http.httpBasic()
        http
                //将自定义的短信验证码校验的过滤器设置到UsernamePasswordAuthenticationFilter之前
                .addFilterBefore(smsCodeFilter,UsernamePasswordAuthenticationFilter.class)
                //将自定义的图形验证码校验的过滤器设置到UsernamePasswordAuthenticationFilter之前
                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
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
                //记住我
                .rememberMe()
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())
                .userDetailsService(userDetailsService)
                .and()
                .authorizeRequests()
                //访问以下url时不需要认证，否则会报 重定向次数过多 的错误
                .antMatchers("/authentication/require",securityProperties.getBrowser().getLoginPage(),"/code/image","/code/*")
                .permitAll()
                .anyRequest()
                .authenticated()
        .and()
         //关闭csrf验证 跨站请求伪造防护
        .csrf().disable()
        //使用SmsCodeAuthenticationSecurityConfig
        .apply(smsCodeAuthenticationSecurityConfig);
    }
}
