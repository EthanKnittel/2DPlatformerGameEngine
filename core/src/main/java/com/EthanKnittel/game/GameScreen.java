package com.EthanKnittel.game;

import com.EthanKnittel.entities.agents.foes.Cactus;
import com.EthanKnittel.entities.agents.foes.Ordi;
import com.EthanKnittel.respawn.SpawnZone;
import com.EthanKnittel.world.Environment;
import com.EthanKnittel.world.TestLevel;
import com.EthanKnittel.inputs.KeyboardInput;
import com.EthanKnittel.inputs.MouseInput;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.world.TiledLevel;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {

    private KeyboardInput keyboardInput;
    private MouseInput mouseInput;
    private OrthographicCamera gameCamera;
    private SpriteBatch batch;
    private Player player;
    private Environment environment;
    private static final float PixelsPerBlocks = 16f;
    private static float zoom = 1.5f;

    private Stage uiStage;
    private Skin skin;
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private Viewport gameViewport;

    private Table pauseTable;
    private Table deathTable;

    private TextButton resumeBtn;
    private TextButton quitBtn;

    @Override
    public void show() {
        // Initialisation du jeu
        keyboardInput = new KeyboardInput();
        mouseInput = new MouseInput();
        batch = new SpriteBatch();
        float worldWidth = (800f / PixelsPerBlocks) / zoom;
        float worldHeight = (600f / PixelsPerBlocks) / zoom;

        gameCamera = new OrthographicCamera();
        gameViewport = new FitViewport(worldWidth, worldHeight, gameCamera);
        environment = new Environment();
        createPlayerAndLevel();

        // Initialisation de l'interface
        uiStage = new Stage(new FitViewport(800, 600), batch);
        createBasicSkin();
        createPauseMenu();
        createDeathScreen();

        // Gestion des inputs dans le multiplexeur
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(keyboardInput);
        inputMultiplexer.addProcessor(mouseInput);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void createPlayerAndLevel() {
        float playerWidth = 32f / PixelsPerBlocks;
        float playerHeight = 32f / PixelsPerBlocks;
        player = new Player(10f, 2f, playerWidth, playerHeight, 100, 20, 2, keyboardInput, mouseInput, environment, gameCamera);

        try {
            TiledLevel level = new TiledLevel("TiledLevels/4.tmx");
            environment.setLevel(level);
            if (level.getPlayerSpawnPoint() != null) {
                player.setPosXY(level.getPlayerSpawnPoint().x, level.getPlayerSpawnPoint().y);
            }

            level.spawnStaticMobs(player, environment.getEntities());

        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Erreur loading level", e);
            environment.setLevel(new TestLevel());
        }
        environment.addEntity(player);
    }

    private void createBasicSkin() {
        skin = new Skin();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        BitmapFont font = new BitmapFont();
        skin.add("default", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        skin.add("default", labelStyle);

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = font;
        titleStyle.fontColor = Color.RED;
        skin.add("title", titleStyle);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = new TextureRegionDrawable(skin.getRegion("white")).tint(Color.GRAY);
        textButtonStyle.down = new TextureRegionDrawable(skin.getRegion("white")).tint(Color.DARK_GRAY);
        textButtonStyle.over = new TextureRegionDrawable(skin.getRegion("white")).tint(Color.LIGHT_GRAY);
        textButtonStyle.fontColor = Color.BLACK;
        skin.add("default", textButtonStyle);
    }

    private void createPauseMenu() {
        pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseTable.setVisible(false);

        TextureRegionDrawable background = new TextureRegionDrawable(skin.getRegion("white"));
        pauseTable.setBackground(background.tint(new Color(0, 0, 0, 0.8f)));

        Label pauseLabel = new Label("PAUSE", skin);
        pauseLabel.setFontScale(2f);

        resumeBtn = new TextButton("Reprendre", skin);
        quitBtn = new TextButton("Quitter", skin);

        pauseTable.add(pauseLabel).padBottom(50).row();
        pauseTable.add(resumeBtn).width(200).height(50).padBottom(20).row();
        pauseTable.add(quitBtn).width(200).height(50).row();

        uiStage.addActor(pauseTable);
    }

    private void createDeathScreen() {
        deathTable = new Table();
        deathTable.setFillParent(true);
        deathTable.setVisible(false);

        TextureRegionDrawable background = new TextureRegionDrawable(skin.getRegion("white"));
        deathTable.setBackground(background.tint(new Color(0, 0, 0, 0.85f)));

        Label deadLabel = new Label("VOUS ETES MORT", skin, "title");
        deadLabel.setFontScale(3f);

        Label subLabel = new Label("Appuyez sur R pour recommencer", skin);

        deathTable.add(deadLabel).padBottom(50).row();
        deathTable.add(subLabel);

        uiStage.addActor(deathTable);
    }

    public void togglePause() {
        if (isGameOver) return;
        isPaused = !isPaused;
        pauseTable.setVisible(isPaused);
    }

    private boolean isActorClicked(Actor actor) {
        if (mouseInput.isButtonDownNow(0)) {
            Vector2 stageCoords = uiStage.screenToStageCoordinates(new Vector2(mouseInput.GetPosX(), mouseInput.GetPosY()));
            Actor hitActor = uiStage.hit(stageCoords.x, stageCoords.y, true);
            return hitActor == actor || hitActor.isDescendantOf(actor);

        }
        return false;
    }

    @Override
    public void render(float delta) {

        // Gestion des inputs dans les menus
        if (isPaused) {
            if (isActorClicked(resumeBtn)) {
                togglePause();
            } else if (isActorClicked(quitBtn)) {
                Gdx.app.exit();
            }
            if (keyboardInput.isKeyDownNow(Input.Keys.ESCAPE)) {
                togglePause();
            }
        } else if (isGameOver) {
            if (keyboardInput.isKeyDownNow(Input.Keys.R)) {
                ((GameEngine) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
                this.dispose();
                return;
            }
        } else {
            if (keyboardInput.isKeyDownNow(Input.Keys.ESCAPE)) {
                togglePause();
            }
        }

        // On fait continuer de tourner le monde, même après la mort du joueur
        if (!isPaused) {
            float effectiveDelta = Math.min(delta, 1 / 16f);

            if (environment.getLevel().getClass().equals(TiledLevel.class)){
                TiledLevel level = (TiledLevel) environment.getLevel();

                for (SpawnZone zone :  level.getSpawnZones()) {
                    zone.update(effectiveDelta, player, environment.getEntities());
                }
            }
            environment.update(effectiveDelta);

            // La caméra suit le cadavre du joueur
            gameCamera.position.set(player.getX(), player.getY(), 0);
            gameCamera.update();


            // On vérifie la mort (pour afficher l'UI, mais sans arrêter le monde)
            if (player.getCurrenthealth() <= 0 && !isGameOver) {
                isGameOver = true;
                deathTable.setVisible(true);
            }
        }

        // On applique le rendu
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameViewport.apply();

        batch.setProjectionMatrix(gameCamera.combined);
        environment.render(batch, gameCamera);

        uiStage.act(delta);
        uiStage.draw();

        // On update les inputs en dernier

        keyboardInput.update();
        mouseInput.update();
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        gameCamera.position.set(player.getX(), player.getY(), 0);
        gameCamera.update();
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        if (!isGameOver) {
            isPaused = true;
            pauseTable.setVisible(true);
        }
    }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        environment.dispose();
        batch.dispose();
        uiStage.dispose();
        skin.dispose();
    }

    public static float getPixelsPerBlocks() {
        return PixelsPerBlocks;
    }
}
