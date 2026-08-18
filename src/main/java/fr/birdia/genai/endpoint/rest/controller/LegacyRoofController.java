package fr.birdia.genai.endpoint.rest.controller;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

import fr.birdia.genai.model.legacy.LegacyAnalyseurToiture;
import fr.birdia.genai.model.legacy.LegacyToit;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Legacy roof report contract (category A-E, flat query params), kept unchanged for callers that
 * have not migrated to the new BIRDIA état apparent contract served by {@link RoofController}.
 */
@RestController
@AllArgsConstructor
public class LegacyRoofController {

  private final LegacyAnalyseurToiture legacyAnalyseurToiture;

  @GetMapping(value = "/toiture", produces = TEXT_HTML_VALUE)
  public String hazavao(
      @RequestParam(required = false) String adresse,
      @RequestParam(required = false) String gps,
      @RequestParam(required = false) Integer millesimeImage1,
      @RequestParam(required = false) Integer millesimeImage2,
      Double surfaceEnM2,
      @RequestParam(required = false) String typeToiture,
      @RequestParam(required = false) Double penteMin,
      @RequestParam(required = false) Double penteMax,
      String revetement,
      Double humidité,
      Double moisissure,
      Double usure,
      @RequestParam(required = false) String obstacles,
      @RequestParam(required = false) String mutation,
      @RequestParam(required = false) Boolean fissureCassure,
      @RequestParam(required = false) Boolean risqueFeu,
      @RequestParam(required = false) Double noteDegradationGlobale,
      @RequestParam(required = false) String categorie,
      @RequestParam(required = false) String revetement2,
      @RequestParam(required = false) Double hauteurBatiment,
      @RequestParam(required = false) String commentaireCouvreur) {

    return legacyAnalyseurToiture.apply(
        new LegacyToit(
            adresse,
            gps,
            millesimeImage1,
            millesimeImage2,
            surfaceEnM2,
            typeToiture,
            penteMin,
            penteMax,
            revetement,
            humidité,
            moisissure,
            usure,
            obstacles,
            mutation,
            fissureCassure,
            risqueFeu,
            noteDegradationGlobale,
            categorie,
            revetement2,
            hauteurBatiment,
            commentaireCouvreur));
  }
}
