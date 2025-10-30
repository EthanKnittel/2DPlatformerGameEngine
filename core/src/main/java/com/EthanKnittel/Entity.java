package com.EthanKnittel;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public abstract class Entity implements Evolving,Disposable {
    protected Vector2 position;

    public Entity(float x, float y) {
        // on initialise le vecteur de LibGDX
        this.position = new Vector2(x, y);
    }

    @Override
    public abstract void update(float deltaTime);

    public abstract void render(SpriteBatch batch);

    @Override
    public abstract void dispose();

    public float GetX() {
        return  position.x;
    }
    public float GetY() {
        return  position.y;
    }
    public void SetPosXY(float x, float y) {
        position.set(x, y);
    }
}
