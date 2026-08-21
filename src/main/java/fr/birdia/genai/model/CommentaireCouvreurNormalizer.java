package fr.birdia.genai.model;

import java.util.Set;
import java.util.function.Function;
import org.springframework.stereotype.Component;

/**
 * Normalizes the roofer's free-text comment so that the report only treats it as "provided" when it
 * actually carries usable information.
 */
@Component
public class CommentaireCouvreurNormalizer implements Function<String, String> {

  private static final Set<String> VALEURS_ABSENTES = Set.of("null", "n/a", "na");

  @Override
  public String apply(String commentaireCouvreur) {
    if (commentaireCouvreur == null) {
      return null;
    }
    var trimmed = commentaireCouvreur.trim();
    if (trimmed.isEmpty() || VALEURS_ABSENTES.contains(trimmed.toLowerCase())) {
      return null;
    }
    return trimmed;
  }
}
