package com.EthanKnittel.respawn;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.entities.agents.foes.Ordi;
import com.badlogic.gdx.utils.Array;

/**
 * Usine concrète pour produire des ennemis "Ordi".
 * <p>
 * Cette classe implémente {@link EnemyFactory} pour encapsuler l'instanciation
 * de la classe {@link Ordi}.
 * </p>
 */
public class OrdiFactory implements EnemyFactory {

    /**
     * Fabrique un nouvel ennemi de type Ordi.
     *
     * @param x           Position X.
     * @param y           Position Y.
     * @param target      Le joueur cible.
     * @param allentities La liste des entités du monde.
     * @return Une nouvelle instance de {@link Ordi}.
     */
    @Override
    public Foe create(float x, float y, Player target, Array<Entity> allentities){
        return new Ordi(x, y, target, allentities);
    }
}
