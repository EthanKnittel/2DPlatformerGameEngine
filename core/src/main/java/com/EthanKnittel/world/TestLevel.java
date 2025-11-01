package com.EthanKnittel.world;

import com.EthanKnittel.entities.artifacts.Wall;

public class TestLevel extends Level {
    public TestLevel() {
        // vide
    }

    @Override
    public void load(Environment environment) {
        // un sol
        for (int i=0; i<100; i++){
            float wallSize = 64f;
            environment.addEntity(new Wall(i * wallSize,0));
        }

        // un plafond
        for (int i=0; i<50; i++){
            float wallSize = 64f;
            environment.addEntity(new Wall(i * wallSize,4 *  wallSize));
        }

        // un mur à gauche
        for (int i=0; i<5; i++){
            float wallSize = 64f;
            environment.addEntity(new Wall(0, i * wallSize));
        }

        // un mur à droite
        for (int i=0; i<5; i++){
            float wallSize = 64f;
            environment.addEntity(new Wall(wallSize * 10, i * wallSize));
        }

    }

    @Override
    public void dispose() {

    }


}
