package com.bujian.satoken.service;

/**
 * 登录service
 *
 * @author bujian
 * @date 2021/7/23
 */
public interface LoginUserService {

    /**
     * 用户 登录
     *
     * @param username 用户唯一标识
     * @param password 用户密码
     * @return java.lang.String token
     */
    String login(String username, String password);

    /**
     * 退出登录
     *
     * @return java.lang.String
     * @author bujian
     */
    String logout();
}
