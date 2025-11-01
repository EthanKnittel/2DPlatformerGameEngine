package com.EthanKnittel.entities.artifacts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.EthanKnittel.entities.Artifact;

public class Wall extends Artifact{
    private Texture texture;
    private static final float wall_size = 64f;

    public Wall(float x, float y){
        super(x, y, wall_size, wall_size);
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
            batch.draw(texture, GetX(), GetY(), GetBounds().width, GetBounds().height);
        }
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
