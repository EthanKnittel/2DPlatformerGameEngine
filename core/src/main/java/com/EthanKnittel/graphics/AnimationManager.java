package com.EthanKnittel.graphics;

import com.EthanKnittel.Evolving;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

public class AnimationManager implements Evolving {
    private Animation<TextureRegion> animation;
    private float stateTime;

    public AnimationManager(Animation<TextureRegion> animation) {
        this.animation = animation;
        stateTime = 0f;
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    public TextureRegion getFrame() {
        return animation.getKeyFrame(stateTime, animation.getPlayMode() ==  Animation.PlayMode.LOOP);
    }

    // pour changer d'animation (genre courir → marcher)
    public void setAnimation(Animation<TextureRegion> Newanimation) {
        if (this.animation != Newanimation) {
            this.animation = Newanimation;
            stateTime = 0f; // on réinitialise le temps de la nouvelle animation
        }
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public float getAnimationDuration() {
        return animation.getAnimationDuration();

    }
}
