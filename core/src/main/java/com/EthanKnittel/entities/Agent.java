package com.EthanKnittel.entities;

import com.EthanKnittel.game.GameScreen;
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
    private static float wallSlideSpeed = -200f/ GameScreen.getPixelsPerBlocks();
    private float moveSpeed = 150f/ GameScreen.getPixelsPerBlocks();
    private float jumpSpeed = 400f/ GameScreen.getPixelsPerBlocks();

    private AnimationManager animationManager;
    private boolean facingLeft = false;



    public Agent(float x, float y, float width, float height, int maxHealth, int damage) {
        super(x, y,  width, height);
        this.maxHealth = maxHealth;
        this.currenthealth = maxHealth;
        this.damage = damage;
        this.setAffectedByGravity(true);
        this.setIsAgent(true);
    }

    public void setAnimation(Animation<TextureRegion> animation){
        if (animationManager == null) {
            animationManager = new AnimationManager(animation);
        } else {
            animationManager.setAnimation(animation);
        }
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
    public void setJumpSpeed(float jumpSpeed) {
        this.jumpSpeed = jumpSpeed;
    }
    public float getMoveSpeed() {
        return moveSpeed;
    }
    public float getJumpSpeed() {
        return jumpSpeed;
    }

    public void setFacingLeft(boolean facingLeft) {
        this.facingLeft = facingLeft;
    }

    public boolean getFacingLeft() {
        return facingLeft;
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
    public boolean getTouchingWall() {
        return isTouchingWall;
    }
    public boolean getWallOnLeft() {
        return isWallOnLeft;
    }
    public float getWallSlideSpeed() {
        return wallSlideSpeed;
    }
    public void setWallSlideSpeed(float speed) {
        wallSlideSpeed = speed;
    }

    public void setIsTouchingWall(boolean touching, boolean isWallOnLeft) {
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
            batch.draw(currentframe, getX(), getY(), getbounds().width, getbounds().height);
        }
    }
}
