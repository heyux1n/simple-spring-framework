package com.simplespring.core.io;

/**
 * 资源加载器接口
 * 定义资源加载规范，兼容 JDK 1.7
 * 
 * @author SimpleSpring Framework
 */
public interface ResourceLoader {

    /** 类路径前缀 */
    String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * 获取指定位置的资源
     * 
     * @param location 资源位置
     * @return 资源对象
     */
    Resource getResource(String location);

    /**
     * 获取类加载器
     * 
     * @return 类加载器
     */
    ClassLoader getClassLoader();
}
