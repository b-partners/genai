package school.hei.hazavao.model;

import static java.net.http.HttpClient.newHttpClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Chat implements Function<String, String> {

  private static final String OPENAPI_URL = "https://api.openai.com/v1/chat/completions";
  private final String openapiKey;

  public Chat(@Value("${openapi.key}") String openapiKey) {
    this.openapiKey = openapiKey;
  }

  @Override
  public String apply(String content) {
    var mapper = new ObjectMapper();

    var request = openapiRequest(content, mapper);
    HttpResponse<String> response = null;
    try {
      response = newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }

    JsonNode root = null;
    try {
      root = mapper.readTree(response.body());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return root.path("choices").get(0).path("message").path("content").asText();
  }

  private HttpRequest openapiRequest(String content, ObjectMapper mapper) {
    var message = mapper.createObjectNode();
    message.put("role", "user");
    message.put("content", content);
    var messages = mapper.createArrayNode();
    messages.add(message);

    var requestBody = mapper.createObjectNode();
    requestBody.put("model", "gpt-4o");
    requestBody.set("messages", messages);
    requestBody.put("temperature", 0.5);

    return HttpRequest.newBuilder()
        .uri(URI.create(OPENAPI_URL))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + openapiKey)
        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
        .build();
  }
}
