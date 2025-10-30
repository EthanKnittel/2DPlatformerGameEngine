package com.EthanKnittel.entities;

public abstract class Agent extends Entity {

    protected int maxHealth;
    protected int currenthealth;
    protected int damage;


    public Agent(float x, float y, float width, float height, int maxHealth, int damage) {
        super(x, y,  width, height);
        this.maxHealth = maxHealth;
        this.currenthealth = maxHealth;
        this.damage = damage;
    }
}
