package fr.birdia.genai.model;

import fr.birdia.genai.model.toiture.PanToiture;
import java.util.ArrayList;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class PanToitureDescriber implements Function<PanToiture, String> {

  @Override
  public String apply(PanToiture pan) {
    var details = new ArrayList<String>();
    if (pan.measurements() != null) {
      pan.measurements().stream()
          .filter(measurement -> !Boolean.TRUE.equals(measurement.isInvisible()))
          .forEach(measurement -> details.add(measurement.value() + measurement.unit()));
    }
    if (pan.infos() != null) {
      pan.infos().forEach(info -> details.add(info.label() + " : " + info.value()));
    }
    return details.isEmpty() ? pan.name() : pan.name() + " (" + String.join(", ", details) + ")";
  }
}
