package com.simplespring.context;

import com.simplespring.core.annotation.Component;
import com.simplespring.core.annotation.Controller;
import com.simplespring.core.annotation.Configuration;
import com.simplespring.core.util.ClassUtils;
import com.simplespring.core.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * 类路径扫描器
 * 
 * 负责扫描指定包路径下的类文件，识别带有特定注解的类。
 * 使用 JDK 1.7 兼容的文件系统遍历和类加载机制。
 * 
 * 支持扫描的注解类型：
 * - @Component: 通用组件注解
 * - @Controller: MVC 控制器注解
 * - @Configuration: 配置类注解
 * 
 * 使用示例：
 * 
 * <pre>
 * {@code
 * ClassPathScanner scanner = new ClassPathScanner();
 * Set<Class<?>> components = scanner.scanPackage("com.example.service");
 * for (Class<?> clazz : components) {
 *   System.out.println("Found component: " + clazz.getName());
 * }
 * }
 * </pre>
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class ClassPathScanner {

  /** 支持的组件注解类型 */
  private static final Class<? extends Annotation>[] COMPONENT_ANNOTATIONS = new Class[] {
      Component.class,
      Controller.class,
      Configuration.class
  };

  /**
   * 扫描指定包路径下的所有组件类
   * 
   * @param basePackage 基础包路径，例如 "com.example.service"
   * @return 扫描到的组件类集合
   * @throws RuntimeException 如果扫描过程中发生错误
   */
  public Set<Class<?>> scanPackage(String basePackage) {
    if (StringUtils.isEmpty(basePackage)) {
      throw new IllegalArgumentException("基础包路径不能为空");
    }

    Set<Class<?>> componentClasses = new HashSet<Class<?>>();
    String packagePath = basePackage.replace('.', '/');

    try {
      ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
      Enumeration<URL> resources = classLoader.getResources(packagePath);

      while (resources.hasMoreElements()) {
        URL resource = resources.nextElement();
        String protocol = resource.getProtocol();

        if ("file".equals(protocol)) {
          // 处理文件系统中的类文件
          String filePath = resource.getFile();
          File packageDir = new File(filePath);
          if (packageDir.exists() && packageDir.isDirectory()) {
            componentClasses.addAll(findClassesInDirectory(packageDir, basePackage, classLoader));
          }
        } else if ("jar".equals(protocol)) {
          // 处理 JAR 文件中的类文件（暂不实现，留作扩展）
          // 在简易框架中主要处理文件系统中的类
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("扫描包路径失败: " + basePackage, e);
    }

    return componentClasses;
  }

  /**
   * 在指定目录中查找组件类
   * 
   * @param directory   目录
   * @param packageName 包名
   * @param classLoader 类加载器
   * @return 找到的组件类集合
   */
  private Set<Class<?>> findClassesInDirectory(File directory, String packageName, ClassLoader classLoader) {
    Set<Class<?>> classes = new HashSet<Class<?>>();

    if (!directory.exists() || !directory.isDirectory()) {
      return classes;
    }

    File[] files = directory.listFiles();
    if (files == null) {
      return classes;
    }

    for (File file : files) {
      if (file.isDirectory()) {
        // 递归扫描子目录
        String subPackageName = packageName + "." + file.getName();
        classes.addAll(findClassesInDirectory(file, subPackageName, classLoader));
      } else if (file.getName().endsWith(".class")) {
        // 处理 .class 文件
        String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
        try {
          Class<?> clazz = ClassUtils.forName(className, classLoader);
          if (isComponentClass(clazz)) {
            classes.add(clazz);
          }
        } catch (ClassNotFoundException e) {
          // 忽略无法加载的类，继续扫描其他类
          System.err.println("警告: 无法加载类 " + className + ": " + e.getMessage());
        } catch (NoClassDefFoundError e) {
          // 忽略依赖缺失的类，继续扫描其他类
          System.err.println("警告: 类依赖缺失 " + className + ": " + e.getMessage());
        }
      }
    }

    return classes;
  }

  /**
   * 检查类是否为组件类（带有组件注解）
   * 
   * @param clazz 要检查的类
   * @return 如果是组件类返回 true，否则返回 false
   */
  private boolean isComponentClass(Class<?> clazz) {
    if (clazz == null) {
      return false;
    }

    // 检查是否为接口、抽象类或注解类型
    if (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum()) {
      return false;
    }

    // 检查是否带有组件注解
    for (Class<? extends Annotation> annotationType : COMPONENT_ANNOTATIONS) {
      if (clazz.isAnnotationPresent(annotationType)) {
        return true;
      }
    }

    return false;
  }

  /**
   * 获取类的组件名称
   * 
   * 根据注解的 value 属性获取组件名称，如果未指定则使用类名的首字母小写形式。
   * 
   * @param clazz 组件类
   * @return 组件名称
   */
  public String getComponentName(Class<?> clazz) {
    if (clazz == null) {
      return null;
    }

    // 检查 @Component 注解
    Component component = clazz.getAnnotation(Component.class);
    if (component != null && StringUtils.hasText(component.value())) {
      return component.value();
    }

    // 检查 @Controller 注解
    Controller controller = clazz.getAnnotation(Controller.class);
    if (controller != null && StringUtils.hasText(controller.value())) {
      return controller.value();
    }

    // 检查 @Configuration 注解
    Configuration configuration = clazz.getAnnotation(Configuration.class);
    if (configuration != null) {
      // @Configuration 注解没有 value 属性，使用默认命名
    }

    // 使用默认命名规则：类名首字母小写
    String shortName = ClassUtils.getShortName(clazz);
    return StringUtils.uncapitalize(shortName);
  }

  /**
   * 检查指定的类是否为组件类
   * 
   * @param clazz 要检查的类
   * @return 如果是组件类返回 true，否则返回 false
   */
  public boolean isComponent(Class<?> clazz) {
    return isComponentClass(clazz);
  }
}
