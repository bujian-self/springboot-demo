package com.bujian.quartz.quartz.utils;

import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GetVarNameUtil {
    private GetVarNameUtil(){}

    /**
     * 获取类里面所有的方法名的名称
     */
    public static List<String> getClassMethodName(Class clazz){
        return Arrays.stream(clazz.getDeclaredMethods()).map(Method::getName).collect(Collectors.toList());
    }

    /**
     * 根据方法 获取 方法入参变量的名称
     */
    public static String[] getMethodParamName(Method method){
        return new DefaultParameterNameDiscoverer().getParameterNames(method);
    }

    /**
     * 根据方法名称获取 方法入参变量的名称
     */
    public static String[] getMethodParamName(Class clazz, String methodName){
        for (Method method : clazz.getDeclaredMethods()) {
            if (Objects.equals(methodName,method.getName())) {
                return getMethodParamName(method);
            }
        }
        throw new NullPointerException(methodName + " is not in " + clazz.getName());
    }
}
