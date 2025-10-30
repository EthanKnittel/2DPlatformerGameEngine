package com.EthanKnittel.entities.agents;

import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.inputs.KeyboardInput;
import com.EthanKnittel.inputs.MouseInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

public class Player extends Agent {
    private final KeyboardInput keyboard;
    private final MouseInput mouse;

    private final float speed = 200f; // Vitesse de déplacement
    private Texture texture;

    public Player(float x, float y, int maxHealth, int damage, KeyboardInput keyboard, MouseInput mouse) {
        super(x,y,64f, 64f, maxHealth, damage);
        this.keyboard = keyboard;
        this.mouse = mouse;

        this.collision=true;

        try {
            texture = new Texture(Gdx.files.internal("knight.png"));
        } catch (Exception e) {
            Gdx.app.error("Player", "Error loading texture", e);
        }
    }

    @Override
    public void update(float deltaTime) {
        velocity.set(0, 0);

        // Déplacements
        if (keyboard.isKeyDown(Input.Keys.W)) {
            velocity.y = 1;
        }
        if (keyboard.isKeyDown(Input.Keys.S)) {
            velocity.y = -1;
        }
        if (keyboard.isKeyDown(Input.Keys.A)) {
            velocity.x = -1;
        }
        if (keyboard.isKeyDown(Input.Keys.D)) {
            velocity.x = 1;
        }
        velocity.scl(speed * deltaTime);

        // logique à rajouter dont celles de souris
    }

    @Override
    public void render(SpriteBatch batch) {
        if  (texture != null) {
            batch.draw(texture, position.x, position.y, bounds.width,bounds.height);
        }
    }
    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

}
