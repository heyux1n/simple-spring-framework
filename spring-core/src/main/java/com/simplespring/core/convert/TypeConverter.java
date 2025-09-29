package com.simplespring.core.convert;

/**
 * 类型转换器接口
 * 定义类型转换的基本规范，兼容 JDK 1.7
 * 
 * @author SimpleSpring Framework
 */
public interface TypeConverter {

    /**
     * 如果需要，将给定的值转换为指定的类型
     * 
     * @param value 要转换的值
     * @param requiredType 目标类型
     * @param <T> 目标类型的泛型
     * @return 转换后的值
     * @throws TypeMismatchException 如果转换失败
     */
    <T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException;

    /**
     * 检查是否可以从源类型转换到目标类型
     * 
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @return 如果可以转换返回 true，否则返回 false
     */
    boolean canConvert(Class<?> sourceType, Class<?> targetType);
}
