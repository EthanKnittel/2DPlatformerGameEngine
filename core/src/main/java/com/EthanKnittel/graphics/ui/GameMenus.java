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

public class GameMenus implements Disposable {

    private Stage stage;
    private Skin skin;
    private MenuCallback callback;

    private Table pauseTable;
    private Table deathTable;

    public GameMenus(SpriteBatch batch, MenuCallback callback) {
        this.callback = callback;
        // On crée un Stage indépendant pour l'UI
        this.stage = new Stage(new ExtendViewport(800, 600), batch);
        this.skin = SkinFactory.createSkin(); // On utilise notre Factory

        createPauseMenu();
        createDeathScreen();
    }

    private void createPauseMenu() {
        pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseTable.setVisible(false); // Caché par défaut

        // Fond semi-transparent
        TextureRegionDrawable background = new TextureRegionDrawable(skin.getRegion("white"));
        pauseTable.setBackground(background.tint(new Color(0, 0, 0, 0.8f)));

        Label pauseLabel = new Label("PAUSE", skin);
        pauseLabel.setFontScale(2f);

        TextButton resumeBtn = new TextButton("Reprendre", skin);
        TextButton menuBtn = new TextButton("Menu Principal", skin);
        TextButton quitBtn = new TextButton("Quitter", skin);

        // --- GESTION DES EVENEMENTS (Event-Driven) ---
        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callback.onResume(); // On appelle le contrat
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

        // Mise en page
        pauseTable.add(pauseLabel).padBottom(50).row();
        pauseTable.add(resumeBtn).width(200).height(50).padBottom(20).row();
        pauseTable.add(menuBtn).width(200).height(50).padBottom(20).row();
        pauseTable.add(quitBtn).width(200).height(50).row();

        stage.addActor(pauseTable);
    }

    private void createDeathScreen() {
        deathTable = new Table();
        deathTable.setFillParent(true);
        deathTable.setVisible(false);

        TextureRegionDrawable background = new TextureRegionDrawable(skin.getRegion("white"));
        deathTable.setBackground(background.tint(new Color(0, 0, 0, 0.85f)));

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

        // <--- LISTENER DU NOUVEAU BOUTON
        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callback.onGoToMainMenu(); // On réutilise la même méthode que pour le menu pause !
            }
        });

        // --- MISE EN PAGE ---
        deathTable.add(deadLabel).padBottom(50).row();
        deathTable.add(restartBtn).width(200).height(50).padBottom(20).row();
        deathTable.add(menuBtn).width(200).height(50); // <--- AJOUT DANS LA TABLE

        stage.addActor(deathTable);
    }

    public void render(float delta) {
        // Le Stage gère lui-même ses animations et son rendu
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    // Méthodes de contrôle
    public void showPause() {
        pauseTable.setVisible(true);
        deathTable.setVisible(false);
    }

    public void hidePause() {
        pauseTable.setVisible(false);
    }

    public void showDeath() {
        deathTable.setVisible(true);
        pauseTable.setVisible(false);
    }

    public boolean isPauseVisible() {
        return pauseTable.isVisible();
    }

    // Pour que LibGDX sache gérer les clics sur CE stage
    public InputProcessor getInputProcessor() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
