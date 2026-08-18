package fr.birdia.genai.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.birdia.genai.model.AnalyseurToiture;
import fr.birdia.genai.model.Chat;
import fr.birdia.genai.model.CommentaireCouvreurNormalizer;
import fr.birdia.genai.model.EtatApparent;
import fr.birdia.genai.model.PanToitureDescriber;
import fr.birdia.genai.model.toiture.Measurement;
import fr.birdia.genai.model.toiture.PanInfo;
import fr.birdia.genai.model.toiture.PanToiture;
import fr.birdia.genai.model.toiture.Toit;
import fr.birdia.genai.prompt.PromptEngine;
import java.util.List;
import org.junit.jupiter.api.Test;

class AnalyseurToitureTest {

  private final Chat chat = mock(Chat.class);
  private final AnalyseurToiture subject =
      new AnalyseurToiture(
          chat, new PromptEngine(), new CommentaireCouvreurNormalizer(), new PanToitureDescriber());

  private Toit toit(String etatApparent, String commentaireCouvreur) {
    return new Toit(
        "1 rue de la Toiture",
        "0,0",
        2018,
        2024,
        "type",
        "Tuiles",
        "Ardoises",
        120.0,
        6.5,
        25.0,
        List.of(
            new PanToiture(
                "https://s3/pan-nord-est.jpg",
                "Pan nord-est",
                null,
                null,
                List.of(new Measurement("m²", 60.0, false), new Measurement("°", 25.0, false)),
                List.of(new PanInfo("Orientation", "Nord-Est")))),
        "partielle",
        15.0,
        5.0,
        3.0,
        "vieillissement normal",
        true,
        commentaireCouvreur,
        EtatApparent.fromLibelle(etatApparent),
        3.0,
        "proximité littorale");
  }

  @Test
  void renders_prompt_from_template_and_returns_chat_response() {
    when(chat.apply(contains("Réparation prioritaire"))).thenReturn("<section>ok</section>");

    var report = subject.apply(toit("Réparation prioritaire", "Toiture refaite en 2018"));

    assertTrue(report.contains("ok"));
    verify(chat).apply(contains("🔴"));
    verify(chat).apply(contains("Revêtement principal : Tuiles"));
    verify(chat).apply(contains("Revêtement secondaire éventuel : Ardoises"));
    verify(chat).apply(contains("Pan nord-est (60.0m², 25.0°, Orientation : Nord-Est)"));
    verify(chat).apply(contains("Commentaire du couvreur : \"Toiture refaite en 2018\""));
  }

  @Test
  void treats_blank_or_placeholder_comment_as_absent() {
    when(chat.apply(contains("Toiture en bon état"))).thenReturn("<section>ok</section>");

    subject.apply(toit("Toiture en bon état", "N/A"));

    verify(chat).apply(contains("Commentaire du couvreur : aucun commentaire renseigné"));
  }

  @Test
  void rejects_an_etat_apparent_outside_the_five_birdia_states() {
    assertThrows(IllegalArgumentException.class, () -> subject.apply(toit("Catégorie D", "RAS")));
  }

  @Test
  void strips_markdown_code_fences_from_chat_response() {
    when(chat.apply(contains("Risque critique"))).thenReturn("```html\n<section>ok</section>\n```");

    var report = subject.apply(toit("Risque critique", "RAS"));

    assertTrue(!report.contains("```"));
  }
}
