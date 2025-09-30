package com.simplespring.aop;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * 切点表达式解析器
 * 解析简单的切点表达式，支持方法名匹配、包路径匹配和通配符
 * 
 * 支持的表达式格式：
 * 1. execution(* com.example.service.*.*(..)) - 执行表达式
 * 2. within(com.example.service.*) - 类型匹配表达式
 * 3. @annotation(com.example.MyAnnotation) - 注解匹配表达式
 * 
 * @author SimpleSpring
 */
public class PointcutExpressionParser {

  /**
   * 解析切点表达式并创建匹配器
   * 
   * @param expression 切点表达式
   * @return 切点匹配器
   * @throws IllegalArgumentException 如果表达式格式不正确
   */
  public static PointcutMatcher parse(String expression) {
    if (expression == null || expression.trim().isEmpty()) {
      throw new IllegalArgumentException("切点表达式不能为空");
    }

    String trimmedExpression = expression.trim();

    // 解析 execution 表达式
    if (trimmedExpression.startsWith("execution(") && trimmedExpression.endsWith(")")) {
      String executionPattern = trimmedExpression.substring(10, trimmedExpression.length() - 1);
      return new ExecutionPointcutMatcher(expression, executionPattern);
    }

    // 解析 within 表达式
    if (trimmedExpression.startsWith("within(") && trimmedExpression.endsWith(")")) {
      String withinPattern = trimmedExpression.substring(7, trimmedExpression.length() - 1);
      return new WithinPointcutMatcher(expression, withinPattern);
    }

    // 解析 @annotation 表达式
    if (trimmedExpression.startsWith("@annotation(") && trimmedExpression.endsWith(")")) {
      String annotationName = trimmedExpression.substring(12, trimmedExpression.length() - 1);
      return new AnnotationPointcutMatcher(expression, annotationName);
    }

    throw new IllegalArgumentException("不支持的切点表达式格式: " + expression);
  }

  /**
   * 执行表达式匹配器
   * 支持格式：execution(修饰符 返回类型 包名.类名.方法名(参数))
   * 例如：execution(* com.example.service.*.*(..))
   */
  private static class ExecutionPointcutMatcher implements PointcutMatcher {
    private final String expression;
    private final String pattern;
    private final Pattern methodPattern;
    private final Pattern classPattern;

    public ExecutionPointcutMatcher(String expression, String pattern) {
      this.expression = expression;
      this.pattern = pattern.trim();

      // 解析执行表达式
      String[] parts = this.pattern.split("\\s+");
      if (parts.length < 2) {
        throw new IllegalArgumentException("执行表达式格式不正确: " + pattern);
      }

      // 获取方法签名部分（最后一部分）
      String methodSignature = parts[parts.length - 1];

      // 解析方法签名：包名.类名.方法名(参数)
      int paramStartIndex = methodSignature.indexOf('(');
      if (paramStartIndex == -1) {
        throw new IllegalArgumentException("方法签名格式不正确，缺少参数括号: " + methodSignature);
      }

      String methodPart = methodSignature.substring(0, paramStartIndex);
      int lastDotIndex = methodPart.lastIndexOf('.');

      if (lastDotIndex == -1) {
        throw new IllegalArgumentException("方法签名格式不正确，缺少类名: " + methodSignature);
      }

      if (lastDotIndex + 1 >= methodPart.length()) {
        throw new IllegalArgumentException("方法签名格式不正确，方法名为空: " + methodSignature);
      }

      String classAndPackage = methodPart.substring(0, lastDotIndex);
      String methodName = methodPart.substring(lastDotIndex + 1);

      // 转换通配符为正则表达式
      // 对于类名模式，* 应该匹配包括 . 在内的所有字符
      String classRegex = wildcardToRegexForClass(classAndPackage);
      String methodRegex = wildcardToRegex(methodName);

      this.classPattern = Pattern.compile(classRegex);
      this.methodPattern = Pattern.compile(methodRegex);
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
      // 检查类名是否匹配
      if (!matches(targetClass)) {
        return false;
      }

      // 检查方法名是否匹配
      return methodPattern.matcher(method.getName()).matches();
    }

    @Override
    public boolean matches(Class<?> targetClass) {
      String className = targetClass.getName();
      return classPattern.matcher(className).matches();
    }

    @Override
    public String getExpression() {
      return expression;
    }
  }

