package com.EthanKnittel.game;

import com.EthanKnittel.audio.AudioManager;
import com.EthanKnittel.graphics.WorldRenderer;
import com.EthanKnittel.graphics.entity.PlayerView;
// Assure-toi d'avoir créé ces fichiers dans le package graphics.ui comme vu précédemment
import com.EthanKnittel.graphics.ui.GameMenus;
import com.EthanKnittel.graphics.ui.MenuCallback;
import com.EthanKnittel.inputs.KeyboardInput;
import com.EthanKnittel.inputs.MouseInput;
import com.EthanKnittel.respawn.SpawnZone;
import com.EthanKnittel.save.SaveManager;
import com.EthanKnittel.score.ScoreManager;
import com.EthanKnittel.world.TestLevel;
import com.EthanKnittel.world.TiledLevel;
import com.EthanKnittel.world.systems.Environment;
import com.EthanKnittel.entities.agents.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen, MenuCallback {

    // --- Inputs ---
    private KeyboardInput keyboardInput;
    private MouseInput mouseInput;

    // --- Rendu & Caméra ---
    private OrthographicCamera gameCamera;
    private static final float PixelsPerBlocks = 16f;
    private static float zoom = 1.5f;
    private SpriteBatch batch;
    private Viewport gameViewport;

    // --- Monde & Logique ---
    private Environment environment;
    private WorldRenderer worldRenderer;
    private Player player;
    private PlayerController playerController;
    private PlayerView playerView;

    // --- Managers ---
    private AudioManager audioManager;
    private ScoreManager scoreManager;
    private SaveManager saveManager;

    // --- Interface Utilisateur (UI) ---
    private GameHud gameHud;       // Gère les stats en jeu (Vie, Score)
    private GameMenus gameMenus;   // Gère les menus (Pause, Mort) -> Nouvelle classe

    // --- États du jeu ---
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private float startCooldown = 3.0f;

    // gestion des niveaux
    private LevelManager levelManager;
    private int scoreToChangeMap = 1000;

    // gestion transition
    private enum State {Play, Fading_Out, Loading, Fading_In}
    private State currentState = State.Play;
    private float fadeAlpha = 0f;
    private float fadeSpeed = 1.0f;

    @Override
    public void show() {
        // 1. Initialisation générale
        keyboardInput = new KeyboardInput();
        mouseInput = new MouseInput();
        batch = new SpriteBatch();

        scoreManager = new ScoreManager();
        saveManager = new SaveManager();
        audioManager = new AudioManager();

        loadAudioAssets();
        audioManager.playMusic("background_Music", true);

        levelManager = new LevelManager();

        // 2. Initialisation de la Caméra
        float worldWidth = (800f / PixelsPerBlocks) / zoom;
        float worldHeight = (600f / PixelsPerBlocks) / zoom;
        gameCamera = new OrthographicCamera();
        gameViewport = new ExtendViewport(worldWidth, worldHeight, gameCamera);

        // 3. Initialisation de l'UI (HUD + Menus)
        gameHud = new GameHud(batch);

        // On instancie les menus en passant 'this' car GameScreen implémente MenuCallback
        gameMenus = new GameMenus(batch, this);

        // 4. Création du Monde
        environment = new Environment();
        loadLevel(levelManager.getNextMapPath());
        createPlayerAndLevel(); // Méthode privée inchangée (voir plus bas)

        worldRenderer = new WorldRenderer(environment, batch, gameCamera);
        playerController = new PlayerController(player, keyboardInput, mouseInput);
        playerView = new PlayerView(player);

        // 5. Gestion des Inputs (Multiplexeur)
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        // IMPORTANT : Les menus sont prioritaires pour cliquer sur les boutons
        inputMultiplexer.addProcessor(gameMenus.getInputProcessor());
        inputMultiplexer.addProcessor(keyboardInput);
        inputMultiplexer.addProcessor(mouseInput);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void loadLevel(String levelPath) {
        try {
            if (environment.getLevel() != null) {
                environment.getEntities().clear();
            }

            TiledLevel level = new TiledLevel(levelPath);
            environment.setLevel(level); // L'environnement a le nouveau niveau

            // --- AJOUT : On prévient le renderer du changement ! ---
            if (worldRenderer != null) {
                worldRenderer.setLevel(level);
            }
            // ---------------------------------------------------dq----

            if (player == null) {
                // ... (code existant inchangé)
            } else {
                player.setCurrenthealth(player.getMaxHealth());
            }

            if (player != null) {
                player.setVelocity(0, 0);
                player.setPosXY(level.getPlayerSpawnPoint().x, level.getPlayerSpawnPoint().y);
            }


            environment.addEntity(player);
            level.spawnStaticMobs(player, environment.getEntities());

            startCooldown = 3.0f;
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Erreur de chargement de niveaux" + levelPath, e);
            environment.setLevel(new TestLevel());
            environment.addEntity(player);
        }
    }

    private boolean CompleteLevelCondition(){
        return scoreManager.getScore() >= scoreToChangeMap;
    }

    // --- IMPLÉMENTATION DE MENU CALLBACK (Lien avec GameMenus) ---

    @Override
    public void onResume() {
        // Appelé quand on clique sur "Reprendre"
        if (!isGameOver) {
            isPaused = false;
            gameMenus.hidePause();
        }
    }

    @Override
    public void onRestart() {
        // Appelé quand on clique sur "Recommencer" (Mort)
        // On recharge simplement un nouveau GameScreen
        ((GameEngine) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
        // Pas besoin de dispose(), le changement d'écran gérera la transition
    }

    @Override
    public void onQuit() {
        // Appelé quand on clique sur "Quitter"
        Gdx.app.exit();
    }

    @Override
    public void onGoToMainMenu() {
        // On sauvegarde avant de quitter par sécurité
        saveStats();
        // On change l'écran vers le Menu Principal
        ((GameEngine) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
    }

    // -----------------------------------------------------------

    @Override
    public void render(float delta) {
        // --- 0. GESTION INPUTS GLOBAUX (Pause) ---
        if (keyboardInput.isKeyDownNow(Input.Keys.ESCAPE)) {
            if (!isGameOver) {
                isPaused = !isPaused;
                if (isPaused) {
                    gameMenus.showPause();
                    saveStats();
                } else {
                    gameMenus.hidePause();
                }
            }
        }

        // --- 1. LOGIQUE & UPDATE (Si le jeu n'est pas en pause) ---
        if (!isPaused && !isGameOver) {

            // A. ÉTAT : EN JEU (PLAY)
            if (currentState == State.Play) {
                float effectiveDelta = Math.min(delta, 1 / 16f);

                // 1. Gestion du Cooldown de début de niveau
                if (startCooldown > 0) {
                    startCooldown -= effectiveDelta;
                }

                // 2. Update du Joueur
                playerController.update(effectiveDelta);

                // 3. Update des Zones de Spawn (Seulement si cooldown fini)
                if (startCooldown <= 0 && environment.getLevel().getClass().equals(TiledLevel.class)) {
                    TiledLevel level = (TiledLevel) environment.getLevel();
                    for (SpawnZone zone : level.getSpawnZones()) {
                        zone.update(effectiveDelta, player, environment.getEntities());
                    }
                }

                // 4. Update Environnement, Score, HUD
                environment.update(effectiveDelta);
                scoreManager.update(effectiveDelta);
                gameHud.update(player);

                // 5. Caméra
                gameCamera.position.set(player.getX(), player.getY(), 0);
                gameCamera.update();

                // 6. Vérification : Changement de niveau requis ?
                if (CompleteLevelCondition()) {
                    currentState = State.Fading_Out; // On lance la transition
                }

                // 7. Vérification : Mort ?
                if (player.getCurrenthealth() <= 0) {
                    isGameOver = true;
                    saveStats();
                    gameMenus.showDeath();
                }
            }

            // B. ÉTAT : FONDU SORTANT (L'écran devient noir)
            else if (currentState == State.Fading_Out) {
                fadeAlpha += delta * fadeSpeed;
                if (fadeAlpha >= 1f) {
                    fadeAlpha = 1f; // Noir total
                    currentState = State.Loading; // On passe au chargement
                }
            }

            // C. ÉTAT : CHARGEMENT (Changement de map)
            else if (currentState == State.Loading) {
                // On charge la map suivante
                loadLevel(levelManager.getNextMapPath());

                // On augmente la difficulté (le score requis augmente)
                scoreToChangeMap += 1000;

                // On lance le fondu entrant
                currentState = State.Fading_In;
            }

            // D. ÉTAT : FONDU ENTRANT (L'écran redevient clair)
            else if (currentState == State.Fading_In) {
                fadeAlpha -= delta * fadeSpeed;

                // On force la caméra sur le joueur pour éviter qu'elle "saute" visuellement
                gameCamera.position.set(player.getX(), player.getY(), 0);
                gameCamera.update();

                if (fadeAlpha <= 0f) {
                    fadeAlpha = 0f;
                    currentState = State.Play; // Retour au jeu
                    // Note: 'startCooldown' a déjà été remis à 3.0f dans loadLevel()
                }
            }
        }

        // --- 2. RENDU GRAPHIQUE (DRAW) ---
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameViewport.apply();
        batch.setProjectionMatrix(gameCamera.combined);

        // A. Dessiner le monde
        worldRenderer.render(delta);

        // B. Dessiner le rectangle noir (Fondu) si nécessaire
        // On dessine si on n'est pas en mode Play OU si on a encore de l'opacité
        if (currentState != State.Play || fadeAlpha > 0) {
            worldRenderer.renderFade(fadeAlpha);
        }

        // C. Dessiner l'UI (Toujours par-dessus le fondu)
        gameHud.stage.act(delta);
        gameHud.stage.draw();

        gameMenus.render(delta);

        // --- 3. INPUTS FINAUX ---
        keyboardInput.update();
        mouseInput.update();
    }

    private void updatePlayingStats(float delta) {
        float effectiveDelta = Math.min(delta, 1 / 16f);

        // 1. Gestion du Cooldown de début de niveau (Sécurité anti-spawn kill)
        if (startCooldown > 0) {
            startCooldown -= effectiveDelta;
            // On peut mettre un return ici si on veut figer le joueur aussi
            // Mais on va juste bloquer les spawns ennemis comme avant
        }

        // 2. Updates classiques
        playerController.update(effectiveDelta);

        // Update Spawns seulement si cooldown fini
        if (startCooldown <= 0 && environment.getLevel().getClass().equals(TiledLevel.class)) {
            TiledLevel level = (TiledLevel) environment.getLevel();
            for (SpawnZone zone : level.getSpawnZones()) {
                zone.update(effectiveDelta, player, environment.getEntities());
            }
        }

        environment.update(effectiveDelta);
        scoreManager.update(effectiveDelta);
        gameHud.update(player);

        gameCamera.position.set(player.getX(), player.getY(), 0);
        gameCamera.update();

        // 3. Vérification Fin de Niveau (Trigger)
        if (CompleteLevelCondition()) {
            currentState = State.Fading_Out; // On lance la transition !
        }

        // 4. Vérification Mort
        if (player.getCurrenthealth() <= 0) {
            isGameOver = true;
            saveStats();
            gameMenus.showDeath();
        }
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        gameCamera.position.set(player.getX(), player.getY(), 0);
        gameCamera.update();

        // On redimensionne aussi les UIs
        gameHud.resize(width, height);
        gameMenus.resize(width, height);
    }

    @Override
    public void pause() {
        if (!isGameOver) {
            isPaused = true;
            gameMenus.showPause();
            saveStats();
        }
    }

    @Override
    public void resume() {
        // Rien de spécial ici, géré par l'état isPaused
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (gameMenus != null) gameMenus.dispose();
        if (gameHud != null) gameHud.dispose();
        if (audioManager != null) audioManager.dispose();
        if (playerView != null) playerView.dispose();
        if (worldRenderer != null) worldRenderer.dispose();
        // Environment dispose le niveau automatiquement
    }

    // --- Méthodes privées métier (inchangées) ---

    private void saveStats() {
        if (saveManager != null && scoreManager != null) {
            float time = scoreManager.getTimeSurvived();
            int score = scoreManager.getScore();
            saveManager.saveSessionStats(time, score);
        }
    }

    private void loadAudioAssets() {
        audioManager.loadMusic("background_Music", "Audio/music/Bonus_Points/Bonus_Points.mp3");
        audioManager.loadSound("jumpEffectSound", "Audio/soundEffect/12_Player_Movement_SFX/30_Jump_03.wav");
    }

    private void createPlayerAndLevel() {
        float playerWidth = 32f / PixelsPerBlocks;
        float playerHeight = 32f / PixelsPerBlocks;
        player = new Player(10f, 2f, playerWidth, playerHeight, 100, 20, 2, environment, gameViewport);

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

    public static float getPixelsPerBlocks() {
        return PixelsPerBlocks;
    }
}
