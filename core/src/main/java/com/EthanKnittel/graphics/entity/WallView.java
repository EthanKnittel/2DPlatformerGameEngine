package com.EthanKnittel.graphics.entity;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.artifacts.Wall;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WallView implements EntityView{
    private Wall wall;
    private Texture texture;

    public WallView(Wall wall) {
        this.wall = wall;
        if (wall.getVisible()) {
            try {
                texture = new Texture(Gdx.files.internal("wall.png"));
            } catch (Exception e) {
                Gdx.app.error("Wall", "Error loading texture", e);
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (texture != null) {
            batch.draw(texture, wall.getX(), wall.getY(), wall.getbounds().width, wall.getbounds().height);
        }
    }

    @Override
    public Entity getEntity() {
        return wall;
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