  /**
   * Within 表达式匹配器
   * 支持格式：within(包名.类名)
   * 例如：within(com.example.service.*)
   */
  private static class WithinPointcutMatcher implements PointcutMatcher {
    private final String expression;
    private final Pattern classPattern;

    public WithinPointcutMatcher(String expression, String pattern) {
      this.expression = expression;
      String regex = wildcardToRegex(pattern.trim());
      this.classPattern = Pattern.compile(regex);
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
      return matches(targetClass);
    }

    @Override
    public boolean matches(Class<?> targetClass) {
      String className = targetClass.getName();
      return classPattern.matcher(className).matches();
    }

    @Override
    public String getExpression() {
      return expression;
    }
  }

  /**
   * 注解表达式匹配器
   * 支持格式：@annotation(注解全限定名)
   * 例如：@annotation(com.example.MyAnnotation)
   */
  private static class AnnotationPointcutMatcher implements PointcutMatcher {
    private final String expression;
    private final String annotationName;
    private Class<?> annotationClass;

    public AnnotationPointcutMatcher(String expression, String annotationName) {
      this.expression = expression;
      this.annotationName = annotationName.trim();

      // 尝试加载注解类
      try {
        this.annotationClass = Class.forName(this.annotationName);
      } catch (ClassNotFoundException e) {
        // 如果找不到注解类，在匹配时会返回 false
        this.annotationClass = null;
      }
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
      if (annotationClass == null) {
        return false;
      }

      // 检查方法上是否有指定的注解
      return method.isAnnotationPresent(annotationClass.asSubclass(java.lang.annotation.Annotation.class));
    }

    @Override
    public boolean matches(Class<?> targetClass) {
      if (annotationClass == null) {
        return false;
      }

      // 检查类上是否有指定的注解
      return targetClass.isAnnotationPresent(annotationClass.asSubclass(java.lang.annotation.Annotation.class));
    }

    @Override
    public String getExpression() {
      return expression;
    }
  }

  /**
   * 将通配符表达式转换为正则表达式（用于类名匹配）
   * 
   * @param wildcard 通配符表达式
   * @return 正则表达式
   */
  private static String wildcardToRegexForClass(String wildcard) {
    StringBuilder regex = new StringBuilder();

    for (int i = 0; i < wildcard.length(); i++) {
      char c = wildcard.charAt(i);
      switch (c) {
        case '*':
          // 对于类名，* 匹配任意字符包括 .
          regex.append(".*");
          break;
        case '.':
          // 检查是否是 .. 模式
          if (i + 1 < wildcard.length() && wildcard.charAt(i + 1) == '.') {
            // .. 匹配任意字符包括 .
            regex.append(".*");
            i++; // 跳过下一个 .
          } else {
            // 单个 . 需要转义
            regex.append("\\.");
          }
          break;
        case '?':
          // ? 匹配单个字符
          regex.append(".");
          break;
        case '(':
        case ')':
        case '[':
        case ']':
        case '{':
        case '}':
        case '^':
        case '$':
        case '+':
        case '|':
        case '\\':
          // 转义正则表达式特殊字符
          regex.append("\\").append(c);
          break;
        default:
          regex.append(c);
          break;
      }
    }

    return regex.toString();
  }

  /**
   * 将通配符表达式转换为正则表达式
   * 
   * @param wildcard 通配符表达式
   * @return 正则表达式
   */
  private static String wildcardToRegex(String wildcard) {
    StringBuilder regex = new StringBuilder();

    for (int i = 0; i < wildcard.length(); i++) {
      char c = wildcard.charAt(i);
      switch (c) {
        case '*':
          // * 匹配任意字符（除了 .）
          regex.append("[^.]*");
          break;
        case '.':
          // 检查是否是 .. 模式
          if (i + 1 < wildcard.length() && wildcard.charAt(i + 1) == '.') {
            // .. 匹配任意字符包括 .
            regex.append(".*");
            i++; // 跳过下一个 .
          } else {
            // 单个 . 需要转义
            regex.append("\\.");
          }
          break;
        case '?':
          // ? 匹配单个字符（除了 .）
          regex.append("[^.]");
          break;
        case '(':
        case ')':
        case '[':
        case ']':
        case '{':
        case '}':
        case '^':
        case '$':
        case '+':
        case '|':
        case '\\':
          // 转义正则表达式特殊字符
          regex.append("\\").append(c);
          break;
        default:
          regex.append(c);
          break;
      }
    }

    return regex.toString();
  }
}
