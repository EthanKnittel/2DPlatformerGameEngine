package com.EthanKnittel.respawn;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.utils.Array;

public interface EnemyFactory {
    Foe create(float x, float y, Player target, Array<Entity> allEntities);
}
