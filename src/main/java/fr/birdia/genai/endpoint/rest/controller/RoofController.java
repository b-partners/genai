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
      @RequestParam String revetement,
      double usure,
      double humidité,
      double moisissure,
      double surfaceEnM2) {
    var analyse =
        analyseurToiture.apply(new Toit(revetement, usure, humidité, moisissure, surfaceEnM2));
    return analyse;
  }
}
