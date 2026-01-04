package com.EthanKnittel.graphics.entity;

import com.EthanKnittel.entities.agents.foes.Cactus;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CactusView extends AnimatedEntityView{
    private Cactus cactus;
    private Animation<TextureRegion> idleAnim, runAnim, hitAnim, fallAnim, jumpAnim;

    public CactusView(Cactus cactus) {
        super(cactus, Gdx.files.internal("Ennemies/cactus/Cactus.atlas").path());
        this.cactus = cactus;
    }

    @Override
    protected void loadAnimations(TextureAtlas atlas) {
        idleAnim = new Animation<>(0.1f, atlas.findRegions("IDLE"), Animation.PlayMode.LOOP);
        runAnim = new Animation<>(0.1f, atlas.findRegions("RUNNING"), Animation.PlayMode.LOOP);
        hitAnim = new Animation<>(0.1f, atlas.findRegions("HIT"), Animation.PlayMode.LOOP);
        fallAnim = new Animation<>(0.1f, atlas.findRegions("FALLING"), Animation.PlayMode.LOOP);
        jumpAnim = new Animation<>(0.1f, atlas.findRegions("JUMPING"), Animation.PlayMode.LOOP);
    }

    @Override
    protected Animation<TextureRegion> getAnimationForState(){
        if (cactus.getVisualHitActive() || !cactus.getAlive()){
            return hitAnim;
        } else if (cactus.getGrounded() && cactus.getVelocity().y < 0) {
            return fallAnim;
        } else if (cactus.getGrounded() && cactus.getVelocity().y > 0) {
            return jumpAnim;
        } else if (cactus.getVelocity().x != 0) {
            return runAnim;
        } else {
            return idleAnim;
        }
    }
    @Override
    protected boolean getIsFacingLeft(){
        return cactus.getFacingLeft();
    }
}
