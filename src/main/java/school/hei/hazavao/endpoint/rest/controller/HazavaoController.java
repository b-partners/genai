package school.hei.hazavao.endpoint.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.hei.hazavao.model.Hazavao;

@RestController
public class HazavaoController {

  private final Hazavao hazavao = new Hazavao();

  @GetMapping("/hazavao")
  public String hazavao(@RequestParam String teny) {
    return hazavao.apply(teny);
  }
}
