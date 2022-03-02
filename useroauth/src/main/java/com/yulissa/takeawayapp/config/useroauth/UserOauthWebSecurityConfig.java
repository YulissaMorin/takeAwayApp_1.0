package com.yulissa.takeawayapp.config.useroauth;

import com.yulissa.takeawayapp.constant.Urls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

/**
 * @author yulissa
 */
@Configuration
public class UserOauthWebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(new BCryptPasswordEncoder())
                .usersByUsernameQuery("select username,password,enabled " +
                        "from user_security_info where username = ?")
                .authoritiesByUsernameQuery("select username,authorities " +
                        "from user_security_info where username = ?");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 关掉basic验证.有clientid验证了这个没必要
        http.httpBasic().disable()
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                // 关掉formlogin验证.有oauth2验证了这个没必要
                .and().formLogin().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/images/**","/js/**","/style/**")
                .antMatchers(Urls.NoAuthUrls.getNoAuthUrlsForConfig());

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }



}
