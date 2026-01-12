package com.EthanKnittel.graphics.ui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * Gestionnaire des menus en jeu (Pause, Game Over).
 * <p>
 * Cette classe utilise la librairie **Scene2D** de LibGDX pour afficher une interface
 * au-dessus du jeu. Elle gère plusieurs "écrans" (Tables) qui peuvent être affichés ou cachés
 * selon l'état du jeu.
 * </p>
 * <p>
 * Elle communique avec le reste du jeu via l'interface {@link MenuCallback}.
 * Cela permet de garder le code de l'UI séparé de la logique du jeu (Pattern MVC/Callback).
 * </p>
 */
public class GameMenus implements Disposable {

    /** Le Stage est le conteneur principal de Scene2D qui gère les clics et le dessin. */
    private Stage stage;

    /** La Skin définit le style graphique (couleurs, polices, textures des boutons). */
    private Skin skin;

    /** L'interface de communication pour envoyer les ordres au GameScreen. */
    private MenuCallback callback;

    // --- SOUS-MENUS (Panneaux) ---
    private Table pauseTable;
    private Table deathTable;


    /**
     * Constructeur.
     *
     * @param batch    Le SpriteBatch partagé pour dessiner l'UI.
     * @param callback L'implémentation du contrat qui réagira aux clics des boutons.
     */
    public GameMenus(SpriteBatch batch, MenuCallback callback) {
        this.callback = callback;

        // 1. Initialisation de Scene2D
        // On utilise un ExtendViewport pour que l'UI s'adapte à la taille de la fenêtre
        // sans être déformée. (800x600 est la taille virtuelle de référence).
        this.stage = new Stage(new ExtendViewport(800, 600), batch);

        // 2. Création du style (Skin) via notre Factory maison
        this.skin = SkinFactory.createSkin();

        // 3. Construction des différents panneaux (cachés par défaut)
        createPauseMenu();
        createDeathScreen();
    }

    /**
     * Construit le menu de Pause.
     * Contient : Titre, Bouton Reprendre, Retour Menu, Quitter.
     */
    private void createPauseMenu() {
        pauseTable = new Table();
        pauseTable.setFillParent(true); // Prend tout l'écran
        pauseTable.setVisible(false); // Caché au départ

        // Fond semi-transparent noir (0.8 alpha) pour assombrir le jeu derrière
        TextureRegionDrawable background = new TextureRegionDrawable(skin.getRegion("white"));
        pauseTable.setBackground(background.tint(new Color(0, 0, 0, 0.8f)));

        // Éléments
        Label pauseLabel = new Label("PAUSE", skin);
        pauseLabel.setFontScale(2f);


        TextButton resumeBtn = new TextButton("Reprendre", skin);
        TextButton menuBtn = new TextButton("Menu Principal", skin);
        TextButton quitBtn = new TextButton("Quitter", skin);

        // --- LISTENERS (Gestion des clics) ---
        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // On appelle la méthode onResume() du GameScreen
                callback.onResume();
            }
        });

        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callback.onGoToMainMenu();
            }
        });

        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callback.onQuit();
            }
        });

        // --- MISE EN PAGE (Layout) ---
        // Scene2D utilise un système de tableau (lignes/colonnes)
        pauseTable.add(pauseLabel).padBottom(50).row(); // Titre + saut de ligne
        pauseTable.add(resumeBtn).width(200).height(50).padBottom(20).row();
        pauseTable.add(menuBtn).width(200).height(50).padBottom(20).row();
        pauseTable.add(quitBtn).width(200).height(50).row();

        // Ajout final au Stage
        stage.addActor(pauseTable);
    }

    /**
     * Construit l'écran de Mort (Game Over).
     * Contient : "Vous êtes mort", Recommencer, Menu Principal.
     */
    private void createDeathScreen() {
        deathTable = new Table();
        deathTable.setFillParent(true);
        deathTable.setVisible(false);

        // Fond un peu plus opaque (0.85) pour marquer la fin
        TextureRegionDrawable background = new TextureRegionDrawable(skin.getRegion("white"));
        deathTable.setBackground(background.tint(new Color(0, 0, 0, 0.85f)));

        // Titre en rouge (style "title" défini dans SkinFactory)
        Label deadLabel = new Label("VOUS ETES MORT", skin, "title");
        deadLabel.setFontScale(3f);

        // Boutons
        TextButton restartBtn = new TextButton("Recommencer", skin);
        TextButton menuBtn = new TextButton("Menu Principal", skin); // <--- NOUVEAU BOUTON

        // --- LISTENERS ---
        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callback.onRestart();
            }
        });


        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callback.onGoToMainMenu();
            }
        });

        // --- MISE EN PAGE ---
        deathTable.add(deadLabel).padBottom(50).row();
        deathTable.add(restartBtn).width(200).height(50).padBottom(20).row();
        deathTable.add(menuBtn).width(200).height(50); // <--- AJOUT DANS LA TABLE

        stage.addActor(deathTable);
    }

    /**
     * Affiche l'UI et gère les animations de boutons.
     *
     * @param delta Temps écoulé.
     */
    public void render(float delta) {
        // 'act' met à jour la logique des acteurs (ex: animations, réactions au survol)
        stage.act(delta);
        // 'draw' dessine tout le contenu du stage à l'écran
        stage.draw();
    }

    /**
     * Redimensionne le viewport de l'UI quand la fenêtre change de taille.
     */
    public void resize(int width, int height) {
        // 'true' centre la caméra du stage
        stage.getViewport().update(width, height, true);
    }

    // --- CONTRÔLE DE L'AFFICHAGE ---

    /** Affiche le menu Pause et cache les autres. */
    public void showPause() {
        pauseTable.setVisible(true);
        deathTable.setVisible(false);
    }

    /** Cache le menu Pause (Reprise du jeu). */
    public void hidePause() {
        pauseTable.setVisible(false);
    }

    /** Affiche l'écran de mort. */
    public void showDeath() {
        deathTable.setVisible(true);
        pauseTable.setVisible(false);
    }

    /** @return true si le jeu est visuellement en pause. */
    public boolean isPauseVisible() {
        return pauseTable.isVisible();
    }

    /**
     * Récupère le processeur d'entrées de l'UI.
     * <p>
     * <b>Important :</b> Ce processeur doit être ajouté à l'InputMultiplexer du jeu,
     * sinon les clics de souris sur les boutons ne seront pas détectés.
     * </p>
     */
    public InputProcessor getInputProcessor() {
        return stage;
    }

    /**
     * Libère les ressources (Stage et Skin).
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
