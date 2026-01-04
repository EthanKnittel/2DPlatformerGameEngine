package com.EthanKnittel.graphics.entity;

import com.EthanKnittel.entities.agents.foes.Ordi;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class OrdiView extends AnimatedEntityView {
    private Ordi ordi;
    private Animation<TextureRegion> idleAnim, runAnim, hitAnim, fallAnim, jumpAnim;

    public OrdiView(Ordi ordi) {
        super(ordi, Gdx.files.internal("Ennemies/ordi/Ordi.atlas").path());
        this.ordi = ordi;
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
        if (ordi.getVisualHitActive() || !ordi.getAlive()){
            return hitAnim;
        } else if (ordi.getGrounded() && ordi.getVelocity().y < 0) {
            return fallAnim;
        } else if (ordi.getGrounded() && ordi.getVelocity().y > 0) {
            return jumpAnim;
        } else if (ordi.getVelocity().x != 0) {
            return runAnim;
        } else {
            return idleAnim;
        }
    }
    @Override
    protected boolean getIsFacingLeft(){
        return ordi.getFacingLeft();
    }
}
