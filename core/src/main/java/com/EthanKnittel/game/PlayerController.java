package com.EthanKnittel.game;

import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.inputs.KeyboardInput;
import com.EthanKnittel.inputs.MouseInput;
import com.badlogic.gdx.Input;

public class PlayerController {
    private Player player;
    private KeyboardInput keyboard;
    private MouseInput mouse;

    public PlayerController(Player player, KeyboardInput keyboard, MouseInput mouse) {
        this.player = player;
        this.keyboard = keyboard;
        this.mouse = mouse;
    }

    public void update(float delta){
        processMovementInput();
        processActionInput();
    }

    private void processMovementInput(){
        boolean running = keyboard.isKeyDown(Input.Keys.SHIFT_LEFT);

        if (!keyboard.isKeyDown(Input.Keys.A) && !keyboard.isKeyDown(Input.Keys.D)) { // si on appuie gauche et droit, on est arreté (sur l'axe X)
            player.stopMovingX();
        }

        if (keyboard.isKeyDown(Input.Keys.A)) {
            player.moveLeft(running);
        }
        if (keyboard.isKeyDown(Input.Keys.D)) {
            player.moveRight(running);
        }
    }

    private void processActionInput() {
        if (keyboard.isKeyDownNow(Input.Keys.SPACE)) {
            player.jump();
        }

        if (mouse.isButtonDownNow(0)) {
            player.shoot(mouse.GetPosX(), mouse.GetPosY());
        }
    }
}
