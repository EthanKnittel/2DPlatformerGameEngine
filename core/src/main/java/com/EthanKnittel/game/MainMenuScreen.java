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

public class MainMenuScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;

    // Les 3 panneaux du menu
    private Table mainTable;
    private Table statsTable;
    private Table creditsTable;

    private SaveManager saveManager;

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage(new ExtendViewport(800, 600), batch);
        skin = SkinFactory.createSkin();

        // On s'assure que le SaveManager est chargé pour lire les stats
        saveManager = new SaveManager();

        Gdx.input.setInputProcessor(stage);

        // Création des 3 sous-menus
        createMainTable();
        createStatsTable();
        createCreditsTable();

        // Au début, on affiche le menu principal
        showMenu(mainTable);
    }

    private void showMenu(Table tableToShow) {
        mainTable.setVisible(false);
        statsTable.setVisible(false);
        creditsTable.setVisible(false);

        tableToShow.setVisible(true);
    }

    private void createMainTable() {
        mainTable = new Table();
        mainTable.setFillParent(true);

        // Fond légèrement coloré (optionnel)
        Texture whiteTex = skin.getRegion("white").getTexture();
        mainTable.setBackground(new TextureRegionDrawable(whiteTex).tint(new Color(0.1f, 0.1f, 0.2f, 1f)));



        TextButton playBtn = new TextButton("Lancer", skin);
        TextButton statsBtn = new TextButton("Statistiques", skin);
        TextButton creditsBtn = new TextButton("Crédits", skin);
        TextButton quitBtn = new TextButton("Quitter", skin);

        // --- ACTIONS ---

        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Lancer le jeu
                ((GameEngine) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
            }
        });

        statsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                refreshStats(); // Mettre à jour les chiffres avant d'afficher
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
                Gdx.app.exit();
            }
        });

        // --- MISE EN PAGE ---
        mainTable.add(playBtn).width(300).height(50).padBottom(20).row();
        mainTable.add(statsBtn).width(300).height(50).padBottom(20).row();
        mainTable.add(creditsBtn).width(300).height(50).padBottom(20).row();
        mainTable.add(quitBtn).width(300).height(50).row();

        stage.addActor(mainTable);
    }

    private void createStatsTable() {
        statsTable = new Table();
        statsTable.setFillParent(true);
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

    private void refreshStats() {
        statsTable.clearChildren();

        Label title = new Label("STATISTIQUES", skin, "title");
        title.setFontScale(2f);
        statsTable.add(title).padBottom(40).row();

        // 1. Récupération des données
        int highScore = saveManager.getHighScore();
        float totalTime = saveManager.getTotalPlaytime();
        float longestSession = saveManager.getLongestSession(); // Récupération de la valeur
        int cactusKills = saveManager.getKillCount("Cactus");
        int ordiKills = saveManager.getKillCount("Ordi");

        // 2. Formatage du TEMPS TOTAL (H et M suffisent souvent)
        int ttHours = (int) totalTime / 3600;
        int ttMinutes = (int) (totalTime % 3600) / 60;
        String totalTimeStr = ttHours + "h " + ttMinutes + "m";

        // 3. Formatage de la PLUS LONGUE SESSION (On ajoute les secondes !)
        int lsMinutes = (int) (longestSession % 3600) / 60;
        int lsSeconds = (int) (longestSession % 60);
        String longestSessionStr = lsMinutes + "m " + lsSeconds + "s";

        // 4. Affichage
        addStatRow("Meilleur Score:", String.valueOf(highScore));
        addStatRow("Temps Total:", totalTimeStr);
        addStatRow("Plus Longue Session:", longestSessionStr); // Nouvelle ligne
        addStatRow("Cactus Éliminés:", String.valueOf(cactusKills));
        addStatRow("Ordis Éliminés:", String.valueOf(ordiKills));

        // Bouton retour
        TextButton backBtn = new TextButton("Retour", skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showMenu(mainTable);
            }
        });

        statsTable.add(backBtn).width(200).height(50).padTop(50);
    }

    private void addStatRow(String name, String value) {
        Table rowTable = new Table();
        rowTable.add(new Label(name, skin)).left().width(200);
        rowTable.add(new Label(value, skin, "title")).right(); // "title" pour l'avoir en rouge/mis en valeur
        statsTable.add(rowTable).padBottom(10).row();
    }

    private void createCreditsTable() {
        creditsTable = new Table();
        creditsTable.setFillParent(true);
        creditsTable.setBackground(new TextureRegionDrawable(skin.getRegion("white").getTexture()).tint(Color.BLACK));

        Label title = new Label("CRÉDITS", skin, "title");
        title.setFontScale(2f);

        // Lecture du fichier
        String creditText = "Fichier credits.txt introuvable";
        FileHandle file = Gdx.files.internal("credits.txt");
        if (file.exists()) {
            creditText = file.readString();
        }

        Label textLabel = new Label(creditText, skin);
        textLabel.setAlignment(Align.center);

        // Ajout d'un ScrollPane au cas où le texte est long
        ScrollPane scrollPane = new ScrollPane(textLabel, skin);

        TextButton backBtn = new TextButton("Retour", skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showMenu(mainTable);
            }
        });

        creditsTable.add(title).pad(20).row();
        creditsTable.add(scrollPane).expand().width(600).row(); // On limite la largeur
        creditsTable.add(backBtn).width(200).height(50).pad(20);

        stage.addActor(creditsTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
    }
}
