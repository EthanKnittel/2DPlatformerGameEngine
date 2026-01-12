package com.EthanKnittel.entities.artifacts;

import com.EthanKnittel.game.GameScreen;
import com.badlogic.gdx.math.Vector2;

/**
 * Entité représentant une "Flèche de Feu" tirée par le joueur.
 * <p>
 * C'est un {@link Projectile} rapide, infligeant des dégâts modérés, qui voyage en ligne droite
 * vers la position visée par la souris au moment du tir.
 * </p>
 */
public class FireArrow extends Projectile {

    /** Vitesse de déplacement de la flèche (Unités/Seconde). */
    private float speed = 50f;

    /** Dégâts infligés à l'ennemi touché (25 = tue un Cactus ou un Ordi en 2 coups). */
    private static int damage = 25;

    /** Durée avant disparition automatique (3 secondes). Suffisant pour traverser l'écran. */
    private static float lifeTime = 3.0f;

    // --- DIMENSIONS ---
    // La flèche est fine et longue.
    // Note : On divise par PixelsPerBlocks pour convertir les pixels (16x3) en unités du monde.
    private static float hitboxWidth = 16f / GameScreen.getPixelsPerBlocks();
    private static float hitboxHeight = 3f / GameScreen.getPixelsPerBlocks();

    /**
     * Crée et tire une flèche.
     *
     * @param centerX Position X de départ (généralement le centre du joueur).
     * @param centerY Position Y de départ (généralement le centre du joueur).
     * @param targetX Position X de la cible (la souris dans le monde).
     * @param targetY Position Y de la cible (la souris dans le monde).
     */
    public FireArrow(float centerX, float centerY, float targetX, float targetY) {
        // 1. Appel au constructeur parent (Projectile)
        // Astuce : On décale la position de départ (x - width/2) pour que le CENTRE de la flèche
        // apparaisse au point (centerX, centerY), et non pas son coin inférieur.
        super(centerX - hitboxWidth / 2f, centerY - hitboxHeight / 2f, hitboxWidth, hitboxHeight, damage, lifeTime);

        // 2. Calcul du Vecteur de Direction (Maths Vectorielles)
        Vector2 start = new Vector2(centerX, centerY);
        Vector2 target = new Vector2(targetX, targetY);

        // Formule : Direction = (Arrivée - Départ)
        // .nor() : "Normalise" le vecteur, c'est-à-dire qu'on garde la direction mais on force
        // sa longueur à 1. Cela permet de multiplier ensuite par la vitesse voulue.
        Vector2 direction = target.sub(start).nor();

        // 3. Application de la vitesse
        // La classe mère Projectile se chargera de calculer la rotation automatique basée sur ce vecteur.
        this.setVelocity(direction.x * speed, direction.y * speed);
    }
}
