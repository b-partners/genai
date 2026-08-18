package fr.birdia.genai.model;

import java.util.Arrays;

/**
 * The five apparent roof states computed upstream by BIRDIA. The LLM report only explains why the
 * observations are consistent with the transmitted state ; it never recomputes or invents one.
 */
public enum EtatApparent {
  BON_ETAT("Toiture en bon état", "🟢"),
  ENTRETIEN_PREVENTIF("Entretien préventif", "🟡"),
  INTERVENTION_NECESSAIRE("Intervention nécessaire", "🟠"),
  REPARATION_PRIORITAIRE("Réparation prioritaire", "🔴"),
  RISQUE_CRITIQUE("Risque critique", "🔴");

  private final String libelle;
  private final String emoji;

  EtatApparent(String libelle, String emoji) {
    this.libelle = libelle;
    this.emoji = emoji;
  }

  public String libelle() {
    return libelle;
  }

  public String emoji() {
    return emoji;
  }

  public static EtatApparent fromLibelle(String libelle) {
    return Arrays.stream(values())
        .filter(etat -> etat.libelle.equalsIgnoreCase(libelle))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Unexpected état apparent value: \"" + libelle + "\""));
  }
}
