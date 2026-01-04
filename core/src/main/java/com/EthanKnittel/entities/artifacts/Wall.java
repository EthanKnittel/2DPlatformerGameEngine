package com.EthanKnittel.entities.artifacts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.EthanKnittel.entities.Artifact;

public class Wall extends Artifact{
    private boolean visible;

    public Wall(float x, float y, float width, float height, boolean loadTexture) {
        super(x, y, width, height);
        this.visible = loadTexture;
    }

    public boolean getVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void update(float delta) {
        // rien car un mur ne fait rien
    }
}
