package fr.birdia.genai.endpoint.rest.controller;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

import fr.birdia.genai.model.AnalyseurToiture;
import fr.birdia.genai.model.Toit;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RoofController {

  private final AnalyseurToiture analyseurToiture;

  @PostMapping(value = "/toiture", produces = TEXT_HTML_VALUE)
  public String hazavao(@RequestBody Toit toit) {
    return analyseurToiture.apply(toit);
  }
}
