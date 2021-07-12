package com.bujian.cache.config;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 自定义 object 类存储 方法还有问题需要优化
 * @author bujian
 * @date 2021/7/10 12:11
 */
public class MyObjectSerializer<T> implements RedisSerializer<T> {

	private static final Charset CHARSET = StandardCharsets.UTF_8;
	private String keyPrefix = "";

	public MyObjectSerializer(){ }
	public MyObjectSerializer(String keyPrefix){
		this.keyPrefix = StringUtils.isNotBlank(keyPrefix) ? keyPrefix : "";
	}

	@SneakyThrows
	@Override
	public T deserialize(@Nullable byte[] bytes) throws SerializationException {
		if (bytes == null || bytes.length < 1) {
			return null;
		}
		return (T) this.bytesToObject(bytes);
	}
	@SneakyThrows
	@Override
	public byte[] serialize(@Nullable T t) throws SerializationException {
		return (t == null ? null : this.objectToBytes(t));
	}

	/**
	 * 字节数组转对象
	 */
	private Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
		try(
				ByteArrayInputStream in = new ByteArrayInputStream(bytes);
				ObjectInputStream sIn = new ObjectInputStream(in);
		){
			return sIn.readObject();
		}
	}
	/**
	 * 对象转字节数组
	 */
	private byte[] objectToBytes(Object obj) throws IOException {
		try(
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ObjectOutputStream sOut = new ObjectOutputStream(out);
		){
			sOut.writeObject(obj);
			sOut.flush();
			byte[] bytes = out.toByteArray();
			// 返回时 batys 数字有7个长度的乱码
			return Arrays.copyOfRange(bytes,7,bytes.length);
		}
	}
}
