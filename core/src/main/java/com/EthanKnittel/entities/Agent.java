package com.EthanKnittel.entities;

import com.EthanKnittel.graphics.AnimationManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Agent extends Entity {

    private int maxHealth;
    private int currenthealth;
    private int damage;
    private boolean isGrounded = false;
    private boolean isTouchingWall = false;
    private boolean isWallOnLeft = false;
    private static float wallSlideSpeed = -200f;

    protected AnimationManager animationManager;
    protected boolean facingLeft = false;



    public Agent(float x, float y, float width, float height, int maxHealth, int damage) {
        super(x, y,  width, height);
        this.maxHealth = maxHealth;
        this.currenthealth = maxHealth;
        this.damage = damage;
        this.SetAffectedByGravity(true);
    }

    protected void setAnimation(Animation<TextureRegion> animation){
        if (animationManager == null) {
            animationManager = new AnimationManager(animation);
        } else {
            animationManager.SetAnimation(animation);
        }
    }

    public int getCurrenthealth() {
        return this.currenthealth;
    }
    public int  getDamage() {
        return this.damage;
    }
    public int getMaxHealth() {
        return this.maxHealth;
    }
    public boolean isAlive() {
        return this.currenthealth > 0;
    }
    public void setGrounded(boolean grounded) {
        isGrounded = grounded;
    }
    public boolean getGrounded() {
        return isGrounded;
    }
    public boolean IsTouchingWall() {
        return isTouchingWall;
    }
    public boolean IsWallOnLeft() {
        return isWallOnLeft;
    }
    public float GetWallSlideSpeed() {
        return wallSlideSpeed;
    }
    public void SetWallSlideSpeed(float speed) {
        wallSlideSpeed = speed;
    }

    public void SetIsTouchingWall(boolean touching, boolean isWallOnLeft) {
        this.isTouchingWall = touching;
        if (touching){ // on met à jour le côté touché
            this.isWallOnLeft = isWallOnLeft;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (animationManager != null) {
            animationManager.update(deltaTime);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (animationManager != null) {
            TextureRegion currentframe = animationManager.getFrame();
            if (currentframe.isFlipX() != facingLeft) {
                currentframe.flip(true, false); // on retourne le x mais pas le y
            }
            batch.draw(currentframe, GetX(), GetY(), GetBounds().width, GetBounds().height);
        }
    }
}
