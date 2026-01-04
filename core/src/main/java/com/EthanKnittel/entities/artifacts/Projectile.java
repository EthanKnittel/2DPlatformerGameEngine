package com.EthanKnittel.entities.artifacts;

import com.EthanKnittel.entities.Artifact;
import com.badlogic.gdx.math.Vector2;

public abstract class Projectile extends Artifact {

    private Vector2 velocity;
    private int damage;
    private float lifeTime;
    private float rotation;

    public Projectile(float x, float y, float width, float height, int damage, float lifeTime) {
        super(x, y, width, height);
        this.damage = damage;
        this.lifeTime = lifeTime;
        this.velocity = new Vector2(0, 0);
        this.rotation = 0f;

        // Configuration par défaut pour tous les projectiles
        this.setIsProjectile(true);
        this.setAffectedByGravity(false);
        this.setCollision(false); // On gère la collision manuellement dans PhysicSystem
    }

    @Override
    public void update(float deltaTime) {
        // Logique de déplacement standard : Position = Position + Vitesse * Temps
        setPosXY(getX() + velocity.x * deltaTime, getY() + velocity.y * deltaTime);

        // Gestion de la durée de vie
        lifeTime -= deltaTime;
        if (lifeTime <= 0) {
            this.setCanBeRemove(true);
        }
    }

    // Getters et Setters communs
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
