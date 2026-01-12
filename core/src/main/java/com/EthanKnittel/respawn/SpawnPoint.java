package com.EthanKnittel.respawn;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.Random;

/**
 * Représente un emplacement géographique précis où un ennemi peut apparaître.
 * <p>
 * Un SpawnPoint ne décide pas <i>quand</i> faire apparaître un monstre (ça, c'est le rôle de la {@link SpawnZone}),
 * mais il décide <i>quoi</i> faire apparaître et <i>comment</i>.
 * </p>
 * <p>
 * Il contient :
 * <ul>
 * <li>Une position (X, Y).</li>
 * <li>Une liste d'usines autorisées (ex: peut spawner un Cactus OU un Ordi).</li>
 * <li>Une liste de stratégies forcées (ex: "Ce point fait apparaître des monstres qui chassent le joueur").</li>
 * </ul>
 * </p>
 */
public class SpawnPoint {

    /** Coordonnées précises du point d'apparition. */
    private final Vector2 position;

    /** * Liste des usines (types d'ennemis) que ce point a le droit d'utiliser.
     * Si la liste contient [CactusFactory, OrdiFactory], le point choisira l'un des deux au hasard.
     */
    private final Array<EnemyFactory> allowedFactories;

    /** Générateur de nombres aléatoires pour choisir l'ennemi. */
    private final Random random;

    /**
     * Liste des noms de stratégies (String) à appliquer aux ennemis créés ici.
     * <p>
     * Ces noms proviennent directement des propriétés personnalisées de l'éditeur Tiled
     * (ex: case à cocher "Chase"). Ils seront convertis en vraies stratégies IA
     * plus tard par la {@link SpawnZone} via le {@link com.EthanKnittel.ai.StrategyRegistry}.
     * </p>
     */
    private final Array<String> forcedStrategies = new Array<>();

    /**
     * Crée un nouveau point de spawn.
     *
     * @param x Position X dans le monde.
     * @param y Position Y dans le monde.
     */
    public SpawnPoint(float x, float y){
        this.position = new Vector2(x, y);
        this.allowedFactories = new Array<>();
        this.random = new Random();
    }

    /**
     * Ajoute un type d'ennemi possible pour ce point.
     *
     * @param factory L'usine à ajouter (ex: {@link CactusFactory}).
     */
    public void addAllowedFactory(EnemyFactory factory){
        if (factory != null && !allowedFactories.contains(factory, true)){
            allowedFactories.add(factory);
        }
    }

    /**
     * Ajoute une stratégie que l'ennemi devra adopter à sa naissance.
     * Appelée lors du chargement de la carte (TiledMap).
     *
     * @param strategyName Le nom de la stratégie (ex: "Chase").
     */
    public void addForcedStrategy(String strategyName) {
        if (strategyName != null && !forcedStrategies.contains(strategyName, false)) {
            forcedStrategies.add(strategyName);
        }
    }

    /**
     * Récupère la liste des stratégies imposées.
     * @return Une liste de Strings (noms des stratégies).
     */
    public Array<String> getForcedStrategies() {
        return forcedStrategies;
    }

    /**
     * Helper pour définir une stratégie unique (compatibilité/simplification).
     * Efface les anciennes stratégies pour ne garder que celle-ci.
     */
    public void setForcedStrategy(String strategyName) {
        forcedStrategies.clear();
        addForcedStrategy(strategyName);
    }

    public Vector2 getPosition(){
        return position;
    }

    /**
     * Vérifie si ce point est configuré correctement (s'il sait quoi faire apparaître).
     * @return true si au moins une usine est associée.
     */
    public boolean hasAllowedFactories(){
        return !allowedFactories.isEmpty();
    }

    /**
     * Choisit aléatoirement un type d'ennemi parmi ceux autorisés.
     *
     * @return Une instance de {@link EnemyFactory} (ex: CactusFactory ou OrdiFactory).
     * Si la liste est vide, renvoie l'usine par défaut du registre pour éviter un crash.
     */
    public EnemyFactory getRandomFactory(){
        if (allowedFactories.size == 0){
            return EnemyRegistry.getDefaultFactory(); // Sécurité (Fallback)
        }
        // Choix aléatoire : random.nextInt(taille) renvoie un index entre 0 et taille-1
        return allowedFactories.get(random.nextInt(allowedFactories.size));
    }
}
