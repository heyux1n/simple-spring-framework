package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.BeanFactory;
import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.core.annotation.Autowired;
import com.simplespring.core.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 字段注入器
 * 处理带有 @Autowired 注解的字段注入，实现按类型查找依赖 Bean 的逻辑
 * 
 * @author SimpleSpring Framework
 */
public class FieldInjector {

    /**
     * Bean 注册表，用于查找依赖的 Bean
     */
    private final BeanRegistry beanRegistry;

    /**
     * Bean 工厂，用于创建依赖的 Bean
     */
    private BeanFactory beanFactory;

    /**
     * 构造函数
     * 
     * @param beanRegistry Bean 注册表
     */
    public FieldInjector(BeanRegistry beanRegistry) {
        if (beanRegistry == null) {
            throw new IllegalArgumentException("BeanRegistry 不能为空");
        }
        this.beanRegistry = beanRegistry;
    }

    /**
     * 设置 Bean 工厂
     * 
     * @param beanFactory Bean 工厂
     */
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 为指定的 Bean 实例进行字段注入
     * 
     * @param beanInstance   Bean 实例
     * @param beanDefinition Bean 定义
     * @throws DependencyInjectionException 如果依赖注入失败
     */
    public void injectFields(Object beanInstance, BeanDefinition beanDefinition) {
        if (beanInstance == null) {
            throw new IllegalArgumentException("Bean 实例不能为空");
        }
        if (beanDefinition == null) {
            throw new IllegalArgumentException("Bean 定义不能为空");
        }

        List<Field> autowiredFields = beanDefinition.getAutowiredFields();
        if (autowiredFields == null || autowiredFields.isEmpty()) {
            return; // 没有需要注入的字段
        }

        for (Field field : autowiredFields) {
            injectField(beanInstance, field);
        }
    }

    /**
     * 注入单个字段
     * 
     * @param beanInstance Bean 实例
     * @param field        需要注入的字段
     * @throws DependencyInjectionException 如果依赖注入失败
     */
    private void injectField(Object beanInstance, Field field) {
        try {
            // 获取字段上的 @Autowired 注解
            Autowired autowired = field.getAnnotation(Autowired.class);
            if (autowired == null) {
                return; // 字段没有 @Autowired 注解，跳过
            }

            // 获取字段类型
            Class<?> fieldType = field.getType();

            // 查找匹配的 Bean
            Object dependencyBean = findDependencyBean(fieldType, autowired.required());

            if (dependencyBean != null) {
                // 设置字段可访问
                ReflectionUtils.makeAccessible(field);

                // 注入依赖
                field.set(beanInstance, dependencyBean);
            } else if (autowired.required()) {
                throw new DependencyInjectionException(
                        "无法为字段 '" + field.getName() + "' 找到类型为 '" +
                                fieldType.getName() + "' 的依赖 Bean");
            }

        } catch (IllegalAccessException e) {
            throw new DependencyInjectionException(
                    "无法访问字段 '" + field.getName() + "': " + e.getMessage(), e);
        } catch (Exception e) {
            throw new DependencyInjectionException(
                    "字段注入失败 '" + field.getName() + "': " + e.getMessage(), e);
        }
    }

    /**
     * 根据类型查找依赖的 Bean
     * 
     * @param requiredType 需要的类型
     * @param required     是否为必需的依赖
     * @return 匹配的 Bean 实例，如果找不到且不是必需的则返回 null
     * @throws DependencyInjectionException 如果找到多个匹配的 Bean
     */
    private Object findDependencyBean(Class<?> requiredType, boolean required) {
        // 根据类型查找 Bean 名称
        List<String> beanNames = beanRegistry.getBeanNamesForType(requiredType);

        if (beanNames.isEmpty()) {
            if (required) {
                throw new DependencyInjectionException(
                        "找不到类型为 '" + requiredType.getName() + "' 的 Bean");
            }
            return null;
        }

        if (beanNames.size() > 1) {
            throw new DependencyInjectionException(
                    "找到多个类型为 '" + requiredType.getName() + "' 的 Bean: " + beanNames +
                            "，无法确定使用哪一个。请使用 @Qualifier 注解指定具体的 Bean 名称");
        }

        // 获取唯一匹配的 Bean 名称
        String beanName = beanNames.get(0);

        // 首先尝试从单例缓存中获取
        Object bean = beanRegistry.getSingleton(beanName);
        if (bean != null) {
            return bean;
        }

        // 如果单例缓存中没有，通过 BeanFactory 创建 Bean
        if (beanFactory != null) {
            try {
                return beanFactory.getBean(beanName);
            } catch (Exception e) {
                if (required) {
                    throw new DependencyInjectionException(
                            "无法创建类型为 '" + requiredType.getName() + "' 的依赖 Bean: " + e.getMessage(), e);
                }
                return null;
            }
        }

        // 如果没有 BeanFactory，返回 null
        return null;
    }

    /**
     * 扫描类中带有 @Autowired 注解的字段
     * 
     * @param beanClass Bean 类
     * @return 带有 @Autowired 注解的字段列表
     */
    public static List<Field> scanAutowiredFields(Class<?> beanClass) {
        if (beanClass == null) {
            throw new IllegalArgumentException("Bean 类不能为空");
        }

        java.util.List<Field> autowiredFields = new java.util.ArrayList<Field>();

        // 扫描当前类及其父类的所有字段
        Class<?> currentClass = beanClass;
        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    autowiredFields.add(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        return autowiredFields;
    }

    /**
     * 检查字段是否需要依赖注入
     * 
     * @param field 字段
     * @return 如果字段带有 @Autowired 注解返回 true，否则返回 false
     */
    public static boolean isAutowiredField(Field field) {
        return field != null && field.isAnnotationPresent(Autowired.class);
    }
}
