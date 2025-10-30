package com.EthanKnittel.entities;

import com.EthanKnittel.Evolving;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public abstract class Entity implements Evolving,Disposable {
    protected Vector2 position;
    public Vector2 velocity= new Vector2();
    public Rectangle bounds; // pour la hitbox
    public boolean collision=false;

    public Entity(float x, float y,  float width, float height) {
        // on initialise le vecteur de LibGDX
        this.position = new Vector2(x, y);
        // limites du rectangle de hitbox
        this.bounds = new Rectangle(x, y, width, height);
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
    public Rectangle GetBounds() {
        return bounds;
    }
    public void SetPosXY(float x, float y) {
        position.set(x, y);
        bounds.setPosition(x,y);
    }
}
