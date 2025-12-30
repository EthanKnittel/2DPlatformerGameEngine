package com.EthanKnittel.world;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.entities.artifacts.Wall;
import com.EthanKnittel.game.GameScreen;
import com.EthanKnittel.respawn.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class TiledLevel extends Level{
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private final String mapFileName;

    private Array<SpawnZone> spawnZones;

    private static final String SpawnLayerName= "SpawnZones";
    private static final String SetupLayerName = "Setup"; // nom du calque de setup


    // on stocke les indices des calques, le render dans BatchTiledMapRenderer a besoin de int[].
    private int[] backgroundLayers;
    private int[] aboveLayers;

    //On va stocker dans cette variable les coordonnées du spawn du joueur rentré dans Tiled
    private Vector2 playerSpawnPoint;
    private Array<Vector2> cactusSpawnPoints;
    private Array<Vector2> ordiSpawnPoints;


    public TiledLevel(String filename){
        this.mapFileName = filename;
        this.spawnZones = new Array<>();
    }

    public Array<SpawnZone> getSpawnZones(){return spawnZones;}
    public Vector2 getPlayerSpawnPoint(){return playerSpawnPoint;}

    @Override
    public Array<Entity> load() {
        Array<Entity> generatedEntities = new Array<>();

        map = new TmxMapLoader().load(mapFileName);
        renderer = new OrthogonalTiledMapRenderer(map, 1f / GameScreen.getPixelsPerBlocks());

        Array<Integer> backGroundLayersIndices = new Array<>();
        Array<Integer> aboveLayersIndices = new Array<>();

        // On gère le spawn du joueur
        MapLayer setuplayer = map.getLayers().get(SetupLayerName);
        for (MapObject object : setuplayer.getObjects()) {
            if (object.getProperties().containsKey("playerSpawnPoint")) {
                float x = object.getProperties().get("x", Float.class);
                float y = object.getProperties().get("y", Float.class);

                this.playerSpawnPoint = new Vector2(x / GameScreen.getPixelsPerBlocks(), y / GameScreen.getPixelsPerBlocks());
            }
        }

        loadSpawnZones();

        // On gère les calques des murs et système de "dessus" ou "arrière plan"
        for (int i=0; i < map.getLayers().getCount(); i++) {
            MapLayer layer = map.getLayers().get(i);

            // on prend les calques avec "above" true, la valeur par défaut étant false si elle n'existe pas
            boolean isAbove= layer.getProperties().get("above",false, Boolean.class);
            if (isAbove) {
                aboveLayersIndices.add(i);
            } else {
                backGroundLayersIndices.add(i);
            }

            // on récupère les calques avec la propriété de collision, la valeur par défaut étant false si elle n'existe pas
            boolean isCollidable = layer.getProperties().get("collidable", false, boolean.class);
            if (!isCollidable) {
                continue;
            }

            // C'est un calque de type tuile (TiledMapTileLayer)
            if (layer.getClass().equals(TiledMapTileLayer.class)) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
                int mapWidth = tileLayer.getWidth();
                int mapHeight = tileLayer.getHeight();

                // On parcourt chaque cellule (x, y) de la grille
                for (int x = 0; x < mapWidth; x++) {
                    for (int y = 0; y < mapHeight; y++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                        // Si non vide -> elle contient une tuile
                        if (cell != null && cell.getTile() != null) {
                            // On crée un mur de 1x1 aux coordonnées de la tuile
                            generatedEntities.add(new Wall(x, y, 1f, 1f, false));
                        }
                    }
                }
            // C'est un calque de type objet:
            } else {
                // On parcourt tous les objets du calque
                for (MapObject object : layer.getObjects()) {
                    float scaledX, scaledY, scaledWidth, scaledHeight;

                    // Objet de type Rectangle
                    if (object.getClass().equals(RectangleMapObject.class)) {
                        Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                        // On utilise .getY() directement (corrigé)
                        scaledX = rectangle.x / GameScreen.getPixelsPerBlocks();
                        scaledY = rectangle.getY() / GameScreen.getPixelsPerBlocks();
                        scaledWidth = rectangle.width / GameScreen.getPixelsPerBlocks();
                        scaledHeight = rectangle.height / GameScreen.getPixelsPerBlocks();

                        // Objet de type Tuile (placé comme un objet)
                    } else if (object.getClass().equals(TiledMapTileMapObject.class)) {
                        TiledMapTileMapObject tileObject = (TiledMapTileMapObject) object;
                        // On utilise .getY() directement (corrigé)
                        scaledX = tileObject.getX() / GameScreen.getPixelsPerBlocks();
                        scaledY = tileObject.getY() / GameScreen.getPixelsPerBlocks();

                        TextureRegion textureRegion = tileObject.getTile().getTextureRegion();
                        scaledWidth = textureRegion.getRegionWidth() / GameScreen.getPixelsPerBlocks();
                        scaledHeight = textureRegion.getRegionHeight() / GameScreen.getPixelsPerBlocks();

                    } else { // Objet inconnu (polygone, etc.)
                        continue; // On passe à l'objet suivant
                    }

                    // On ajoute le mur à l'environnement
                    generatedEntities.add(new Wall(scaledX, scaledY, scaledWidth, scaledHeight, false));
                }
            }
        }

        backgroundLayers = convertToIntArray(backGroundLayersIndices);
        aboveLayers = convertToIntArray(aboveLayersIndices);

        return generatedEntities;
    }

    private void loadSpawnZones(){
        MapLayer spawnLayer = map.getLayers().get(SpawnLayerName);

        Array<SpawnZone> tempZones = new Array<>();
        Array<MapObject> tempPoints = new Array<>();

        Array<EnemyAssociation> allKnownFoes = EnemyRegistry.getAllAssociations(); // on récupère toute la liste d'ennemies connu d'un coup

        // On sépare les points des rectangles
        for (MapObject object : spawnLayer.getObjects()) {
            boolean isPoint = false;
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

        // On configure les points et on les ajoute aux zones
        for (MapObject points : tempPoints) {
            float rawX = points.getProperties().get("x", Float.class);
            float rawY = points.getProperties().get("y", Float.class);
            float x =  rawX / GameScreen.getPixelsPerBlocks();
            float y = rawY / GameScreen.getPixelsPerBlocks();

            SpawnPoint spawnPoint = new SpawnPoint(x, y);

            for (int i=0; i < allKnownFoes.size; i++) {
                EnemyAssociation association = allKnownFoes.get(i);
                String enemyName = association.getName();

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
            // On ajoute le point à la zone
            for (SpawnZone zone : tempZones) {
                if (zone.getZoneBounds().contains(x,y)){
                    zone.addSpawnPoint(spawnPoint);
                    break;
                }
            }
        }

        this.spawnZones.addAll(tempZones);
    }

    public void spawnStaticMobs(Player player, Array<Entity> globalEntityList){
        MapLayer setupLayer = map.getLayers().get(SetupLayerName);
        for (MapObject object : setupLayer.getObjects()) {
            String objectName = object.getName();
            EnemyFactory factory = EnemyRegistry.getFactory(objectName);
            if (factory != null) {
                float x = object.getProperties().get("x", Float.class) /  GameScreen.getPixelsPerBlocks();
                float y = object.getProperties().get("y", Float.class) /   GameScreen.getPixelsPerBlocks();

                Foe newFoe = factory.create(x,y,player, globalEntityList);
                globalEntityList.add(newFoe);
            }
        }
    }

    private int[] convertToIntArray(Array<Integer> list) {
        int[] array = new int[list.size];
        for (int i = 0; i < list.size; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private void updateCamera(OrthographicCamera camera) {
        float viewX = camera.position.x - camera.viewportWidth / 2f;
        float viewY = camera.position.y - camera.viewportHeight / 2f;
        float padding = 4f;
        renderer.setView(camera.combined,viewX-padding,viewY-padding, camera.viewportWidth+2*padding,camera.viewportHeight+2*padding);
    }

    @Override
    public void renderBackground(OrthographicCamera camera){
        if (renderer != null && backgroundLayers.length>0) {
            updateCamera(camera);
            renderer.render(backgroundLayers);
        }
    }

    @Override
    public void renderAbove(OrthographicCamera camera) {
        if(renderer != null && aboveLayers.length>0) {
            updateCamera(camera);
            renderer.render(aboveLayers);
        }
    }

    @Override
    public void dispose() {
        if (map != null) {
            map.dispose();
        }
        if (renderer != null) {
            renderer.dispose();
        }
    }

}
