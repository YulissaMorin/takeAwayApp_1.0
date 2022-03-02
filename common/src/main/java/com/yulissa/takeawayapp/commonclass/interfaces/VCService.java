package com.yulissa.takeawayapp.commonclass.interfaces;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yulissa
 */
public interface VCService {

    /**
     * @throws Exception 可能会抛出io和数据库链接异常
     * 如果在外部存储 用户名-验证码 键值对要记得用base64处理用户名,防止明文存储所带来的风险,
     * 若认为用户名为敏感信息可加强为对称加密存储
     */
    String saveAndSendVC(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     *
     * @param username 如果是外部存储,这里应该是base64处理后的用户名,即encodedUsername
     * @param VC
     * @return
     */
    default boolean verifyVC(HttpServletRequest request, String username, String VC) {
        String storedVC = getStoredVC(request, username);
        if (StringUtils.isEmpty(storedVC)) {
            return false;
        } else {
            return storedVC.equals(VC);
        }
    }

    String getStoredVC(HttpServletRequest request, String username);

}
