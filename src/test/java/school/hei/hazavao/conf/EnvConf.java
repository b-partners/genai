package school.hei.hazavao.conf;

import org.springframework.test.context.DynamicPropertyRegistry;

public class EnvConf {
  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("openai.key", () -> "dummy");
  }
}
