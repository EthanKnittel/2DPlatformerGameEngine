package com.EthanKnittel.respawn;

import com.EthanKnittel.ai.EnemyStrategy;
import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Gère une zone géographique d'apparition d'ennemis (Wave Manager).
 * <p>
 * Une SpawnZone est un rectangle invisible défini dans l'éditeur de niveau.
 * Elle contient plusieurs {@link SpawnPoint}s.
 * </p>
 * <p>
 * <b>Fonctionnement :</b>
 * <ol>
 * <li>Le joueur entre dans la zone (collision avec le rectangle).</li>
 * <li>La zone vérifie s'il y a assez d'ennemis vivants ({@code activeFoes}).</li>
 * <li>Si le nombre est inférieur au minimum requis, elle lance une vague de réapparition.</li>
 * </ol>
 * </p>
 */
public class SpawnZone {
    /** Les limites géographiques de la zone (Rectangle Tiled). */
    private final Rectangle zoneBounds;

    /** Liste des points précis où les monstres peuvent apparaître dans cette zone. */
    private final Array<SpawnPoint> spawnPoints;

    /** Liste des ennemis actuellement vivants gérés par cette zone. */
    private final Array<Foe> activeFoes;

    // --- RÈGLES DE JEU (Game Design) ---
    /** Nombre maximum d'ennemis simultanés dans cette zone (Plafond). */
    private final int maxEnemiesInZone = 15;

    /** Seuil de déclenchement : si on tombe sous ce nombre, on respawn. */
    private final int minEnemiesInZone = 3;

    /**
     * Distance minimale (en blocs/mètres) entre le joueur et un point de spawn.
     * Empêche un monstre d'apparaître trop proche du joueur.
     */
    private final float minDistanceToPlayer = 10.0f;

    /**
     * Crée une nouvelle zone de spawn.
     * @param zoneBounds Le rectangle définissant la zone (depuis Tiled).
     */
    public  SpawnZone(Rectangle zoneBounds) {
        this.zoneBounds = zoneBounds;
        this.spawnPoints = new Array<>();
        this.activeFoes = new Array<>();
    }

    /** Ajoute un point de spawn à cette zone (appelé lors du chargement du niveau). */
    public void addSpawnPoint(SpawnPoint spawnPoint) {
        this.spawnPoints.add(spawnPoint);
    }

    public Rectangle getZoneBounds() {
        return zoneBounds;
    }

    /**
     * Met à jour la logique de la zone.
     * Appelée à chaque frame par le GameScreen.
     *
     * @param deltaTime        Temps écoulé.
     * @param player           Le joueur (pour vérifier s'il est dedans).
     * @param globalEntityList La liste globale des entités du jeu (pour y ajouter les nouveaux nés).
     */
    public void update(float deltaTime, Player player, Array<Entity> globalEntityList) {

        // 1. Nettoyage de la liste locale (on oublie les morts)
        // On parcourt à l'envers pour pouvoir supprimer sans casser les index.
        for (int i = activeFoes.size - 1; i >= 0; i--) {
            Foe foe = activeFoes.get(i);
            // Si l'ennemi est marqué pour suppression (mort + anim finie)
            if (foe.getCanBeRemove()) {
                activeFoes.removeIndex(i);
            }
        }

        // 2. Vérification de présence du joueur
        // On utilise le centre du joueur pour être précis
        float pX = player.getX() + player.getbounds().width / 2f;
        float pY = player.getY() + player.getbounds().height / 2f;

        if (zoneBounds.contains(pX, pY)) {
            // Le joueur est DANS la zone.
            // Faut-il faire apparaître des renforts ?
            if (activeFoes.size <= minEnemiesInZone) {
                spawnWave(player, globalEntityList);
            }
        }
        // Note : Si le joueur sort de la zone, les ennemis restants ne despawnent pas (choix de design).
    }

    /**
     * Génère une vague d'ennemis pour remplir la zone jusqu'à son maximum.
     */
    private void spawnWave(Player player, Array<Entity> globalEntityList) {
        // On calcule combien d'ennemis il manque pour être complet
        int enemiesToSpawn = maxEnemiesInZone - activeFoes.size;

        for (SpawnPoint spawnPoint : spawnPoints) {
            // Si on a atteint le quota, on arrête de boucler sur les points
            if (enemiesToSpawn <= 0) {
                break;
            }

            // Sécurité : Si le point est mal configuré (pas d'usine), on le saute
            if (!spawnPoint.hasAllowedFactories()) {
                continue;
            }

            // 1. Test de distance (Fair-play)
            float dist = Vector2.dst(player.getX(), player.getY(), spawnPoint.getPosition().x, spawnPoint.getPosition().y);

            // On ne fait apparaître le monstre que si le joueur est assez loin
            if (dist >= minDistanceToPlayer) {

                // 2. Création de l'entité (le corps physique)
                // Le SpawnPoint choisit aléatoirement un type (Cactus ou Ordi)
                EnemyFactory factory = spawnPoint.getRandomFactory();
                Foe newFoe = factory.create(spawnPoint.getPosition().x, spawnPoint.getPosition().y, player, globalEntityList);

                // 3. Configuration de l'IA (le cerveau)
                // On récupère les instructions "forcées" depuis Tiled (ex: ["Chase", "Patrol"])
                Array<String> strategiesToCheck = spawnPoint.getForcedStrategies();

                if (strategiesToCheck != null && strategiesToCheck.size > 0) {
                    // Création du conteneur "Cerveau"
                    com.EthanKnittel.ai.BrainStrategy brain = new com.EthanKnittel.ai.BrainStrategy();

                    // On transforme chaque nom (String) en classe Java via le Registre
                    for (String strategyName : strategiesToCheck) {
                        com.EthanKnittel.ai.EnemyStrategy s = com.EthanKnittel.ai.StrategyRegistry.create(strategyName);
                        if (s != null) {
                            brain.addStrategy(s);
                        }
                    }

                    // Greffe du cerveau sur le monstre
                    newFoe.setStrategy(brain);
                }

                // 4. Enregistrement final
                activeFoes.add(newFoe);       // Pour le suivi de la zone (quota)
                globalEntityList.add(newFoe); // Pour le moteur physique et l'affichage

                enemiesToSpawn--; // Un de moins à faire !
            }
        }
    }
}
