package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.BeanFactory;
import com.simplespring.core.annotation.Autowired;
import com.simplespring.core.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * 构造函数注入器
 * 处理构造函数参数的依赖解析，实现构造函数选择逻辑和参数注入
 * 
 * @author SimpleSpring Framework
 */
public class ConstructorInjector {

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
    public ConstructorInjector(BeanRegistry beanRegistry) {
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
     * 选择合适的构造函数并创建 Bean 实例
     * 
     * @param beanClass Bean 类
     * @return Bean 实例
     * @throws DependencyInjectionException 如果构造函数注入失败
     */
    public Object createBeanInstance(Class<?> beanClass) {
        if (beanClass == null) {
            throw new IllegalArgumentException("Bean 类不能为空");
        }

        try {
            // 选择合适的构造函数
            Constructor<?> constructor = selectConstructor(beanClass);

            // 如果是默认构造函数，直接创建实例
            if (constructor.getParameterTypes().length == 0) {
                ReflectionUtils.makeAccessible(constructor);
                return constructor.newInstance();
            }

            // 解析构造函数参数
            Object[] args = resolveConstructorParameters(constructor);

            // 创建实例
            ReflectionUtils.makeAccessible(constructor);
            return constructor.newInstance(args);

        } catch (Exception e) {
            if (e instanceof DependencyInjectionException) {
                throw (DependencyInjectionException) e;
            }
            throw new DependencyInjectionException(
                    "无法创建类 '" + beanClass.getName() + "' 的实例: " + e.getMessage(), e);
        }
    }

    /**
     * 选择合适的构造函数
     * 优先选择带有 @Autowired 注解的构造函数，如果没有则选择默认构造函数
     * 
     * @param beanClass Bean 类
     * @return 选择的构造函数
     * @throws DependencyInjectionException 如果无法选择合适的构造函数
     */
    private Constructor<?> selectConstructor(Class<?> beanClass) {
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();

        // 查找带有 @Autowired 注解的构造函数
        Constructor<?> autowiredConstructor = null;
        int autowiredCount = 0;

        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                autowiredConstructor = constructor;
                autowiredCount++;
            }
        }

        // 如果有多个 @Autowired 构造函数，抛出异常
        if (autowiredCount > 1) {
            throw new DependencyInjectionException(
                    "类 '" + beanClass.getName() + "' 有多个带有 @Autowired 注解的构造函数，只能有一个");
        }

        // 如果找到了 @Autowired 构造函数，使用它
        if (autowiredConstructor != null) {
            return autowiredConstructor;
        }

        // 如果没有 @Autowired 构造函数，尝试使用默认构造函数
        try {
            return beanClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            // 如果没有默认构造函数，选择参数最少的构造函数
            Constructor<?> selectedConstructor = null;
            int minParameterCount = Integer.MAX_VALUE;

            for (Constructor<?> constructor : constructors) {
                int parameterCount = constructor.getParameterTypes().length;
                if (parameterCount < minParameterCount) {
                    minParameterCount = parameterCount;
                    selectedConstructor = constructor;
                }
            }

            if (selectedConstructor == null) {
                throw new DependencyInjectionException(
                        "类 '" + beanClass.getName() + "' 没有可用的构造函数");
            }

            return selectedConstructor;
        }
    }

    /**
     * 解析构造函数参数
     * 
     * @param constructor 构造函数
     * @return 参数值数组
     * @throws DependencyInjectionException 如果无法解析参数
     */
    private Object[] resolveConstructorParameters(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];

        // 检查是否为必需的依赖注入
        boolean required = true;
        Autowired autowired = constructor.getAnnotation(Autowired.class);
        if (autowired != null) {
            required = autowired.required();
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Object dependencyBean = findDependencyBean(parameterType, required);

            if (dependencyBean == null && required) {
                throw new DependencyInjectionException(
                        "无法为构造函数的第 " + (i + 1) + " 个参数找到类型为 '" +
                                parameterType.getName() + "' 的依赖 Bean");
            }

            args[i] = dependencyBean;
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
     * 获取类的首选构造函数
     * 
     * @param beanClass Bean 类
     * @return 首选构造函数，如果没有找到返回 null
     */
    public static Constructor<?> getPreferredConstructor(Class<?> beanClass) {
        if (beanClass == null) {
            return null;
        }

        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();

        // 查找带有 @Autowired 注解的构造函数
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                return constructor;
            }
        }

        // 如果没有 @Autowired 构造函数，返回默认构造函数
        try {
            return beanClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            // 如果没有默认构造函数，返回参数最少的构造函数
            Constructor<?> selectedConstructor = null;
            int minParameterCount = Integer.MAX_VALUE;

            for (Constructor<?> constructor : constructors) {
                int parameterCount = constructor.getParameterTypes().length;
                if (parameterCount < minParameterCount) {
                    minParameterCount = parameterCount;
                    selectedConstructor = constructor;
                }
            }

            return selectedConstructor;
        }
    }

    /**
     * 检查构造函数是否需要依赖注入
     * 
     * @param constructor 构造函数
     * @return 如果构造函数带有 @Autowired 注解或有参数返回 true，否则返回 false
     */
    public static boolean requiresDependencyInjection(Constructor<?> constructor) {
        if (constructor == null) {
            return false;
        }

        return constructor.isAnnotationPresent(Autowired.class) ||
                constructor.getParameterTypes().length > 0;
    }

    /**
     * 检查类是否有带有 @Autowired 注解的构造函数
     * 
     * @param beanClass Bean 类
     * @return 如果有带有 @Autowired 注解的构造函数返回 true，否则返回 false
     */
    public static boolean hasAutowiredConstructor(Class<?> beanClass) {
        if (beanClass == null) {
            return false;
        }

        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                return true;
            }
        }

        return false;
    }
}
