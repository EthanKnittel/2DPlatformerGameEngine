package com.EthanKnittel.entities;

import com.EthanKnittel.game.GameScreen;

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
    private float invincibilityTimer = 0f;
    private boolean isHit = false;
    private float hitStunDuration = 0.4f; //
    private float hitTimer = 0f;
    private float invincibilityDuration = 1.0f;
    private float visualHitTimer = 0f;
    private float visualHitDuration = 0.2f;

    private boolean facingLeft = false;

    private String jumpSoundName = null;



    public Agent(float x, float y, float width, float height, int maxHealth, int damage) {
        super(x, y,  width, height);
        this.maxHealth = maxHealth;
        this.currenthealth = maxHealth;
        this.damage = damage;
        this.setAffectedByGravity(true);
        this.setIsAgent(true);
    }

    public void setJumpSoundName(String jumpSoundName) {
        this.jumpSoundName = jumpSoundName;
    }
    public String getJumpSoundName() {
        return jumpSoundName;
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
    public boolean getAlive() {
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

    public void takeDamage(int amount) {
        if (invincibilityTimer > 0) return; // invincible = 0 dégats

        this.currenthealth -= amount;
        if (this.currenthealth < 0) this.currenthealth = 0;

        if (hitStunDuration > 0){
            this.isHit=true;
            this.hitTimer = hitStunDuration;
        }

        this.visualHitTimer = visualHitDuration;

        this.invincibilityTimer = invincibilityDuration;

        // effet knockback
        this.setVelocityY(100f / GameScreen.getPixelsPerBlocks());
        this.setVelocityX(100f / GameScreen.getPixelsPerBlocks());
    }

    @Override
    public void update(float deltaTime) {
        if (invincibilityTimer > 0) {
            invincibilityTimer -= deltaTime;
        }
        if (isHit) {
            hitTimer -= deltaTime;
            if (hitTimer <= 0) {
                isHit = false;
            }
        }
        if (visualHitTimer > 0) {
            visualHitTimer -= deltaTime;
        }
    }

    public boolean isHit() {
        return isHit;
    }
    public boolean getVisualHitActive() {
        return visualHitTimer > 0;
    }
    public void setVisualHitDuration(float duration) {
        this.visualHitDuration = duration;
    }

    public void setHitStunDuration(float hitStunDuration) {
        this.hitStunDuration = hitStunDuration;
    }
    public void setInvincibilityDuration(float invincibilityDuration) {
        this.invincibilityDuration = invincibilityDuration;
    }
}
