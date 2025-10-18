package com.simplespring.beans.factory.support;

import com.simplespring.beans.factory.config.BeanDefinition;
import com.simplespring.core.annotation.Autowired;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * 循环依赖检测器
 * 检测 Bean 之间的循环依赖关系，构建依赖图并检测环路
 * 
 * @author SimpleSpring Framework
 */
public class CircularDependencyDetector {

  /**
   * 依赖图：Bean 名称 -> 依赖的 Bean 名称集合
   */
  private final Map<String, Set<String>> dependencyGraph = new HashMap<String, Set<String>>();

  /**
   * Bean 注册表引用
   */
  private final BeanRegistry beanRegistry;

  /**
   * 构造函数
   * 
   * @param beanRegistry Bean 注册表
   */
  public CircularDependencyDetector(BeanRegistry beanRegistry) {
    this.beanRegistry = beanRegistry;
  }

  /**
   * 构建依赖图
   * 分析所有 Bean 定义，构建完整的依赖关系图
   */
  public void buildDependencyGraph() {
    dependencyGraph.clear();

    String[] beanNames = beanRegistry.getBeanDefinitionNames();
    for (String beanName : beanNames) {
      BeanDefinition beanDefinition = beanRegistry.getBeanDefinition(beanName);
      if (beanDefinition != null) {
        Set<String> dependencies = analyzeBeanDependencies(beanDefinition);
        dependencyGraph.put(beanName, dependencies);
      }
    }
  }

  /**
   * 分析单个 Bean 的依赖关系
   * 
   * @param beanDefinition Bean 定义
   * @return 依赖的 Bean 名称集合
   */
  private Set<String> analyzeBeanDependencies(BeanDefinition beanDefinition) {
    Set<String> dependencies = new HashSet<String>();
    Class<?> beanClass = beanDefinition.getBeanClass();

    if (beanClass == null) {
      return dependencies;
    }

    // 分析构造函数依赖
    analyzeConstructorDependencies(beanClass, dependencies);

    // 分析字段依赖
    analyzeFieldDependencies(beanClass, dependencies);

    // 分析方法依赖
    analyzeMethodDependencies(beanClass, dependencies);

    return dependencies;
  }

  /**
   * 分析构造函数依赖
   * 
   * @param beanClass    Bean 类
   * @param dependencies 依赖集合
   */
  private void analyzeConstructorDependencies(Class<?> beanClass, Set<String> dependencies) {
    Constructor<?>[] constructors = beanClass.getDeclaredConstructors();

    for (Constructor<?> constructor : constructors) {
      // 优先分析带有 @Autowired 注解的构造函数
      if (constructor.isAnnotationPresent(Autowired.class)) {
        analyzeConstructorParameters(constructor, dependencies);
        return; // 找到 @Autowired 构造函数后停止
      }
    }

    // 如果没有 @Autowired 构造函数，分析默认构造函数
    try {
      Constructor<?> defaultConstructor = beanClass.getDeclaredConstructor();
      analyzeConstructorParameters(defaultConstructor, dependencies);
    } catch (NoSuchMethodException e) {
      // 没有默认构造函数，分析第一个构造函数
      if (constructors.length > 0) {
        analyzeConstructorParameters(constructors[0], dependencies);
      }
    }
  }

  /**
   * 分析构造函数参数
   * 
   * @param constructor  构造函数
   * @param dependencies 依赖集合
   */
  private void analyzeConstructorParameters(Constructor<?> constructor, Set<String> dependencies) {
    Class<?>[] parameterTypes = constructor.getParameterTypes();
    for (Class<?> parameterType : parameterTypes) {
      String dependencyBeanName = findBeanNameByType(parameterType);
      if (dependencyBeanName != null) {
        dependencies.add(dependencyBeanName);
      }
    }
  }

