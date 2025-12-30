package com.EthanKnittel.respawn;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class SpawnPoint {
    private final Vector2 position;
    private final Array<EnemyFactory> allowedFactories;
    private final Random random;

    public SpawnPoint(float x, float y){
        this.position = new Vector2(x, y);
        this.allowedFactories = new Array<>();
        this.random = new Random();
    }

    public void addAllowedFactory(EnemyFactory factory){
        if (factory != null && !allowedFactories.contains(factory, true)){
            allowedFactories.add(factory);
        }
    }

    public Vector2 getPosition(){
        return position;
    }

    public boolean hasAllowedFactories(){
        return !allowedFactories.isEmpty();
    }

    public EnemyFactory getRandomFactory(){
        if (allowedFactories.size == 0){
            return EnemyRegistry.getDefaultFactory();
        }
        return allowedFactories.get(random.nextInt(allowedFactories.size));
    }

}
