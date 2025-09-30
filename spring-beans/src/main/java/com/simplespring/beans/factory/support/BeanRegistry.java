package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.core.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean 注册表
 * 管理 Bean 定义和单例实例的缓存，提供 Bean 的注册、查找和管理功能
 * 
 * @author SimpleSpring Framework
 */
public class BeanRegistry {

    /**
     * 单例 Bean 实例缓存
     * Key: Bean 名称, Value: Bean 实例
     */
    private final Map<String, Object> singletonBeans = new ConcurrentHashMap<String, Object>();

    /**
     * Bean 定义缓存
     * Key: Bean 名称, Value: Bean 定义
     */
    private final Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<String, BeanDefinition>();

    /**
     * Bean 名称到类型的映射
     * Key: Bean 名称, Value: Bean 类型
     */
    private final Map<String, Class<?>> beanNameToType = new ConcurrentHashMap<String, Class<?>>();

    /**
     * 类型到 Bean 名称集合的映射
     * Key: Bean 类型, Value: Bean 名称集合
     */
    private final Map<Class<?>, Set<String>> typeToBeanNames = new ConcurrentHashMap<Class<?>, Set<String>>();

    /**
     * 正在创建中的 Bean 名称集合，用于检测循环依赖
     */
    private final Set<String> beansCurrentlyInCreation = new HashSet<String>();

    /**
     * 注册 Bean 定义
     * 
     * @param beanName       Bean 名称
     * @param beanDefinition Bean 定义
     * @throws IllegalArgumentException 如果 Bean 名称为空或 Bean 定义为空
     */
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        if (!StringUtils.hasText(beanName)) {
            throw new IllegalArgumentException("Bean 名称不能为空");
        }
        if (beanDefinition == null) {
            throw new IllegalArgumentException("Bean 定义不能为空");
        }

        // 检查是否已经存在同名的 Bean 定义
        if (containsBeanDefinition(beanName)) {
            throw new IllegalStateException("已存在名为 '" + beanName + "' 的 Bean 定义");
        }

        // 注册 Bean 定义
        beanDefinitions.put(beanName, beanDefinition);

