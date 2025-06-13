package school.hei.hazavao.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class HazavaoTest {
  Hazavao hazavao = new Hazavao();

  @Disabled
  @Test
  void define_salama() {
    var toDefine = "salama";

    var definition = hazavao.apply(toDefine);

    assertTrue(definition.toLowerCase().contains(toDefine));
    assertTrue(definition.length() > toDefine.length());
  }
}
