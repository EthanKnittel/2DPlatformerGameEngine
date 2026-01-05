package com.EthanKnittel.game;

import com.badlogic.gdx.Game;


public class GameEngine extends Game {

    @Override
    public void create() {
        // On lance le premier écran de jeu
        setScreen(new MainMenuScreen());
    }

    // Le render est géré par la classe Game directement

    @Override
    public void dispose() {
        super.dispose();
    }
}