  /**
   * 分析字段依赖
   * 
   * @param beanClass    Bean 类
   * @param dependencies 依赖集合
   */
  private void analyzeFieldDependencies(Class<?> beanClass, Set<String> dependencies) {
    Field[] fields = beanClass.getDeclaredFields();
    for (Field field : fields) {
      if (field.isAnnotationPresent(Autowired.class)) {
        Class<?> fieldType = field.getType();
        String dependencyBeanName = findBeanNameByType(fieldType);
        if (dependencyBeanName != null) {
          dependencies.add(dependencyBeanName);
        }
      }
    }

    // 递归分析父类字段
    Class<?> superClass = beanClass.getSuperclass();
    if (superClass != null && superClass != Object.class) {
      analyzeFieldDependencies(superClass, dependencies);
    }
  }

  /**
   * 分析方法依赖
   * 
   * @param beanClass    Bean 类
   * @param dependencies 依赖集合
   */
  private void analyzeMethodDependencies(Class<?> beanClass, Set<String> dependencies) {
    Method[] methods = beanClass.getDeclaredMethods();
    for (Method method : methods) {
      if (method.isAnnotationPresent(Autowired.class)) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> parameterType : parameterTypes) {
          String dependencyBeanName = findBeanNameByType(parameterType);
          if (dependencyBeanName != null) {
            dependencies.add(dependencyBeanName);
          }
        }
      }
    }

    // 递归分析父类方法
    Class<?> superClass = beanClass.getSuperclass();
    if (superClass != null && superClass != Object.class) {
      analyzeMethodDependencies(superClass, dependencies);
    }
  }

  /**
   * 根据类型查找 Bean 名称
   * 
   * @param type 类型
   * @return Bean 名称，如果找不到或有多个匹配返回 null
   */
  private String findBeanNameByType(Class<?> type) {
    List<String> beanNames = beanRegistry.getBeanNamesForType(type);
    if (beanNames.size() == 1) {
      return beanNames.get(0);
    }
    return null; // 找不到或有多个匹配，无法确定依赖关系
  }

  /**
   * 检测循环依赖
   * 使用深度优先搜索算法检测依赖图中的环路
   * 
   * @return 循环依赖路径列表，如果没有循环依赖返回空列表
   */
  public List<List<String>> detectCircularDependencies() {
    List<List<String>> cycles = new ArrayList<List<String>>();
    Set<String> visited = new HashSet<String>();
    Set<String> recursionStack = new HashSet<String>();
    Stack<String> path = new Stack<String>();

    for (String beanName : dependencyGraph.keySet()) {
      if (!visited.contains(beanName)) {
        detectCyclesFromNode(beanName, visited, recursionStack, path, cycles);
      }
    }

    return cycles;
  }

  /**
   * 从指定节点开始检测循环依赖
   * 
   * @param beanName       当前 Bean 名称
   * @param visited        已访问的节点集合
   * @param recursionStack 递归栈中的节点集合
   * @param path           当前路径
   * @param cycles         检测到的循环依赖列表
   */
  private void detectCyclesFromNode(String beanName, Set<String> visited, Set<String> recursionStack,
      Stack<String> path, List<List<String>> cycles) {

    visited.add(beanName);
    recursionStack.add(beanName);
    path.push(beanName);

    Set<String> dependencies = dependencyGraph.get(beanName);
    if (dependencies != null) {
      for (String dependency : dependencies) {
        if (!visited.contains(dependency)) {
          // 递归访问未访问的依赖
          detectCyclesFromNode(dependency, visited, recursionStack, path, cycles);
        } else if (recursionStack.contains(dependency)) {
          // 发现循环依赖
          List<String> cycle = extractCycle(path, dependency);
          if (!cycle.isEmpty()) {
            cycles.add(cycle);
          }
        }
      }
    }

    recursionStack.remove(beanName);
    path.pop();
  }

  /**
   * 从路径中提取循环依赖
   * 
   * @param path       当前路径
   * @param cycleStart 循环开始的节点
   * @return 循环依赖路径
   */
  private List<String> extractCycle(Stack<String> path, String cycleStart) {
    List<String> cycle = new ArrayList<String>();
    boolean foundStart = false;

    // 从栈底开始查找循环开始节点
    for (String node : path) {
      if (node.equals(cycleStart)) {
        foundStart = true;
      }
      if (foundStart) {
        cycle.add(node);
      }
    }

    // 添加循环结束节点（与开始节点相同）
    if (foundStart) {
      cycle.add(cycleStart);
    }

    return cycle;
  }

  /**
   * 检测指定 Bean 是否存在循环依赖
   * 
   * @param beanName Bean 名称
   * @return 如果存在循环依赖返回 true，否则返回 false
   */
  public boolean hasCircularDependency(String beanName) {
    Set<String> visited = new HashSet<String>();
    Set<String> recursionStack = new HashSet<String>();
    return hasCircularDependencyFromNode(beanName, visited, recursionStack);
  }

  /**
   * 从指定节点检测是否存在循环依赖
   * 
   * @param beanName       当前 Bean 名称
   * @param visited        已访问的节点集合
   * @param recursionStack 递归栈中的节点集合
   * @return 如果存在循环依赖返回 true，否则返回 false
   */
  private boolean hasCircularDependencyFromNode(String beanName, Set<String> visited, Set<String> recursionStack) {
    visited.add(beanName);
    recursionStack.add(beanName);

    Set<String> dependencies = dependencyGraph.get(beanName);
    if (dependencies != null) {
      for (String dependency : dependencies) {
        if (!visited.contains(dependency)) {
          if (hasCircularDependencyFromNode(dependency, visited, recursionStack)) {
            return true;
          }
        } else if (recursionStack.contains(dependency)) {
          return true; // 发现循环依赖
        }
      }
    }

    recursionStack.remove(beanName);
    return false;
  }

  /**
   * 获取指定 Bean 的直接依赖
   * 
   * @param beanName Bean 名称
   * @return 直接依赖的 Bean 名称集合
   */
  public Set<String> getDirectDependencies(String beanName) {
    Set<String> dependencies = dependencyGraph.get(beanName);
    return dependencies != null ? new HashSet<String>(dependencies) : new HashSet<String>();
  }

  /**
   * 获取指定 Bean 的所有依赖（包括间接依赖）
   * 
   * @param beanName Bean 名称
   * @return 所有依赖的 Bean 名称集合
   */
  public Set<String> getAllDependencies(String beanName) {
    Set<String> allDependencies = new HashSet<String>();
    Set<String> visited = new HashSet<String>();
    collectAllDependencies(beanName, allDependencies, visited);
    return allDependencies;
  }

  /**
   * 递归收集所有依赖
   * 
   * @param beanName        当前 Bean 名称
   * @param allDependencies 所有依赖集合
   * @param visited         已访问的节点集合
   */
  private void collectAllDependencies(String beanName, Set<String> allDependencies, Set<String> visited) {
    if (visited.contains(beanName)) {
      return; // 避免无限递归
    }

    visited.add(beanName);
    Set<String> directDependencies = dependencyGraph.get(beanName);
    if (directDependencies != null) {
      for (String dependency : directDependencies) {
        allDependencies.add(dependency);
        collectAllDependencies(dependency, allDependencies, visited);
      }
    }
  }

  /**
   * 获取依赖图的副本
   * 
   * @return 依赖图副本
   */
  public Map<String, Set<String>> getDependencyGraph() {
    Map<String, Set<String>> copy = new HashMap<String, Set<String>>();
    for (Map.Entry<String, Set<String>> entry : dependencyGraph.entrySet()) {
      copy.put(entry.getKey(), new HashSet<String>(entry.getValue()));
    }
    return copy;
  }

  /**
   * 清空依赖图
   */
  public void clear() {
    dependencyGraph.clear();
  }

  /**
   * 获取依赖图中的所有 Bean 名称
   * 
   * @return Bean 名称集合
   */
  public Set<String> getAllBeanNames() {
    return new HashSet<String>(dependencyGraph.keySet());
  }

  /**
   * 检查依赖图是否为空
   * 
   * @return 如果依赖图为空返回 true，否则返回 false
   */
  public boolean isEmpty() {
    return dependencyGraph.isEmpty();
  }
}
