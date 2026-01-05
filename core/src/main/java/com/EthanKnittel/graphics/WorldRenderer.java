package com.EthanKnittel.graphics;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.entities.agents.foes.Cactus;
import com.EthanKnittel.entities.agents.foes.Ordi;
import com.EthanKnittel.entities.artifacts.FireArrow;
import com.EthanKnittel.entities.artifacts.Wall;
import com.EthanKnittel.graphics.entity.*;
import com.EthanKnittel.world.Level;
import com.EthanKnittel.world.TiledLevel;
import com.EthanKnittel.world.systems.Environment;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class WorldRenderer {
    private Environment environment;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private LevelView levelView;

    private ShapeRenderer shapeRenderer;

    private Array<EntityView> views;

    public WorldRenderer(Environment environment, SpriteBatch batch, OrthographicCamera camera) {
        this.environment = environment;
        this.batch = batch;
        this.camera = camera;
        this.views = new Array<>();
        this.shapeRenderer = new ShapeRenderer();
        if (environment.getLevel().getClass().equals(TiledLevel.class)){
            this.levelView = new LevelView((TiledLevel) environment.getLevel());
        }
    }

    public void render(float delta){
        if (levelView != null) {
            levelView.renderBackground(camera);
        }

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
                if (entity instanceof Player){
                    views.add(new PlayerView((Player) entity));
                }
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

        if (levelView != null) {
            levelView.renderAbove(camera);
        }
    }

    public void setLevel(Level level) {
        // 1. On nettoie l'ancienne vue pour éviter les fuites de mémoire
        if (levelView != null) {
            levelView.dispose();
            levelView = null;
        }

        // 2. Si le nouveau niveau est un TiledLevel, on recrée la vue
        if (level instanceof TiledLevel) {
            this.levelView = new LevelView((TiledLevel) level);
        }
    }

    public void renderFade(float alpha){
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, alpha); // noir avec transparence

        float width = camera.viewportWidth * camera.zoom;
        float height = camera.viewportHeight * camera.zoom;
        shapeRenderer.rect(camera.position.x - width / 2, camera.position.y - height / 2, width, height);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose(){
        for (EntityView view : views) {
            view.dispose();
        }
        views.clear();
        shapeRenderer.dispose();
    }
}
