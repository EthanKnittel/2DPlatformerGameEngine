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

public class SkinFactory {

    public static Skin createSkin() {
        Skin skin = new Skin();

        // 1. Génération des textures de base (blanc pur)
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture whiteTexture = new Texture(pixmap);
        skin.add("white", whiteTexture);

        // 2. Polices
        BitmapFont font = new BitmapFont();
        skin.add("default", font);

        // 3. Styles des Labels
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        skin.add("default", labelStyle);

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = font;
        titleStyle.fontColor = Color.RED;
        skin.add("title", titleStyle);

        // 4. Styles des Boutons
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = new TextureRegionDrawable(new TextureRegionDrawable(whiteTexture)).tint(Color.GRAY);
        textButtonStyle.down = new TextureRegionDrawable(new TextureRegionDrawable(whiteTexture)).tint(Color.DARK_GRAY);
        textButtonStyle.over = new TextureRegionDrawable(new TextureRegionDrawable(whiteTexture)).tint(Color.LIGHT_GRAY);
        textButtonStyle.fontColor = Color.BLACK;
        skin.add("default", textButtonStyle);

        // 5. Styles de ProgressBar
        BaseDrawable barBorder = (BaseDrawable) skin.newDrawable("white", Color.BLACK);
        barBorder.setMinHeight(26);
        barBorder.setTopHeight(3); barBorder.setBottomHeight(3);
        barBorder.setLeftWidth(3); barBorder.setRightWidth(3);

        BaseDrawable barFill = (BaseDrawable) skin.newDrawable("white", Color.GREEN);
        barFill.setMinHeight(20);

        ProgressBar.ProgressBarStyle barStyle = new ProgressBar.ProgressBarStyle(barBorder, null);
        barStyle.knobBefore = barFill;
        skin.add("default-horizontal", barStyle);

        // 6. AJOUT : Style du ScrollPane (C'était l'erreur !)
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        // Fond de la barre verticale (gris foncé)
        scrollPaneStyle.vScroll = new TextureRegionDrawable(new TextureRegionDrawable(whiteTexture)).tint(Color.DARK_GRAY);
        // Bouton qu'on déplace (gris clair)
        scrollPaneStyle.vScrollKnob = new TextureRegionDrawable(new TextureRegionDrawable(whiteTexture)).tint(Color.LIGHT_GRAY);

        skin.add("default", scrollPaneStyle);

        return skin;
    }
}
