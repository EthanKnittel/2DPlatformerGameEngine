package com.EthanKnittel.entities.artifacts;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.game.GameScreen;
import com.EthanKnittel.graphics.AnimationManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class FireArrow extends Entity {
    private Vector2 velocity;
    private float rotation;
    private AnimationManager animationManager;
    private int damage = 25;
    private float lifeTime = 3.0f;

    // Correction de l'angle car j'ai des mauvais assets
    private static final float ROTATION_OFFSET = 180f;

    // Taille visuel, rien à voir avec la hitbox
    private float visualWidth = 64f / GameScreen.getPixelsPerBlocks();
    private float visualHeight = 16f / GameScreen.getPixelsPerBlocks();

    // Taille de la hitbox
    private static float hitboxWidth = 16f / GameScreen.getPixelsPerBlocks();
    private static float hitboxHeight = 3f / GameScreen.getPixelsPerBlocks();

    public FireArrow(float centerX, float centerY, float targetX, float targetY) {
        // On centre la flèche
        super(centerX - hitboxWidth / 2f, centerY - hitboxHeight / 2f, hitboxWidth, hitboxHeight);
        this.setIsProjectile(true);

        Vector2 start = new Vector2(centerX, centerY);
        Vector2 target = new Vector2(targetX, targetY);
        Vector2 direction = target.sub(start).nor();

        float speed = 50f;
        this.velocity = direction.scl(speed);

        // Calcul de l'angle + Offset
        this.rotation = this.velocity.angleDeg() + ROTATION_OFFSET;

        this.setAffectedByGravity(false);
        this.setCollision(false);

        loadAnimations();
    }

    private void loadAnimations() {
        try {
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("FireArrow/fire_arrow.atlas"));
            Animation<TextureRegion> anim = new Animation<>(0.1f, atlas.findRegions("Fire Arrow_Frame"), Animation.PlayMode.LOOP);
            animationManager = new AnimationManager(anim);
        } catch (Exception e) {
            Gdx.app.error("FireArrow", "Erreur de chargement de l'atlas", e);
        }
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void update(float deltaTime) {
        setPosXY(getX() + velocity.x * deltaTime, getY() + velocity.y * deltaTime);
        if (animationManager != null) animationManager.update(deltaTime);

        lifeTime -= deltaTime;
        if (lifeTime <= 0) {
            this.setCanBeRemove(true);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame = animationManager.getFrame();

        // On calcule le décalage pour centrer le VISUEL sur la HITBOX
        float drawX = getX() - (visualWidth - getbounds().width) / 2f;
        float drawY = getY() - (visualHeight - getbounds().height) / 2f;

        batch.draw(frame, drawX, drawY,visualWidth / 2f, visualHeight / 2f, visualWidth, visualHeight,1f, 1f, rotation);

    }

    @Override
    public void dispose() {}
}
