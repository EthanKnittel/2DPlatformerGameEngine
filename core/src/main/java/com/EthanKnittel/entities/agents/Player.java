package com.EthanKnittel.entities.agents;

import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.inputs.KeyboardInput;
import com.EthanKnittel.inputs.MouseInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Player extends Agent {
    private final KeyboardInput keyboard;
    private final MouseInput mouse;

    private final float speed = 200f; // Vitesse de déplacement
    private Texture texture;
    private final Vector2 velocity = new Vector2();

    public Player(float x, float y, int maxHealth, int damage, KeyboardInput keyboard, MouseInput mouse) {
        super(x,y,maxHealth,damage);
        this.keyboard = keyboard;
        this.mouse = mouse;

        try {
            texture = new Texture(Gdx.files.internal("libgdx.png"));
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
        position.add(velocity);

        // logique à rajouter dont celles de souris
    }

    @Override
    public void render(SpriteBatch batch) {
        if  (texture != null) {
            batch.draw(texture, position.x, position.y);
        }
    }
    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

}
