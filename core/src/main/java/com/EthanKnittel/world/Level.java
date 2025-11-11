package com.EthanKnittel.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Disposable;

public abstract class Level implements Disposable {
    public abstract void load(Environment environment);
    public abstract void render(OrthographicCamera camera);
}
