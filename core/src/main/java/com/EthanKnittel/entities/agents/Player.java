package com.EthanKnittel.entities.agents;

import com.EthanKnittel.audio.AudioManager;
import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.game.GameScreen;
import com.EthanKnittel.inputs.KeyboardInput;
import com.EthanKnittel.inputs.MouseInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.EthanKnittel.world.Environment;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.EthanKnittel.entities.artifacts.FireArrow;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Player extends Agent {
    private final KeyboardInput keyboard;
    private final MouseInput mouse;
    private TextureAtlas atlas;
    private Animation<TextureRegion> idleAnim, walkAnim, runAnim, jumpAnim, wallSlideAnim, hitAnim, fallAnim, doubleJumpAnim;

    private final float walkSpeed = 200f/ GameScreen.getPixelsPerBlocks(); // Vitesse de déplacement
    private final float runSpeed = 300f/ GameScreen.getPixelsPerBlocks();
    private final float jumpSpeed = 400f/ GameScreen.getPixelsPerBlocks();
    private final float wallJumpYSpeed = 500f/ GameScreen.getPixelsPerBlocks();
    private final float wallJumpXSpeed = 300f/ GameScreen.getPixelsPerBlocks();
    private float wallJumpTimer = 0f;
    private final float wallJumpControl = 0.1f; // temps avant de pouvoir recontroler notre personnage
    private int jumpCount = 0;
    private int jumpCountMax;
    private Environment environment;
    private Viewport viewport;

    private transient Texture spriteSheet;


    public Player(float x, float y,float width, float height, int maxHealth, int damage, int jumpCountMax, KeyboardInput keyboard, MouseInput mouse, Environment env, Viewport viewport) {
        super(x,y,width, height, maxHealth, damage);
        this.environment = env;
        this.viewport = viewport;
        this.jumpCountMax = jumpCountMax;
        this.keyboard = keyboard;
        this.mouse = mouse;
        this.setCollision(false); // ce n'est pas un "obstacle"
        loadAnimation();
        this.setHitStunDuration(0f); // pas de stun pour le joueur
        this.setInvincibilityDuration(1.5f);
        this.setIsPlayer(true);
        this.setVisualHitDuration(0.3f);
        setJumpSoundName("jumpEffectSound");
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
            doubleJumpAnim = new Animation<>(0.1f, atlas.findRegions("DOUBLEJUMP"), Animation.PlayMode.LOOP);

            setAnimation(idleAnim); // on défini de base le Idle
        } catch (Exception e) {
            Gdx.app.error("Player", "Erreur de chargement de l'atlas", e);
        }
    }


    @Override
    public void update(float deltaTime) {

        if (!isAlive()){
            setAnimation(hitAnim);
            setVelocityX(0);
            super.update(deltaTime);
            return;
        }
        if (isHit()) {
            setAnimation(hitAnim);
            super.update(deltaTime);
            return;
        }

        super.update(deltaTime); // update des animations provenant de Agent

        if (mouse.isButtonDownNow(0)) {
            shoot();
        }

        if(getGrounded()){
            jumpCount = 0;
            setVelocityX(0);
        }
        if (wallJumpTimer > 0){
            wallJumpTimer -= deltaTime;
        }

        boolean running = (keyboard.isKeyDown(Input.Keys.SHIFT_LEFT));

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

        // Sauts
        if (keyboard.isKeyDownNow(Input.Keys.SPACE)) {
            boolean jumped = false;
            if (getGrounded()) {
                setVelocityY(jumpSpeed);
                setGrounded(false);
                jumpCount = 1;
                jumped = true;
            } else if (getTouchingWall() && !getGrounded() && jumpCount < jumpCountMax) {
                setVelocityY(wallJumpYSpeed);
                if (getWallOnLeft()){
                    setVelocityX(wallJumpXSpeed);
                } else {
                    setVelocityX(-wallJumpXSpeed);
                }
                wallJumpTimer = wallJumpControl;
                setGrounded(false);
                jumpCount++;
                jumped = true;
            } else if (jumpCount < jumpCountMax){
                setVelocityY(jumpSpeed);
                if (jumpCount == 0){
                    jumpCount = 2;
                } else {
                    jumpCount++;
                }
                jumped = true;
            }
            if (jumped && getJumpSoundName() != null && AudioManager.audioManager != null){
                AudioManager.audioManager.playSound(getJumpSoundName());
            }
        }

        // Animations
        if (getVisualHitActive()){
            setAnimation(hitAnim);
        } else if (getTouchingWall() && !getGrounded() && getVelocity().y<0){
            setAnimation(wallSlideAnim);
        } else if (!getGrounded()) {
            if (getVelocity().y<0){
                setAnimation(fallAnim);
            } else {
                if (jumpCount > 1 ){
                    setAnimation(doubleJumpAnim);
                } else {
                    setAnimation(jumpAnim);
                }
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

        // logique à rajouter dont celles de souris
    }

    @Override
    public void dispose() {
        if (atlas != null) {
            atlas.dispose();
        }
    }

    private void shoot() {
        // Position de la souris
        int screenX = mouse.GetPosX();
        int screenY = mouse.GetPosY();

        // Conversion des positions pour le monde
        Vector3 worldPos = new Vector3(screenX, screenY, 0);
        viewport.unproject(worldPos);

        // Départ depuis le centre du joueur
        float startX = getX() + getbounds().width / 2f;
        float startY = getY() + getbounds().height / 2f;

        // Création projectiles
        FireArrow arrow = new FireArrow(startX, startY, worldPos.x, worldPos.y);
        environment.addEntity(arrow);
    }
}

