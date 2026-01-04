package com.EthanKnittel.graphics.entity;

import com.EthanKnittel.entities.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface EntityView {
    void render(SpriteBatch batch, float delta);
    void dispose();
    Entity getEntity();
}
