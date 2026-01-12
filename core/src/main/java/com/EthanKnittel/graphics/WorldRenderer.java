package com.EthanKnittel.graphics;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.entities.agents.foes.Cactus;
import com.EthanKnittel.entities.agents.foes.Ordi;
import com.EthanKnittel.entities.artifacts.FireArrow;
import com.EthanKnittel.entities.artifacts.Wall;
import com.EthanKnittel.graphics.entity.*;
import com.EthanKnittel.world.Level;
import com.EthanKnittel.world.TiledLevel;
import com.EthanKnittel.world.systems.Environment;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

/**
 * Moteur de rendu principal du monde de jeu.
 * <p>
 * Cette classe fait le lien entre le modèle de données (l'{@link Environment}) et l'écran.
 * Elle est responsable de :
 * <ul>
 * <li>Dessiner le niveau (Sol, Murs, Décor) via {@link LevelView}.</li>
 * <li>Gérer et dessiner les vues de toutes les entités dynamiques (Joueur, Ennemis, Projectiles).</li>
 * <li>Gérer les effets globaux comme le fondu au noir (Fade In/Out).</li>
 * </ul>
 * </p>
 */
public class WorldRenderer {
    /** Référence au modèle du monde (contient la liste des entités). */
    private Environment environment;

    /** Le "pinceau" principal pour dessiner les textures (Sprites). */
    private SpriteBatch batch;

    /** La caméra qui définit la zone visible du monde. */
    private OrthographicCamera camera;

    /** Sous-système pour afficher la carte Tiled (Tuiles). */
    private LevelView levelView;

    /** Outil pour dessiner des formes géométriques simples (utilisé pour le rectangle noir du fondu). */
    private ShapeRenderer shapeRenderer;

    /**
     * Liste des vues graphiques actives.
     * <p>
     * Chaque {@link Entity} du modèle possède une {@link EntityView} correspondante ici.
     * Cette liste est synchronisée à chaque frame avec celle de l'environnement.
     * </p>
     */
    private Array<EntityView> views;

    /**
     * Constructeur.
     *
     * @param environment Le monde à afficher.
     * @param batch       Le SpriteBatch partagé par le jeu.
     * @param camera      La caméra configurée.
     */

    public WorldRenderer(Environment environment, SpriteBatch batch, OrthographicCamera camera) {
        this.environment = environment;
        this.batch = batch;
        this.camera = camera;
        this.views = new Array<>();
        this.shapeRenderer = new ShapeRenderer();

        // Si le niveau chargé est une Map Tiled, on prépare sa vue dédiée
        if (environment.getLevel().getClass().equals(TiledLevel.class)){
            this.levelView = new LevelView((TiledLevel) environment.getLevel());
        }
    }

    /**
     * Méthode principale de dessin (Appelée à chaque frame).
     *
     * @param delta Temps écoulé depuis la dernière frame.
     */
    public void render(float delta){
        // 1. DESSIN DU FOND (Background)
        // On dessine d'abord le décor lointain (derrière le joueur)
        if (levelView != null) {
            levelView.renderBackground(camera);
        }

        // On configure le Batch pour qu'il dessine selon le point de vue de la caméra
        batch.setProjectionMatrix(camera.combined);
        batch.begin(); // Début du tracé des sprites

        // 2. GESTION DYNAMIQUE DES VUES (Synchronisation Modèle -> Vue)
        // On parcourt toutes les entités logiques du monde pour voir s'il en manque une à l'écran
        for (Entity entity : environment.getEntities()) {
            boolean viewExists = false;

            // Est-ce que cette entité a déjà une vue dans notre liste ?
            for (EntityView view : views) {
                if (view.getEntity().equals(entity)) { // Comparaison par référence (identité)
                    viewExists = true;
                    break;
                }
            }

            // Si non, on doit CRÉER la vue appropriée (Factory Pattern implicite)
            if (!viewExists) {
                if (entity.getClass().equals(Player.class)){
                    views.add(new PlayerView((Player) entity));
                }
                if (entity.getClass().equals(Ordi.class)){
                    views.add(new OrdiView((Ordi) entity));
                }
                if  (entity.getClass().equals(Cactus.class)) {
                    views.add(new CactusView((Cactus) entity));
                }
                if (entity.getClass().equals(FireArrow.class)){
                    views.add(new FireArrowView((FireArrow) entity));
                }
                if (entity.getClass().equals(Wall.class)){
                    views.add(new WallView((Wall) entity));
                }
                // Ajoutez ici les futurs types d'entités (ex: ZombieView) avec la même structure
            }
        }
        // 3. NETTOYAGE ET DESSIN DES ENTITÉS
        // On parcourt la liste des vues à l'envers pour pouvoir supprimer sans casser les index
        for (int i = views.size - 1; i >= 0; i--) {
            EntityView view = views.get(i);

            // Si l'entité n'existe plus dans le monde (morte/détruite), on supprime sa vue
            if (!environment.getEntities().contains(view.getEntity(), true)){
                view.dispose(); // Libération de la mémoire (textures)
                views.removeIndex(i);
            } else {
                // Sinon, on la dessine
                view.render(batch, delta);
            }
        }

        batch.end(); // Fin du tracé des sprites

        // 4. DESSIN DU PREMIER PLAN (Foreground)
        // On dessine les éléments du décor qui doivent passer DEVANT le joueur (toits, herbes)
        if (levelView != null) {
            levelView.renderAbove(camera);
        }
    }

    /**
     * Met à jour le niveau à afficher (lors d'un changement de map).
     *
     * @param level Le nouveau niveau chargé dans l'environnement.
     */
    public void setLevel(Level level) {
        // 1. On nettoie l'ancienne vue pour éviter les fuites de mémoire
        if (levelView != null) {
            levelView.dispose();
            levelView = null;
        }

        // 2. Si le nouveau niveau est un TiledLevel, on recrée la vue
        if (level.getClass().equals(TiledLevel.class)) {
            this.levelView = new LevelView((TiledLevel) level);
        }
    }

    /**
     * Dessine un rectangle noir plein écran avec transparence variable.
     * Utiliser pour les transitions (Fondu entrant / sortant).
     *
     * @param alpha Opacité (0.0 = transparent, 1.0 = noir total).
     */
    public void renderFade(float alpha){
        // On active le mélange des couleurs (Blending) pour gérer la transparence
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Couleur : Noir (0,0,0) avec alpha variable
        shapeRenderer.setColor(0, 0, 0, alpha);

        // On dessine un rectangle qui couvre toute la vue de la caméra
        float width = camera.viewportWidth * camera.zoom;
        float height = camera.viewportHeight * camera.zoom;

        // On centre le rectangle sur la caméra
        shapeRenderer.rect(camera.position.x - width / 2, camera.position.y - height / 2, width, height);

        shapeRenderer.end();

        // On désactive le blending pour ne pas perturber les autres rendus
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * Libère toutes les ressources graphiques.
     */
    public void dispose(){
        // Nettoyage de la vue du niveau
        if (levelView != null) {
            levelView.dispose();
        }
        // Nettoyage de toutes les vues d'entités
        for (EntityView view : views) {
            view.dispose();
        }
        views.clear();
        // Nettoyage du ShapeRenderer
        shapeRenderer.dispose();
    }
}
