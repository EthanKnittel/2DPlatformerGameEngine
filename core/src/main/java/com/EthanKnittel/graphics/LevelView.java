package com.EthanKnittel.graphics;

import com.EthanKnittel.game.GameScreen;
import com.EthanKnittel.world.TiledLevel;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;

/**
 * Vue graphique responsable du rendu des niveaux Tiled (.tmx).
 * <p>
 * Cette classe ne gère pas la logique du niveau (collisions, spawns), mais uniquement son affichage.
 * Elle utilise {@link OrthogonalTiledMapRenderer} de LibGDX pour dessiner la carte.
 * </p>
 * <p>
 * <b>Fonctionnalité clé : Gestion des plans (Z-Ordering)</b><br>
 * Elle trie les calques de la carte en deux groupes :
 * <ul>
 * <li><b>Background :</b> Dessinés AVANT les entités (sol, murs).</li>
 * <li><b>Above (Premier plan) :</b> Dessinés APRÈS les entités (toits, arches, herbes hautes).</li>
 * </ul>
 * Cela permet au joueur (et autres entités) de passer visuellement "derrière" certains éléments du décor.
 * </p>
 */
public class LevelView {

    /** Le moteur de rendu de carte fourni par LibGDX. */
    private OrthogonalTiledMapRenderer renderer;

    /** Le niveau logique à afficher. */
    private TiledLevel level;

    // --- GESTION DES CALQUES ---
    // Le renderer de LibGDX (BatchTiledMapRenderer) demande un tableau d'entiers (int[])
    // représentant les index des calques à dessiner.
    private int[] backgroundLayers;
    private int[] aboveLayers;

    /**
     * Constructeur.
     * Initialise le renderer et trie les calques.
     *
     * @param level Le niveau Tiled chargé.
     */
    public LevelView(TiledLevel level) {
        this.level = level;

        // On récupère la map depuis le modèle
        if (level.getMap() != null) {
            // Initialisation du Renderer.
            // Le 2ème paramètre est l'échelle (Scale).
            // Nos maps sont en pixels, mais notre monde est en "Blocs" (Mètres).
            // On divise donc par 16 (PixelsPerBlocks) pour que 1 tuile de 16px fasse 1 unité dans le monde.
            this.renderer = new OrthogonalTiledMapRenderer(level.getMap(), 1f / GameScreen.getPixelsPerBlocks());

            // Analyse des calques pour savoir qui dessiner quand
            calculateLayers();
        }
    }

    /**
     * Trie les calques de la carte en deux catégories (Fond et Premier plan).
     * <p>
     * Se base sur la propriété personnalisée "above" (booléen) définie dans l'éditeur Tiled.
     * Ignore également les calques techniques ("SpawnZones", "Setup").
     * </p>
     */
    private void calculateLayers() {
        TiledMap map = level.getMap();
        Array<Integer> backGroundIndices = new Array<>();
        Array<Integer> aboveIndices = new Array<>();

        // Parcours de tous les calques du fichier .tmx
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            MapLayer layer = map.getLayers().get(i);

            // 1. Filtrage des calques techniques (invisibles)
            // Ces calques contiennent des objets logiques (Rectangles de collision, Spawners)
            // et ne doivent pas être dessinés par le renderer graphique.
            if (layer.getName().equals("SpawnZones") || layer.getName().equals("Setup")) {
                continue;
            }

            // 2. Lecture de la propriété "above"
            // Dans Tiled, on peut ajouter une propriété booléenne "above" à un calque.
            // Si elle est vraie, ce calque sera dessiné au-dessus du joueur.
            boolean isAbove = layer.getProperties().get("above", false, Boolean.class);

            if (isAbove) {
                aboveIndices.add(i);
            } else {
                backGroundIndices.add(i);
            }
        }

        // Conversion des listes dynamiques (Array) en tableaux primitifs (int[]) requis par LibGDX
        this.backgroundLayers = convertToIntArray(backGroundIndices);
        this.aboveLayers = convertToIntArray(aboveIndices);
    }

    /**
     * Utilitaire de conversion Array<Integer> -> int[].
     */
    private int[] convertToIntArray(Array<Integer> list) {
        int[] array = new int[list.size];
        for (int i = 0; i < list.size; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    /**
     * Met à jour la caméra du renderer.
     * <p>
     * <b>Astuce technique :</b> On applique un "padding" (marge) à la vue.
     * Par défaut, LibGDX ne dessine que les tuiles strictement visibles à l'écran (Culling).
     * Parfois, cela crée du "flickering" (clignotement) sur les bords si une tuile est à moitié sortie.
     * En agrandissant un peu la zone de rendu (+4 unités), on évite ces bugs graphiques.
     * </p>
     *
     * @param camera La caméra du jeu.
     */
    private void updateCamera(OrthographicCamera camera) {
        float viewX = camera.position.x - camera.viewportWidth / 2f;
        float viewY = camera.position.y - camera.viewportHeight / 2f;

        float padding = 4f; // Marge de sécurité

        renderer.setView(camera.combined,
            viewX - padding, viewY - padding,
            camera.viewportWidth + 2 * padding, camera.viewportHeight + 2 * padding);
    }

    /**
     * Dessine les calques d'arrière-plan (Sol, Murs du fond).
     * Doit être appelé AVANT de dessiner le joueur.
     */
    public void renderBackground(OrthographicCamera camera) {
        if (renderer != null && backgroundLayers != null && backgroundLayers.length > 0) {
            updateCamera(camera);
            renderer.render(backgroundLayers);
        }
    }

    /**
     * Dessine les calques de premier plan.
     * Doit être appelé APRÈS avoir dessiné tout le reste (background, entités, joueur).
     */
    public void renderAbove(OrthographicCamera camera) {
        if (renderer != null && aboveLayers != null && aboveLayers.length > 0) {
            updateCamera(camera);
            renderer.render(aboveLayers);
        }
    }

    /**
     * Libère le renderer.
     * Appelé lors du changement de niveau.
     */
    public void dispose() {
        if (renderer != null) {
            renderer.dispose();
        }
    }
}
