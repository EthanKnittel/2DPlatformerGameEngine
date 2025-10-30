package com.EthanKnittel.entities.artifacts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.EthanKnittel.entities.Artifact;

public class Wall extends Artifact{
    private Texture texture;

    public Wall(float x, float y){
        super(x, y);
        try {
            texture = new Texture(Gdx.files.internal("wall.png"));
        } catch (Exception e){
            Gdx.app.error("Wall", "Error loading texture");
        }
    }
    @Override
    public void update(float delta) {
        // rien car un mur ne fait rien
    }
    @Override
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x, position.y);
        }
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
