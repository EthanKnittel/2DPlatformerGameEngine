package com.EthanKnittel.ai;

import com.EthanKnittel.entities.agents.Foe;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Stratégie de Patrouille (Comportement "Idle" ou par défaut).
 * <p>
 * L'ennemi se déplace de manière aléatoire (gauche, droite, ou pause).
 * </p>
 * <p>
 * Cette classe inclut une logique rudimentaire de "désynchronisation" :
 * si deux ennemis se touchent pendant une patrouille, ils changent de direction
 * pour éviter de rester collés indéfiniment.
 * </p>
 */
public class PatrolStrategy implements EnemyStrategy {
    private float timer = 0f;     // Timer accumulé pour savoir quand changer d'avis
    private float currentDirection = 1;  // Direction actuelle : 1 (droite), -1 (gauche), 0 (pause)
    private float changeMove = 2f; // Durée cible avant le prochain changement de direction naturel

    private Vector2 output = new Vector2();  // Vecteur réutilisable pour éviter l'allocation de mémoire à chaque frame

    @Override
    public Vector2 calculateMove(Foe foe, float deltaTime) {
        output.set(0, 0); // Reset du vecteur de mouvement
        timer += deltaTime;

        // --- LOGIQUE DE CHANGEMENT D'ÉTAT ---
        // On change de direction si l'une des 3 conditions est remplie :
        // 1. Le temps imparti est écoulé (comportement naturel).
        // 2. On fonce dans un mur (on est bloqué).
        // 3. On touche un autre ennemi depuis un petit moment (évitement social).

        boolean timeIsUp = timer > changeMove;
        boolean hitWall = foe.getTouchingWall() && currentDirection != 0;
        boolean bumpingAlly = foe.getTouchingAlly() && timer > 0.2f;
        if (timeIsUp || hitWall || bumpingAlly) {
            // CAS SPÉCIAL : Collision avec un allié (Bumping)
            if (foe.getTouchingAlly() && timer != 0) {
                // On force un nouveau choix aléatoire pour se séparer
                pickRandomDirection();
                // ASTUCE : On met le timer à une valeur NÉGATIVE aléatoire (-0.5s à -1.5s).
                // Cela force cet ennemi à garder sa nouvelle direction plus longtemps que prévu,
                // ce qui "désynchronise" son rythme par rapport à celui qu'il vient de toucher.
                timer = -MathUtils.random(0.5f, 1.5f);

            } else {
                // Changement naturel ou mur : on choisit juste une nouvelle direction
                pickRandomDirection();
                timer = 0f; // On reset le timer normalement
            }

            // On définit quand aura lieu le prochain changement naturel (entre 2 et 5 sec)
            changeMove = MathUtils.random(2f, 5f);
        }

        // --- APPLICATION DU MOUVEMENT ---
        // En patrouille, on marche moins vite (ici 50% de la vitesse max)
        output.x = (foe.getMoveSpeed() * 0.5f) * currentDirection;
        return output;
    }

    /**
     * Helper pour choisir une direction aléatoire parmi : Gauche, Droite, Stop.
     */
    private void pickRandomDirection() {
        int choice = MathUtils.random(2); // Retourne 0, 1 ou 2
        if (choice == 0){
            currentDirection = -1; // Gauche
        }  else if (choice == 1){
            currentDirection = 1; // Droite
        } else {
            currentDirection = 0; // Stop
        }
    }

    /**
     * Cette stratégie est toujours valide. C'est le comportement de repli.
     */
    @Override
    public boolean shouldExecute(Foe parent) {
        return true; // Toujours dispo si rien de mieux à faire
    }

    /**
     * Priorité très faible (10). N'importe quelle autre stratégie (Chasse, Fuite)
     * prendra le dessus si elle s'active.
     */
    @Override
    public int getPriority() {
        return 10; // Priorité faible
    }

    /**
     * On désactive la séparation physique (Boids) ici car cette stratégie gère
     * elle-même les collisions entre alliés via la logique "bumpingAlly" dans calculateMove.
     * Cela donne un mouvement plus naturel et moins "magnétique" pour une patrouille.
     */
    @Override
    public boolean enableSeparation() {
        return false;
    }
}
