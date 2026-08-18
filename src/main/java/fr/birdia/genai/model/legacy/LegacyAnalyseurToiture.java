package fr.birdia.genai.model.legacy;

import fr.birdia.genai.model.Chat;
import fr.birdia.genai.prompt.PromptEngine;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Original category A-E report logic, kept unchanged for callers of the legacy contract. */
@Slf4j
@Component
@AllArgsConstructor
public class LegacyAnalyseurToiture implements Function<LegacyToit, String> {

  private static final String PROMPT_TEMPLATE = "analyseur-toiture-legacy";
  private static final String NON_RENSEIGNE = "Non renseigné";

  private final Chat chat;
  private final PromptEngine promptEngine;

  @Override
  public String apply(LegacyToit toit) {

    Instant startOfExecution = Instant.now();
    String aiReport = getAIReport(toit);
    Instant endOfExecution = Instant.now();

    Duration duration = Duration.between(endOfExecution, startOfExecution);
    log.info("Report from AI obtained in {} ms.", duration.toMillis());

    return aiReport;
  }

  private String getAIReport(LegacyToit toit) {
    Map<String, Object> variables = new HashMap<>();
    variables.put("categoryEmoji", getCategoryEmoji(toit));
    variables.put("category", getCategory(toit));
    variables.put("etatToiture", getEtatToiture(toit));
    variables.put("surfaceEnM2", orDefault(toit.surfaceEnM2()));
    variables.put("revetement", orDefault(toit.revetement()));
    variables.put("revetement2", orDefault(toit.revetement2()));
    variables.put("humidite", orDefault(toit.humidité()));
    variables.put("moisissure", orDefault(toit.moisissure()));
    variables.put("usure", orDefault(toit.usure()));
    variables.put("obstacles", orDefault(toit.obstacles()));
    variables.put("fissureCassure", Boolean.TRUE.equals(toit.fissureCassure()) ? "OUI" : "NON");
    variables.put("risqueFeu", Boolean.TRUE.equals(toit.risqueFeu()) ? "OUI" : "NON");
    variables.put("hauteurBatiment", orDefault(toit.hauteurBatiment()));
    variables.put("commentaireCouvreur", orDefault(toit.commentaireCouvreur()));

    var prompt = promptEngine.render(PROMPT_TEMPLATE, variables);
    log.info("AI Prompt : {}", prompt);
    return chat.apply(prompt).replace("```html", "").replace("```", "");
  }

  private String getCategoryEmoji(LegacyToit toit) {
    var category = getCategory(toit);
    return switch (category) {
      case "A" -> "🟢";
      case "B", "C" -> "🟡";
      case "D" -> "🟠";
      case "E" -> "🔴";
      default -> throw new IllegalStateException("Unexpected value: " + category);
    };
  }

  private Object orDefault(Object value) {
    return value == null ? NON_RENSEIGNE : value;
  }

  private String getCategory(LegacyToit toit) {
    var categoryFromConsumer = toit.category();
    if (categoryFromConsumer == null || categoryFromConsumer.isEmpty()) {
      var globalRate = toit.noteDegradationGlobale() == null ? 0d : toit.noteDegradationGlobale();
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

  private String getEtatToiture(LegacyToit toit) {
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