        // 更新类型映射
        Class<?> beanClass = beanDefinition.getBeanClass();
        if (beanClass != null) {
            beanNameToType.put(beanName, beanClass);

            // 更新类型到名称的映射
            Set<String> beanNames = typeToBeanNames.get(beanClass);
            if (beanNames == null) {
                beanNames = new HashSet<String>();
                typeToBeanNames.put(beanClass, beanNames);
            }
            beanNames.add(beanName);

            // 同时为所有父类和接口建立映射
            registerTypeMapping(beanClass, beanName);
        }
    }

    /**
     * 为类型及其父类、接口建立映射关系
     * 
     * @param beanClass Bean 类型
     * @param beanName  Bean 名称
     */
    private void registerTypeMapping(Class<?> beanClass, String beanName) {
        // 为当前类建立映射
        addTypeToBeanNameMapping(beanClass, beanName);

        // 为父类建立映射
        Class<?> superClass = beanClass.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            registerTypeMapping(superClass, beanName);
        }

        // 为接口建立映射
        Class<?>[] interfaces = beanClass.getInterfaces();
        for (Class<?> interfaceClass : interfaces) {
            registerTypeMapping(interfaceClass, beanName);
        }
    }

    /**
     * 添加类型到 Bean 名称的映射
     * 
     * @param type     类型
     * @param beanName Bean 名称
     */
    private void addTypeToBeanNameMapping(Class<?> type, String beanName) {
        Set<String> beanNames = typeToBeanNames.get(type);
        if (beanNames == null) {
            beanNames = new HashSet<String>();
            typeToBeanNames.put(type, beanNames);
        }
        beanNames.add(beanName);
    }

    /**
     * 获取 Bean 定义
     * 
     * @param beanName Bean 名称
     * @return Bean 定义，如果不存在返回 null
     */
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanDefinitions.get(beanName);
    }

    /**
     * 检查是否包含指定名称的 Bean 定义
     * 
     * @param beanName Bean 名称
     * @return 如果包含返回 true，否则返回 false
     */
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitions.containsKey(beanName);
    }

    /**
     * 获取所有 Bean 定义的名称
     * 
     * @return Bean 名称数组
     */
    public String[] getBeanDefinitionNames() {
        return beanDefinitions.keySet().toArray(new String[0]);
    }

    /**
     * 获取 Bean 定义数量
     * 
     * @return Bean 定义数量
     */
    public int getBeanDefinitionCount() {
        return beanDefinitions.size();
    }

    /**
     * 注册单例 Bean 实例
     * 
     * @param beanName        Bean 名称
     * @param singletonObject 单例对象
     */
    public void registerSingleton(String beanName, Object singletonObject) {
        if (!StringUtils.hasText(beanName)) {
            throw new IllegalArgumentException("Bean 名称不能为空");
        }
        if (singletonObject == null) {
            throw new IllegalArgumentException("单例对象不能为空");
        }

        synchronized (singletonBeans) {
            if (singletonBeans.containsKey(beanName)) {
                throw new IllegalStateException("已存在名为 '" + beanName + "' 的单例 Bean");
            }
            singletonBeans.put(beanName, singletonObject);
        }
    }

    /**
     * 获取单例 Bean 实例
     * 
     * @param beanName Bean 名称
     * @return 单例对象，如果不存在返回 null
     */
    public Object getSingleton(String beanName) {
        return singletonBeans.get(beanName);
    }

    /**
     * 检查是否包含指定名称的单例 Bean
     * 
     * @param beanName Bean 名称
     * @return 如果包含返回 true，否则返回 false
     */
    public boolean containsSingleton(String beanName) {
        return singletonBeans.containsKey(beanName);
    }

    /**
     * 获取所有单例 Bean 的名称
     * 
     * @return 单例 Bean 名称数组
     */
    public String[] getSingletonNames() {
        return singletonBeans.keySet().toArray(new String[0]);
    }

    /**
     * 根据类型查找 Bean 名称
     * 
     * @param type Bean 类型
     * @return 匹配的 Bean 名称列表
     */
    public List<String> getBeanNamesForType(Class<?> type) {
        List<String> result = new ArrayList<String>();
        if (type == null) {
            return result;
        }

        Set<String> beanNames = typeToBeanNames.get(type);
        if (beanNames != null) {
            result.addAll(beanNames);
        }

        return result;
    }

    /**
     * 根据类型获取 Bean 的类型
     * 
     * @param beanName Bean 名称
     * @return Bean 类型，如果不存在返回 null
     */
    public Class<?> getType(String beanName) {
        return beanNameToType.get(beanName);
    }

    /**
     * 检查指定名称的 Bean 是否正在创建中
     * 
     * @param beanName Bean 名称
     * @return 如果正在创建中返回 true，否则返回 false
     */
    public boolean isCurrentlyInCreation(String beanName) {
        return beansCurrentlyInCreation.contains(beanName);
    }

    /**
     * 标记 Bean 开始创建
     * 
     * @param beanName Bean 名称
     */
    public void beforeSingletonCreation(String beanName) {
        if (!beansCurrentlyInCreation.add(beanName)) {
            throw new IllegalStateException("检测到循环依赖: Bean '" + beanName + "' 正在创建中");
        }
    }

    /**
     * 标记 Bean 创建完成
     * 
     * @param beanName Bean 名称
     */
    public void afterSingletonCreation(String beanName) {
        beansCurrentlyInCreation.remove(beanName);
    }

    /**
     * 移除 Bean 定义
     * 
     * @param beanName Bean 名称
     */
    public void removeBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = beanDefinitions.remove(beanName);
        if (beanDefinition != null) {
            Class<?> beanClass = beanDefinition.getBeanClass();
            if (beanClass != null) {
                beanNameToType.remove(beanName);
                removeTypeToBeanNameMapping(beanClass, beanName);
            }
        }
    }

    /**
     * 移除类型到 Bean 名称的映射
     * 
     * @param beanClass Bean 类型
     * @param beanName  Bean 名称
     */
    private void removeTypeToBeanNameMapping(Class<?> beanClass, String beanName) {
        Set<String> beanNames = typeToBeanNames.get(beanClass);
        if (beanNames != null) {
            beanNames.remove(beanName);
            if (beanNames.isEmpty()) {
                typeToBeanNames.remove(beanClass);
            }
        }

        // 同时移除父类和接口的映射
        Class<?> superClass = beanClass.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            removeTypeToBeanNameMapping(superClass, beanName);
        }

        Class<?>[] interfaces = beanClass.getInterfaces();
        for (Class<?> interfaceClass : interfaces) {
            removeTypeToBeanNameMapping(interfaceClass, beanName);
        }
    }

    /**
     * 移除单例 Bean 实例
     * 
     * @param beanName Bean 名称
     */
    public void removeSingleton(String beanName) {
        synchronized (singletonBeans) {
            singletonBeans.remove(beanName);
        }
    }

    /**
     * 清空所有缓存
     */
    public void clear() {
        singletonBeans.clear();
        beanDefinitions.clear();
        beanNameToType.clear();
        typeToBeanNames.clear();
        beansCurrentlyInCreation.clear();
    }
}
