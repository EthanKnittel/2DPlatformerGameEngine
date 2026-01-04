package com.EthanKnittel.entities.artifacts;

import com.EthanKnittel.entities.Artifact;
import com.EthanKnittel.game.GameScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class FireArrow extends Artifact {
    private Vector2 velocity;
    private float rotation;
    private int damage = 25;
    private float lifeTime = 3.0f;

    // Taille de la hitbox
    private static float hitboxWidth = 16f / GameScreen.getPixelsPerBlocks();
    private static float hitboxHeight = 3f / GameScreen.getPixelsPerBlocks();

    public FireArrow(float centerX, float centerY, float targetX, float targetY) {
        // On centre la flèche
        super(centerX - hitboxWidth / 2f, centerY - hitboxHeight / 2f, hitboxWidth, hitboxHeight);

        this.setIsProjectile(true);
        this.setAffectedByGravity(false);
        this.setCollision(false);

        Vector2 start = new Vector2(centerX, centerY);
        Vector2 target = new Vector2(targetX, targetY);
        Vector2 direction = target.sub(start).nor();

        float speed = 50f;
        this.velocity = direction.scl(speed);

        // Calcul de l'angle + Offset
        this.rotation = this.velocity.angleDeg();
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void update(float deltaTime) {
        setPosXY(getX() + velocity.x * deltaTime, getY() + velocity.y * deltaTime);
        lifeTime -= deltaTime;
        if (lifeTime <= 0) {
            this.setCanBeRemove(true);
        }
    }

    public float getRotation() {
        return rotation;
    }
}
