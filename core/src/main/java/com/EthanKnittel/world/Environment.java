package com.EthanKnittel.world;

import com.EthanKnittel.Evolving;
import com.EthanKnittel.entities.Entity;
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
