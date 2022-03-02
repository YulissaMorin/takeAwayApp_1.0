package com.yulissa.takeawayapp.config.useroauth;

import com.yulissa.takeawayapp.util.RealIPExtractor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yulissa
 */
@Configuration
public class ConflictConfigBeans {
    @Bean
    KeyPair keyPair(@Qualifier("user_oauth_keyProperties") KeyProperties keyProperties) {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(keyProperties.getKeyStore().getLocation(),keyProperties.getKeyStore().getPassword().toCharArray());
        return keyStoreKeyFactory.getKeyPair("app");
    }

    @Bean(name = "user_oauth_keyProperties")
    public KeyProperties keyProperties(){
        return new KeyProperties();
    }


    // 踩坑之一,必须得配成bean不然TokenKeyEndpoint无法自动注入这个类导致/token_key返回404
    @Bean
    JwtAccessTokenConverter authTokenConverter(KeyPair keyPair) {
        JwtAccessTokenConverter myTokenConverter = new JwtAccessTokenConverter() {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
                String IP = RealIPExtractor.getRealIp(request);
                DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
                Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
                info.put("IP", IP);
                result.setAdditionalInformation(info);
                result.setValue(encode(result, authentication));
                return result;
            }
        };
        myTokenConverter.setKeyPair(keyPair);
        return myTokenConverter;
    }
}
