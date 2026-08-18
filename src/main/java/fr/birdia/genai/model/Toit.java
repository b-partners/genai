package fr.birdia.genai.model;

import java.util.List;

public record Toit(
    String adresse,
    String gps,
    Integer millesimeImage1,
    Integer millesimeImage2,
    String typeToiture,
    String revetement,
    String revetement2,
    Double surfaceRampantM2,
    Double hauteurBatiment,
    Double penteDeg,
    List<PanToiture> pansToiture3d,
    String niveauUsure,
    Double tauxUsurePct,
    Double tauxMoisissurePct,
    Double tauxHumiditePct,
    String mutation,
    Boolean risqueVegetationFeu,
    String commentaireCouvreur,
    String etatApparent,
    Double scoreDegradationVisible,
    String contexteGeographique) {}
