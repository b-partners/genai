package fr.birdia.genai.endpoint.event.consumer.model;

import fr.birdia.genai.PojaGenerated;
import fr.birdia.genai.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
