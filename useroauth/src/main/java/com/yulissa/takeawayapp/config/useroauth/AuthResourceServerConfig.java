package com.yulissa.takeawayapp.config.useroauth;

import com.yulissa.takeawayapp.commonclass.SSOTokenExtractor;
import com.yulissa.takeawayapp.handler.OauthFailHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.security.KeyPair;

/**
 * @author yulissa
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AuthResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Qualifier("authTokenConverter")
    @Autowired
    JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    KeyPair keyPair;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("user").tokenStore(new JwtTokenStore(jwtAccessTokenConverter))
                .tokenExtractor(new SSOTokenExtractor(keyPair))
                .authenticationEntryPoint(new OauthFailHandler());
    }


}
