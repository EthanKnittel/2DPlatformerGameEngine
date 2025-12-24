package com.EthanKnittel.world;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.artifacts.Wall;
import com.EthanKnittel.game.GameScreen;
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

    // on stocke les indices des calques, le render dans BatchTiledMapRenderer a besoin de int[].
    private int[] backgroundLayers;
    private int[] aboveLayers;

    //On va stocker dans cette variable les coordonnées du spawn du joueur rentré dans Tiled
    private Vector2 playerSpawnPoint;
    private Array<Vector2> cactusSpawnPoints;
    private static final String SetupLayerName = "Setup"; // nom du calque de setup

    public TiledLevel(String filename){
        this.mapFileName = filename;
        this.cactusSpawnPoints = new Array<>();
    }

    public Array<Vector2> getCactusSpawnPoints(){
        return cactusSpawnPoints;
    }

    public Vector2 getPlayerSpawnPoint(){
        return playerSpawnPoint;
    }

    @Override
    public Array<Entity> load() {
        Array<Entity> generatedEntities = new Array<>();

        map = new TmxMapLoader().load(mapFileName);
        renderer = new OrthogonalTiledMapRenderer(map, 1f / GameScreen.getPixelsPerBlocks());

        Array<Integer> backGroundLayersIndices = new Array<>();
        Array<Integer> aboveLayersIndices = new Array<>();

        MapLayer setuplayer = map.getLayers().get(SetupLayerName);
        if (setuplayer != null) {
            for (MapObject object : setuplayer.getObjects()) {
                if (object.getProperties().containsKey("playerSpawnPoint")) {
                    float x = object.getProperties().get("x", Float.class);
                    float y = object.getProperties().get("y", Float.class);

                    this.playerSpawnPoint = new Vector2(x / GameScreen.getPixelsPerBlocks(), y / GameScreen.getPixelsPerBlocks());
                }
                if (object.getProperties().containsKey("CactusSpawn")) {
                    float x = object.getProperties().get("x",0f, Float.class);
                    float y = object.getProperties().get("y",0f, Float.class);

                    cactusSpawnPoints.add(new Vector2(x / GameScreen.getPixelsPerBlocks(), y/GameScreen.getPixelsPerBlocks()));
                }

            }
        }

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
                // C'est un calque de type objet -> MapLayer générique
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
