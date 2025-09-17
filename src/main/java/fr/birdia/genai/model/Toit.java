package fr.birdia.genai.model;

public record Toit(
    String adresse,
    String gps,
    Integer millesimeImage1,
    Integer millesimeImage2,
    Double surfaceEnM2,
    String typeToiture,
    Double penteMin,
    Double penteMax,
    String revetement,
    Double humidité,
    Double moisissure,
    Double usure,
    String obstacles,
    String mutation,
    Boolean fissureCassure,
    Boolean risqueFeu,
    Double noteDegradationGlobale) {}
