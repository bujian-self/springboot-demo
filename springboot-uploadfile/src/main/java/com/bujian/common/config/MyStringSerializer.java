package com.bujian.common.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 自定义 key 值序列化,添加了key值前缀
 *
 * @author bujian
 * @date 2021/7/10 11:29
 */
public class MyStringSerializer implements RedisSerializer<String> {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private String keyPrefix = "";

    public MyStringSerializer() {
    }

    public MyStringSerializer(String keyPrefix) {
        this.keyPrefix = StringUtils.isNotBlank(keyPrefix) ? keyPrefix : "";
    }

    @Override
    public String deserialize(@Nullable byte[] bytes) {
        return (bytes == null ? null : new String(bytes, CHARSET));
    }

    @Override
    public byte[] serialize(@Nullable String string) {
        return (string == null ? null : (keyPrefix + string).getBytes(CHARSET));
    }
}
