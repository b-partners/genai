package fr.birdia.genai.model;

import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnalyseurToiture implements Function<Toit, String> {

  private final Chat chat;

  @Override
  public String apply(Toit toit) {
    return chat.apply(
            "Tu es un artisan couvreur expérimenté. Tu t’adresses à un propriétaire : ton ton est clair, pédagogique et rassurant. Utilise quelques emojis pertinents (sans en abuser).s\n"
                .concat(
                    String.format(
                        """
➤ Données d’entrée (variables) :
•Adresse : {{adresse}} dont la valeur est %s
•Coordonnées GPS : {{gps}} dont la valeur est %s
•Millésimes d’images : {{mille_1}} ; {{mille_2}} dont les valeurs sont respectivement %s et %s.
•Surface totale (m²) : {{surface_totale}} dont la valeur est %s.
•Type de toiture (nb de pans / terrasse) : {{type_toiture}} dont la valeur est %s.
•Pente (°) : {{pente_min}}–{{pente_max}} dont les valeurs sont respectivement %s et %s.
•Revêtements (liste) : {{revetements}} dont la valeur est %s.
•Taux d’humidité (%) : {{taux_humidite}} dont la valeur est %s.
•Taux de moisissure (%) : {{taux_moisissure}} dont la valeur est %s.
•Taux d’usure (%) : {{taux_usure}} dont la valeur est %s.
•Obstacles/détails (velux, cheminées, PV, équipements) : {{obstacles}} dont la valeur est %s.
•Mutations (ex : rénovation, dégradation) : {{mutation}} dont la valeur est %s.
•Fissures/cassures : {{fissures}} dont la valeur est %s.
•Risque feu : {{risque_feu}} dont la valeur est %s.
•Note de dégradation globale (%) : {{note_degradation}} dont la valeur est %s.
""",
                        toit.adresse(),
                        toit.gps(),
                        toit.millesimeImage1(),
                        toit.millesimeImage2(),
                        toit.surfaceEnM2(),
                        toit.typeToiture(),
                        toit.penteMin(),
                        toit.penteMax(),
                        toit.revetement(),
                        toit.humidité(),
                        toit.moisissure(),
                        toit.usure(),
                        toit.obstacles(),
                        toit.mutation(),
                        toit.fissureCassure() ? "OUI" : "NON",
                        toit.risqueFeu() ? "OUI" : "NON",
                        toit.noteDegradationGlobale()))
                .concat(
                    """

➤ Règles de sortie :
1) Rends UNIQUEMENT du HTML (aucun texte hors balises).
2) Structure à respecter :
   <h2>COMPRENDRE VOTRE RAPPORT</h2>
   <p>(catégorie A–E calculée à partir de la {{note_degradation}} : A <8 ; B 8–20 ; C 20–30 ; D 30–40 ; E >40). Affiche la pastille couleur et l’intitulé exact.</p>

   <h3>Analyse des résultats</h3>
   <p>Analyse factuelle (300–400 mots au total pour le document, cette section inclut des chiffres clés) :\s
   – Décris l’état des revêtements {{revetements}} sur {{surface_totale}} m² et la pente {{pente_min}}–{{pente_max}}°.\s
   – Interprète les indicateurs : humidité {{taux_humidite}} %, moisissure {{taux_moisissure}} %, usure {{taux_usure}} %.\s
   – Mentionne les obstacles {{obstacles}} et leurs effets (écoulement, points singuliers).\s
   – Compare {{mille_1}} vs {{mille_2}} si {{mutation}} ≠ “néant”.\s
   – Si humidité >25 % ⇒ évoque stagnation/écoulement ; si moisissure >20 % ⇒ évoque porosité/entretien ; si usure >30 % ⇒ évoque perte d’étanchéité ; si pente <5° ⇒ vigilance sur ruissellement ; si fissures = oui ⇒ alerte infiltration ; sinon souligne les points positifs.
   – Cappe toute valeur aberrante >100 % à 100 % dans le texte.</p>

   <h3>Conseils de l’expert</h3>
   <ul>
     <li>🔍 Inspection ciblée (où et pourquoi, en citant les zones et causes probables).</li>
     <li>🧼 Entretien/Nettoyage (mousses, chéneaux, crapaudines, évacuations).</li>
     <li>🛠️ Travaux correctifs (priorisés selon le risque et la catégorie A–E ; indiquer court/moyen terme).</li>
     <li>📆 Suivi (fréquence de contrôle par imagerie et/ou visite, selon saison et exposition).</li>
     <li>⚡ Option énergie (si PV possible : état du champ libre, ombrages, prérequis étanchéité).</li>
   </ul>

3) Style :
•300–400 mots au total (les deux sections cumulées).
•Pas de jargon inutile ; explications concrètes et actionnables.
•N’affiche pas les variables brutes ; intègre-les naturellement dans le texte.
•N’ajoute pas de disclaimer juridique.

Produis maintenant le rapport en HTML selon ces consignes, à partir des variables fournies."""))
        .replace("```html", "")
        .replace("```", "");
  }
}
