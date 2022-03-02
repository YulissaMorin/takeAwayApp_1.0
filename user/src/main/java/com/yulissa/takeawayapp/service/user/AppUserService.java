package com.yulissa.takeawayapp.service.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yulissa.takeawayapp.entity.user.AppUser;
import com.yulissa.takeawayapp.vo.ApiResult;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yulissa
 * @since 2022-01-26
 */
public interface AppUserService extends IService<AppUser> {

    public AppUser findByUsername(HttpServletRequest request);

    public byte[] getSignedDaysThisWeek();

    ApiResult signIn();
    /**
     * 查询分页数据
     *
     * @param page      页码
     * @param pageCount 每页条数
     * @return IPage<AppUser>
     */
    IPage<AppUser> findListByPage(Integer page, Integer pageCount);

    /**
     * 添加,返回值为
     *
     * @param appUser 
     * @return int
     */
    int add(AppUser appUser);

    /**
     * 删除
     *
     *
     * @param username@return int
     */
    int delete(String username);

    /**
     * 修改
     *
     * @param appUser 
     * @return int
     */
    int updateData(AppUser appUser);

    /**
     * id查询数据
     *
     * @param id id
     * @return AppUser
     */
    AppUser findById(Long id);
}
