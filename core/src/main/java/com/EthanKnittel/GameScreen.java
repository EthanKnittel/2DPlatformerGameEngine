package com.EthanKnittel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Screen;

public class GameScreen implements Screen {

    private KeyboardInput keyboardInput;
    private MouseInput mouseInput;

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
    }

    @Override
    public void render(float delta) {
        // Update de nos inputs
        keyboardInput.update();
        mouseInput.update();

        // on efface l'écran en mettant du noir
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {

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

    }
}
