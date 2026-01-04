package com.EthanKnittel.entities.artifacts;

import com.EthanKnittel.game.GameScreen;
import com.badlogic.gdx.math.Vector2;

public class FireArrow extends Projectile {
    private float speed = 50f;
    private static int damage = 25;
    private static float lifeTime = 3.0f;

    // Taille de la hitbox
    private static float hitboxWidth = 16f / GameScreen.getPixelsPerBlocks();
    private static float hitboxHeight = 3f / GameScreen.getPixelsPerBlocks();

    public FireArrow(float centerX, float centerY, float targetX, float targetY) {
        // On centre la flèche
        super(centerX - hitboxWidth / 2f, centerY - hitboxHeight / 2f, hitboxWidth, hitboxHeight, damage, lifeTime);

        Vector2 start = new Vector2(centerX, centerY);
        Vector2 target = new Vector2(targetX, targetY);
        Vector2 direction = target.sub(start).nor();

        this.setVelocity(direction.x * speed, direction.y * speed);
    }
}
