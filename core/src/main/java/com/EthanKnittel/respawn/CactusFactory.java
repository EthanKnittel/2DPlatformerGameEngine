package com.EthanKnittel.respawn;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.entities.agents.foes.Cactus;
import com.badlogic.gdx.utils.Array;

/**
 * Usine concrète pour produire des Cactus.
 * <p>
 * Cette classe implémente {@link EnemyFactory} pour encapsuler l'instanciation
 * de la classe {@link Cactus}.
 * </p>
 */
public class CactusFactory implements EnemyFactory {

    /**
     * Fabrique un nouvel ennemi de type Cactus.
     *
     * @param x           Position X.
     * @param y           Position Y.
     * @param target      Le joueur cible.
     * @param allentities La liste des entités du monde.
     * @return Une nouvelle instance de {@link Cactus}.
     */
    @Override
    public Foe create(float x, float y, Player target, Array<Entity> allentities){
        return new Cactus(x, y, target, allentities);
    }
}
