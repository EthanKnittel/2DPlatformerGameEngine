package com.EthanKnittel.entities;

import com.EthanKnittel.Evolving;
import com.EthanKnittel.game.GameScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public abstract class Entity implements Evolving,Disposable {
    private Vector2 position;
    private Vector2 velocity= new Vector2();
    private Rectangle bounds; // pour la hitbox
    private boolean collision = false;
    private boolean affectedByGravity = false;
    private static float default_gravity = -980f/ GameScreen.getPixelsPerBlocks();

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
    public Vector2 GetVelocity() {
        return velocity;
    }
    public void SetVelocityX(float x) {
        velocity.x = x;
    }
    public void SetVelocityY(float y) {
        velocity.y = y;
    }
    public void SetVelocity(float x, float y) {
        velocity.x = x;
        velocity.y = y;
    }

    public boolean GetCollision() {
        return collision;
    }
    public void SetCollision(boolean collision) {
        this.collision = collision;
    }

    public boolean GetAffectedByGravity() {
        return affectedByGravity;
    }
    public void SetAffectedByGravity(boolean affectedByGravity) {
        this.affectedByGravity = affectedByGravity;
    }
    public void Setgravity(float gravity) {
        default_gravity = gravity;
    }
    public static float GetGravity() {
        return default_gravity;
    }
}
