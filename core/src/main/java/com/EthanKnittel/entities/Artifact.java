package com.EthanKnittel.entities;

public abstract class Artifact extends Entity{
    public Artifact (float x, float y, float width, float height) {
        super(x,y, width,height);
        setCollision(true);
    }
}
