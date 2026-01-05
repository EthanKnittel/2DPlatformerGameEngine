package com.EthanKnittel.world;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.artifacts.Wall;
import com.badlogic.gdx.utils.Array;

public class TestLevel extends Level {
    public TestLevel() {
        // vide
    }

    @Override
    public Array<Entity> load() {
        Array<Entity> generatedEntities = new Array<>();
        // un sol
        float wallSize = 4f;
        for (int i=0; i<10; i++){
            generatedEntities.add(new Wall(i * wallSize,0, wallSize,wallSize, true));
        }

        // un plafond
        for (int i=0; i<5; i++){
            generatedEntities.add(new Wall(i * wallSize,4 *  wallSize,wallSize,wallSize, true));
        }

        // un mur à gauche
        for (int i=0; i<5; i++){
            generatedEntities.add(new Wall(0, i * wallSize,wallSize,wallSize, true));
        }

        // un mur à droite
        for (int i=0; i<50; i++){
            generatedEntities.add(new Wall(wallSize * 10, i * wallSize,wallSize,wallSize, true));
        }

        return generatedEntities;

    }

    @Override
    public void dispose() {}
}
