package com.EthanKnittel.game;

import com.EthanKnittel.world.Environment;
import com.EthanKnittel.world.TestLevel;
import com.EthanKnittel.inputs.KeyboardInput;
import com.EthanKnittel.inputs.MouseInput;
import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {

    private KeyboardInput keyboardInput;
    private MouseInput mouseInput;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Player player;
    private Environment environment;

    @Override
    public void show() {
        keyboardInput = new KeyboardInput();
        mouseInput = new MouseInput();

        // on ajoute nos InputProcessors au multiplexeur
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(keyboardInput);
        inputMultiplexer.addProcessor(mouseInput);

        // On défini notre multiplexeur comme étant celui qui gère
        // tous les inputs tant que l'écran est actif
        Gdx.input.setInputProcessor(inputMultiplexer);

        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        environment = new Environment();
        player = new Player(200, 100, 100, 20, keyboardInput, mouseInput);
        environment.setLevel(new TestLevel());
        environment.addEntity(player);
    }

    @Override
    public void render(float delta) {
        // Update de nos inputs
        keyboardInput.update();
        mouseInput.update();
        // on bride le delta
        // (sinon lorsqu'on "secoue" la fenêtre notre personnage passe à travers les murs)
        float effectiveDelta = Math.min(delta, 1/16f);


        // Update de toutes les entités dont le joueur
        environment.update(effectiveDelta);

        //on fixe la caméra sur le joueur (le z est à 0 car on est en 2D)
        camera.position.set(player.GetX(), player.GetY(), 0);
        camera.update();

        // on efface l'écran en mettant du noir
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        environment.render(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = 800f;
        camera.viewportHeight = 800f * ((float)height / width);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
    @Override
    public void dispose() {
        environment.dispose();
        batch.dispose();
    }
}
