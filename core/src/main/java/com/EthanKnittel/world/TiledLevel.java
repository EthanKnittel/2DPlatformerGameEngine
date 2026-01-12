package com.EthanKnittel.world;

import com.EthanKnittel.ai.BrainStrategy;
import com.EthanKnittel.ai.EnemyStrategy;
import com.EthanKnittel.ai.StrategyRegistry;
import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.entities.artifacts.Wall;
import com.EthanKnittel.game.GameScreen;
import com.EthanKnittel.respawn.*; // Importe tout le dossier respawn (SpawnPoint, Zone, etc)
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Implémentation d'un {@link Level} chargé depuis un fichier Tiled (.tmx).
 * <p>
 * Cette classe est le "parseur" qui transforme les données brutes de la carte (Tuiles, Objets, Calques)
 * en entités du jeu (Murs, Ennemis, Zones de Spawn).
 * </p>
 * <p>
 * Elle gère notamment :
 * <ul>
 * <li>La conversion des coordonnées (Pixels Tiled -> Mètres/Blocs Box2D/Monde).</li>
 * <li>La lecture des propriétés personnalisées (ex: "collidable", "Chase", "Cactus").</li>
 * <li>La configuration des zones d'apparition d'ennemis.</li>
 * </ul>
 * </p>
 */
public class TiledLevel extends Level{

    /** L'objet TiledMap technique de LibGDX (contient toutes les données du fichier). */
    private TiledMap map;
    private final String mapFileName;

    /** Liste des zones de spawn configurées sur cette carte. */
    private Array<SpawnZone> spawnZones;

    /** Point de départ du joueur (lu depuis le calque "Setup"). */
    private Vector2 playerSpawnPoint;

    // --- CONSTANTES (Noms des calques dans Tiled) ---
    private static final String SpawnLayerName= "SpawnZones";
    private static final String SetupLayerName = "Setup"; // nom du calque de setup

    /**
     * Crée un niveau à partir d'un fichier .tmx.
     *
     * @param filename Chemin du fichier (ex: "maps/level1.tmx").
     */
    public TiledLevel(String filename){
        this.mapFileName = filename;
        this.spawnZones = new Array<>();
    }

    // --- GETTERS ---
    public Array<SpawnZone> getSpawnZones(){
        return spawnZones;
    }
    public Vector2 getPlayerSpawnPoint(){
        return playerSpawnPoint;
    }

    /** @return La carte brute (utile pour le {@link com.EthanKnittel.graphics.LevelView}). */
    public TiledMap getMap() {
        return map;
    }

