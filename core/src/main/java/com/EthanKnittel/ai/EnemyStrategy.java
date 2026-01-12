package com.EthanKnittel.ai;

import com.EthanKnittel.entities.agents.Foe;
import com.badlogic.gdx.math.Vector2;

/**
 * Interface définissant le contrat pour les comportements d'Intelligence Artificielle (IA) des ennemis.
 * <p>
 * Cette interface utilise le pattern "Strategy". Elle permet de créer des comportements
 * modulaires (ex: Patrouille, Chasse, Fuite) qui peuvent être échangés dynamiquement
 * ou combinés via un {@link BrainStrategy}.
 * </p>
 */
public interface EnemyStrategy {

    /**
     * Calcule le vecteur de déplacement que l'ennemi doit appliquer pour cette frame.
     * <p>
     * C'est ici que réside la logique principale du mouvement (courir vers le joueur,
     * sauter un obstacle, faire des aller-retours).
     * </p>
     *
     * @param foe       L'instance de l'ennemi (le contexte) qui exécute cette stratégie.
     * @param deltaTime Le temps écoulé depuis la dernière frame (en secondes), pour lisser les mouvements.
     * @return Un {@link Vector2} représentant la vitesse ou la direction à appliquer à l'ennemi.
     */
    Vector2 calculateMove(Foe foe, float deltaTime);

    /**
     * Indique si la force de séparation (physique) doit être active pendant cette stratégie.
     * <p>
     * La séparation permet aux ennemis de se "pousser" mutuellement pour ne pas se superposer.
     * </p>
     *
     * @return {@code true} si l'ennemi doit éviter de se coller aux autres (ex: en mode Chasse),
     * {@code false} si la superposition est acceptée ou gérée autrement (ex: en Patrouille stricte).
     */
    boolean enableSeparation();

    /**
     * Détermine si les conditions sont réunies pour exécuter cette stratégie.
     * <p>
     * Par exemple, une stratégie de "Chasse" ne doit s'exécuter que si le joueur est
     * à portée de vue ou de détection.
     * </p>
     *
     * @param foe L'ennemi qui tente d'évaluer la stratégie.
     * @return {@code true} si la stratégie peut être lancée, {@code false} sinon.
     */
    boolean shouldExecute(Foe foe);

    /**
     * Retourne la priorité de la stratégie.
     * <p>
     * Utilisé par le {@link BrainStrategy} pour décider quel comportement choisir si
     * plusieurs stratégies sont valides ({@code shouldExecute} renvoie true) en même temps.
     * Une valeur plus élevée signifie une priorité plus forte.
     * </p>
     *
     * @return Un entier représentant l'importance de la stratégie (ex: 50 pour Chasse, 10 pour Patrouille).
     */
    int getPriority();
}
