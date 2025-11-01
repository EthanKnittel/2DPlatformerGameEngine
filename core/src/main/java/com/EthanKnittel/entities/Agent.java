package com.EthanKnittel.entities;


public abstract class Agent extends Entity {

    private int maxHealth;
    private int currenthealth;
    private int damage;
    private boolean isGrounded = false;
    private boolean isTouchingWall = false;
    private boolean isWallOnLeft = false;
    private static float wallSlideSpeed = -200f;



    public Agent(float x, float y, float width, float height, int maxHealth, int damage) {
        super(x, y,  width, height);
        this.maxHealth = maxHealth;
        this.currenthealth = maxHealth;
        this.damage = damage;
        this.SetAffectedByGravity(true);
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
    public void SetGrounded(boolean grounded) {
        isGrounded = grounded;
    }
    public boolean GetIsGrounded() {
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


}
