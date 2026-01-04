package com.EthanKnittel.graphics.entity;

import com.EthanKnittel.entities.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class AnimatedEntityView implements EntityView {
    private Entity entity;
    private TextureAtlas atlas;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime = 0f;

    public AnimatedEntityView(Entity entity, String atlasPath) {
        this.entity = entity;
        try {
            this.atlas = new TextureAtlas(atlasPath);
            loadAnimations(this.atlas);
        } catch (Exception e) {
            Gdx.app.error("Erreur du chargement de l'atlas: ", atlasPath, e);
        }
    }

    protected abstract void loadAnimations(TextureAtlas atlas);
    protected abstract Animation<TextureRegion> getAnimationForState();
    protected abstract boolean getIsFacingLeft();

    @Override
    public void render(SpriteBatch batch, float delta) {
        stateTime = stateTime + delta;

        Animation<TextureRegion> nextAnimation = getAnimationForState();

        if (currentAnimation != nextAnimation) {
            currentAnimation = nextAnimation;
            stateTime = 0f;
        }
        if (currentAnimation != null) {
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

            boolean flip = getIsFacingLeft();
            if (flip && !currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            } else if (!flip && currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            }
            batch.draw(currentFrame, entity.getX(), entity.getY(), entity.getbounds().width, entity.getbounds().height);
        }
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }

    public float getStateTime() {
        return stateTime;
    }
    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }
}
