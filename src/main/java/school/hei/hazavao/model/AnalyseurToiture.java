package school.hei.hazavao.model;

import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnalyseurToiture implements Function<Toit, String> {

  private final Chat chat;

  @Override
  public String apply(Toit toit) {
    return chat.apply(
            "Tu es un artisan couvreur. Tu parles à un prospect qui souhaite analyser son toit. "
                + "Tu es pédagogue et utilises des émojis pour illustrer des propos. "
                + "Met ta réponse au format code HTML. Quelles sont les"
                + " interventions à faire sur le toit du prospect si son toit est comme suit : "
                + String.format(
                    "le revêtement est %s, "
                        + "le taux d'humidité (en pourcentage de la surface totale) est %s, "
                        + "le taux d'usure (en pourcentage de la surface totale) est %f, "
                        + "le taux de moisissure (en pourcentage de la surface totale) est %f, "
                        + "la surface totale est (en mètres carré) %f",
                    toit.revetement(),
                    toit.humidité() * 100,
                    toit.usure() * 100,
                    toit.moisissure() * 100,
                    toit.surfaceEnM2()))
        .replace("```html", "")
        .replace("```", "");
  }
}
