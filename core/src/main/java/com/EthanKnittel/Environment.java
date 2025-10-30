package com.EthanKnittel;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Environment implements Disposable,Evolving {

    // private Level level

    private Array<Entity> entities;

    public Environment() {
        entities = new Array<>();
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
    }

    public void  render(SpriteBatch batch) {
        for(Entity entity : entities){
            entity.render(batch);
        }
    }

    @Override
    public void dispose() {
        for(Entity entity : entities){
            entity.dispose();
        }
    }
}
