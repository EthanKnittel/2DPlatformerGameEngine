package com.EthanKnittel.game;

import com.EthanKnittel.audio.AudioManager;
import com.EthanKnittel.graphics.WorldRenderer;
import com.EthanKnittel.graphics.entity.PlayerView;
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

/**
 * Écran principal de jeu (Le "Game Loop").
 * <p>
 * Cette classe est le cœur du moteur. Elle orchestre la communication entre :
 * <ul>
 * <li><b>Les Entrées (Inputs) :</b> Clavier/Souris via le {@link PlayerController}.</li>
 * <li><b>Le Modèle (Logic) :</b> L'{@link Environment}, la physique, les spawners.</li>
 * <li><b>La Vue (Render) :</b> Le {@link WorldRenderer} et l'UI ({@link GameHud}, {@link GameMenus}).</li>
 * </ul>
 * </p>
 * <p>
 * Elle gère également le cycle de vie de la partie : chargement des niveaux, pause,
 * transitions (fondus au noir) et conditions de victoire/défaite.
 * </p>
 */
public class GameScreen implements Screen, MenuCallback {

    // --- GESTION DES ENTRÉES ---
    /** Gestionnaire brut du clavier (Stocke l'état des touches). */
    private KeyboardInput keyboardInput;
    /** Gestionnaire brut de la souris (Position, Clics). */
    private MouseInput mouseInput;

    // --- RENDU & CAMÉRA ---
    /** La caméra qui suit le joueur dans le monde. */
    private OrthographicCamera gameCamera;

    /** Échelle de conversion : 16 pixels = 1 unité (mètre/bloc) dans le monde physique. */
    private static final float PixelsPerBlocks = 16f;

    /** Niveau de zoom de la caméra (plus c'est bas, plus on est près). */
    private static float zoom = 1.5f;

    /** Le "crayon" partagé pour dessiner toutes les textures. */
    private SpriteBatch batch;

    /** Gère le ratio de l'écran lors du redimensionnement de la fenêtre. */
    private Viewport gameViewport;

    // --- MONDE & LOGIQUE ---
    /** Le conteneur de toutes les entités (Murs, Ennemis, Joueur). */
    private Environment environment;

    /** Le système responsable d'afficher l'environnement à l'écran. */
    private WorldRenderer worldRenderer;

    /** L'entité contrôlée par l'utilisateur. */
    private Player player;

    /** Interprète les inputs pour faire bouger le joueur. */
    private PlayerController playerController;

    /** Vue spécifique du joueur (Animations). */
    private PlayerView playerView;

    // --- MANAGERS (Singletons ou Gestionnaires globaux) ---
    private AudioManager audioManager;
    private ScoreManager scoreManager;
    private SaveManager saveManager;

    // --- INTERFACE UTILISATEUR (UI) ---
    /** Affichage Tête Haute (HUD) : Vie, Score, Temps. */
    private GameHud gameHud;

    /** Menus superposés : Pause, Game Over. */
    private GameMenus gameMenus;

    // --- ÉTATS DU JEU ---
    /** Si vrai, la boucle logique (update) est figée, mais le rendu continue. */
    private boolean isPaused = false;

    /** Si vrai, le joueur est mort. */
    private boolean isGameOver = false;

    /**
     * Délai de sécurité au début d'un niveau (en secondes).
     * Empêche les ennemis de spawner ou d'attaquer instantanément pendant la transition.
     */
    private float startCooldown = 3.0f;

    // --- GESTION DES NIVEAUX ---
    /** Système de rotation des cartes Tiled (.tmx). */
    private LevelManager levelManager;

    /** Score nécessaire pour déclencher le passage au niveau suivant. */
    private int scoreToChangeMap = 1000;

    // --- MACHINE À ÉTATS (Transitions) ---
    /**
     * États possibles du flux de jeu.
     * Permet de gérer les fondus (Fade In/Out) proprement.
     */
    private enum State {
        Play,       // Jeu normal
        Fading_Out, // L'écran devient noir (Fin de niveau)
        Loading,    // Chargement technique du niveau suivant
        Fading_In   // L'écran redevient visible (Début de niveau)
    }

    private State currentState = State.Play;

    /** Opacité du rectangle noir de transition (0 = Transparent, 1 = Noir). */
    private float fadeAlpha = 0f;
    private float fadeSpeed = 1.0f; // Vitesse de la transition

