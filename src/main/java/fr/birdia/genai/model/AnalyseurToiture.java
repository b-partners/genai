package fr.birdia.genai.model;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AnalyseurToiture implements Function<Toit, String> {

  private final Chat chat;

  @Override
  public String apply(Toit toit) {

    Instant startOfExecution = Instant.now();
    String aiReport = getAIReport(toit);
    Instant endOfExecution = Instant.now();

    Duration duration = Duration.between(endOfExecution, startOfExecution);
    log.info("Report from AI obtained in {} ms.", duration.toMillis());

    return aiReport;
  }

  private String getAIReport(Toit toit) {
    return chat.apply(
            """
Tu es un artisan couvreur expérimenté.
Tu t’adresses à un propriétaire : ton ton est clair, pédagogique et rassurant.
Utilise quelques emojis pertinents (sans en abuser)."""
                .concat(
                    String.format(
                        """
➤ Données d’entrée (variables) :
• Millésimes d’images : {{mille_1}} = %s ; {{mille_2}} = %s
• Surface totale (m²) : {{surface_totale}} = %s
• Type de toiture (nb de pans / terrasse) : {{type_toiture}} = %s
• Pente (°) : {{pente_min}}–{{pente_max}} = %s-%s
• Revêtements (liste) : {{revetements}} = %s
• Taux d’humidité (%%) : {{taux_humidite}} = %s
• Taux de moisissure (%%) : {{taux_moisissure}} = %s
• Taux d’usure (%%) : {{taux_usure}} = %s
• Obstacles/détails (velux, cheminées, PV, équipements) : {{obstacles}} = %s
• Mutations (ex : rénovation, dégradation) : {{mutation}} = %s
• Fissures/cassures : {{fissures}} = %s
• Risque feu : {{risque_feu}} = %s

""",
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
                        toit.risqueFeu() ? "OUI" : "NON"))
                .concat(
                    """
➤ Règles de sortie :
1) Rends UNIQUEMENT du HTML (aucun texte hors balises).
2) Structure à respecter :
   <h2>COMPRENDRE VOTRE RAPPORT</h2>
   <p>(catégorie A–E calculée à partir de la note_degradation : A <8 ; B 8–20 ; C 20–30 ; D 30–40 ; E >40). Affiche la pastille couleur et l’intitulé exact.</p>

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
• 300–400 mots au total (les deux sections cumulées).
• Pas de jargon inutile ; explications concrètes et actionnables.
• N’affiche pas les variables brutes ; intègre-les naturellement dans le texte.
• N’ajoute pas de disclaimer juridique.

Produis maintenant le rapport en HTML selon ces consignes, à partir des variables fournies."""))
        .replace("```html", "")
        .replace("```", "");
  }
}
