package fr.birdia.genai.model;

import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Hazavao implements Function<String, String> {

  private final Chat chat;

  @Override
  public String apply(String toDefine) {
    return chat.apply(
        "Describe the word \"" + toDefine + "\" concisely in one sentence. Do it in Malagasy.");
  }
}
