package com.yulissa.takeawayapp.dao.user;

import com.yulissa.takeawayapp.entity.user.AppUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yulissa
 * @since 2022-01-26
 */
public interface AppUserMapper extends BaseMapper<AppUser> {

    AppUser loadUserDetail(String email);

}
