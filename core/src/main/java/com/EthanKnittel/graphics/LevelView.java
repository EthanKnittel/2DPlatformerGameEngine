package com.EthanKnittel.graphics;

import com.EthanKnittel.game.GameScreen;
import com.EthanKnittel.world.TiledLevel;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;

public class LevelView {
    private OrthogonalTiledMapRenderer renderer;
    private TiledLevel level;

    // on stocke les indices des calques, le render dans BatchTiledMapRenderer a besoin de int[].
    private int[] backgroundLayers;
    private int[] aboveLayers;

    public LevelView(TiledLevel level) {
        this.level = level;
        // On récupère la map depuis le modèle
        if (level.getMap() != null) {
            this.renderer = new OrthogonalTiledMapRenderer(level.getMap(), 1f / GameScreen.getPixelsPerBlocks());
            calculateLayers();
        }
    }

    private void calculateLayers() {
        TiledMap map = level.getMap();
        Array<Integer> backGroundIndices = new Array<>();
        Array<Integer> aboveIndices = new Array<>();

        // 2. Tri des calques pour l'affichage
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            MapLayer layer = map.getLayers().get(i);

            // On ignore les calques techniques (Spawn, Setup) si on ne veut pas les dessiner
            if (layer.getName().equals("SpawnZones") || layer.getName().equals("Setup")) {
                continue;
            }

            // Lecture de la propriété "above" (Modèle -> Vue)
            boolean isAbove = layer.getProperties().get("above", false, Boolean.class);

            if (isAbove) {
                aboveIndices.add(i);
            } else {
                backGroundIndices.add(i);
            }
        }

        // Conversion Array<Integer> -> int[]
        this.backgroundLayers = convertToIntArray(backGroundIndices);
        this.aboveLayers = convertToIntArray(aboveIndices);
    }

    private int[] convertToIntArray(Array<Integer> list) {
        int[] array = new int[list.size];
        for (int i = 0; i < list.size; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    // Logique de mise à jour de la caméra (Padding pour éviter le flickering sur les bords)
    private void updateCamera(OrthographicCamera camera) {
        float viewX = camera.position.x - camera.viewportWidth / 2f;
        float viewY = camera.position.y - camera.viewportHeight / 2f;
        float padding = 4f; // Marge de sécurité pour le culling
        renderer.setView(camera.combined, viewX - padding, viewY - padding, camera.viewportWidth + 2 * padding, camera.viewportHeight + 2 * padding);
    }

    public void renderBackground(OrthographicCamera camera) {
        if (renderer != null && backgroundLayers != null && backgroundLayers.length > 0) {
            updateCamera(camera);
            renderer.render(backgroundLayers);
        }
    }

    public void renderAbove(OrthographicCamera camera) {
        if (renderer != null && aboveLayers != null && aboveLayers.length > 0) {
            updateCamera(camera);
            renderer.render(aboveLayers);
        }
    }

    public void dispose() {
        if (renderer != null) {
            renderer.dispose();
        }
    }
}
