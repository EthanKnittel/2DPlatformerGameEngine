package com.EthanKnittel.entities.agents;

import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.inputs.KeyboardInput;
import com.EthanKnittel.inputs.MouseInput;
import com.EthanKnittel.graphics.AnimationManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;

public class Player extends Agent {
    private final KeyboardInput keyboard;
    private final MouseInput mouse;

    private final float speed = 200f; // Vitesse de déplacement
    private final float jumpSpeed = 400f;
    private final float wallJumpYSpeed = 500f;
    private final float wallJumpXSpeed = 300f;
    private float wallJumpTimer = 0f;
    private final float wallJumpControl = 0.1f; // temps avant de pouvoir recontroler notre personnage

    private transient Texture spriteSheet;
    private transient AnimationManager animationManager;

    public Player(float x, float y, int maxHealth, int damage, KeyboardInput keyboard, MouseInput mouse) {
        super(x,y,64f, 64f, maxHealth, damage);
        this.keyboard = keyboard;
        this.mouse = mouse;
        this.SetCollision(false); // ce n'est pas un "obstacle"

        try { // on essaie de charger la texture
            spriteSheet = new Texture(Gdx.files.internal("WALK.png"));
            // Pour des sprites de 64x64
            TextureRegion[][] textureRegions = TextureRegion.split(spriteSheet, 32, 64);
            Array<TextureRegion> sprite = new Array<>();
            for (int i = 0; i < textureRegions.length; i++) {
                for (int j = 0; j < textureRegions[i].length; j++) {
                    sprite.add(textureRegions[i][j]);
                }
            }
            float frameDuration = 0.2f;
            Animation<TextureRegion> animation = new Animation<>(frameDuration, sprite, Animation.PlayMode.LOOP);
            animationManager = new AnimationManager(animation);

        } catch (Exception e) { // si ça marche pas:
            Gdx.app.error("Player", "Error loading texture", e);
        }
    }

    @Override
    public void update(float deltaTime) {
        if (animationManager != null) {
            animationManager.update(deltaTime);
        }
        if (wallJumpTimer > 0){
            wallJumpTimer -= deltaTime;
        }
        if (GetIsGrounded()) {
            SetVelocityX(0);
        }
        // Déplacements
        if (wallJumpTimer <= 0) {
            if (keyboard.isKeyDown(Input.Keys.A)) {
                SetVelocityX(-speed);
            }
            if (keyboard.isKeyDown(Input.Keys.D)) {
                SetVelocityX(speed);
            }
        }
        if (keyboard.isKeyDown(Input.Keys.SPACE)) {
            if (GetIsGrounded()) {
                SetVelocityY(jumpSpeed);
                SetGrounded(false);
            } else if (IsTouchingWall()){
                SetVelocityY(wallJumpYSpeed);
                if (IsWallOnLeft()){
                    SetVelocityX(wallJumpXSpeed);
                } else {
                    SetVelocityX(-wallJumpXSpeed);
                }
                wallJumpTimer = wallJumpControl;
                SetGrounded(false);
            }
        }


        // logique à rajouter dont celles de souris
    }

    @Override
    public void render(SpriteBatch batch) {
        if  (animationManager != null) {
            TextureRegion currentFrame = animationManager.getFrame();
            batch.draw(currentFrame, GetX(), GetY(), GetBounds().width, GetBounds().height);
        }
    }
    @Override
    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
        }
    }

}