    /**
     * Charge le niveau, analyse les calques et crée les entités physiques.
     *
     * @return La liste de toutes les entités statiques (Murs) créées.
     */
    @Override
    public Array<Entity> load() {
        Array<Entity> generatedEntities = new Array<>();

        // 1. Chargement du fichier TMX en mémoire
        map = new TmxMapLoader().load(mapFileName);

        // 2. Recherche du point de spawn du joueur (Calque "Setup")
        MapLayer setuplayer = map.getLayers().get(SetupLayerName);
        for (MapObject object : setuplayer.getObjects()) {
            if (object.getProperties().containsKey("playerSpawnPoint")) {
                float x = object.getProperties().get("x", Float.class);
                float y = object.getProperties().get("y", Float.class);

                // Conversion Pixels -> Unités Monde
                this.playerSpawnPoint = new Vector2(x / GameScreen.getPixelsPerBlocks(), y / GameScreen.getPixelsPerBlocks());
            }
        }

        // 3. Configuration des zones de spawn (Ennemis dynamiques)
        loadSpawnZones();

        // 4. Génération des Murs (Physique)
        // On parcourt tous les calques de la carte
        for (int i=0; i < map.getLayers().getCount(); i++) {
            MapLayer layer = map.getLayers().get(i);

            // On ne traite que les calques marqués "collidable" dans Tiled
            boolean isCollidable = layer.getProperties().get("collidable", false, boolean.class);
            if (!isCollidable) {
                continue;
            }

            // CAS A : Calque de Tuiles (Dessiné grille par grille)
            if (layer.getClass().equals(TiledMapTileLayer.class)) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
                int mapWidth = tileLayer.getWidth();
                int mapHeight = tileLayer.getHeight();

                // On scanne chaque case de la grille
                for (int x = 0; x < mapWidth; x++) {
                    for (int y = 0; y < mapHeight; y++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                        // Si la case contient une tuile, on crée un mur physique invisible par dessus
                        if (cell != null && cell.getTile() != null) {
                            // Wall(x, y, w, h, visible=false) car c'est le LevelView qui dessine la map
                            generatedEntities.add(new Wall(x, y, 1f, 1f, false));
                        }
                    }
                }
                // CAS B : Calque d'Objets (Rectangles placés librement)
            } else {
                for (MapObject object : layer.getObjects()) {
                    float scaledX, scaledY, scaledWidth, scaledHeight;

                    // Objet Rectangle standard
                    if (object.getClass().equals(RectangleMapObject.class)) {
                        Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                        scaledX = rectangle.x / GameScreen.getPixelsPerBlocks();
                        scaledY = rectangle.getY() / GameScreen.getPixelsPerBlocks();
                        scaledWidth = rectangle.width / GameScreen.getPixelsPerBlocks();
                        scaledHeight = rectangle.height / GameScreen.getPixelsPerBlocks();

                        // Objet Tuile (Image placée librement)
                    } else if (object.getClass().equals(TiledMapTileMapObject.class)) {
                        TiledMapTileMapObject tileObject = (TiledMapTileMapObject) object;
                        scaledX = tileObject.getX() / GameScreen.getPixelsPerBlocks();
                        scaledY = tileObject.getY() / GameScreen.getPixelsPerBlocks();

                        TextureRegion textureRegion = tileObject.getTile().getTextureRegion();
                        scaledWidth = textureRegion.getRegionWidth() / GameScreen.getPixelsPerBlocks();
                        scaledHeight = textureRegion.getRegionHeight() / GameScreen.getPixelsPerBlocks();

                    } else {
                        continue; // Type d'objet non supporté
                    }

                    // Ajout du mur physique
                    generatedEntities.add(new Wall(scaledX, scaledY, scaledWidth, scaledHeight, false));
                }
            }
        }

