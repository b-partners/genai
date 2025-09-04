package fr.birdia.genai.endpoint.rest.security;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class ApiKeyInterceptor implements HandlerInterceptor {
  private final AuthenticationConf authenticationConf;
  private static final String API_KEY_HEADER = "x-api-key";

  @SneakyThrows
  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    String apiKey =
        request.getHeader(API_KEY_HEADER) == null
            ? request.getParameter(API_KEY_HEADER)
            : request.getHeader(API_KEY_HEADER);
    if (apiKey == null || apiKey.isEmpty()) {
      response.sendError(UNAUTHORIZED.value(), "Bad credentials");
      return false;
    }
    if (!apiKey.equals(authenticationConf.getAdminApiKey())) {
      response.sendError(FORBIDDEN.value(), "Access denied");
      return true;
    }
    return true;
  }
}