    /**
     * Initialisation de l'écran.
     * Appelé par {@link GameEngine} lors du `setScreen()`.
     */
    @Override
    public void show() {
        // 1. Initialisation des outils de base
        keyboardInput = new KeyboardInput();
        mouseInput = new MouseInput();
        batch = new SpriteBatch();

        // Récupération des instances uniques (Singletons)
        scoreManager = ScoreManager.getScoreManager();
        saveManager = SaveManager.getInstance();
        audioManager = AudioManager.getInstance();

        // Chargement des sons
        loadAudioAssets();
        audioManager.playMusic("background_Music", true);

        levelManager = new LevelManager();

        // 2. Configuration de la Caméra
        // On calcule la taille du monde visible en divisant la résolution par l'échelle des blocs et le zoom
        float worldWidth = (800f / PixelsPerBlocks) / zoom;
        float worldHeight = (600f / PixelsPerBlocks) / zoom;

        gameCamera = new OrthographicCamera();
        // ExtendViewport : Si la fenêtre s'agrandit, on voit plus de monde (pas d'étirement)
        gameViewport = new ExtendViewport(worldWidth, worldHeight, gameCamera);

        // 3. Initialisation de l'UI (HUD + Menus)
        gameHud = new GameHud(batch);

        // On instancie les menus en passant 'this' car GameScreen implémente l'interface MenuCallback.
        // Cela permet aux boutons du menu d'appeler les méthodes onResume(), onQuit()... ici même.
        gameMenus = new GameMenus(batch, this);

        // 4. Création du Monde (Physique + Entités)
        environment = new Environment();

        // Chargement initial d'une carte (la première disponible via le LevelManager)
        loadLevel(levelManager.getNextMapPath());

        // Création/Réinitialisation du joueur
        createPlayerAndLevel();

        // 5. Initialisation des systèmes dépendants du monde
        worldRenderer = new WorldRenderer(environment, batch, gameCamera);
        playerController = new PlayerController(player, keyboardInput, mouseInput);
        playerView = new PlayerView(player); // Vue séparée pour gérer les anims complexes du joueur

        // 6. Gestion des Inputs (Multiplexeur)
        // C'est CRUCIAL : On empile les processeurs d'entrée.
        // L'UI (Menus) est prioritaire : elle intercepte les clics en premier.
        // Si l'UI ne traite pas l'input, il passe au clavier/souris du jeu.

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(gameMenus.getInputProcessor());
        inputMultiplexer.addProcessor(keyboardInput);
        inputMultiplexer.addProcessor(mouseInput);

        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    /**
     * Charge un nouveau niveau dans l'environnement.
     * <p>
     * Cette méthode s'occupe de nettoyer l'ancien niveau, charger le fichier .tmx,
     * et repositionner le joueur au point de départ défini dans Tiled.
     * </p>
     *
     * @param levelPath Chemin vers le fichier .tmx.
     */
    private void loadLevel(String levelPath) {
        try {
            // Nettoyage des entités de l'ancien niveau (sauf le joueur qu'on va réutiliser/réinitialiser)
            if (environment.getLevel() != null) {
                environment.getEntities().clear();
            }

            // Création du niveau Tiled
            TiledLevel level = new TiledLevel(levelPath);
            environment.setLevel(level);

            // Notification au Renderer que la carte a changé (pour qu'il recrée le TiledMapRenderer)
            if (worldRenderer != null) {
                worldRenderer.setLevel(level);
            }

            // Gestion de la santé du joueur (Restauration ou création)
            if (player == null) {
                // Sera créé dans createPlayerAndLevel() si null
            } else {
                player.setCurrenthealth(player.getMaxHealth());
            }

            // Repositionnement du joueur au spawn (défini dans le calque "Setup" de Tiled)
            if (player != null) {
                player.setVelocity(0, 0); // On stop le mouvement
                player.setPosXY(level.getPlayerSpawnPoint().x, level.getPlayerSpawnPoint().y);
            }

            // Réinsertion du joueur et des mobs statiques dans le nouveau monde
            environment.addEntity(player);
            level.spawnStaticMobs(player, environment.getEntities());

            // On remet le timer de sécurité (avant les vagues de monstres
            startCooldown = 3.0f;

        } catch (Exception e) {
            // Fallback : Si le fichier plante, on charge un niveau de test généré par code
            Gdx.app.error("GameScreen", "Erreur de chargement de niveaux" + levelPath, e);
            environment.setLevel(new TestLevel());
            environment.addEntity(player);
        }
    }

    /**
     * Vérifie si les conditions pour finir le niveau sont remplies.
     * Actuellement : Avoir atteint un certain score.
     */
    private boolean CompleteLevelCondition(){
        return scoreManager.getScore() >= scoreToChangeMap;
    }

    // --- IMPLÉMENTATION DE MENU CALLBACK (Réactions aux boutons de l'UI) ---
    // Ces méthodes sont appelées par la classe GameMenus quand on clique sur les boutons.

    /**
     * Appelé par le bouton "Reprendre" du menu Pause.
     */
    @Override
    public void onResume() {
        // Appelé quand on clique sur "Reprendre"
        if (!isGameOver) {
            isPaused = false;
            gameMenus.hidePause(); // On cache l'UI
        }
    }

    /**
     * Appelé par le bouton "Recommencer" (Game Over).
     * Relance une instance fraîche du GameScreen.
     */
    @Override
    public void onRestart() {
        // Appelé quand on clique sur "Recommencer" (Mort)
        // On recharge simplement un nouveau GameScreen
        ((GameEngine) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
        scoreManager.setScore(0);
    }

    /**
     * Appelé par le bouton "Quitter".
     */
    @Override
    public void onQuit() {
        // Appelé quand on clique sur "Quitter"
        Gdx.app.exit();
    }

    /**
     * Appelé pour revenir au Menu Principal.
     * Sauvegarde les stats avant de partir.
     */
    @Override
    public void onGoToMainMenu() {
        // On sauvegarde avant de quitter par sécurité
        saveStats();
        scoreManager.setScore(0); // on remet le score à 0
        // On change l'écran vers le Menu Principal
        ((GameEngine) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen());
    }


    /**
     * Méthode appelée à chaque frame (environ 60 fois par seconde).
     * <p>
     * C'est ici que tout se joue. La méthode est divisée en 3 phases classiques :
     * <ol>
     * <li><b>INPUT :</b> Lecture des touches (Escape pour pause).</li>
     * <li><b>UPDATE :</b> Mise à jour de la logique du monde (Physique, IA) si le jeu n'est pas en pause.</li>
     * <li><b>RENDER :</b> Dessin des éléments à l'écran (Monde, UI, Effets).</li>
     * </ol>
     * </p>
     *
     * @param delta Le temps écoulé (en secondes) depuis la dernière frame.
     */
    @Override
    public void render(float delta) {
        // --- 0. GESTION INPUTS GLOBAUX (Pause) ---
        // On vérifie si le joueur appuie sur ESCAPE à cette frame précise
        if (keyboardInput.isKeyDownNow(Input.Keys.ESCAPE)) {
            if (!isGameOver) {
                isPaused = !isPaused; // Bascule Pause On/Off
                if (isPaused) {
                    gameMenus.showPause(); // Affiche le menu
                    saveStats(); // Sauvegarde de sécurité
                } else {
                    gameMenus.hidePause(); // Cache le menu
                }
            }
        }

        // --- 1. LOGIQUE & UPDATE (Si le jeu tourne) ---
        // On ne met à jour le monde que si le jeu est actif (Pas de pause, Pas de Game Over)
        if (!isPaused && !isGameOver) {

            // A. ÉTAT : EN JEU (PLAY) - Comportement standard
            if (currentState == State.Play) {
                // On plafonne le delta time pour éviter les bugs de physique si le jeu lague (max 1/16s)
                float effectiveDelta = Math.min(delta, 1 / 16f);

                // 1. Gestion du Cooldown de début de niveau (Sécurité anti-spawn kill)
                if (startCooldown > 0) {
                    startCooldown -= effectiveDelta;
                }

                // 2. Update du Joueur (Mouvements, Actions)
                playerController.update(effectiveDelta);

                // 3. Update des Zones de Spawn (Vagues d'ennemis)
                // On attend que le cooldown soit fini pour commencer à faire apparaître des monstres
                if (startCooldown <= 0 && environment.getLevel().getClass().equals(TiledLevel.class)) {
                    TiledLevel level = (TiledLevel) environment.getLevel();
                    for (SpawnZone zone : level.getSpawnZones()) {
                        zone.update(effectiveDelta, player, environment.getEntities());
                    }
                }

                // 4. Update Global (Physique, Score, Interface)
                environment.update(effectiveDelta);      // Déplacements & Collisions
                scoreManager.update(effectiveDelta);     // Temps & Points passifs
                gameHud.update(player);                  // Barre de vie & Textes

                // 5. Caméra : Elle suit le joueur
                // On centre la caméra sur les coordonnées X,Y du joueur
                gameCamera.position.set(player.getX(), player.getY(), 0);
                gameCamera.update(); // Recalcul des matrices de projection

                // 6. Condition de Victoire : Changement de niveau ?
                if (CompleteLevelCondition()) {
                    currentState = State.Fading_Out; // On lance la transition de fin
                }

                // 7. Condition de Défaite : Mort ?
                if (player.getCurrenthealth() <= 0) {
                    isGameOver = true;
                    saveStats(); // On enregistre le score final
                    gameMenus.showDeath(); // On affiche l'écran de mort
                }
            }

            // B. ÉTAT : FONDU SORTANT (L'écran devient noir)
            // Transition visuelle avant de changer de niveau
            else if (currentState == State.Fading_Out) {
                fadeAlpha += delta * fadeSpeed; // On augmente l'opacité
                if (fadeAlpha >= 1f) {
                    fadeAlpha = 1f; // Noir total
                    currentState = State.Loading; // L'écran est caché, on peut charger la suite
                }
            }

            // C. ÉTAT : CHARGEMENT TECHNIQUE
            else if (currentState == State.Loading) {
                // Chargement effectif du fichier .tmx suivant
                loadLevel(levelManager.getNextMapPath());

                // Augmentation du score requis pour le prochain niveau
                scoreToChangeMap += 1000;

                // On lance le fondu entrant (réapparition de l'image)
                currentState = State.Fading_In;
            }

            // D. ÉTAT : FONDU ENTRANT (L'écran redevient clair)
            else if (currentState == State.Fading_In) {
                fadeAlpha -= delta * fadeSpeed; // On diminue l'opacité

                // Important : On force la caméra sur le joueur immédiatement
                // pour éviter qu'elle ne "saute" visuellement d'un coup quand l'image revient.
                gameCamera.position.set(player.getX(), player.getY(), 0);
                gameCamera.update();

                if (fadeAlpha <= 0f) {
                    fadeAlpha = 0f;
                    currentState = State.Play; // Retour au jeu normal
                    // Note: 'startCooldown' a déjà été remis à 3.0f dans loadLevel()
                }
            }
        }

        // --- 2. RENDU GRAPHIQUE (DRAW) ---
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // B. Application de la caméra au batch de dessin
        gameViewport.apply();
        batch.setProjectionMatrix(gameCamera.combined);

        // C. Dessin du Monde (Murs, Ennemis, Joueur) via le Renderer
        worldRenderer.render(delta);

        // D. Dessin de l'effet de Fondu (Rectangle noir par dessus le monde)
        // On ne le dessine que si nécessaire (transition en cours)
        if (currentState != State.Play || fadeAlpha > 0) {
            worldRenderer.renderFade(fadeAlpha);
        }

        // E. Dessin de l'Interface Utilisateur (HUD + Menus)
        // L'UI est dessinée en DERNIER pour être toujours au-dessus de tout (même du fondu noir)
        gameHud.stage.act(delta);
        gameHud.stage.draw();

        gameMenus.render(delta);

        // --- 3. INPUTS FINAUX ---
        // On nettoie les états "Just Pressed" des inputs pour la prochaine frame
        keyboardInput.update();
        mouseInput.update();
    }

    // -----------------------------------------------------------
    // GESTION DU REDIMENSIONNEMENT & CYCLE DE VIE
    // -----------------------------------------------------------

    /**
     * Appelé lorsque la fenêtre du jeu change de taille.
     * <p>
     * C'est crucial pour garder un affichage correct sans étirement.
     * On doit prévenir tous les Viewports (Caméra, HUD, Menus).
     * </p>
     */
    @Override
    public void resize(int width, int height) {
        // Mise à jour de la vue principale du jeu
        gameViewport.update(width, height, true);

        // On recentre la caméra
        gameCamera.position.set(player.getX(), player.getY(), 0);
        gameCamera.update();

        // On redimensionne aussi les interfaces utilisateur (HUD et Menus)
        // pour que les boutons restent à la bonne place.
        gameHud.resize(width, height);
        gameMenus.resize(width, height);
    }

    /**
     * Appelé lorsque le jeu perd le focus (ex: Alt-Tab, appel téléphonique sur Android).
     * <p>
     * Par sécurité, on met le jeu en pause et on sauvegarde.
     * </p>
     */
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
        // Méthode appelée au retour du focus.
        // Rien à faire ici car l'état 'isPaused' gère déjà le blocage de l'update.
    }

    /**
     * Appelé juste avant que cet écran ne soit remplacé par un autre.
     */
    @Override
    public void hide() {
        dispose(); // On nettoie tout quand on quitte l'écran
    }

    /**
     * Libération des ressources mémoire (Memory Management).
     * <p>
     * <b>TRES IMPORTANT :</b> En Java/LibGDX, les objets graphiques (Textures, Batchs, Shaders)
     * ne sont pas gérés par le Garbage Collector. Il faut les détruire manuellement
     * pour éviter les fuites de mémoire (Memory Leaks).
     * </p>
     */
    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (gameMenus != null) gameMenus.dispose();
        if (gameHud != null) gameHud.dispose();
        if (audioManager != null) audioManager.dispose();
        if (playerView != null) playerView.dispose();
        if (worldRenderer != null) worldRenderer.dispose();
        if (environment != null) environment.getLevel().dispose();
    }

