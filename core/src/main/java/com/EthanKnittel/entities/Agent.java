package com.EthanKnittel.entities;

public abstract class Agent extends Entity {

    protected int maxHealth;
    protected int health = maxHealth;
    protected int damage;


    public Agent(float x, float y, int maxHealth, int damage) {
        super(x, y);
        this.maxHealth = maxHealth;
        this.damage = damage;
    }
}
