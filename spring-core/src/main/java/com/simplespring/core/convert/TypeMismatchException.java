package com.simplespring.core.convert;

/**
 * 类型转换异常
 * 当类型转换失败时抛出此异常
 * 
 * @author SimpleSpring Framework
 */
public class TypeMismatchException extends RuntimeException {

    private final Object value;
    private final Class<?> requiredType;

    /**
     * 构造函数
     * 
     * @param value 转换失败的值
     * @param requiredType 目标类型
     */
    public TypeMismatchException(Object value, Class<?> requiredType) {
        super("Failed to convert value of type '" + 
              (value != null ? value.getClass().getName() : "null") + 
              "' to required type '" + requiredType.getName() + "'");
        this.value = value;
        this.requiredType = requiredType;
    }

    /**
     * 构造函数
     * 
     * @param value 转换失败的值
     * @param requiredType 目标类型
     * @param cause 原因异常
     */
    public TypeMismatchException(Object value, Class<?> requiredType, Throwable cause) {
        super("Failed to convert value of type '" + 
              (value != null ? value.getClass().getName() : "null") + 
              "' to required type '" + requiredType.getName() + "'", cause);
        this.value = value;
        this.requiredType = requiredType;
    }

    /**
     * 获取转换失败的值
     * 
     * @return 转换失败的值
     */
    public Object getValue() {
        return value;
    }

    /**
     * 获取目标类型
     * 
     * @return 目标类型
     */
    public Class<?> getRequiredType() {
        return requiredType;
    }
}
