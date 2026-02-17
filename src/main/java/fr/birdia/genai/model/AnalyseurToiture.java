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
    var prompt = """
            Tu es un artisan couvreur expérimenté, spécialiste des toitures depuis plus de 15 ans.
            
            Tu viens de lancer une détection sur une image HD qui te donne les informations suivantes :
            
            1. Le revêtement ou les revêtements de la toiture : Revêtement 1 ou Revêtement 2 (tuiles, ardoises, etc.).
            2. Hauteur du bâtiment en mètres.
            3. Pente globale en degrés (°).
            4. Niveau d’usure : minime, partielle, avancée, extrême.
            5. Taux d’usure du revêtement (0 à 100 %), représentant la superficie concernée par rapport à l’ensemble du toit.
            6. Taux de moisissure du revêtement (0 à 100 %), représentant la superficie concernée par rapport à l’ensemble du toit.
            7. Taux d’humidité, d’eau stagnante ou de porosité du revêtement (0 à 100 %), représentant la superficie concernée par rapport à l’ensemble du toit.
            8. Mutation / évolution du bâtiment dans le temps entre l’image HD et une image très récente : toiture potentiellement réparée, vieillissement normal ou dégradation en cours.
            9. Présence d’obstacles (cheminée, Velux, panneaux solaires, etc.) pouvant augmenter les risques de défaut d’étanchéité, de jointure ou d’infiltration.
            10. Risque végétation / feu : végétation autour du bâtiment pouvant obstruer les évacuations (notamment après l’automne) et/ou générer un risque incendie en été, impliquant un besoin d’élagage.
            11. Un espace “Commentaire” : champ libre renseigné par le couvreur selon son expertise terrain (matériaux, état réel, contraintes spécifiques, etc.).
            
            ⚠️ Le commentaire du couvreur est prioritaire sur l’analyse issue de l’IA BIRDIA. Le rapport doit donc s’appuyer d’abord sur son expertise, puis relier et ajuster l’analyse en cohérence avec les données de détection.
            
            Tu rédiges un rapport court (300–400 mots max), clair, professionnel et pédagogique pour un propriétaire non-expert.
            Ton objectif est double :
            	1.	Expliquer factuellement l’état de la toiture en croisant les données techniques, les types de matériaux et les points sensibles.
            	2.	Formuler des conseils concrets et actionnables pour maintenir ou prolonger la durée de vie de la toiture, comme si tu préparais un devis de vraies interventions à court ou moyen terme.
            
            🛑 Interdictions :
              • Ne fais aucun disclaimer juridique sauf si le couvreur te le demande dans le commentaire
              • Ne dépasse jamais 400 mots.
              • Ne parle jamais d’argent ni de prix sauf si le couvreur te le demande dans le commentaire
              • Ne répète pas les données brutes telles quelles : interprète-les (expliquer le pourquoi et le risque plutôt que lister des chiffres).
            
            ✅ Contraintes de forme :
            	•	Résultat UNIQUEMENT en HTML (aucun texte hors balises).
            	•	Utilise uniquement les emojis suivants : 🟢🟡🟠🔴 🔍 🧼 🛠️ 📸 🧪 🧯
            	•	Utilise la police suivante en priorité : Kumbh Sans
            	•	Respecte strictement la structure suivante, de telle sorte que les blocs INSTRUCTION soient remplacés par l’instruction donnée:
            
            👉 Le commentaire du couvreur est prioritaire : en cas de contradiction entre la détection
            automatique et son commentaire, tu suis le commentaire et tu t’en sers pour
            interpréter/nuancer les données de BIRDIA
            
            <head>
              <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans:wght@400;700&display=swap" rel="stylesheet">
              <style>
                body {
                  font-family: 'Kumbh Sans', sans-serif;
                }
              </style>
            </head>
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
                                    
                                        L’analyse couvre %s m². Revêtement : %s. Revêtement 2 : %s
                                        Humidité : %s %% • Moisissure : %s %% • Usure : %s %% — interprète leur impact selon le type de revêtement (ex. stagnation, porosité, vieillissement prématuré).
                                        Points sensibles (obstacles) : %s — peut être utilisé pour expliquer leur impact (pénétrations, joints, zones à risque d’infiltration ou de mousse).
                                        Signes de détérioration : fissures = "%s" ; risque feu = "%s" — peut être utilisé pour interprèter le contexte (zones à forte exposition, végétation proche, matériaux inflammables, etc.).
                                        Hauteur Bâtiment: %s .
                                        Commentaire couvreur : "%s " .
                                    
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
                            toit.revetement2(),
                            toit.humidité(),
                            toit.moisissure(),
                            toit.usure(),
                            toit.obstacles(),
                            toit.fissureCassure() ? "OUI" : "NON",
                            toit.risqueFeu() ? "OUI" : "NON",
                            toit.hauteurBatiment(),
                            toit.commentaireCouvreur()))
            .concat(
                    """
                            </section>
                            <section>
                              <h2>CONSEILS DE L’ARTISAN COUVREUR</h2>
                            """)
            .concat(
                """
                            <ul>
                              <li>🔍 Inspection ciblée : INSTRUCTION: recommander précisément les zones à vérifier en priorité (ex. autour des obstacles s’ils sont à true, angles rentrants, zones d’accumulation d’eau ou de mousse, rives, noues, sorties de ventilation). FIN_INSTRUCTION</li>
                              <li>🧼 Entretien recommandé : INSTRUCTION: proposer un entretien adapté au revêtement et aux pathologies dominantes (démoussage doux, nettoyage des lichens, curage des gouttières et descentes, suppression des dépôts qui gardent l’humidité). FIN_INSTRUCTION</li>
                              <li>🛠️ Travaux à envisager : INSTRUCTION: lister les réparations concrètes en fonction de la gravité (note globale et niveaux d’humidité/moisissure/usure) : remplacement de tuiles ou ardoises dégradées, reprise de joints, renforcement d’étanchéité locale, contrôle de la sous-toiture. Indiquer si c’est à court terme (urgent) ou à moyen terme (à programmer).FIN_INSTRUCTION</li>
                              <li>📸 Suivi : INSTRUCTION: recommander un rythme de contrôle (visuel / photos / drone) selon la catégorie : si note globale < 4 %% → suivi tous les 3–5 ans ; 4–20 %% → contrôle tous les 2–3 ans ; > 20 %% → suivi annuel voire après chaque gros épisode météo.FIN_INSTRUCTION</li>
                              <li>🧪 Vérifications complémentaires : INSTRUCTION: proposer des contrôles ciblés si le contexte s’y prête (ex. arrosage ciblé sur zones suspectes, visite en combles pour repérer traces d’humidité, caméra thermique si suspicion de défaut d’isolation ou d’infiltration cachée). FIN_INSTRUCTION</li>
                            </ul>
                            """)
            .concat(
                    """
                            </section>
                            
                            INSTRUCTION
                            👉 Logique d’analyse attendue (à respecter dans le ton du rapport) :
                            • Donne plus de poids à la moisissure et à l’humidité qu’à l’usure simple : une
                            toiture peu usée mais fortement encrassée/humide doit être décrite comme à risque
                            si rien n’est fait.
                            • Adapte ton analyse au revêtement :
                            – Sur tuiles/ardoises poreuses : insiste sur les effets de l’humidité et de la mousse
                            (porosité, gel, soulèvement).
                            – Sur toits terrasses ou pentes faibles : la stagnation d’eau est prioritaire (risque
                            d’infiltration rapide).
                            • Si des obstacles sont présents (velux, souches, sorties, panneaux…), insiste sur
                            l’étanchéité périphérique : solins, raccords, relevés, points bas.
                            • Utilise la note de dégradation globale pour caler le discours :
                            – < 4 % : rassurant, bon état, entretien léger à prévoir.
                            – 4–10 % : entretien à programmer pour éviter que la situation ne se dégrade.
                            – 11–20 % : entretien nécessaire, risques à moyen terme si rien n’est fait.
                            – 21–40 % : réparation nécessaire, risques d’infiltration significatifs.
                            – > 40 % : intervention urgente, la toiture n’assure plus correctement son rôle.
                            • Si les fissures sont absentes mais que moisissure/humidité montent : signale une
                            usure lente liée au manque d’entretien, avec recommandation forte de
                            démoussage et nettoyage.
                            • Si un risque feu est indiqué : mentionne la présence possible de végétation proche,
                            d’aiguilles/feuilles sur toit, ou de revêtements bitumineux, et recommande un
                            nettoyage de sécurité et un éloignement de la végétation.
            
                            FIN_INSTRUCTION
                            """);
    log.info("AI Prompt : {}", prompt);
    return chat.apply(prompt)
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
