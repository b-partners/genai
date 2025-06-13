package school.hei.hazavao.model;

import static java.net.http.HttpClient.newHttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class Hazavao implements Function<String, String> {

  private static final String OPENAPI_URL = "https://api.openai.com/v1/chat/completions";
  private final String openapiKey;

  @SneakyThrows
  @Override
  public String apply(String toDefine) {
    var mapper = new ObjectMapper();

    var request = openapiRequest(toDefine, mapper);
    var response = newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    var root = mapper.readTree(response.body());
    return root.path("choices").get(0).path("message").path("content").asText();
  }

  private HttpRequest openapiRequest(String toDefine, ObjectMapper mapper) {
    var message = mapper.createObjectNode();
    message.put("role", "user");
    message.put(
        "content",
        "Describe the word \"" + toDefine + "\" concisely in one sentence. Do it in Malagasy.");
    var messages = mapper.createArrayNode();
    messages.add(message);

    var requestBody = mapper.createObjectNode();
    requestBody.put("model", "gpt-3.5-turbo");
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
