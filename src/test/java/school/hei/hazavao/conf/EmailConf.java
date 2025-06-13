package school.hei.hazavao.conf;

import org.springframework.test.context.DynamicPropertyRegistry;
import school.hei.hazavao.PojaGenerated;

@PojaGenerated
public class EmailConf {

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("aws.ses.source", () -> "dummy-ses-source");
  }
}
