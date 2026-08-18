package fr.birdia.genai.model.legacy;

/** Original flat roof payload (category A-E), kept for callers that have not migrated yet. */
public record LegacyToit(
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
    Double noteDegradationGlobale,
    String category,
    String revetement2,
    Double hauteurBatiment,
    String commentaireCouvreur) {}
