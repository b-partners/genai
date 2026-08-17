package fr.birdia.genai.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.birdia.genai.model.AnalyseurToiture;
import fr.birdia.genai.model.Chat;
import fr.birdia.genai.model.Toit;
import fr.birdia.genai.prompt.PromptEngine;
import org.junit.jupiter.api.Test;

class AnalyseurToitureTest {

  private final Chat chat = mock(Chat.class);
  private final AnalyseurToiture subject = new AnalyseurToiture(chat, new PromptEngine());

  private Toit toit(String commentaireCouvreur) {
    return new Toit(
        "1 rue de la Toiture",
        "0,0",
        2018,
        2024,
        120.0,
        "type",
        5.0,
        20.0,
        "Tuiles",
        15.0,
        5.0,
        3.0,
        "cheminée",
        "vieillissement normal",
        false,
        true,
        3.0,
        null,
        "Ardoises",
        6.5,
        commentaireCouvreur);
  }

  @Test
  void renders_prompt_from_template_and_returns_chat_response() {
    when(chat.apply(contains("CATÉGORIE A"))).thenReturn("<section>ok</section>");

    var report = subject.apply(toit("Toiture refaite en 2018"));

    assertTrue(report.contains("ok"));
    verify(chat)
        .apply(contains("L’analyse couvre 120.0 m². Revêtement : Tuiles. Revêtement 2 : Ardoises"));
    verify(chat).apply(contains("Commentaire couvreur : \"Toiture refaite en 2018 \""));
    verify(chat).apply(contains("fissures = \"NON\" ; risque feu = \"OUI\""));
  }

  @Test
  void strips_markdown_code_fences_from_chat_response() {
    when(chat.apply(contains("CATÉGORIE"))).thenReturn("```html\n<section>ok</section>\n```");

    var report = subject.apply(toit("RAS"));

    assertTrue(!report.contains("```"));
  }
}
