package com.EthanKnittel.game;

import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.score.ScoreManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Gestionnaire de l'Affichage Tête Haute (HUD).
 * <p>
 * Cette classe gère l'interface utilisateur affichée en permanence pendant la partie.
 * Elle superpose des informations cruciales au rendu du monde :
 * <ul>
 * <li><b>Barre de vie :</b> Indique la santé restante du joueur.</li>
 * <li><b>Score :</b> Points accumulés.</li>
 * <li><b>Chronomètre :</b> Temps de survie.</li>
 * </ul>
 * </p>
 * <p>
 * Elle possède son propre {@link Stage} (scène UI) indépendant de celui du jeu ou des menus,
 * ce qui permet de le dessiner par-dessus tout le reste.
 * </p>
 */
public class GameHud implements Disposable {

    /** La scène Scene2D qui contient les widgets du HUD. */
    public Stage stage;

    /** Le viewport dédié à l'UI (pour que le HUD garde sa taille peu importe la résolution). */
    private Viewport viewport;

    // --- WIDGETS (Éléments graphiques) ---
    private Label scoreLabel;
    private Label timeLabel;
    private ProgressBar healthbBar;

    /** La skin générée localement pour styliser les barres et textes. */
    private Skin skin;

    /**
     * Constructeur.
     *
     * @param batch Le SpriteBatch partagé pour dessiner l'interface.
     */
    public GameHud(SpriteBatch batch) {
        // On utilise un ExtendViewport 800x600 : si la fenêtre s'agrandit, le HUD reste à l'échelle
        // mais l'espace visible augmente (les éléments restent ancrés aux coins).
        this.viewport = new ExtendViewport(800,600);
        this.stage = new Stage(viewport,batch);

        // 1. Création des styles graphiques (Textures, Polices)
        createSkin();

        // 2. Placement des éléments à l'écran
        setupLayout();
    }

    /**
     * Génère les ressources graphiques nécessaires au HUD (Skin programmatique).
     * <p>
     * Similaire à {@link com.EthanKnittel.graphics.ui.SkinFactory}, mais spécifique au HUD
     * (notamment pour la barre de vie verte).
     * </p>
     */
    private void createSkin() {
        skin = new Skin();

        // A. Texture de base (Pixel blanc) pour dessiner des rectangles de couleur
        Pixmap pixmap = new Pixmap(1,1,Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture baseTexture = new Texture(pixmap);

        skin.add("white", baseTexture);

        // B. Police par défaut
        skin.add("default", new BitmapFont());

        // C. Style des Labels (Texte blanc standard)
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        skin.add("default",labelStyle);

        // D. Style de la Barre de Vie (ProgressBar)
        int totalHeight = 26;
        int border = 3;
        int fillHeight = totalHeight - (border * 2);

        // 1. Le fond/contour (Noir)
        BaseDrawable barBorder = (BaseDrawable) skin.newDrawable("white", Color.BLACK);
        barBorder.setMinHeight(totalHeight);

        // On définit des "marges internes" (padding) pour que le remplissage ne recouvre pas la bordure
        barBorder.setTopHeight(border);
        barBorder.setBottomHeight(border);
        barBorder.setLeftWidth(border);
        barBorder.setRightWidth(border);

        // 2. Le remplissage (Vert)
        BaseDrawable barFill = (BaseDrawable) skin.newDrawable("white", Color.GREEN);
        barFill.setMinHeight(fillHeight);

        // 3. Assemblage du style
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle(barBorder, null);
        style.knobBefore = barFill; // "knobBefore" est la partie remplie à gauche

        skin.add("default-horizontal",style);
    }

    /**
     * Organise les widgets à l'écran via un tableau (Table).
     */
    private void setupLayout() {
        Table table = new Table();
        table.top(); // On ancre tout en HAUT de l'écran
        table.setFillParent(true); // Le tableau prend toute la place disponible

        // Création des widgets
        scoreLabel = new Label("Score: 0", skin);
        timeLabel = new Label("Time: 00:00", skin);

        // Barre de vie : Min 0, Max 100, Pas de 1, Vertical=False
        healthbBar = new ProgressBar(0, 100, 1, false, skin);
        healthbBar.setAnimateDuration(0.25f); // Animation fluide quand la vie change

        // MISE EN PAGE :
        // 1ère Ligne : Barre de vie (Gauche) .......... Temps (Droite)
        table.add(healthbBar).width(200).height(20).pad(10).left();
        table.add(timeLabel).expandX().pad(10).right(); // expandX pousse le temps vers la droite

        table.row(); // Nouvelle ligne

        // 2ème Ligne : Score (Gauche, sous la barre de vie)
        table.add(scoreLabel).padLeft(10).left();

        // Ajout final à la scène
        stage.addActor(table);
    }

    /**
     * Met à jour les valeurs affichées (Data Binding).
     * Appelé à chaque frame par le GameScreen.
     *
     * @param player Le joueur (pour lire ses PV actuels/max).
     */
    public void update(Player player) {

        // Mise à jour des textes via le ScoreManager (Singleton)
        scoreLabel.setText("Score: " + ScoreManager.getScoreManager().getScore());
        timeLabel.setText("Time: " + ScoreManager.getScoreManager().getFormattedTime());

        // Mise à jour de la barre de vie
        healthbBar.setRange(0, player.getMaxHealth());
        healthbBar.setValue(player.getCurrenthealth());
    }


    /**
     * Redimensionne le HUD quand la fenêtre change de taille.
     */
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }


    /**
     * Libère les ressources graphiques (Stage et Skin).
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

}
