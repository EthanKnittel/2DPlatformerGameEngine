package com.EthanKnittel.entities.artifacts;

import com.EthanKnittel.entities.Artifact;
import com.badlogic.gdx.math.Vector2;

/**
 * Classe abstraite de base pour tous les projectiles (Flèches, Balles, Magie).
 * <p>
 * Un projectile est un {@link Artifact} (objet inanimé) qui possède :
 * <ul>
 * <li>Une vitesse de déplacement.</li>
 * <li>Une durée de vie limitée (pour ne pas surcharger la mémoire si on tire dans le vide).</li>
 * <li>Une valeur de dégâts.</li>
 * <li>Une rotation visuelle (pour que la flèche pointe dans le sens du mouvement).</li>
 * </ul>
 * </p>
 */
public abstract class Projectile extends Artifact {

    private Vector2 velocity;
    private int damage;

    /** Temps restant (en secondes) avant que le projectile ne disparaisse de lui-même. */
    private float lifeTime;

    /** Rotation en degrés du sprite (0 = droite, 90 = haut). */
    private float rotation;

    /**
     * Constructeur de base pour un projectile.
     *
     * @param x        Position X initiale.
     * @param y        Position Y initiale.
     * @param width    Largeur de la hitbox.
     * @param height   Hauteur de la hitbox.
     * @param damage   Dégâts infligés au contact.
     * @param lifeTime Durée de vie en secondes.
     */
    public Projectile(float x, float y, float width, float height, int damage, float lifeTime) {
        super(x, y, width, height);
        this.damage = damage;
        this.lifeTime = lifeTime;
        this.velocity = new Vector2(0, 0);
        this.rotation = 0f;

        // --- CONFIGURATION SPÉCIALE ---

        // Tag spécifique pour que le PhysicSystem sache qu'il doit traiter les collisions
        // comme des "Triggers" (Dégâts) et non comme des obstacles physiques.
        this.setIsProjectile(true);

        // Par défaut, une flèche ne tombe pas (trajectoire rectiligne).
        // S'il faut malgré tout une gravité (pour une grenade par exemple), il faut mettre ceci à true dans la sous-classe.
        this.setAffectedByGravity(false);

        // IMPORTANT : On désactive la collision physique standard ("solide").
        // On ne veut pas que le joueur soit "poussé" par sa propre flèche.
        // La détection de l'impact est gérée manuellement dans PhysicSystem avec handleProjectileCollision.
        this.setCollision(false);
    }

    @Override
    public void update(float deltaTime) {
        // 1. Logique de déplacement standard : Position = Position + Vitesse * Temps
        // On modifie directement X et Y car setPosXY mettrait aussi à jour la hitbox.
        setPosXY(getX() + velocity.x * deltaTime, getY() + velocity.y * deltaTime);

        // 2. Gestion de la durée de vie (Nettoyage automatique)
        lifeTime -= deltaTime;
        if (lifeTime <= 0) {
            this.setCanBeRemove(true);
        }
    }

    // --- GETTERS & SETTERS ---

    public int getDamage() {
        return damage;
    }
    public void setDamage(int damage) {
        this.damage = damage;
    }

    public float getRotation() {
        return rotation;
    }
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    /**
     * Définit la vitesse du projectile et met à jour son orientation visuelle.
     */
    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);
        if (x != 0 || y != 0) {
            this.rotation = this.velocity.angleDeg();
        }
    }

    public Vector2 getVelocityVector() {
        return velocity;
    }
}
