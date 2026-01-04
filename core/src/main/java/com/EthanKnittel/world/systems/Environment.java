package com.EthanKnittel.world.systems;

import com.EthanKnittel.Evolving;
import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.world.Level;
import com.badlogic.gdx.utils.Array;

public class Environment implements Evolving {

    private Level currentlevel;
    private Array<Entity> entities;

    private PhysicSystem physics;

    public Environment() {
        entities = new Array<>();
        physics = new PhysicSystem(null);
    }

    public void setLevel(Level level) {
        if (this.currentlevel != null) {
            this.currentlevel.dispose();
            entities.clear();
        }
        this.currentlevel = level;
        physics = new PhysicSystem(this.currentlevel);

        Array<Entity> levelEntities = this.currentlevel.load();
        this.entities.addAll(levelEntities);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    @Override
    public void update(float deltaTime) {
        // On met les entités à jour

        for (int i = entities.size - 1; i >= 0; i--) {
            if (entities.get(i).getCanBeRemove()) {
                entities.removeIndex(i);
            }
        }
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);
            entity.update(deltaTime);
        }

        physics.update(deltaTime, entities);
    }

    public Array<Entity> getEntities() {
        return entities;
    }

    public Level getLevel() {
        return currentlevel;
    }
}
