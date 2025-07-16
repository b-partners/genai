package school.hei.hazavao.model;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class OpenAiChat implements Chat {
  private final String openAiKey;

  public OpenAiChat(@Value("${openai.key}") String openAiKey) {
    this.openAiKey = openAiKey;
  }

  @Override
  public String apply(String input) {
    var model =
        dev.langchain4j.model.openai.OpenAiChatModel.builder()
            .apiKey(openAiKey)
            .modelName(GPT_4_O)
            .build();

    return model.chat(input);
  }
}
