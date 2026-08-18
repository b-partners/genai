package fr.birdia.genai.model;

import fr.birdia.genai.model.toiture.Toit;
import fr.birdia.genai.prompt.PromptEngine;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
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
  private final CommentaireCouvreurNormalizer commentaireCouvreurNormalizer;
  private final PanToitureDescriber panToitureDescriber;

  @Override
  public String apply(Toit toit) {

    Instant startOfExecution = Instant.now();
    String aiReport = getAIReport(toit);
    Instant endOfExecution = Instant.now();

    Duration duration = Duration.between(startOfExecution, endOfExecution);
    log.info("Report from AI obtained in {} ms.", duration.toMillis());

    return aiReport;
  }

  private String getAIReport(Toit toit) {
    var etatApparent = Objects.requireNonNull(toit.etatApparent(), "État apparent BIRDIA manquant");
    var commentaireCouvreur = commentaireCouvreurNormalizer.apply(toit.commentaireCouvreur());

    var variables = new LinkedHashMap<String, Object>();
    variables.put("etatApparentEmoji", etatApparent.emoji());
    variables.put("etatApparent", etatApparent.libelle());
    variables.put("scoreDegradationVisible", toit.scoreDegradationVisible());
    variables.put("revetement", toit.revetement());
    variables.put("revetement2", toit.revetement2());
    variables.put("surfaceRampantM2", toit.surfaceRampantM2());
    variables.put("hauteurBatiment", toit.hauteurBatiment());
    variables.put("penteDeg", toit.penteDeg());
    variables.put("pansToitureDescriptions", describePans(toit));
    variables.put("niveauUsure", toit.niveauUsure());
    variables.put("tauxUsurePct", toit.tauxUsurePct());
    variables.put("tauxMoisissurePct", toit.tauxMoisissurePct());
    variables.put("tauxHumiditePct", toit.tauxHumiditePct());
    variables.put("mutation", toit.mutation());
    variables.put("risqueVegetationFeu", toit.risqueVegetationFeu());
    variables.put("commentaireCouvreur", commentaireCouvreur);
    variables.put("contexteGeographique", toit.contexteGeographique());

    var prompt = promptEngine.render(PROMPT_TEMPLATE, variables);
    log.info("AI Prompt : {}", prompt);
    return chat.apply(prompt).replace("```html", "").replace("```", "");
  }

  private List<String> describePans(Toit toit) {
    if (toit.pansToiture3d() == null) {
      return null;
    }
    return toit.pansToiture3d().stream().map(panToitureDescriber).toList();
  }
}
