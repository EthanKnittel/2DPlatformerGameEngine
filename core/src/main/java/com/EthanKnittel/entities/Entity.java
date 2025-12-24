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
    private boolean isAgent = false;
    private boolean isEnemy = false;
    private boolean isPlayer = false;
    private boolean canBeRemove = false;


    public Entity(float x, float y,  float width, float height) {
        // on initialise le vecteur de LibGDX
        this.position = new Vector2(x, y);
        // limites du rectangle de hitbox
        this.bounds = new Rectangle(x, y, width, height);
    }

    public boolean getIsEnemy(){
        return isEnemy;
    }
    public void setIsEnemy(boolean isEnemy){
        this.isEnemy = isEnemy;
    }

    public boolean getIsAgent() {
        return isAgent;
    }

    public void setIsAgent(boolean isAgent) {
        this.isAgent = isAgent;
    }
    public void setIsPlayer(boolean isPlayer) {
        this.isPlayer = isPlayer;
    }
    public boolean getIsPlayer(){
        return isPlayer;
    }

    @Override
    public abstract void update(float deltaTime);

    public abstract void render(SpriteBatch batch);

    @Override
    public abstract void dispose();

    public float getX() {
        return  position.x;
    }
    public float getY() {
        return  position.y;
    }

    public Rectangle getbounds() {
        return bounds;
    }
    public void setPosXY(float x, float y) {
        position.set(x, y);
        bounds.setPosition(x,y);
    }
    public Vector2 getVelocity() {
        return velocity;
    }
    public void setVelocityX(float x) {
        velocity.x = x;
    }
    public void setVelocityY(float y) {
        velocity.y = y;
    }
    public void setVelocity(float x, float y) {
        velocity.x = x;
        velocity.y = y;
    }

    public boolean getCollision() {
        return collision;
    }
    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public boolean getAffectedByGravity() {
        return affectedByGravity;
    }
    public void setAffectedByGravity(boolean affectedByGravity) {
        this.affectedByGravity = affectedByGravity;
    }
    public void setGravity(float gravity) {
        default_gravity = gravity;
    }
    public static float getGravity() {
        return default_gravity;
    }
    public void setCanBeRemove(boolean canBeRemove) {
        this.canBeRemove = canBeRemove;
    }

    public boolean getCanBeRemove() {
        return this.canBeRemove;
    }
}
