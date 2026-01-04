package com.EthanKnittel.graphics;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.foes.Cactus;
import com.EthanKnittel.entities.agents.foes.Ordi;
import com.EthanKnittel.entities.artifacts.FireArrow;
import com.EthanKnittel.entities.artifacts.Wall;
import com.EthanKnittel.graphics.entity.*;
import com.EthanKnittel.world.Environment;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class WorldRenderer {
    private Environment environment;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private Array<EntityView> views;

    public WorldRenderer(Environment environment, SpriteBatch batch, OrthographicCamera camera) {
        this.environment = environment;
        this.batch = batch;
        this.camera = camera;
        this.views = new Array<>();
    }

    public void render(float delta){
        environment.getLevel().renderBackground(camera);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        for (Entity entity : environment.getEntities()) {
            boolean viewExists = false;
            for (EntityView view : views) {
                if (view.getEntity().equals(entity)) {
                    viewExists = true;
                    break;
                }
            }
            if (!viewExists) {
                if (entity instanceof Ordi){
                    views.add(new OrdiView((Ordi) entity));
                }
                if  (entity instanceof Cactus) {
                    views.add(new CactusView((Cactus) entity));
                }
                if (entity instanceof FireArrow){
                    views.add(new FireArrowView((FireArrow) entity));
                }
                if (entity instanceof Wall){
                    views.add(new WallView((Wall) entity));
                }
                //  ajout d'autres entités ici
            }
        }
        for (int i = views.size - 1; i >= 0; i--) {
            EntityView view = views.get(i);

            if (!environment.getEntities().contains(view.getEntity(), true)){
                view.dispose();
                views.removeIndex(i);
            } else {
                view.render(batch, delta);
            }
        }

        batch.end();

        environment.getLevel().renderAbove(camera);
    }

    public void dispose(){
        for (EntityView view : views) {
            view.dispose();
        }
        views.clear();
    }
}