        return generatedEntities;
    }

    /**
     * Analyse le calque "SpawnZones" pour configurer le système de vagues d'ennemis.
     * <p>
     * Distingue deux types d'objets :
     * <ul>
     * <li><b>Points (SpawnPoint) :</b> Emplacements précis. Lisent les propriétés (ex: "Cactus"=true) pour savoir quel monstre faire apparaître.</li>
     * <li><b>Zones (SpawnZone) :</b> Rectangles géographiques qui regroupent les points qu'ils contiennent.</li>
     * </ul>
     * </p>
     */
    private void loadSpawnZones(){
        MapLayer spawnLayer = map.getLayers().get(SpawnLayerName);

        Array<SpawnZone> tempZones = new Array<>();
        Array<MapObject> tempPoints = new Array<>();

        // On récupère toute la liste d'ennemis connus d'un coup
        Array<EnemyAssociation> allKnownFoes = EnemyRegistry.getAllAssociations();

        // 1. Tri : On sépare les Points des Zones
        for (MapObject object : spawnLayer.getObjects()) {
            boolean isPoint = false;

            // Si l'objet a une propriété portant le nom d'un ennemi connu, c'est un Point de Spawn
            for (int i = 0; i < allKnownFoes.size; i++) {
                String enemyName = allKnownFoes.get(i).getName();
                if (object.getProperties().containsKey(enemyName)){
                    isPoint = true;
                    break;
                }
            }

            if (isPoint) {
                tempPoints.add(object);
            } else {
                // Sinon, si c'est un rectangle, c'est une Zone de déclenchement
                if (object.getClass().equals(RectangleMapObject.class)) {
                    Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                    float x = rectangle.x / GameScreen.getPixelsPerBlocks();
                    float y = rectangle.y / GameScreen.getPixelsPerBlocks();
                    float width = rectangle.width / GameScreen.getPixelsPerBlocks();
                    float height = rectangle.height / GameScreen.getPixelsPerBlocks();

                    tempZones.add(new SpawnZone(new Rectangle(x, y, width, height)));
                }
            }
        }

        // 2. Configuration des Points
        for (MapObject points : tempPoints) {
            float rawX = points.getProperties().get("x", Float.class);
            float rawY = points.getProperties().get("y", Float.class);
            float x =  rawX / GameScreen.getPixelsPerBlocks();
            float y = rawY / GameScreen.getPixelsPerBlocks();

            SpawnPoint spawnPoint = new SpawnPoint(x, y);

            // A. Détection des Stratégies (IA) forcées
            // On parcourt toutes les propriétés pour voir si elles correspondent à des stratégies connues (ex: "Chase")
            Array<String> listeDesProprietes = new Array<>();
            java.util.Iterator<String> it = points.getProperties().getKeys();
            while(it.hasNext()) {
                listeDesProprietes.add(it.next());
            }
            for (String key : listeDesProprietes) {
                // Si le nom de la propriété correspond à une stratégie connue dans notre registre
                if (StrategyRegistry.exists(key)) {
                    // Si la case est cochée (true)
                    boolean isEnabled = points.getProperties().get(key, false, Boolean.class);
                    if (isEnabled) {
                        spawnPoint.addForcedStrategy(key);
                    }
                }
            }

            // B. Détection des Types d'Ennemis autorisés
            for (int i=0; i < allKnownFoes.size; i++) {
                EnemyAssociation association = allKnownFoes.get(i);
                String enemyName = association.getName();

                // Si la propriété "Cactus" est true, on ajoute l'usine CactusFactory
                boolean isEnabled = false;
                try {
                    isEnabled = points.getProperties().get(enemyName, false, Boolean.class);
                } catch (Exception e) {
                    System.out.println("Erreur lecture propriété: " + enemyName);
                }

                if (isEnabled) {
                    spawnPoint.addAllowedFactory(association.getFactory());
                }
            }

            // 3. Assignation du Point à sa Zone
            // On regarde dans quel rectangle (Zone) tombe ce point (Point)
            for (SpawnZone zone : tempZones) {
                if (zone.getZoneBounds().contains(x,y)){
                    zone.addSpawnPoint(spawnPoint);
                    break;
                }
            }
        }

        this.spawnZones.addAll(tempZones);
    }

    /**
     * Fait apparaître les ennemis "statiques" placés directement dans le calque Setup via Tiled.
     * <p>
     * Ces ennemis sont présents dès le début du niveau et ne réapparaissent pas une fois tués.
     * </p>
     *
     * @param player           Le joueur (cible initiale).
     * @param globalEntityList La liste globale pour ajouter les ennemis créés.
     */
    public void spawnStaticMobs(Player player, Array<Entity> globalEntityList){
        MapLayer setupLayer = map.getLayers().get(SetupLayerName);

        if (setupLayer != null) {
            for (MapObject object : setupLayer.getObjects()) {
                String objectName = object.getName(); // ex: "Cactus"
                EnemyFactory factory = EnemyRegistry.getFactory(objectName);

                if (factory != null) {
                    float x = object.getProperties().get("x", Float.class) /  GameScreen.getPixelsPerBlocks();
                    float y = object.getProperties().get("y", Float.class) /   GameScreen.getPixelsPerBlocks();

                    Foe newFoe = factory.create(x, y, player, globalEntityList);

                    // Configuration IA (similaire aux SpawnPoints)
                    BrainStrategy brain = new BrainStrategy();
                    boolean strategyFound = false;

                    Array<String> listeDesProprietes = new Array<>();
                    java.util.Iterator<String> it = object.getProperties().getKeys();
                    while (it.hasNext()) {
                        listeDesProprietes.add(it.next());
                    }
                    for (String key : listeDesProprietes) {
                        // Si c'est une stratégie connue
                        if (StrategyRegistry.exists(key)) {
                            boolean isEnabled = object.getProperties().get(key, false, Boolean.class);

                            if (isEnabled) {
                                EnemyStrategy s = StrategyRegistry.create(key);
                                if (s != null) {
                                    brain.addStrategy(s);
                                    strategyFound = true;
                                }
                            }
                        }
                    }

                    // Si on a trouvé au moins une stratégie, on remplace le cerveau par défaut
                    if (strategyFound) {
                        newFoe.setStrategy(brain);
                    }

                    globalEntityList.add(newFoe);
                }
            }
        }
    }

    /**
     * Nettoie les ressources graphiques (la map) lors du changement de niveau.
     */
    @Override
    public void dispose() {
        if (map != null) {
            map.dispose();
        }
    }
}
