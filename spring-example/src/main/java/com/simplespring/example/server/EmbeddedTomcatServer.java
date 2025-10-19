package com.simplespring.example.server;

import com.simplespring.webmvc.DispatcherServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 嵌入式 Tomcat 服务器
 * 提供独立的 HTTP 服务，类似于 Spring Boot 的运行方式
 */
public class EmbeddedTomcatServer {

  private static final Logger logger = LoggerFactory.getLogger(EmbeddedTomcatServer.class);

  private final int port;
  private final String contextPath;
  private Tomcat tomcat;
  private DispatcherServlet dispatcherServlet;

  public EmbeddedTomcatServer(int port, String contextPath) {
    this.port = port;
    this.contextPath = contextPath;
  }

  /**
   * 设置 DispatcherServlet
   */
  public void setDispatcherServlet(DispatcherServlet dispatcherServlet) {
    this.dispatcherServlet = dispatcherServlet;
  }

  /**
   * 启动服务器
   */
  public void start() throws LifecycleException {
    logger.info("正在启动嵌入式 Tomcat 服务器...");
    logger.info("端口: {}", port);
    logger.info("上下文路径: {}", contextPath);

    // 创建 Tomcat 实例
    tomcat = new Tomcat();
    tomcat.setPort(port);
    tomcat.setHostname("localhost");

    // 设置工作目录
    String workingDir = System.getProperty("java.io.tmpdir");
    tomcat.setBaseDir(workingDir);

    // 创建上下文
    Context context = tomcat.addContext(contextPath, new File(".").getAbsolutePath());

    // 添加 DispatcherServlet
    if (dispatcherServlet != null) {
      Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet);
      context.addServletMappingDecoded("/*", "dispatcherServlet");
      logger.info("已注册 DispatcherServlet，映射路径: /*");
    } else {
      logger.warn("DispatcherServlet 未设置");
    }

    // 启动 Tomcat
    tomcat.start();

    logger.info("嵌入式 Tomcat 服务器启动成功!");
    logger.info("访问地址: http://localhost:{}{}", port, contextPath);
    logger.info("API 文档:");
    logger.info("  用户接口:");
    logger.info("    GET    http://localhost:{}{}/users - 获取所有用户", port, contextPath);
    logger.info("    GET    http://localhost:{}{}/users/{{id}} - 根据ID获取用户", port, contextPath);
    logger.info("    POST   http://localhost:{}{}/users - 创建用户", port, contextPath);
    logger.info("    POST   http://localhost:{}{}/users/login - 用户登录", port, contextPath);
    logger.info("    PUT    http://localhost:{}{}/users/{{id}} - 更新用户", port, contextPath);
    logger.info("    DELETE http://localhost:{}{}/users/{{id}} - 删除用户", port, contextPath);
    logger.info("  订单接口:");
    logger.info("    GET    http://localhost:{}{}/orders - 获取所有订单", port, contextPath);
    logger.info("    GET    http://localhost:{}{}/orders/{{id}} - 根据ID获取订单", port, contextPath);
    logger.info("    GET    http://localhost:{}{}/orders/user/{{userId}} - 根据用户ID获取订单", port, contextPath);
    logger.info("    POST   http://localhost:{}{}/orders - 创建订单", port, contextPath);
    logger.info("    PUT    http://localhost:{}{}/orders/{{id}}/confirm - 确认订单", port, contextPath);
    logger.info("    PUT    http://localhost:{}{}/orders/{{id}}/process - 处理订单", port, contextPath);
    logger.info("    PUT    http://localhost:{}{}/orders/{{id}}/ship - 发货订单", port, contextPath);
    logger.info("    PUT    http://localhost:{}{}/orders/{{id}}/complete - 完成订单", port, contextPath);
    logger.info("    PUT    http://localhost:{}{}/orders/{{id}}/cancel - 取消订单", port, contextPath);
    logger.info("    GET    http://localhost:{}{}/orders/statistics - 获取订单统计", port, contextPath);
  }

  /**
   * 等待服务器运行
   */
  public void await() {
    if (tomcat != null) {
      tomcat.getServer().await();
    }
  }

  /**
   * 停止服务器
   */
  public void stop() throws LifecycleException {
    if (tomcat != null) {
      logger.info("正在停止嵌入式 Tomcat 服务器...");
      tomcat.stop();
      tomcat.destroy();
      logger.info("嵌入式 Tomcat 服务器已停止");
    }
  }

  /**
   * 获取服务器端口
   */
  public int getPort() {
    return port;
  }

  /**
   * 获取上下文路径
   */
  public String getContextPath() {
    return contextPath;
  }

  /**
   * 检查服务器是否正在运行
   */
  public boolean isRunning() {
    return tomcat != null && tomcat.getServer().getState().isAvailable();
  }
}
