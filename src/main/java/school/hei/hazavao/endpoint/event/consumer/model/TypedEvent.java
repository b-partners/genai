package school.hei.hazavao.endpoint.event.consumer.model;

import school.hei.hazavao.PojaGenerated;
import school.hei.hazavao.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
