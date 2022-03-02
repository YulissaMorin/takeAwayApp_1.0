package com.yulissa.takeawayapp.config.user;

import com.yulissa.takeawayapp.commonclass.SSOTokenExtractor;
import com.yulissa.takeawayapp.handler.MyAccessDeniedHandler;
import com.yulissa.takeawayapp.handler.OauthFailHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * @author yulissa
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Bean(name = "user_keyProperties")
    public KeyProperties keyProperties() {
        return new KeyProperties();
    }
    @Qualifier("user_keyProperties")
    @Autowired
    KeyProperties keyProperties;

    @Autowired
    KeyStoreKeyFactory keyStoreKeyFactory;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
                .and().csrf().disable()
                .exceptionHandling().accessDeniedHandler(new MyAccessDeniedHandler());
                }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("user").tokenStore(new JwtTokenStore(myTokenConverter()))
        .tokenExtractor(new SSOTokenExtractor(keyStoreKeyFactory.getKeyPair("app")))
                .authenticationEntryPoint(new OauthFailHandler());
    }

    JwtAccessTokenConverter myTokenConverter() {
        JwtAccessTokenConverter myTokenConverter = new JwtAccessTokenConverter();
        myTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair("app"));
        return myTokenConverter;
    }

    @Bean
    KeyStoreKeyFactory keyStoreKeyFactory() {
        return new KeyStoreKeyFactory(keyProperties.getKeyStore().getLocation(), keyProperties.getKeyStore().getPassword().toCharArray());
    }

}
