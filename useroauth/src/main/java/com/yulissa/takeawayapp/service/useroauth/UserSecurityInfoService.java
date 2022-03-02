package com.yulissa.takeawayapp.service.useroauth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yulissa.takeawayapp.entity.useroauth.UserSecurityInfo;
import com.yulissa.takeawayapp.vo.ApiResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yulissa
 * @since 2022-01-29
 */
public interface UserSecurityInfoService extends IService<UserSecurityInfo> {

    void logout(HttpServletRequest request, HttpServletResponse response);

    ApiResult sendRegisterInfoToMq(String encodedUsername, String emailCode) throws Exception;


        ApiResult registerConfirm(String encodedUsername, String emailCode) throws Exception;
    /**
     * 查询分页数据
     *
     * @param page      页码
     * @param pageCount 每页条数
     * @return IPage<UserSecurityInfo>
     */
    IPage<UserSecurityInfo> findListByPage(Integer page, Integer pageCount);

    ApiResult login(String username, String password, HttpServletRequest request, HttpServletResponse response);

    /**
     * 添加
     *
     * @param userSecurityInfo
     * @return int
     */
    ApiResult register(UserSecurityInfo userSecurityInfo, HttpServletRequest request) throws Exception;

    /**
     * 删除
     * @return
     */
    String delete(HttpServletRequest request, HttpServletResponse response);

    ApiResult deleteByUsername(String username);

    /**
     * 修改
     *
     * @param userSecurityInfo
     * @return int
     */
    ApiResult updateData(UserSecurityInfo userSecurityInfo);

    /**
     * id查询数据
     *
     * @param id id
     * @return UserSecurityInfo
     */
    UserSecurityInfo findById(Long id);

    void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception;

    ApiResult resetPasswordStep1(String email);

    ApiResult resetPasswordStep2(long uid, String emailCode, String password);
}
