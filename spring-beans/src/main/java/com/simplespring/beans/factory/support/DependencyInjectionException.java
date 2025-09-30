package com.simplespring.beans.factory.support;

/**
 * 依赖注入异常
 * 当依赖注入过程中发生错误时抛出此异常
 * 
 * @author SimpleSpring Framework
 */
public class DependencyInjectionException extends RuntimeException {
    
    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 构造函数
     * @param message 异常消息
     */
    public DependencyInjectionException(String message) {
        super(message);
    }
    
    /**
     * 构造函数
     * @param message 异常消息
     * @param cause 异常原因
     */
    public DependencyInjectionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 构造函数
     * @param cause 异常原因
     */
    public DependencyInjectionException(Throwable cause) {
        super(cause);
    }
}
