package fr.birdia.genai.endpoint.rest.controller;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

import fr.birdia.genai.model.AnalyseurToiture;
import fr.birdia.genai.model.Toit;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RoofController {

  private final AnalyseurToiture analyseurToiture;

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
      @RequestParam(required = false) String categorie) {

    return analyseurToiture.apply(
        new Toit(
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
            categorie));
  }
}
