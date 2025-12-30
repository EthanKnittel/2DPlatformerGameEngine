package com.EthanKnittel.respawn;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class SpawnZone {
    private final Rectangle zoneBounds;
    private final Array<SpawnPoint> spawnPoints;
    private final Array<Foe> activeFoes;

    private final int maxEnemiesInZone = 15;
    private final int minEnemiesInZone = 3;
    private final float minDistanceToPlayer = 10.0f;

    public  SpawnZone(Rectangle zoneBounds) {
        this.zoneBounds = zoneBounds;
        this.spawnPoints = new Array<>();
        this.activeFoes = new Array<>();
    }

    public void addSpawnPoint(SpawnPoint spawnPoint) {
        this.spawnPoints.add(spawnPoint);
    }
    public Rectangle getZoneBounds() {
        return zoneBounds;
    }
    public void update(float deltaTime, Player player, Array<Entity> globalEntityList) {

        // Nettoyage de la liste (on supprime les morts)
        for (int i = activeFoes.size - 1; i >= 0; i--) {
            Foe foe = activeFoes.get(i);
            if (foe.getCanBeRemove()) {
                activeFoes.removeIndex(i);
            }
        }

        // On vérifie si le joueur est dans la zone
        float pX = player.getX() + player.getbounds().width/2f;
        float pY = player.getY() + player.getbounds().height/2f;

        if (zoneBounds.contains(pX, pY)) {
            if (activeFoes.size <= minEnemiesInZone) {
                spawnWave(player, globalEntityList);
            }
        }
    }

    private void spawnWave(Player player, Array<Entity> globalEntityList) {
        int enemiesToSpawn = maxEnemiesInZone - activeFoes.size;

        for (SpawnPoint spawnPoint : spawnPoints) {
            if (enemiesToSpawn <=0) break;
            if (!spawnPoint.hasAllowedFactories()) continue;

            float dist= Vector2.dst(player.getX(), player.getY(), spawnPoint.getPosition().x, spawnPoint.getPosition().y);

            if (dist >= minDistanceToPlayer) {
                EnemyFactory factory = spawnPoint.getRandomFactory();
                Foe newFoe = factory.create(spawnPoint.getPosition().x, spawnPoint.getPosition().y, player, globalEntityList);

                activeFoes.add(newFoe);
                globalEntityList.add(newFoe);

                enemiesToSpawn = enemiesToSpawn - 1;
            }
        }
    }
}
