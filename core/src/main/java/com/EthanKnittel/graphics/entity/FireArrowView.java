package com.EthanKnittel.graphics.entity;

import com.EthanKnittel.entities.artifacts.FireArrow;
import com.EthanKnittel.game.GameScreen;
import com.EthanKnittel.graphics.entity.AnimatedEntityView;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FireArrowView extends AnimatedEntityView {
    private FireArrow arrow;
    private Animation<TextureRegion>  flyAnim;

    public FireArrowView(FireArrow arrow) {
        super(arrow, Gdx.files.internal("FireArrow/fire_arrow.atlas").path());
        this.arrow = arrow;
    }

    @Override
    protected void loadAnimations(TextureAtlas atlas) {
        flyAnim = new Animation<>(0.1f, atlas.findRegions("Fire Arrow_Frame"), Animation.PlayMode.LOOP);
    }

    @Override
    protected Animation<TextureRegion> getAnimationForState() {
        return flyAnim;
    }

    @Override
    protected boolean getIsFacingLeft() {
        return false;
    }

    @Override
    public void render(SpriteBatch batch, float delta){
        setStateTime(getStateTime() + delta);

        TextureRegion currentFrame = flyAnim.getKeyFrame(getStateTime(), true);

        float width = 64f / GameScreen.getPixelsPerBlocks();
        float height = 16f / GameScreen.getPixelsPerBlocks();

        batch.draw(currentFrame,
            arrow.getX() + arrow.getbounds().width / 2f - width / 2f, arrow.getY() + arrow.getbounds().height / 2f - height / 2f, width / 2f, height / 2f, width, height, 1, 1, arrow.getRotation()+180f);
    }
}
