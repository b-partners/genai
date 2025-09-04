package fr.birdia.genai.endpoint.rest.controller;

import fr.birdia.genai.model.Hazavao;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class HazavaoController {

  private final Hazavao hazavao;

  @GetMapping("/hazavao")
  public String hazavao(@RequestParam String teny) {
    return hazavao.apply(teny);
  }
}
