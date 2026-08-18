package fr.birdia.genai.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.birdia.genai.model.Measurement;
import fr.birdia.genai.model.PanInfo;
import fr.birdia.genai.model.PanToiture;
import fr.birdia.genai.model.PanToitureDescriber;
import java.util.List;
import org.junit.jupiter.api.Test;

class PanToitureDescriberTest {

  private final PanToitureDescriber subject = new PanToitureDescriber();

  @Test
  void describes_visible_measurements_and_infos() {
    var pan =
        new PanToiture(
            "https://s3/pan.jpg",
            "Pan nord",
            null,
            null,
            List.of(new Measurement("m²", 45.0, false), new Measurement("°", 22.0, false)),
            List.of(new PanInfo("Orientation", "Nord")));

    assertEquals("Pan nord (45.0m², 22.0°, Orientation : Nord)", subject.apply(pan));
  }

  @Test
  void hides_invisible_measurements() {
    var pan =
        new PanToiture(
            null,
            "Pan sud",
            null,
            null,
            List.of(new Measurement("m²", 45.0, true), new Measurement("°", 12.0, false)),
            null);

    assertEquals("Pan sud (12.0°)", subject.apply(pan));
  }

  @Test
  void falls_back_to_the_name_when_nothing_else_is_available() {
    var pan = new PanToiture(null, "Pan est", null, null, null, null);

    assertEquals("Pan est", subject.apply(pan));
  }
}
