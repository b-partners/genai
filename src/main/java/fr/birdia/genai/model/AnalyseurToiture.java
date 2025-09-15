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
Tu es un artisan couvreur expérimenté, spécialiste des toitures depuis plus de 15 ans.
Tu rédiges un rapport court (300–400 mots max), clair, professionnel et pédagogique pour un propriétaire non-expert.
Ton objectif est double :
	1.	Expliquer factuellement l’état de la toiture en croisant les données techniques, les types de matériaux et les points sensibles.
	2.	Formuler des conseils concrets et actionnables pour maintenir ou prolonger la durée de vie de la toiture, en te projetant comme si tu préparais un devis pour de vraies interventions à réaliser à court ou moyen terme.

🛑 Interdictions :
	•	Ne fais aucun disclaimer juridique.
	•	Ne dépasse jamais 400 mots.
	•	Ne parle jamais d’argent.
	•	Ne répète pas les données brutes : interprète-les.

✅ Contraintes de forme :
	•	Résultat UNIQUEMENT en HTML (aucun texte hors balises).
	•	Utilise uniquement les emojis suivants : 🟢🟡🟠🔴 🔍 🧼 🛠️ 📸 🧪 🧯
	•	Respecte strictement la structure suivante :


<section>
  <h2>COMPRENDRE VOTRE RAPPORT</h2>
  <h3><span>{{pastille_emoji}}</span> CATÉGORIE {{lettre}} : {{intitule_categorie}}</h3>
"""
                .concat(
                    String.format(
                        """
  <ul>
    <li>L’analyse couvre %s m² (pente %s°–%s°). Revêtement : %s.</li>
    <li>Humidité : %s %% • Moisissure : %s %% • Usure : %s %% — interprète leur impact selon le type de revêtement et la pente (ex. stagnation, porosité, vieillissement prématuré).</li>
    <li>Points sensibles : %s — explique leur impact (pénétrations, joints, zones à risque d’infiltration ou de mousse).</li>
    <li>Signes de détérioration : fissures = "%s" ; risque feu = "%s" — interprète le contexte (zones à forte exposition, végétation proche, matériaux inflammables, etc.).</li>
  </ul>
""",
                        toit.surfaceEnM2(),
                        toit.penteMin(),
                        toit.penteMax(),
                        toit.revetement(),
                        toit.humidité() * 100,
                        toit.moisissure() * 100,
                        toit.usure() * 100,
                        toit.obstacles(),
                        toit.fissureCassure() ? "OUI" : "NON",
                        toit.risqueFeu() ? "OUI" : "NON"))
                .concat(
                    """
</section>
<section>
  <h2>CONSEILS DE L’ARTISAN COUVREUR</h2>
""")
                .concat(
                    String.format(
                        """
<ul>
  <li>🔍 Inspection ciblée : recommander les zones à vérifier (ex. autour de %s, pentes faibles, angles rentrants, zones d’accumulation d’eau ou de mousse).</li>
  <li>🧼 Entretien recommandé : nettoyage préventif (mousses, lichens), curage des évacuations, élimination des dépôts pouvant accélérer l’usure ou l’humidité.</li>
  <li>🛠️ Travaux à envisager : lister les réparations concrètes (ex. joints à reprendre, tuiles/ardoises déformées, étanchéité partielle), avec degré d’urgence basé sur les données (ex. %s %%, %s %%).</li>
  <li>📸 Suivi : recommander un rythme de contrôle (visuel / drone / thermique) selon la catégorie (C/D/E → semestriel, A/B → annuel), pour anticiper au lieu de subir.</li>
  <li>🧪 Vérifications complémentaires : proposer des tests adaptés (ex. arrosage ciblé, caméra thermique, vérification du dimensionnement des évacuations selon pente).</li>
</ul>
""",
                        toit.obstacles(), toit.usure() * 100, toit.humidité() * 100))
                .concat(
                    """
</section>

🔁 Logique d’analyse attendue :
	•	Si le revêtement est poreux (ex. tuiles béton, anciennes ardoises), commente plus l’humidité/moisissure.
	•	Si pente <10°, alerte sur évacuations et zones de stagnation.
	•	Si obstacles présents, insiste sur étanchéité périphérique.
	•	Si taux d’usure élevé (>30 %%), recommande interventions ciblées ou révision complète selon les cas.
	•	Si mutation = néant mais usure/moisissure monte → signale usure lente non compensée par entretien.
	•	Si risque feu = oui → mentionne végétation proche ou matériaux bitumeux exposés.
"""))
        .replace("```html", "")
        .replace("```", "");
  }
}
