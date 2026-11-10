package fr.birdia.genai.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.birdia.genai.endpoint.rest.controller.BadRequestAdvice;
import fr.birdia.genai.model.toiture.Toit;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class BadRequestAdviceTest {

  private final BadRequestAdvice advice = new BadRequestAdvice();

  @Test
  void unknown_etat_apparent_is_a_400_naming_the_bad_label() {
    Exception cause =
        assertThrowsOnParse("{\"etatApparent\":\"Intervention nécessaire \",\"adresse\":\"x\"}");

    var response =
        advice.handle(
            new HttpMessageNotReadableException("unreadable", cause, mock(HttpInputMessage.class)));

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody().contains("Unexpected état apparent value"));
  }

  @Test
  void type_mismatch_is_a_400_naming_the_param_and_value() {
    var response =
        advice.handle(
            new MethodArgumentTypeMismatchException(
                "12,5", Double.class, "surfaceEnM2", null, new NumberFormatException()));

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid value for 'surfaceEnM2': 12,5", response.getBody());
  }

  private static Exception assertThrowsOnParse(String json) {
    try {
      new ObjectMapper().readValue(json, Toit.class);
    } catch (Exception e) {
      return e;
    }
    throw new AssertionError("expected parsing to fail");
  }
}
