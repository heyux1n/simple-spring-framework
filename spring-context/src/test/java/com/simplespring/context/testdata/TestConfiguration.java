package com.simplespring.context.testdata;

import com.simplespring.core.annotation.Configuration;

/**
 * 测试用的配置类
 */
@Configuration
public class TestConfiguration {

  public String getConfigValue() {
    return "config-value";
  }
}
