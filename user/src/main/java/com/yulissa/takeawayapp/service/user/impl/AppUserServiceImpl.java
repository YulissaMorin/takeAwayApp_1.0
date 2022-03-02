package com.yulissa.takeawayapp.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yulissa.takeawayapp.commonmethod.CommonMethods;
import com.yulissa.takeawayapp.dao.user.AppUserMapper;
import com.yulissa.takeawayapp.entity.user.AppUser;
import com.yulissa.takeawayapp.service.user.AppUserService;
import com.yulissa.takeawayapp.util.RedisTemplateBitmapUtil;
import com.yulissa.takeawayapp.util.WhichDateUtil;
import com.yulissa.takeawayapp.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yulissa
 * @since 2022-01-26
 */
@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser> implements AppUserService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public ApiResult signIn() {
        String username = CommonMethods.getUsernameFromContext();
        String weekSignInTableKey = "signIn_"+username+"_"+ WhichDateUtil.getDateInWeek(1)+"-"+WhichDateUtil.getDateInWeek(7);
        RedisTemplateBitmapUtil.setBit(redisTemplate, weekSignInTableKey, WhichDateUtil.whatDayItIsToday(), true);
        return new ApiResult().successApiResult();
    }

    @Override
    public byte[] getSignedDaysThisWeek() {
        String username = CommonMethods.getUsernameFromContext();
        String weekSignInTableKey = "signIn_"+username+"_"+ WhichDateUtil.getDateInWeek(1)+"-"+WhichDateUtil.getDateInWeek(7);
        byte value = ((byte[]) RedisTemplateBitmapUtil.get(redisTemplate, weekSignInTableKey))[0];
        byte[] days = new byte[7];
        for (int i = 0; i < 7; i++) {
            days[6-i] = (byte) (value & 1);
            value = (byte) (value>>1);
        }
        return days;
    }

    @Override
    public  IPage<AppUser> findListByPage(Integer page, Integer pageCount){
        IPage<AppUser> wherePage = new Page<>(page, pageCount);
        AppUser where = new AppUser();

        return   baseMapper.selectPage(wherePage, Wrappers.query(where));
    }

    @Override
    public int add(AppUser appUser){
        appUser.setMoney(null);
        return baseMapper.insert(appUser);
    }

    @Override
    public int delete(String email){
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("email", email);
        return baseMapper.delete(wrapper);
    }

    @Override
    public int updateData(AppUser appUser){
        // 防止非法篡改账户余额,只能通过addMoney方法(需鉴权)增加余额.
        appUser.setMoney(null);
        return baseMapper.updateById(appUser);
    }

    @Override
    public AppUser findById(Long id){
        return  baseMapper.selectById(id);
    }

    @Override
    public AppUser findByUsername(HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("email", username);
        return baseMapper.selectOne(wrapper);
    }

}
