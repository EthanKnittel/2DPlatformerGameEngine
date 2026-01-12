package com.EthanKnittel.game;

import com.EthanKnittel.graphics.ui.SkinFactory;
import com.EthanKnittel.save.SaveManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * Écran du menu principal (Main Menu).
 * <p>
 * C'est le point d'entrée visuel du jeu. Il gère la navigation entre :
 * <ul>
 * <li>Le lancement d'une nouvelle partie ({@link GameScreen}).</li>
 * <li>La consultation des statistiques persistantes (via {@link SaveManager}).</li>
 * <li>L'affichage des crédits.</li>
 * </ul>
 * </p>
 * <p>
 * Cette classe utilise intensivement <b>Scene2D</b>. Elle fonctionne avec un système de "panneaux"
 * (des {@link Table}s) que l'on affiche ou cache selon le sous-menu désiré, évitant ainsi
 * de devoir changer d'écran (Screen) pour de simples menus textuels.
 * </p>
 */
public class MainMenuScreen implements Screen {

    /** La scène Scene2D qui contient et gère tous les acteurs (boutons, labels). */
    private Stage stage;

    /** La skin définit l'apparence (couleurs, polices, textures) des widgets UI. */
    private Skin skin;

    /** Le batch utilisé pour dessiner le Stage. */
    private SpriteBatch batch;

    // --- SOUS-MENUS (Panneaux) ---
    // On garde une référence vers chaque table pour pouvoir basculer leur visibilité (setVisible).

    /** Le panneau principal (Boutons Jouer, Stats, Crédits, Quitter). */
    private Table mainTable;

    /** Le panneau des statistiques (Meilleur score, Temps de jeu...). */
    private Table statsTable;

    /** Le panneau des crédits (Texte défilant). */
    private Table creditsTable;

    /** Référence au gestionnaire de sauvegarde pour récupérer les records. */
    private SaveManager saveManager;

    /**
     * Initialise l'écran.
     * Appelé par LibGDX au moment où cet écran devient l'écran actif.
     */
    @Override
    public void show() {
        batch = new SpriteBatch();

        // Utilisation d'un ExtendViewport pour que l'interface s'adapte aux redimensionnements
        // sans déformer les ratios. 800x600 est la résolution virtuelle de base.
        stage = new Stage(new ExtendViewport(800, 600), batch);

        // Génération des ressources graphiques via la Factory
        skin = SkinFactory.createSkin();

        // On s'assure que le SaveManager est chargé pour lire les stats plus tard
        saveManager = SaveManager.getInstance();

        // IMPORTANT : On donne le focus à la scène pour qu'elle capte les clics de souris
        Gdx.input.setInputProcessor(stage);

        // Construction des différents sous-menus (cachés ou visibles)
        createMainTable();
        createStatsTable();
        createCreditsTable();

        // Au démarrage, on affiche uniquement le menu racine
        showMenu(mainTable);
    }

    /**
     * Méthode utilitaire pour basculer entre les différents panneaux.
     * Cache tout, puis affiche uniquement la table demandée.
     *
     * @param tableToShow La {@link Table} à rendre visible.
     */
    private void showMenu(Table tableToShow) {
        mainTable.setVisible(false);
        statsTable.setVisible(false);
        creditsTable.setVisible(false);

        tableToShow.setVisible(true);
    }

