package fr.birdia.genai.model;

import fr.birdia.genai.prompt.PromptEngine;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AnalyseurToiture implements Function<Toit, String> {

  private static final String PROMPT_TEMPLATE = "analyseur-toiture";

  private final Chat chat;
  private final PromptEngine promptEngine;

  @Override
  public String apply(Toit toit) {

    Instant startOfExecution = Instant.now();
    String aiReport = getAIReport(toit);
    Instant endOfExecution = Instant.now();

    Duration duration = Duration.between(endOfExecution, startOfExecution);
    log.info("Report from AI obtained in {} ms.", duration.toMillis());

    return aiReport;
  }

  private String getAIReport(Toit toit) {
    var variables =
        Map.<String, Object>ofEntries(
            Map.entry("categoryEmoji", getCategoryEmoji(toit)),
            Map.entry("category", getCategory(toit)),
            Map.entry("etatToiture", getEtatToiture(toit)),
            Map.entry("surfaceEnM2", toit.surfaceEnM2()),
            Map.entry("revetement", toit.revetement()),
            Map.entry("revetement2", toit.revetement2()),
            Map.entry("humidite", toit.humidité()),
            Map.entry("moisissure", toit.moisissure()),
            Map.entry("usure", toit.usure()),
            Map.entry("obstacles", toit.obstacles()),
            Map.entry("fissureCassure", toit.fissureCassure() ? "OUI" : "NON"),
            Map.entry("risqueFeu", toit.risqueFeu() ? "OUI" : "NON"),
            Map.entry("hauteurBatiment", toit.hauteurBatiment()),
            Map.entry("commentaireCouvreur", toit.commentaireCouvreur()));

    var prompt = promptEngine.render(PROMPT_TEMPLATE, variables);
    log.info("AI Prompt : {}", prompt);
    return chat.apply(prompt).replace("```html", "").replace("```", "");
  }

  private String getCategoryEmoji(Toit toit) {
    var category = getCategory(toit);
    return switch (category) {
      case "A" -> "🟢";
      case "B", "C" -> "🟡";
      case "D" -> "🟠";
      case "E" -> "🔴";
      default -> throw new IllegalStateException("Unexpected value: " + category);
    };
  }

  private String getCategory(Toit toit) {
    var categoryFromConsumer = toit.category();
    if (categoryFromConsumer == null || categoryFromConsumer.isEmpty()) {
      var globalRate = toit.noteDegradationGlobale();
      if (globalRate < 4) {
        return "A";
      }
      if (globalRate >= 4 && globalRate < 11) {
        return "B";
      }
      if (globalRate >= 11 && globalRate < 21) {
        return "C";
      }
      if (globalRate >= 21 && globalRate < 41) {
        return "D";
      }
      return "E";
    }
    return categoryFromConsumer;
  }

  private String getEtatToiture(Toit toit) {
    var category = getCategory(toit);
    return switch (category) {
      case "A" -> "Bon état, RAS";
      case "B" -> "Entretien à prévoir";
      case "C" -> "Entretien nécessaire";
      case "D" -> "Réparation nécessaire";
      case "E" -> "Intervention urgente";
      default -> throw new IllegalStateException("Unexpected value: " + category);
    };
  }
}
