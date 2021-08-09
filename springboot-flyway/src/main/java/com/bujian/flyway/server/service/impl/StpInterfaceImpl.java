package com.bujian.flyway.server.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义权限验证接口扩展
 *
 * @author satoken
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        System.err.println("getPermissionList");
        return Arrays.asList("role", "edit");
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        System.err.println("getRoleList");
        // 本list仅做模拟，实际项目中要根据具体业务逻辑来查询角色
        return Arrays.asList("admin", "super-admin");
    }

}
