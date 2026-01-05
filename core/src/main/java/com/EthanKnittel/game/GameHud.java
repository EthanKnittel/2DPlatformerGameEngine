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

public class GameHud implements Disposable {
    public Stage stage;
    private Viewport viewport;

    private Label scoreLabel;
    private Label timeLabel;
    private ProgressBar healthbBar;

    private Skin skin;

    public GameHud(SpriteBatch batch) {
        this.viewport = new ExtendViewport(800,600);
        this.stage = new Stage(viewport,batch);
        createSkin();
        setupLayout();
    }

    private void createSkin() {
        skin = new Skin();

        Pixmap pixmap = new Pixmap(1,1,Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture baseTexture = new Texture(pixmap);

        skin.add("white", baseTexture);
        skin.add("default", new BitmapFont());

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        skin.add("default",labelStyle);

        int totalHeight = 26;
        int border = 3;
        int fillHeight = totalHeight - (border * 2);

        BaseDrawable barBorder = (BaseDrawable) skin.newDrawable("white", Color.BLACK);

        barBorder.setMinHeight(totalHeight);

        // on "coince" la barre en définissant les marges et avoir les bordures en noir
        barBorder.setTopHeight(border);
        barBorder.setBottomHeight(border);
        barBorder.setLeftWidth(border);
        barBorder.setRightWidth(border);

        // remplissage de la barre de vie
        BaseDrawable barFill = (BaseDrawable) skin.newDrawable("white", Color.GREEN);
        barFill.setMinHeight(fillHeight);

        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle(barBorder, null);
        style.knobBefore = barFill;

        skin.add("default-horizontal",style);
    }

    private void setupLayout() {
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        scoreLabel = new Label("Score: 0", skin);
        timeLabel = new Label("Time: 00:00", skin);

        healthbBar = new ProgressBar(0, 100, 1, false, skin);
        healthbBar.setAnimateDuration(0.25f);

        table.add(healthbBar).width(200).height(20).pad(10).left();
        table.add(timeLabel).expandX().pad(10).right();
        table.row();

        table.add(scoreLabel).padLeft(10).left();

        stage.addActor(table);
    }

    public void update(Player player) {
        scoreLabel.setText("Score: " + ScoreManager.instance.getScore());
        timeLabel.setText("Time: " + ScoreManager.instance.getFormattedTime());

        healthbBar.setRange(0, player.getMaxHealth());
        healthbBar.setValue(player.getCurrenthealth());
    }


    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

}
