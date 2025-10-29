package com.EthanKnittel;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameEngine extends ApplicationAdapter {
    private KeyboardInput keyboardInput;
    private MouseInput mouseInput;

    @Override
    public void create() {
        keyboardInput = new KeyboardInput();
        mouseInput = new MouseInput();

        //Pour permettre à plusieurs InputProcessors de fonctionner en même temps
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(keyboardInput);
        inputMultiplexer.addProcessor(mouseInput);

        // On défini notre multiplexeur comme étant celui qui gère tous les inputs
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render() {
        // l'update de nos inputsProcessors
        keyboardInput.update();
        mouseInput.update();
    }

    @Override
    public void dispose() {
    }
}
