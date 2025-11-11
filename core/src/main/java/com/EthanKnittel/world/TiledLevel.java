package com.EthanKnittel.world;

import com.EthanKnittel.entities.artifacts.Wall;
import com.EthanKnittel.game.GameScreen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Gdx;
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

public class TiledLevel extends Level{
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private final String mapFileName;
    private String collisionLayerName;

    public TiledLevel(String filename, String collisionLayerName){
        this.mapFileName = filename;
        this.collisionLayerName = collisionLayerName;
    }

    @Override
    public void load(Environment environment) {
        map = new TmxMapLoader().load(mapFileName);
        renderer = new OrthogonalTiledMapRenderer(map, 1f / GameScreen.getPixelsPerBlocks());

        MapLayer layer = map.getLayers().get(this.collisionLayerName);

        // Vérification de si le calque existe
        if (layer == null) {
            Gdx.app.error("TiledLevel", "ERREUR: Calque '" + this.collisionLayerName + "' non trouvé (est null).");
            return;
        }


        // C'est un calque de type tuile (TiledMapTileLayer)
        if (layer instanceof TiledMapTileLayer) {
            Gdx.app.log("TiledLevel", "Type de calque détecté: Calque de Tuiles (TiledMapTileLayer).");
            TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;

            int mapWidth = tileLayer.getWidth();
            int mapHeight = tileLayer.getHeight();
            int wallsCreated = 0;

            // On parcourt chaque cellule (x, y) de la grille
            for (int x = 0; x < mapWidth; x++) {
                for (int y = 0; y < mapHeight; y++) {

                    TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);

                    // Si non vide -> elle contient une tuile
                    if (cell != null && cell.getTile() != null) {
                        // On crée un mur de 1x1 aux coordonnées de la tuile
                        environment.addEntity(new Wall(x, y, 1f, 1f, false));
                        wallsCreated++;
                    }
                }
            }
            Gdx.app.log("TiledLevel", wallsCreated + " murs (tuiles) créés.");

            // C'est un calque de type objet -> MapLayer générique
        } else {
            Gdx.app.log("TiledLevel", "Type de calque détecté: Calque d'Objets (MapLayer).");
            int wallsCreated = 0;

            // On parcourt tous les objets du calque
            for (MapObject object : layer.getObjects()) {

                float scaledX, scaledY, scaledWidth, scaledHeight;

                // Objet de type Rectangle
                if (object instanceof RectangleMapObject) {
                    Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                    // On utilise .getY() directement (corrigé)
                    scaledX = rectangle.x / GameScreen.getPixelsPerBlocks();
                    scaledY = rectangle.getY() / GameScreen.getPixelsPerBlocks();
                    scaledWidth = rectangle.width / GameScreen.getPixelsPerBlocks();
                    scaledHeight = rectangle.height / GameScreen.getPixelsPerBlocks();

                    Gdx.app.log("TiledLevel", "Mur (Rect) créé à : X=" + scaledX + ", Y=" + scaledY);

                    // Objet de type Tuile (placé comme un objet)
                } else if (object instanceof TiledMapTileMapObject) {
                    TiledMapTileMapObject tileObject = (TiledMapTileMapObject) object;
                    // On utilise .getY() directement (corrigé)
                    scaledX = tileObject.getX() / GameScreen.getPixelsPerBlocks();
                    scaledY = tileObject.getY() / GameScreen.getPixelsPerBlocks();

                    TextureRegion textureRegion = tileObject.getTile().getTextureRegion();
                    scaledWidth = textureRegion.getRegionWidth() / GameScreen.getPixelsPerBlocks();
                    scaledHeight = textureRegion.getRegionHeight() / GameScreen.getPixelsPerBlocks();

                    Gdx.app.log("TiledLevel", "Mur (TileObj) créé à : X=" + scaledX + ", Y=" + scaledY);

                    // Objet inconnu (polygone, etc.)
                } else {
                    Gdx.app.log("TiledLevel", "Objet de type inconnu ignoré: " + object.getClass().getSimpleName());
                    continue; // On passe à l'objet suivant
                }

                // On ajoute le mur à l'environnement
                environment.addEntity(new Wall(scaledX, scaledY, scaledWidth, scaledHeight, false));
                wallsCreated++;
            }
            Gdx.app.log("TiledLevel", wallsCreated + " murs (objets) créés.");
        }
    }

    @Override
    public void render(OrthographicCamera camera){
        if (renderer != null) {
            float viewX = camera.position.x - camera.viewportWidth / 2f;
            float viewY = camera.position.y - camera.viewportHeight / 2f;

            float padding = 4f;
            renderer.setView(camera.combined,viewX-padding,viewY-padding, camera.viewportWidth+2*padding,camera.viewportHeight+2*padding);
            renderer.render();
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
