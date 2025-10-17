package com.simplespring.webmvc;

/**
 * 处理器执行链，封装处理器方法和相关的拦截器
 * 
 * HandlerExecutionChain 包含了处理一个 HTTP 请求所需的所有信息：
 * - 处理器方法（HandlerMethod）
 * - 拦截器链（暂时简化，不实现拦截器功能）
 * 
 * 在完整的 Spring MVC 中，执行链还会包含拦截器，用于在处理器方法执行前后
 * 进行额外的处理（如权限检查、日志记录等）。
 * 
 * @author Simple Spring Framework
 * @since 1.0.0
 */
public class HandlerExecutionChain {

  /**
   * 处理器方法
   */
  private HandlerMethod handler;

  /**
   * 构造函数
   * 
   * @param handler 处理器方法
   */
  public HandlerExecutionChain(HandlerMethod handler) {
    this.handler = handler;
  }

  /**
   * 获取处理器方法
   * 
   * @return 处理器方法
   */
  public HandlerMethod getHandler() {
    return handler;
  }

  /**
   * 设置处理器方法
   * 
   * @param handler 处理器方法
   */
  public void setHandler(HandlerMethod handler) {
    this.handler = handler;
  }

  @Override
  public String toString() {
    return "HandlerExecutionChain{" +
        "handler=" + handler +
        '}';
  }
}
