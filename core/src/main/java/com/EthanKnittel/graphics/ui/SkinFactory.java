package com.EthanKnittel.graphics.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Usine (Factory) pour créer l'apparence de l'interface utilisateur (Skin).
 * <p>
 * Dans LibGDX, une {@link Skin} est un conteneur qui stocke les ressources (Textures, Polices, Couleurs)
 * et les styles (boutons, labels...) utilisés par les widgets de la Scene2D.
 * </p>
 * <p>
 * Cette classe génère une Skin "programmatique" : au lieu de charger des fichiers externes,
 * elle crée des textures de couleur unie (via {@link Pixmap}) en mémoire.
 * Cela permet d'avoir une UI fonctionnelle immédiatement sans graphiste.
 * </p>
 */
public class SkinFactory {

    /**
     * Génère et configure une nouvelle Skin complète.
     *
     * @return L'objet Skin prêt à être utilisé par les Stages.
     */
    public static Skin createSkin() {
        Skin skin = new Skin();

        // --- 1. GÉNÉRATION DES TEXTURES DE BASE ---
        // On crée une image de 1x1 pixel en mémoire, remplie de blanc pur.
        // Pourquoi blanc ? Car on pourra ensuite la teinter (tint) en n'importe quelle couleur
        // (Rouge, Gris, Noir...) lors du dessin. C'est la technique du "White Pixel".
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        // On convertit le Pixmap (CPU) en Texture (GPU)
        Texture whiteTexture = new Texture(pixmap);

        // On ajoute cette texture à la skin sous le nom "white" pour s'en resservir partout
        skin.add("white", whiteTexture);

        // On libère le pixmap car la texture est créée
        pixmap.dispose();

        // --- 2. CONFIGURATION DES POLICES ---
        // On utilise la police par défaut de LibGDX (Arial 15pt environ).
        // Pour un vrai jeu, on chargerait ici un .fnt ou .ttf personnalisé.
        BitmapFont font = new BitmapFont();
        skin.add("default", font);

        // --- 3. STYLES DES LABELS (Textes) ---

        // Style standard (Blanc)
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        skin.add("default", labelStyle);

        // Style "Titre" (Rouge) - Utilisé pour les "Game Over" ou en-têtes
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = font;
        titleStyle.fontColor = Color.RED;
        skin.add("title", titleStyle);

        // --- 4. STYLES DES BOUTONS ---
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;

        // État NORMAL : Gris
        textButtonStyle.up = new TextureRegionDrawable(new TextureRegionDrawable(whiteTexture)).tint(Color.GRAY);

        // État ENFONCÉ (Clic) : Gris Foncé
        textButtonStyle.down = new TextureRegionDrawable(new TextureRegionDrawable(whiteTexture)).tint(Color.DARK_GRAY);

        // État SURVOL (Souris dessus) : Gris Clair
        textButtonStyle.over = new TextureRegionDrawable(new TextureRegionDrawable(whiteTexture)).tint(Color.LIGHT_GRAY);

        textButtonStyle.fontColor = Color.BLACK; // Couleur du texte
        skin.add("default", textButtonStyle);

        // --- 5. STYLES DES BARRES DE PROGRESSION (Barre de vie) ---

        // Fond de la barre (Bordure noire)
        // BaseDrawable permet de définir des minHeight/minWidth pour que la barre ait une épaisseur
        BaseDrawable barBorder = (BaseDrawable) skin.newDrawable("white", Color.BLACK);
        barBorder.setMinHeight(26);
        // On définit un padding de 3px pour créer l'effet de bordure
        barBorder.setTopHeight(3); barBorder.setBottomHeight(3);
        barBorder.setLeftWidth(3); barBorder.setRightWidth(3);

        // Intérieur de la barre (Vert)
        BaseDrawable barFill = (BaseDrawable) skin.newDrawable("white", Color.GREEN);
        barFill.setMinHeight(20);

        ProgressBar.ProgressBarStyle barStyle = new ProgressBar.ProgressBarStyle(barBorder, null);
        barStyle.knobBefore = barFill; // "knobBefore" est la partie remplie (à gauche du curseur)

        skin.add("default-horizontal", barStyle);

        // --- 6. STYLE DU SCROLLPANE (Défilement crédits) ---
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();

        // Fond de la barre verticale (Rail) -> Gris foncé
        scrollPaneStyle.vScroll = new TextureRegionDrawable(new TextureRegionDrawable(whiteTexture)).tint(Color.DARK_GRAY);

        // Bouton qu'on déplace (Knob) -> Gris clair
        scrollPaneStyle.vScrollKnob = new TextureRegionDrawable(new TextureRegionDrawable(whiteTexture)).tint(Color.LIGHT_GRAY);

        skin.add("default", scrollPaneStyle);

        return skin;
    }
}
