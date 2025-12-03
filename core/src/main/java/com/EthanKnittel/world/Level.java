package com.EthanKnittel.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Array;
import com.EthanKnittel.entities.Entity;

public abstract class Level implements Disposable {
    public abstract Array<Entity> load();
    public abstract void renderAbove(OrthographicCamera camera);
    public abstract void renderBackground(OrthographicCamera camera);
}
