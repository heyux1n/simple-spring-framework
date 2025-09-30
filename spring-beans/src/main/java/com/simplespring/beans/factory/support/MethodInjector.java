package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.BeanFactory;
import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.core.annotation.Autowired;
import com.simplespring.core.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 方法注入器
 * 处理带有 @Autowired 注解的方法注入，支持 setter 方法和普通方法的依赖注入
 * 
 * @author SimpleSpring Framework
 */
public class MethodInjector {

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
    public MethodInjector(BeanRegistry beanRegistry) {
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
     * 为指定的 Bean 实例进行方法注入
     * 
     * @param beanInstance   Bean 实例
     * @param beanDefinition Bean 定义
     * @throws DependencyInjectionException 如果依赖注入失败
     */
    public void injectMethods(Object beanInstance, BeanDefinition beanDefinition) {
        if (beanInstance == null) {
            throw new IllegalArgumentException("Bean 实例不能为空");
        }
        if (beanDefinition == null) {
            throw new IllegalArgumentException("Bean 定义不能为空");
        }

        List<Method> autowiredMethods = beanDefinition.getAutowiredMethods();
        if (autowiredMethods == null || autowiredMethods.isEmpty()) {
            return; // 没有需要注入的方法
        }

        for (Method method : autowiredMethods) {
            injectMethod(beanInstance, method);
        }
    }

    /**
     * 注入单个方法
     * 
     * @param beanInstance Bean 实例
     * @param method       需要注入的方法
     * @throws DependencyInjectionException 如果依赖注入失败
     */
    private void injectMethod(Object beanInstance, Method method) {
        try {
            // 获取方法上的 @Autowired 注解
            Autowired autowired = method.getAnnotation(Autowired.class);
            if (autowired == null) {
                return; // 方法没有 @Autowired 注解，跳过
            }

            // 获取方法参数类型
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 0) {
                // 无参方法，直接调用
                ReflectionUtils.makeAccessible(method);
                method.invoke(beanInstance);
                return;
            }

            // 解析方法参数
            Object[] args = resolveMethodParameters(parameterTypes, method, autowired.required());

            if (args != null) {
                // 设置方法可访问
                ReflectionUtils.makeAccessible(method);

                // 调用方法进行注入
                method.invoke(beanInstance, args);
            } else if (autowired.required()) {
                throw new DependencyInjectionException(
                        "无法为方法 '" + method.getName() + "' 解析所有必需的参数");
            }

        } catch (Exception e) {
            if (e instanceof DependencyInjectionException) {
                throw (DependencyInjectionException) e;
            }
            throw new DependencyInjectionException(
                    "方法注入失败 '" + method.getName() + "': " + e.getMessage(), e);
        }
    }

    /**
     * 解析方法参数
     * 
     * @param parameterTypes 参数类型数组
     * @param method         方法对象（用于错误信息）
     * @param required       是否为必需的依赖
     * @return 参数值数组，如果无法解析且不是必需的则返回 null
     * @throws DependencyInjectionException 如果无法解析必需的参数
     */
    private Object[] resolveMethodParameters(Class<?>[] parameterTypes, Method method, boolean required) {
        Object[] args = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Object dependencyBean = findDependencyBean(parameterType, required);

            if (dependencyBean == null && required) {
                throw new DependencyInjectionException(
                        "无法为方法 '" + method.getName() + "' 的第 " + (i + 1) +
                                " 个参数找到类型为 '" + parameterType.getName() + "' 的依赖 Bean");
            }

            args[i] = dependencyBean;
        }

        // 检查是否所有必需的参数都已解析
        if (required) {
            for (Object arg : args) {
                if (arg == null) {
                    return null; // 有必需参数未解析
                }
            }
        }

        return args;
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
     * 扫描类中带有 @Autowired 注解的方法
     * 
     * @param beanClass Bean 类
     * @return 带有 @Autowired 注解的方法列表
     */
    public static List<Method> scanAutowiredMethods(Class<?> beanClass) {
        if (beanClass == null) {
            throw new IllegalArgumentException("Bean 类不能为空");
        }

        java.util.List<Method> autowiredMethods = new java.util.ArrayList<Method>();

        // 扫描当前类及其父类的所有方法
        Class<?> currentClass = beanClass;
        while (currentClass != null && currentClass != Object.class) {
            Method[] methods = currentClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Autowired.class)) {
                    autowiredMethods.add(method);
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        return autowiredMethods;
    }

    /**
     * 检查方法是否需要依赖注入
     * 
     * @param method 方法
     * @return 如果方法带有 @Autowired 注解返回 true，否则返回 false
     */
    public static boolean isAutowiredMethod(Method method) {
        return method != null && method.isAnnotationPresent(Autowired.class);
    }

    /**
     * 检查方法是否为 setter 方法
     * 
     * @param method 方法
     * @return 如果是 setter 方法返回 true，否则返回 false
     */
    public static boolean isSetterMethod(Method method) {
        if (method == null) {
            return false;
        }

        String methodName = method.getName();
        return methodName.startsWith("set") &&
                methodName.length() > 3 &&
                Character.isUpperCase(methodName.charAt(3)) &&
                method.getParameterTypes().length == 1 &&
                method.getReturnType() == void.class;
    }
}