    // -----------------------------------------------------------
    // MÉTHODES PRIVÉES (Helper Methods)
    // -----------------------------------------------------------

    /**
     * Sauvegarde les statistiques de la session en cours via le SaveManager.
     */
    private void saveStats() {
        if (saveManager != null && scoreManager != null) {
            float time = scoreManager.getTimeSurvived();
            int score = scoreManager.getScore();
            // On envoie les données brutes au manager qui gère l'écriture JSON
            saveManager.saveSessionStats(time, score);
        }
    }

    /**
     * Pré-charge les sons essentiels du jeu.
     */
    private void loadAudioAssets() {
        audioManager.loadMusic("background_Music", "Audio/music/Bonus_Points/Bonus_Points.mp3");
        audioManager.loadSound("jumpEffectSound", "Audio/soundEffect/12_Player_Movement_SFX/30_Jump_03.wav");
    }

    /**
     * Initialise le joueur et charge un niveau par défaut si nécessaire.
     * <p>
     * Cette méthode sert de point de départ robuste : si le chargement de la carte Tiled échoue,
     * elle charge un niveau de test généré procéduralement pour éviter le crash.
     * </p>
     */
    private void createPlayerAndLevel() {
        // Calcul des dimensions du joueur (converties en unités monde)
        float playerWidth = 32f / PixelsPerBlocks;
        float playerHeight = 32f / PixelsPerBlocks;
        // Création de l'entité Joueur
        // (PV: 100, Dégâts: 20, MaxSauts: 2)
        player = new Player(10f, 2f, playerWidth, playerHeight, 100, 20, 2, environment, gameViewport);

        try {
            // Tentative de chargement du niveau par défaut (4.tmx)
            TiledLevel level = new TiledLevel("TiledLevels/4.tmx");
            environment.setLevel(level);
            // Placement au spawn
            if (level.getPlayerSpawnPoint() != null) {
                player.setPosXY(level.getPlayerSpawnPoint().x, level.getPlayerSpawnPoint().y);
            }
            // Ajout des mobs
            level.spawnStaticMobs(player, environment.getEntities());
        } catch (Exception e) {
            // En cas d'erreur (ex: fichier introuvable), on charge le niveau de test (Rectangles simples)
            Gdx.app.error("GameScreen", "Erreur loading level", e);
            environment.setLevel(new TestLevel());
        }
        // Ajout final du joueur au monde
        environment.addEntity(player);
    }

    /**
     * Récupère le ratio de conversion Pixels -> Blocs.
     * @return 16.0f (Une tuile fait 16 pixels).
     */
    public static float getPixelsPerBlocks() {
        return PixelsPerBlocks;
    }
}
