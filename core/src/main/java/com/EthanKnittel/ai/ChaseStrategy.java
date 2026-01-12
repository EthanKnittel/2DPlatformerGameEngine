package com.EthanKnittel.ai;

import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.math.Vector2;

/**
 * Stratégie de Poursuite (Chase).
 * <p>
 * Dans cet état, l'ennemi a repéré le joueur et tente de le rejoindre au plus vite.
 * Cette stratégie inclut un système de <b>mémoire à court terme</b> : si le joueur passe derrière un mur,
 * l'ennemi continue de se diriger vers sa dernière position connue pendant quelques secondes
 * avant d'abandonner.
 * </p>
 */
public class ChaseStrategy implements EnemyStrategy {

    // Vecteur réutilisable pour éviter l'allocation de mémoire à chaque frame
    private final Vector2 output = new Vector2();

    // Timer pour gérer le délai avant de sauter par-dessus un mur
    private float wallTimer = 0f;

    // --- SYSTÈME DE MÉMOIRE ---
    private float memoryTimer = 0f; // Temps restant avant d'abandonner

    /** Durée (en secondes) pendant laquelle l'ennemi se souvient du joueur après l'avoir perdu de vue. */
    private static final float memoryDuration = 2.0f;

    /** Distance maximale à laquelle l'ennemi peut "sentir" ou voir le joueur pour activer la chasse. */
    private static final float detectionRadius = 12.0f;

    /** * Zone morte (en unités monde) : si la différence de X est inférieure à cette valeur,
     * l'ennemi arrête de bouger horizontalement. Cela évite l'effet de "tremblement"
     * quand l'ennemi est superposé au joueur.
     */
    private static final float deadZone = 0.5f;

    /** Temps d'attente contre un mur avant de décider de sauter. */
    private static final float wallJumpDelay = 0.15f;


    /**
     * Vérifie si la stratégie de chasse doit être activée.
     * Logique :
     * 1. Si je vois le joueur et qu'il est proche -> J'active et je recharge ma mémoire.
     * 2. Si je ne le vois pas, mais que je m'en souviens encore -> Je reste actif.
     * 3. Sinon -> Je désactive.
     */
    @Override
    public boolean shouldExecute(Foe foe) {
        Player target = foe.getTarget();
        if (target == null) return false;

        float distance = foe.getPosition().dst(target.getPosition());
        boolean isCloseEnough = distance < detectionRadius;

        // On vérifie la vue réelle (Raycast pour voir s'il y a des murs)
        boolean canSee = foe.hasLineOfSight(target);

        // Cas 1 : Contact visuel direct et à portée
        if (isCloseEnough && canSee) {
            memoryTimer = memoryDuration;
            return true;
        }

        // Cas 2 : Perte de vue, mais la mémoire est encore active (l'ennemi "devine" la position)
        if (memoryTimer > 0) {
            return true;
        }

        // Cas 3 : Trop loin ou oublié -> On abandonne (le Brain passera à une autre stratégie ou alors le monstre restera immobile)
        return false;
    }

    /**
     * La chasse est prioritaire sur la patrouille (50 vs 10).
     */
    @Override
    public int getPriority() {
        return 50;
    }

    /**
     * En mode chasse, on active la séparation (Boids) pour éviter que tous les ennemis
     * ne s'empilent au même point exact sur le joueur, ce qui rendrait le combat illisible.
     */
    @Override
    public boolean enableSeparation() {
        return true;
    }

    @Override
    public Vector2 calculateMove(Foe foe, float deltaTime) {
        output.set(0, 0); // On reset le vecteur

        Player target = foe.getTarget();
        if (target == null) return output;

        // --- GESTION DU TIMER DE MÉMOIRE ---
        // Si la ligne de vue est coupée, la mémoire s'estompe
        if (!foe.hasLineOfSight(target)) {
            memoryTimer -= deltaTime;
        } else {
            // Si on le voit, on rafraîchit la mémoire
            memoryTimer = memoryDuration;
        }

        // --- DÉPLACEMENT HORIZONTAL (X) ---

        float currentX = foe.getX();
        float speed = foe.getMoveSpeed();
        float distanceToPlayerX = target.getX() - currentX;

            // Si on est suffisamment loin du joueur (hors de la Dead Zone), on bouge
            if (Math.abs(distanceToPlayerX) > deadZone) {
            if (distanceToPlayerX < 0) {
                output.x = -speed; // Aller à gauche
            } else {
                output.x = speed; // Aller à droite
            }
        } else {
            output.x = 0; // On est sur la cible, on s'arrête (évite le jitter)
        }

        // --- GESTION DU SAUT (Y) ---
        boolean needToJump = false;
        float distanceToPlayerY = target.getY() - foe.getY();

        // Condition A : Bloqué par un mur
        if (foe.getTouchingWall() && foe.getGrounded()) {
            // On ne saute que si le joueur n'est pas juste à côté (sinon on saute sur place contre lui)
            if (Math.abs(distanceToPlayerX) > 3.0f) {
                wallTimer += deltaTime;
                if (wallTimer >= wallJumpDelay) {
                    needToJump = true;
                    wallTimer = 0f;
                }
            }
        } else {
            wallTimer = 0f;
        }

        // Condition B : Le joueur est sur une plateforme au-dessus
        // Si le joueur est plus haut (> 1.5 blocs) et proche horizontalement (< 3.0 blocs)
        if (foe.getGrounded() && distanceToPlayerY > 1.5f && Math.abs(distanceToPlayerX) < 3.0f) {
            needToJump = true;
        }

        // Application du saut
        if (needToJump) {
            output.y = foe.getJumpSpeed();
        }

        return output;
    }
}
