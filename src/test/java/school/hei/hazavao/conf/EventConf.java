package school.hei.hazavao.conf;

import org.springframework.test.context.DynamicPropertyRegistry;
import school.hei.hazavao.PojaGenerated;

@PojaGenerated
public class EventConf {

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("aws.region", () -> "dummy-region");
    registry.add("aws.sqs.queue.url", () -> "dummy-queue-url");
    registry.add("aws.eventBridge.bus", () -> "dummy-bus-url");
  }
}
