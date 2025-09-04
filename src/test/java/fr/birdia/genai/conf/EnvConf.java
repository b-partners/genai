package fr.birdia.genai.conf;

import org.springframework.test.context.DynamicPropertyRegistry;

public class EnvConf {
  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("openai.key", () -> "dummy");
    registry.add("admin.api.key", () -> "dummy");
  }
}
