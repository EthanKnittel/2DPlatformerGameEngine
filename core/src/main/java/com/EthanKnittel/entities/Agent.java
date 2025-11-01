package com.EthanKnittel.entities;


public abstract class Agent extends Entity {

    private int maxHealth;
    private int currenthealth;
    private int damage;

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

}
