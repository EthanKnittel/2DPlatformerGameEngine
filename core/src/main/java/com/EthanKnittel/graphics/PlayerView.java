package com.EthanKnittel.graphics;

import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerView {
    private Player player;

    private TextureAtlas atlas;
    private Animation<TextureRegion> idleAnim, walkAnim, runAnim, jumpAnim, wallSlideAnim, hitAnim, fallAnim, doubleJumpAnim;
    private Animation<TextureRegion> currentAnimation;

    private float stateTime = 0f; // temps écoulé pour l'animation

    public  PlayerView(Player player) {
        this.player = player;
        loadAssets();
    }

    private void loadAssets() {
        try {
            atlas = new TextureAtlas(Gdx.files.internal("Player1/Player1.atlas"));
            idleAnim = new Animation<>(0.1f, atlas.findRegions("IDLE"), Animation.PlayMode.LOOP);
            walkAnim = new Animation<>(0.1f, atlas.findRegions("RUNNING"), Animation.PlayMode.LOOP);
            runAnim = new Animation<>(0.05f, atlas.findRegions("RUNNING"), Animation.PlayMode.LOOP);
            jumpAnim = new Animation<>(0.1f, atlas.findRegions("JUMPING"), Animation.PlayMode.LOOP);
            wallSlideAnim = new Animation<>(0.1f, atlas.findRegions("WALLJUMPING"), Animation.PlayMode.LOOP);
            hitAnim = new Animation<>(0.1f, atlas.findRegions("HIT"), Animation.PlayMode.LOOP);
            fallAnim = new Animation<>(0.1f, atlas.findRegions("FALLING"), Animation.PlayMode.LOOP);
            doubleJumpAnim = new Animation<>(0.1f, atlas.findRegions("DOUBLEJUMP"), Animation.PlayMode.LOOP);

            currentAnimation = idleAnim; // on défini de base le Idle
        } catch (Exception e) {
            Gdx.app.error("Player", "Erreur de chargement de l'atlas", e);
        }
    }

    public void render(SpriteBatch batch, float delta) {
        stateTime += delta;
        Animation<TextureRegion> previousAnimation = currentAnimation;
        updateCurrentAnimation();

        if (currentAnimation != previousAnimation) {
            stateTime = 0f;
        }

        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        if (player.getFacingLeft() && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!player.getFacingLeft() && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        batch.draw(currentFrame, player.getX(), player.getY(), player.getbounds().width, player.getbounds().height);

    }

    private void updateCurrentAnimation() {
        if (player.getVisualHitActive() || !player.getAlive()) {
            currentAnimation = hitAnim;
        } else if (player.getTouchingWall() && !player.getGrounded() && player.getVelocity().y<0) {
            currentAnimation = wallSlideAnim;
        } else if (!player.getGrounded()) {
            if (player.getVelocity().y<0){
                currentAnimation = fallAnim;
            } else {
                if (player.getJumpCount() > 1) {
                    currentAnimation = doubleJumpAnim;
                } else {
                    currentAnimation = jumpAnim;
                }
            }
        } else if (player.getVelocity().x !=0){
            if (player.getRunning()){
                currentAnimation = runAnim;
            } else {
                currentAnimation = walkAnim;
            }
        } else {
            currentAnimation = idleAnim;
        }
    }

    public void dispose() {
        atlas.dispose();
    }
}
