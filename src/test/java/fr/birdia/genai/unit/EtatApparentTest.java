package fr.birdia.genai.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import fr.birdia.genai.model.EtatApparent;
import org.junit.jupiter.api.Test;

class EtatApparentTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void parses_the_exact_birdia_labels() {
    assertEquals(EtatApparent.RISQUE_CRITIQUE, EtatApparent.fromLibelle("Risque critique"));
  }

  @Test
  void rejects_a_label_outside_the_five_birdia_states() {
    assertThrows(IllegalArgumentException.class, () -> EtatApparent.fromLibelle("Catégorie D"));
  }

  @Test
  void deserializes_from_its_json_label() throws Exception {
    var etatApparent = objectMapper.readValue("\"Entretien préventif\"", EtatApparent.class);

    assertEquals(EtatApparent.ENTRETIEN_PREVENTIF, etatApparent);
  }

  @Test
  void fails_to_deserialize_an_unknown_label() {
    assertThrows(
        ValueInstantiationException.class,
        () -> objectMapper.readValue("\"Catégorie D\"", EtatApparent.class));
  }

  @Test
  void serializes_back_to_its_json_label() throws Exception {
    assertEquals(
        "\"Risque critique\"", objectMapper.writeValueAsString(EtatApparent.RISQUE_CRITIQUE));
  }
}
