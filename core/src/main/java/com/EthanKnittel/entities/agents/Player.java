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
    private final float jumpSpeed = 400f;
    private Texture texture;

    public Player(float x, float y, int maxHealth, int damage, KeyboardInput keyboard, MouseInput mouse) {
        super(x,y,64f, 64f, maxHealth, damage);
        this.keyboard = keyboard;
        this.mouse = mouse;
        this.SetCollision(false); // ce n'est pas un "obstacle"

        try { // on essaie de charger la texture
            texture = new Texture(Gdx.files.internal("knight.png"));
        } catch (Exception e) { // si ça marche pas:
            Gdx.app.error("Player", "Error loading texture", e);
        }
    }

    @Override
    public void update(float deltaTime) {
        SetVelocityX(0);
        // Déplacements
        if (keyboard.isKeyDown(Input.Keys.A)) {
            SetVelocityX(-speed);
        }
        if (keyboard.isKeyDown(Input.Keys.D)) {
            SetVelocityX(speed);
        }
        if (keyboard.isKeyDown(Input.Keys.SPACE) && GetIsGrounded()) {
            SetVelocityY(jumpSpeed);
        }


        // logique à rajouter dont celles de souris
    }

    @Override
    public void render(SpriteBatch batch) {
        if  (texture != null) {
            batch.draw(texture, GetX(), GetY(), GetBounds().width, GetBounds().height);
        }
    }
    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

}
