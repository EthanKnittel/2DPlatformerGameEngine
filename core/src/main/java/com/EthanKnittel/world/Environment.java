package com.EthanKnittel.world;

import com.EthanKnittel.Evolving;
import com.EthanKnittel.entities.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Environment implements Disposable, Evolving {

    private Level currentlevel;
    private Array<Entity> entities;

    public Environment() {
        entities = new Array<>();
    }

    public void setLevel(Level level) {
        if (this.currentlevel != null) {
            this.currentlevel.dispose();
        }
        this.currentlevel = level;
        this.currentlevel.load(this);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    @Override
    public void update(float delta) {
        // mise à jour des entités
        for(Entity entity : entities){
            entity.update(delta);
        }

        for (Entity entity : entities){
            if (entity.velocity.isZero() || !entity.collision) {
                continue;
            }

            // future position X
            float newX = entity.GetX() + entity.velocity.x;

            Rectangle futureboundsX = new Rectangle(entity.GetBounds());
            futureboundsX.setX(newX);
            boolean collisionX = false;

            for (Entity other : entities) {
                if (entity==other || !other.collision) {
                    continue;
                }
                if (futureboundsX.overlaps(other.GetBounds())) {
                    collisionX = true; // collision
                    entity.velocity.x = 0; // on stoppe le mouvement x
                    break;
                }
            }

            // pas de collisions -> on bouge
            if (!collisionX){
                entity.SetPosXY(newX, entity.GetY());
            }

            //future position Y
            float newY = entity.GetY() + entity.velocity.y;

            Rectangle futureboundsY = new Rectangle(entity.GetBounds());
            futureboundsY.setY(newY);

            boolean collisionY = false;
            for (Entity other : entities) {
                if (entity==other || !other.collision) {
                    continue;
                }
                if (futureboundsY.overlaps(other.GetBounds())) {
                    collisionY = true;
                    entity.velocity.y = 0;
                    break;
                }
            }
            if (!collisionY){
                entity.SetPosXY(entity.GetX(), newY);
            }
        }
    }

    public void  render(SpriteBatch batch) {
        for(Entity entity : entities){
            entity.render(batch);
        }
    }

    @Override
    public void dispose() {
        if (currentlevel != null) {
            currentlevel.dispose();
        }
        for(Entity entity : entities){
            entity.dispose();
        }
    }
}
