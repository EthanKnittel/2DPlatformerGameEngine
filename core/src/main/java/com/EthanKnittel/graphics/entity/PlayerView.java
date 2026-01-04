package com.EthanKnittel.graphics.entity;

import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerView extends AnimatedEntityView{
    private Player player;
    private Animation<TextureRegion> idleAnim, walkAnim, runAnim, jumpAnim, wallSlideAnim, hitAnim, fallAnim, doubleJumpAnim;

    public PlayerView(Player player) {
        super(player, Gdx.files.internal("Player1/Player1.atlas").path());
        this.player = player;
    }

    @Override
    protected void loadAnimations(TextureAtlas atlas) {
        idleAnim = new Animation<>(0.1f, atlas.findRegions("IDLE"), Animation.PlayMode.LOOP);
        walkAnim = new Animation<>(0.1f, atlas.findRegions("RUNNING"), Animation.PlayMode.LOOP);
        runAnim = new Animation<>(0.05f, atlas.findRegions("RUNNING"), Animation.PlayMode.LOOP);
        jumpAnim = new Animation<>(0.1f, atlas.findRegions("JUMPING"), Animation.PlayMode.LOOP);
        wallSlideAnim = new Animation<>(0.1f, atlas.findRegions("WALLJUMPING"), Animation.PlayMode.LOOP);
        hitAnim = new Animation<>(0.1f, atlas.findRegions("HIT"), Animation.PlayMode.LOOP);
        fallAnim = new Animation<>(0.1f, atlas.findRegions("FALLING"), Animation.PlayMode.LOOP);
        doubleJumpAnim = new Animation<>(0.1f, atlas.findRegions("DOUBLEJUMP"), Animation.PlayMode.LOOP);
    }

    @Override
    protected Animation<TextureRegion> getAnimationForState() {
        if (player.getVisualHitActive() || !player.getAlive()) {
            return hitAnim;
        } else if (player.getTouchingWall() && !player.getGrounded() && player.getVelocity().y < 0) {
            return wallSlideAnim;
        } else if (!player.getGrounded()) {
            if (player.getVelocity().y < 0) {
                return fallAnim;
            } else {
                if (player.getJumpCount() > 1) {
                    return doubleJumpAnim;
                } else {
                    return jumpAnim;
                }
            }
        } else if (player.getVelocity().x != 0) {
            if (player.getRunning()) {
                return runAnim;
            } else {
                return walkAnim;
            }
        } else {
            return idleAnim;
        }
    }

    @Override
    protected boolean getIsFacingLeft() {
        return player.getFacingLeft();
    }
}
