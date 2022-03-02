package com.yulissa.takeawayapp.listener.user;

import com.alibaba.fastjson.JSON;
import com.yulissa.takeawayapp.entity.user.AppUser;
import com.yulissa.takeawayapp.service.user.AppUserService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yulissa
 */
@Component
public class BackupRegisterListener {

    @Autowired
    AppUserService appUserService;

    @RabbitListener(queues = "register_backup_queue")
    void doBackupRegister(Message message) {
        Map<String, Object> userInfo = JSON.parseObject(message.getBody(), Map.class);
        AppUser appUser = new AppUser();
        appUser.setEmail((String) userInfo.get("email"));
        appUser.setUid((Long) userInfo.get("uid"));
        if (appUserService.findById((long)userInfo.get("uid")) != null) {return;}
        appUserService.add(appUser);
    }
}
