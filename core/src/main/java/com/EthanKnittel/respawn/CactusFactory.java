package com.EthanKnittel.respawn;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.entities.agents.foes.Cactus;
import com.badlogic.gdx.utils.Array;

public class CactusFactory implements EnemyFactory {
    @Override
    public Foe create(float x, float y, Player target, Array<Entity> allentities){
        return new Cactus(x, y, target, allentities);
    }
}
