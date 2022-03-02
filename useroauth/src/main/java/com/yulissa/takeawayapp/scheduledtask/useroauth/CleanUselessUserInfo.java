package com.yulissa.takeawayapp.scheduledtask.useroauth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yulissa.takeawayapp.entity.useroauth.UserSecurityInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author yulissa
 */
public class CleanUselessUserInfo {
    @Autowired
    BaseMapper<UserSecurityInfo> baseMapper;

    @Scheduled(cron = "0 0 4 * * ?")
    public void cleanUselessUserInfo() {
        QueryWrapper<UserSecurityInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enabled", false);
        baseMapper.delete(queryWrapper);
    }
}
