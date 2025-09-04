package fr.birdia.genai.endpoint.rest.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AuthenticationConf {
  private final String adminApiKey;

  public AuthenticationConf(@Value("${admin.api.key}") String adminApiKey) {
    this.adminApiKey = adminApiKey;
  }
}
