package com.bujian.quartz.server.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.bujian.quartz.common.utils.Md5Util;
import com.bujian.quartz.server.service.LoginUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 登录实现
 *
 * @author bujian
 * @date 2021/7/30
 */
@Service
public class LoginUserServiceImpl implements LoginUserService {

    @Override
    public String login(String username, String password) {
        Assert.isTrue(StringUtils.isNotBlank(username), "登录账户不能为空");
        // TODO 数据库 查询 用户 逻辑
        Assert.isTrue(Objects.equals("admin", username), "用户不存在");
        // 验证 用户
        Assert.isTrue(Md5Util.verify(password, Md5Util.generate("123456")), "密码错误");
        // 用户登录
        StpUtil.login(username);
        // 返回token
        return JSONObject.toJSONString(StpUtil.getTokenInfo());
    }

    @Override
    public String logout() {
        StpUtil.logout();
        return "注销成功";
    }
}
