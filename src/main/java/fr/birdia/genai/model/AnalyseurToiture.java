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
Tu es un artisan couvreur expérimenté. Tu rédiges un rapport court, clair et pédagogique pour un propriétaire.
RÉSULTAT EXIGÉ : UNIQUEMENT du HTML (aucun texte hors balises). 300–400 mots max au total.
N’utilise que les emojis suivants : 🟢🟡🟠🔴 🔍 🧼 🛠️ 📸 🧪 🧯.
Ne fais pas de disclaimer juridique.\n"""
            .concat(
                String.format(
                    """
Données (variables) :
• Millésimes comparés : {{millesime_1}} = %s ; {{millesime_2}} = %s
• Surface totale (m²) : {{surface_totale}} = %s
• Type de toiture : {{type_toiture}} = %s (ex. 3 pans / terrasse)
• Pente (°) : {{pente_min}}–{{pente_max}} = %s-%s
• Revêtements détectés : {{revetements}} = %s
• Taux d’humidité (%) : {{taux_humidite}} = %s
• Taux de moisissure (%) : {{taux_moisissure}} = %s
• Taux d’usure (%) : {{taux_usure}} = %s
• Obstacles/équipements : {{obstacles}} = %s (velux, cheminées, PV, HVAC…)
• Fissures/cassures : {{fissures}} = %s (oui/non)
• Risque feu : {{risque_feu}} = %s (oui/non)

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
                    toit.fissureCassure() ? "OUI" : "NON",
                    toit.risqueFeu() ? "OUI" : "NON"))
            .concat(
                """
Règles:
• Catégorie = A (<8), B (8–20), C (20–30), D (30–40), E (>40).
• Pastille : A=🟢, B=🟡, C=🟠, D=🟠, E=🔴.\s
• Libellés : A « Bon état », B « Entretien à prévoir », C « Entretien nécessaire », D « Réparation nécessaire », E « Intervention urgente ».
• Arrondir les % à l’entier, plafonner toute valeur >100 à 100.\s
• Dans « Conseils », ne répète pas tout l’état, passe à l’action, priorise court/moyen terme et cite les zones (ex. autour des velux, acrotères…).

Contraintes de structure (respect strict) :

<section>
<h2>COMPRENDRE VOTRE RAPPORT</h2>
<h3><span>{{pastille_emoji}}</span> CATÉGORIE {{lettre}} : {{intitule_categorie}}</h3>

<ul>
<li>L’analyse porte sur {{surface_totale}} m² au {{millesime_2}} (référence : {{millesime_1}}). Revêtement : {{revetements}} ; type : {{type_toiture}} ; pente {{pente_min}}–{{pente_max}}°.</li>
<li>Humidité : {{taux_humidite}} % • Moisissure : {{taux_moisissure}} % • Usure : {{taux_usure}} % — interprète l’impact (écoulement, porosité, vieillissement).</li>
<li>Points singuliers : {{obstacles}} — expliquer les effets (joints, relevés, évacuations, pénétrations).</li>
<li>Évolution {{millesime_1}}→{{millesime_2}} : résumer la tendance (stagnation, progression de taches, pose PV, réfection…).</li>
<li>Risques spécifiques : "{{fissures}}" fissures/cassures ; risque feu : "{{risque_feu}}" ; indiquer zones à vigilance (faible pente, ombres permanentes, acrotères, naissances d’eaux pluviales).</li>
</ul>
</section>


<section>
<h2>CONSEILS DE L’ARTISAN COUVREUR</h2>
<ul>
<li>🔍 Inspection ciblée : préciser les zones et le but (étanchéité autour {{obstacles}}, contrôle pentes/naissances EP, sondage d’adhérence si terrasse bitume/EPDM).</li>
<li>🧼 Entretien : nettoyage anti-mousse (pentes nord), curage chéneaux/crapaudines, dégagement des points d’évacuation, désencombrement des acrotères.</li>
<li>🛠️ Correctifs (priorité) : réparation des relevés et joints, reprise des éléments défaillants (ex. solins/abergements), étanchéité locale ; si {{taux_humidite}}>25 % ou {{taux_usure}}>30 %, prévoir intervention sous 1–3 mois.</li>
<li>📸 Suivi : contrôle visuel semestriel si catégorie C/D/E ; sinon annuel. Comparer systématiquement avec {{millesime_1}} pour objectiver l’évolution.</li>
<li>🧪 Options complémentaires : test d’arrosage local, caméra thermique à froid, vérification du dimensionnement des EP selon pente {{pente_min}}–{{pente_max}}°.</li>
</ul>
</section>


Produis maintenant le rapport en HTML en remplissant intelligemment les variables ci-dessus."""));
  }
}
