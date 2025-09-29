package com.simplespring.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 类操作工具类
 * 提供类操作的工具方法，兼容 JDK 1.7
 * 
 * @author SimpleSpring Framework
 */
public class ClassUtils {

    /** 基本类型到包装类型的映射 */
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>();
    
    /** 包装类型到基本类型的映射 */
    private static final Map<Class<?>, Class<?>> wrapperPrimitiveTypeMap = new HashMap<Class<?>, Class<?>>();
    
    static {
        // 初始化基本类型映射
        primitiveWrapperTypeMap.put(boolean.class, Boolean.class);
        primitiveWrapperTypeMap.put(byte.class, Byte.class);
        primitiveWrapperTypeMap.put(char.class, Character.class);
        primitiveWrapperTypeMap.put(double.class, Double.class);
        primitiveWrapperTypeMap.put(float.class, Float.class);
        primitiveWrapperTypeMap.put(int.class, Integer.class);
        primitiveWrapperTypeMap.put(long.class, Long.class);
        primitiveWrapperTypeMap.put(short.class, Short.class);
        
        // 初始化包装类型映射
        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            wrapperPrimitiveTypeMap.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * 根据类名加载类
     * 
     * @param name 类名
     * @param classLoader 类加载器
     * @return 加载的类
     * @throws ClassNotFoundException 如果类未找到
     */
    public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        if (classLoader != null) {
            return Class.forName(name, true, classLoader);
        } else {
            return Class.forName(name);
        }
    }

    /**
     * 检查左侧类型是否可以赋值给右侧类型
     * 
     * @param lhsType 左侧类型
     * @param rhsType 右侧类型
     * @return 如果可以赋值返回 true，否则返回 false
     */
    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        if (lhsType == null || rhsType == null) {
            return false;
        }
        
        if (lhsType.equals(rhsType)) {
            return true;
        }
        
        if (rhsType.isAssignableFrom(lhsType)) {
            return true;
        }
        
        // 处理基本类型和包装类型之间的赋值
        if (lhsType.isPrimitive()) {
            Class<?> wrapperType = primitiveWrapperTypeMap.get(lhsType);
            if (wrapperType != null && rhsType.equals(wrapperType)) {
                return true;
            }
        }
        
        if (rhsType.isPrimitive()) {
            Class<?> wrapperType = primitiveWrapperTypeMap.get(rhsType);
            if (wrapperType != null && lhsType.equals(wrapperType)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 获取类的简短名称（不包含包名）
     * 
     * @param clazz 类
     * @return 简短名称
     */
    public static String getShortName(Class<?> clazz) {
        if (clazz == null) {
            return "";
        }
        
        return getShortName(clazz.getName());
    }

    /**
     * 获取类名的简短名称（不包含包名）
     * 
     * @param className 完整类名
     * @return 简短名称
     */
    public static String getShortName(String className) {
        if (StringUtils.isEmpty(className)) {
            return "";
        }
        
        int lastDotIndex = className.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return className.substring(lastDotIndex + 1);
        }
        
        return className;
    }

    /**
     * 获取默认的类加载器
     * 
     * @return 类加载器
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // 无法访问线程上下文类加载器，继续使用其他方式
        }
        
        if (cl == null) {
            // 使用当前类的类加载器
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                // 使用系统类加载器
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // 无法访问系统类加载器
                }
            }
        }
        
        return cl;
    }

    /**
     * 检查类是否为基本类型
     * 
     * @param clazz 类
     * @return 如果是基本类型返回 true，否则返回 false
     */
    public static boolean isPrimitiveType(Class<?> clazz) {
        return clazz != null && clazz.isPrimitive();
    }

    /**
     * 检查类是否为基本类型的包装类型
     * 
     * @param clazz 类
     * @return 如果是包装类型返回 true，否则返回 false
     */
    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        return wrapperPrimitiveTypeMap.containsKey(clazz);
    }

    /**
     * 获取基本类型对应的包装类型
     * 
     * @param primitiveType 基本类型
     * @return 对应的包装类型，如果不是基本类型则返回原类型
     */
    public static Class<?> resolvePrimitiveWrapper(Class<?> primitiveType) {
        Class<?> wrapperType = primitiveWrapperTypeMap.get(primitiveType);
        return wrapperType != null ? wrapperType : primitiveType;
    }
}
