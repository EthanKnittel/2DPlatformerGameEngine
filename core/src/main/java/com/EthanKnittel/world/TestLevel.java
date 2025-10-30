package com.EthanKnittel.world;

import com.EthanKnittel.entities.artifacts.Wall;

public class TestLevel extends Level {
    public TestLevel() {
        // vide
    }

    @Override
    public void load(Environment environment) {
        for (int i=0; i<10; i++){
            float wallSize = 64f;
            environment.addEntity(new Wall(i * wallSize,0));
        }
        for (int i=0; i<5; i++){
            float wallSize = 64f;
            environment.addEntity(new Wall(0, i * wallSize));
        }
    }

    @Override
    public void dispose() {

    }


}
