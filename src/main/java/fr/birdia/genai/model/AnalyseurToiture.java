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
	•	Respecte strictement la structure suivante, de telle sorte que les blocs INSTRUCTION soient remplacés par l’instruction donnée:


<section>
  <h2>COMPRENDRE VOTRE RAPPORT</h2>
"""
                .concat(
                    String.format(
                        """
                        <h3><span>%s</span> CATÉGORIE %s : %s</h3>""",
                        getCategoryEmoji(toit), getCategory(toit), getEtatToiture(toit)))
                .concat(
                    String.format(
                        """
    INSTRUCTION
    !IMPORTANT! Cette instruction n'est pas à afficher.

    Voici les donnée à utiliser:

    L’analyse couvre %s m². Revêtement : %s.
    Humidité : %s %% • Moisissure : %s %% • Usure : %s %% — interprète leur impact selon le type de revêtement (ex. stagnation, porosité, vieillissement prématuré).
    Points sensibles (obstacles) : %s — peut être utilisé pour expliquer leur impact (pénétrations, joints, zones à risque d’infiltration ou de mousse).
    Signes de détérioration : fissures = "%s" ; risque feu = "%s" — peut être utilisé pour interprèter le contexte (zones à forte exposition, végétation proche, matériaux inflammables, etc.).
    FIN_INSTRUCTION

    <ul>
      <li>INSTRUCTION: en commençant par une phrase similaire à "L'analyse a montré que", fais une analyse générale en montrant le constat, la cause et la conséquence en utilisant les données ci-dessus. FIN_INSTRUCTION</li>
    </ul>

    <ul>
      <li>INSTRUCTION: fais une analyse des données concernant le type de toiture, l'humidité et le taux d'usure, en expliquant leur impact. FIN_INSTRUCTION</li>
    </ul>
""",
                        toit.surfaceEnM2(),
                        toit.revetement(),
                        toit.humidité(),
                        toit.moisissure(),
                        toit.usure(),
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
  <li>🔍 Inspection ciblée : INSTRUCTION: recommander les zones à vérifier (ex. autour de %s, angles rentrants, zones d’accumulation d’eau ou de mousse). FIN_INSTRUCTION</li>
  <li>🧼 Entretien recommandé : INSTRUCTION: nettoyage préventif (mousses, lichens), curage des évacuations, élimination des dépôts pouvant accélérer l’usure ou l’humidité. FIN_INSTRUCTION</li>
  <li>🛠️ Travaux à envisager : INSTRUCTION: lister les réparations concrètes (ex. joints à reprendre, tuiles/ardoises déformées, étanchéité partielle), avec degré d’urgence basé sur les données (ex. %s %%, %s %%).FIN_INSTRUCTION</li>
  <li>📸 Suivi : INSTRUCTION: recommander un rythme de contrôle (visuel / drone / thermique) selon la catégorie (C/D/E → semestriel, A/B → annuel), pour anticiper au lieu de subir. FIN_INSTRUCTION</li>
  <li>🧪 Vérifications complémentaires : INSTRUCTION: proposer des tests adaptés (ex. arrosage ciblé, caméra thermique). FIN_INSTRUCTION</li>
</ul>
""",
                        toit.obstacles(), toit.usure(), toit.humidité()))
                .concat(
                    """
</section>

INSTRUCTION
🔁 Logique d’analyse attendue :
	•	Si le revêtement est poreux (ex. tuiles béton, anciennes ardoises), commente plus l’humidité/moisissure.
	•	Si obstacles présents, insiste sur étanchéité périphérique.
	•	Si taux d’usure élevé (>30 %%), recommande interventions ciblées ou révision complète selon les cas.
	•	Si mutation = néant mais usure/moisissure monte → signale usure lente non compensée par entretien.
	•	Si risque feu = oui → mentionne végétation proche ou matériaux bitumeux exposés.
FIN_INSTRUCTION
"""))
        .replace("```html", "")
        .replace("```", "");
  }

  private String getCategoryEmoji(Toit toit) {
    var category = getCategory(toit);
    return switch (category) {
      case "A" -> "🟢";
      case "B", "C" -> "🟡";
      case "D" -> "🟠";
      case "E" -> "🔴";
      default -> throw new IllegalStateException("Unexpected value: " + category);
    };
  }

  private String getCategory(Toit toit) {
    var categoryFromConsumer = toit.category();
    if (categoryFromConsumer == null || categoryFromConsumer.isEmpty()) {
      var globalRate = toit.noteDegradationGlobale();
      if (globalRate < 8) {
        return "A";
      }
      if (globalRate >= 8 && globalRate < 20) {
        return "B";
      }
      if (globalRate >= 20 && globalRate < 30) {
        return "C";
      }
      if (globalRate >= 30 && globalRate < 40) {
        return "D";
      }
      return "E";
    }
    return categoryFromConsumer;
  }

  private String getEtatToiture(Toit toit) {
    var category = getCategory(toit);
    return switch (category) {
      case "A" -> "Excellent état général";
      case "B" -> "Bon état avec légères réparations à prévoir";
      case "C" -> "État moyen, entretien recommandé rapidement";
      case "D" -> "Mauvais état, réparations importantes nécessaires";
      case "E" -> "Très mauvais état, rénovation complète recommandée";
      default -> throw new IllegalStateException("Unexpected value: " + category);
    };
  }
}