    /**
     * Construit le panneau du menu principal.
     * Contient les boutons de navigation vers les autres sections.
     */
    private void createMainTable() {
        mainTable = new Table();
        mainTable.setFillParent(true); // La table occupe tout l'écran

        // Ajout d'un fond légèrement teinté (Bleu nuit très sombre)
        Texture whiteTex = skin.getRegion("white").getTexture();
        mainTable.setBackground(new TextureRegionDrawable(whiteTex).tint(new Color(0.1f, 0.1f, 0.2f, 1f)));

        // Création des boutons
        TextButton playBtn = new TextButton("Lancer", skin);
        TextButton statsBtn = new TextButton("Statistiques", skin);
        TextButton creditsBtn = new TextButton("Crédits", skin);
        TextButton quitBtn = new TextButton("Quitter", skin);

        // --- GESTION DES ÉVÉNEMENTS (Listeners) ---

        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Changement d'écran vers le jeu
                ((GameEngine) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
            }
        });

        statsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Avant d'afficher, on rafraîchit les données pour avoir les stats à jour
                refreshStats();
                showMenu(statsTable);
            }
        });

        creditsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showMenu(creditsTable);
            }
        });

        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Fermeture propre de l'application
                Gdx.app.exit();
            }
        });

        // --- MISE EN PAGE (Layout) ---
        // On empile les boutons verticalement avec un espacement (padBottom)
        mainTable.add(playBtn).width(300).height(50).padBottom(20).row();
        mainTable.add(statsBtn).width(300).height(50).padBottom(20).row();
        mainTable.add(creditsBtn).width(300).height(50).padBottom(20).row();
        mainTable.add(quitBtn).width(300).height(50).row();

        // Ajout final à la scène
        stage.addActor(mainTable);
    }

    /**
     * Initialise la structure du tableau des statistiques.
     * <p>
     * Note : Le contenu textuel (chiffres) n'est pas rempli ici, mais dans {@link #refreshStats()}.
     * Cette méthode ne crée que le conteneur et le fond.
     * </p>
     */
    private void createStatsTable() {
        statsTable = new Table();
        statsTable.setFillParent(true);

        // Fond noir opaque
        statsTable.setBackground(new TextureRegionDrawable(skin.getRegion("white").getTexture()).tint(Color.BLACK));

        Label title = new Label("STATISTIQUES", skin, "title");
        title.setFontScale(2f);

        // Le contenu sera rempli dynamiquement dans refreshStats()
        // On ajoute juste un bouton retour ici

        statsTable.add(title).top().pad(20).row();
        // On laisse une cellule vide qui sera remplie plus tard
        statsTable.add(new Label("Loading...", skin)).expand().row();

        TextButton backBtn = new TextButton("Retour", skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showMenu(mainTable);
            }
        });

        statsTable.add(backBtn).width(200).height(50).bottom().pad(20);

        stage.addActor(statsTable);
    }

    /**
     * Met à jour et affiche les statistiques depuis le fichier de sauvegarde.
     * <p>
     * Cette méthode reconstruit le contenu de {@code statsTable} à chaque appel
     * pour garantir que les données affichées sont fraîches.
     * </p>
     */
    private void refreshStats() {
        // On vide l'ancien contenu pour éviter les doublons
        statsTable.clearChildren();

        // Titre
        Label title = new Label("STATISTIQUES", skin, "title");
        title.setFontScale(2f);
        statsTable.add(title).padBottom(40).row();

        // 1. RÉCUPÉRATION DES DONNÉES (Data Binding)
        int highScore = saveManager.getHighScore();
        float totalTime = saveManager.getTotalPlaytime();
        float longestSession = saveManager.getLongestSession(); // Récupération de la valeur
        int cactusKills = saveManager.getKillCount("Cactus");
        int ordiKills = saveManager.getKillCount("Ordi");

        // 2. FORMATAGE DU TEMPS (Conversion secondes -> H/M/S)
        int ttHours = (int) totalTime / 3600;
        int ttMinutes = (int) (totalTime % 3600) / 60;
        String totalTimeStr = ttHours + "h " + ttMinutes + "m";


        int lsMinutes = (int) (longestSession % 3600) / 60;
        int lsSeconds = (int) (longestSession % 60);
        String longestSessionStr = lsMinutes + "m " + lsSeconds + "s";

        // 3. REMPLISSAGE DU TABLEAU
        addStatRow("Meilleur Score:", String.valueOf(highScore));
        addStatRow("Temps Total:", totalTimeStr);
        addStatRow("Plus Longue Session:", longestSessionStr); // Nouvelle ligne
        addStatRow("Cactus Éliminés:", String.valueOf(cactusKills));
        addStatRow("Ordis Éliminés:", String.valueOf(ordiKills));

        // 4. BOUTON RETOUR
        TextButton backBtn = new TextButton("Retour", skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showMenu(mainTable);
            }
        });

        // Ajout du bouton en bas avec une marge
        statsTable.add(backBtn).width(200).height(50).padTop(50);
    }

    /**
     * Méthode utilitaire pour ajouter une ligne de statistique formatée proprement.
     * <p>
     * Crée une sous-table contenant le libellé à gauche et la valeur à droite.
     * </p>
     *
     * @param name  Le nom de la statistique (ex: "Temps Total").
     * @param value La valeur à afficher (ex: "1h 30m").
     */
    private void addStatRow(String name, String value) {
        Table rowTable = new Table();
        // Label aligné à gauche
        rowTable.add(new Label(name, skin)).left().width(200);
        // Valeur alignée à droite, style "title" pour la mettre en valeur (Rouge)
        rowTable.add(new Label(value, skin, "title")).right();
        statsTable.add(rowTable).padBottom(10).row();
    }

    /**
     * Construit le panneau des crédits.
     * <p>
     * Charge le texte depuis un fichier externe "credits.txt" et l'affiche
     * dans un {@link ScrollPane} pour permettre le défilement si le texte est long.
     * </p>
     */
    private void createCreditsTable() {
        creditsTable = new Table();
        creditsTable.setFillParent(true);
        creditsTable.setBackground(new TextureRegionDrawable(skin.getRegion("white").getTexture()).tint(Color.BLACK));

        Label title = new Label("CRÉDITS", skin, "title");
        title.setFontScale(2f);

        // Lecture du fichier texte
        String creditText = "Fichier credits.txt introuvable";
        FileHandle file = Gdx.files.internal("credits.txt");
        if (file.exists()) {
            creditText = file.readString();
        }

        // Configuration du label contenant le texte
        Label textLabel = new Label(creditText, skin);
        textLabel.setAlignment(Align.center);

        // Ajout d'un ScrollPane (Ascenseur) pour gérer le texte long
        ScrollPane scrollPane = new ScrollPane(textLabel, skin);

        TextButton backBtn = new TextButton("Retour", skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showMenu(mainTable);
            }
        });

        // Mise en page
        creditsTable.add(title).pad(20).row();
        // expand() permet au scrollPane de prendre toute la place disponible
        creditsTable.add(scrollPane).expand().width(600).row();
        creditsTable.add(backBtn).width(200).height(50).pad(20);

        stage.addActor(creditsTable);
    }

    /**
     * Boucle de rendu de l'écran.
     *
     * @param delta Temps écoulé depuis la dernière frame.
     */
    @Override
    public void render(float delta) {
        // 1. Nettoyage de l'écran (Fond noir)
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2. Mise à jour de la logique UI (Animations, survols souris)
        stage.act(delta);

        // 3. Dessin de l'interface
        stage.draw();
    }

    /**
     * Redimensionnement de la fenêtre.
     */
    @Override
    public void resize(int width, int height) {
        // Important : On notifie le viewport pour recalculer l'échelle de l'UI
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    /**
     * Libération des ressources graphiques.
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
    }
}
