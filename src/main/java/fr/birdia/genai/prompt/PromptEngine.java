package fr.birdia.genai.prompt;

import java.util.Map;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Component
public class PromptEngine {

  private final TemplateEngine templateEngine;

  public PromptEngine() {
    var templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setPrefix("templates/");
    templateResolver.setSuffix(".txt");
    templateResolver.setTemplateMode(TemplateMode.TEXT);
    templateResolver.setCharacterEncoding("UTF-8");

    this.templateEngine = new TemplateEngine();
    this.templateEngine.setTemplateResolver(templateResolver);
  }

  public String render(String templateName, Map<String, Object> variables) {
    var context = new Context();
    context.setVariables(variables);
    return templateEngine.process(templateName, context);
  }
}
