package com.bujian.security.service;

import com.bujian.security.bean.LoginUser;

/**
 * token 服务
 * @author bujian
 * @date 2021/7/23
 */
public interface TokenService {

    /**
     * 创建token
     * @param loginUser
     * @return java.lang.String
     */
    String creatToken(LoginUser loginUser);

    /**
     * 移除token
     * @return boolean
     */
    boolean removeToken();

    /**
     * 刷新token
     * @return java.lang.String
     */
    String refreshToken();

    /**
     * 获取token
     * @return java.lang.String
     */
    String getToken();

    /**
     * 解析token
     * @return com.bujian.security.bean.LoginUser
     */
    LoginUser parseToken();

    /**
     * 验证token
     * @return boolean
     */
    boolean verifyToken();
}
