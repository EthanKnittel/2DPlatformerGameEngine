package com.EthanKnittel.ai;

import com.EthanKnittel.entities.agents.Foe;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Le "Cerveau" de l'IA (Pattern Composite / Selector).
 * <p>
 * Cette classe contient une liste de stratégies possibles (ex: Patrouille, Chasse, Fuite).
 * À chaque frame, elle évalue quelle est la stratégie la plus prioritaire qui peut être exécutée
 * et lui délègue le contrôle du mouvement.
 * </p>
 * <p>
 * C'est ce qui permet à un ennemi de passer fluidement de "Je patrouille" à "Je te pourchasse"
 * sans qu'on ait besoin de coder des transitions complexes avec des `if/else` imbriqués.
 * </p>
 */
public class BrainStrategy implements EnemyStrategy {

    /** Liste de tous les comportements que cet ennemi connait. */
    private Array<EnemyStrategy> knownStrategies = new Array<>();

    /** La stratégie qui a été choisie pour la frame en cours. */
    private EnemyStrategy currentActiveStrategy = null;

    /**
     * Ajoute un comportement au cerveau de l'ennemi.
     * <p>
     * <b>Important :</b> La liste est automatiquement retriée par priorité descendante.
     * Si on ajoute une stratégie "Attaque" (Priorité 100) et une "Patrouille" (Priorité 10),
     * "Attaque" sera toujours placée en premier dans la liste pour être testée avant.
     * </p>
     *
     * @param strategy L'instance de la stratégie à ajouter.
     */
    public void addStrategy(EnemyStrategy strategy) {
        knownStrategies.add(strategy);
        // Tri : Les plus hautes priorités (ex: 50) en premier, les basses (ex: 10) à la fin.
        knownStrategies.sort((s1, s2) -> Integer.compare(s2.getPriority(), s1.getPriority()));
    }

    /**
     * Le cycle de décision (Le cœur de l'IA).
     * 1. On parcourt la liste des stratégies de la plus importante à la moins importante.
     * 2. On demande à chaque stratégie : "Peux-tu t'activer ?" (shouldExecute).
     * 3. La première qui dit "Oui" gagne et prend le contrôle.
     */
    @Override
    public Vector2 calculateMove(Foe foe, float deltaTime) {
        currentActiveStrategy = null;

        // Boucle de décision
        for (EnemyStrategy s : knownStrategies) {
            if (s.shouldExecute(foe)) {
                currentActiveStrategy = s;
                break; // On a trouvé la plus importante, on arrête de chercher les autres.
            }
        }

        // Exécution : On délègue le calcul du mouvement à la stratégie gagnante.
        if (currentActiveStrategy != null) {
            return currentActiveStrategy.calculateMove(foe, deltaTime);
        }

        // Si aucune stratégie ne veut s'activer (ex: pas de Patrouille ajoutée), on ne bouge pas.
        return new Vector2(0,0);
    }

    /**
     * Le cerveau est toujours actif par défaut.
     */
    @Override
    public boolean shouldExecute(Foe foe) {
        return true; // Le cerveau est toujours actif
    }

    /**
     * Priorité neutre (rarement utilisée car le Brain est souvent la stratégie racine).
     */
    @Override
    public int getPriority() {
        return 0;
    }

    /**
     * Délègue la décision de séparation physique à la stratégie active.
     * <p>
     * Par exemple : Si c'est {@code ChaseStrategy} qui pilote, elle renverra Vrai.
     * Si c'est {@code PatrolStrategy}, elle renverra Faux.
     * </p>
     */
    @Override
    public boolean enableSeparation() {
        if (currentActiveStrategy != null) {
            return currentActiveStrategy.enableSeparation();
        }
        return true;
    }
}
