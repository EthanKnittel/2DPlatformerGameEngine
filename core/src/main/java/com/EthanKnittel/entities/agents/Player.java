package com.EthanKnittel.entities.agents;

import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.game.GameScreen;
import com.EthanKnittel.inputs.KeyboardInput;
import com.EthanKnittel.inputs.MouseInput;
import com.EthanKnittel.graphics.AnimationManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Player extends Agent {
    private final KeyboardInput keyboard;
    private final MouseInput mouse;
    private TextureAtlas atlas;
    private Animation<TextureRegion> idleAnim, walkAnim, runAnim, jumpAnim, wallSlideAnim, hitAnim, fallAnim;

    private final float walkSpeed = 200f/ GameScreen.getPixelsPerBlocks(); // Vitesse de déplacement
    private final float runSpeed = 300f/ GameScreen.getPixelsPerBlocks();
    private final float jumpSpeed = 400f/ GameScreen.getPixelsPerBlocks();
    private final float wallJumpYSpeed = 500f/ GameScreen.getPixelsPerBlocks();
    private final float wallJumpXSpeed = 300f/ GameScreen.getPixelsPerBlocks();
    private float wallJumpTimer = 0f;
    private final float wallJumpControl = 0.1f; // temps avant de pouvoir recontroler notre personnage
    private transient AnimationManager animationManager;

    private transient Texture spriteSheet;


    public Player(float x, float y,float width, float height, int maxHealth, int damage, KeyboardInput keyboard, MouseInput mouse) {
        super(x,y,width, height, maxHealth, damage);
        this.keyboard = keyboard;
        this.mouse = mouse;
        this.setCollision(false); // ce n'est pas un "obstacle"
        loadAnimation();
    }


    private void loadAnimation(){
        try {
            atlas = new TextureAtlas(Gdx.files.internal("Player1/Player1.atlas"));
            idleAnim = new Animation<>(0.1f, atlas.findRegions("IDLE"), Animation.PlayMode.LOOP);
            walkAnim = new Animation<>(0.1f, atlas.findRegions("RUNNING"), Animation.PlayMode.LOOP);
            runAnim = new Animation<>(0.05f, atlas.findRegions("RUNNING"), Animation.PlayMode.LOOP);
            jumpAnim = new Animation<>(0.1f, atlas.findRegions("JUMPING"), Animation.PlayMode.LOOP);
            wallSlideAnim = new Animation<>(0.1f, atlas.findRegions("WALLJUMPING"), Animation.PlayMode.LOOP);
            hitAnim = new Animation<>(0.1f, atlas.findRegions("HIT"), Animation.PlayMode.LOOP);
            fallAnim = new Animation<>(0.1f, atlas.findRegions("FALLING"), Animation.PlayMode.LOOP);

            setAnimation(idleAnim); // on défini de base le Idle
        } catch (Exception e) {
            Gdx.app.error("Player", "Erreur de chargement de l'atlas", e);
        }
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime); // update des animations provenant de Agent
        boolean running = (keyboard.isKeyDown(Input.Keys.SHIFT_LEFT));

        if (getTouchingWall() && !getGrounded()){
            setAnimation(wallSlideAnim);
        } else if (!getGrounded()) {
            if (getVelocity().y<0){
                setAnimation(fallAnim);
            } else {
                setAnimation(jumpAnim);
            }
        } else if (getVelocity().x != 0){
            if (running){
                setAnimation(runAnim);
            }
            else {
                setAnimation(walkAnim);
            }
        } else {
            setAnimation(idleAnim);
        }

        if (getVelocity().x < 0 ){
            setFacingLeft(true);
        } else if (getVelocity().x > 0) {
            setFacingLeft(false);

        }

        if (wallJumpTimer > 0){
            wallJumpTimer -= deltaTime;
        }
        if (getGrounded()) {
            setVelocityX(0);
        }
        // Déplacements
        if (wallJumpTimer <= 0) {
            float currentSpeed;
            if (running) {
                currentSpeed = runSpeed;
            } else {
                currentSpeed = walkSpeed;
            }

            if (keyboard.isKeyDown(Input.Keys.A)) {
                setVelocityX(-currentSpeed);
            }
            if (keyboard.isKeyDown(Input.Keys.D)) {
                setVelocityX(currentSpeed);
            }
        }
        if (keyboard.isKeyDown(Input.Keys.SPACE)) {
            if (getGrounded()) {
                setVelocityY(jumpSpeed);
                setGrounded(false);
            } else if (getTouchingWall() && getVelocity().y <= 0){
                setVelocityY(wallJumpYSpeed);
                if (getWallOnLeft()){
                    setVelocityX(wallJumpXSpeed);
                } else {
                    setVelocityX(-wallJumpXSpeed);
                }
                wallJumpTimer = wallJumpControl;
                setGrounded(false);
            }
        }


        // logique à rajouter dont celles de souris
    }

    @Override
    public void dispose() {
        if (atlas != null) {
            atlas.dispose();
        }
    }

}
