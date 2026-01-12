package com.EthanKnittel.respawn;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.utils.Array;

/**
 * Interface pour le pattern "Factory" (Usine) de création d'ennemis.
 * <p>
 * Le but de cette interface est de permettre au système de Spawn ({@link SpawnPoint})
 * de générer des ennemis sans savoir exactement de quelle classe ils sont (Cactus, Ordi...).
 * </p>
 * <p>
 * Chaque type d'ennemi aura sa propre usine (ex: {@link CactusFactory}) qui implémente cette méthode.
 * </p>
 */
public interface EnemyFactory {

    /**
     * Crée une nouvelle instance d'un ennemi à une position donnée.
     *
     * @param x           Position X d'apparition.
     * @param y           Position Y d'apparition.
     * @param target      La cible principale de l'ennemi (le Joueur).
     * @param allEntities La liste des entités du monde (pour que l'ennemi puisse voir les murs).
     * @return Une nouvelle instance de {@link Foe} (Ennemi) prête à être ajoutée au monde.
     */
    Foe create(float x, float y, Player target, Array<Entity> allEntities);
}
