package com.EthanKnittel.entities.agents;

import com.EthanKnittel.audio.AudioManager;
import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.game.GameScreen;
import com.EthanKnittel.world.systems.Environment;
import com.badlogic.gdx.math.Vector3;
import com.EthanKnittel.entities.artifacts.FireArrow;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Player extends Agent {

    private boolean running = false;
    private boolean jumping = false;
    private int jumpCount = 0;
    private int jumpCountMax;

    private final float walkSpeed = 200f/ GameScreen.getPixelsPerBlocks(); // Vitesse de déplacement
    private final float runSpeed = 300f/ GameScreen.getPixelsPerBlocks();
    private final float jumpSpeed = 400f/ GameScreen.getPixelsPerBlocks();
    private final float wallJumpYSpeed = 500f/ GameScreen.getPixelsPerBlocks();
    private final float wallJumpXSpeed = 300f/ GameScreen.getPixelsPerBlocks();

    private float wallJumpTimer = 0f;
    private final float wallJumpControl = 0.1f; // temps avant de pouvoir recontroler notre personnage

    private Environment environment;
    private Viewport viewport;

    public Player(float x, float y,float width, float height, int maxHealth, int damage, int jumpCountMax, Environment env, Viewport viewport) {
        super(x,y,width, height, maxHealth, damage);
        this.environment = env;
        this.viewport = viewport;
        this.jumpCountMax = jumpCountMax;
        this.setCollision(false); // ce n'est pas un "obstacle"
        this.setHitStunDuration(0f); // pas de stun pour le joueur
        this.setInvincibilityDuration(1.5f);
        this.setIsPlayer(true);
        this.setVisualHitDuration(0.3f);
        setJumpSoundName("jumpEffectSound");
    }

    public boolean getRunning() {
        return running;
    }
    public int getJumpCount() {
        return jumpCount;
    }

    @Override
    public void update(float deltaTime) {

        if (!getAlive()){
            setVelocityX(0);
            super.update(deltaTime); // update des animations provenant de Agent
            return;
        }
        if (isHit()) {
            super.update(deltaTime);
            return;
        }

        super.update(deltaTime);

        if(getGrounded()){
            jumpCount = 0;
        }
        if (wallJumpTimer > 0){
            wallJumpTimer -= deltaTime;
        }
    }

    public void stopMovingX(){
        if (wallJumpTimer <= 0){
            setVelocityX(0);
            running = false;
        }
    }

    public void moveLeft(boolean run){
        if  (wallJumpTimer <= 0){
            float speed = walkSpeed;
            if (run) {
                speed = runSpeed;
            }
            setVelocityX(-speed);
            setFacingLeft(true);
            running = run;
        }
    }

    public void moveRight(boolean run){
        if  (wallJumpTimer <= 0){
            float speed = walkSpeed;
            if (run) {
                speed = runSpeed;
            }
            setVelocityX(speed);
            setFacingLeft(false);
            running = run;
        }
    }

    public void jump() {
        jumping = false;
        if (getGrounded()) {
            setVelocityY(jumpSpeed);
            setGrounded(false);
            jumpCount = 1;
            jumping = true;
            playsound();
        } else if (getTouchingWall() && !getGrounded() && jumpCount < jumpCountMax) {
            setVelocityY(wallJumpYSpeed);
            if (getWallOnLeft()) {
                setVelocityX(wallJumpXSpeed);
            } else {
                setVelocityX(-wallJumpXSpeed);
            }
            wallJumpTimer = wallJumpControl;
            setGrounded(false);
            jumpCount = jumpCount + 1;
            jumping = true;
            playsound();
        } else if (jumpCount < jumpCountMax) {
            setVelocityY(jumpSpeed);
            if (jumpCount == 0) {
                jumpCount = 2;
            } else {
                jumpCount = jumpCount + 1;
            }
            jumping = true;
            playsound();
        }
    }

    public void shoot(int screenX, int screenY) {
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

    private void playsound() {
        if (jumping && getJumpSoundName() != null && AudioManager.audioManager != null){
            AudioManager.audioManager.playSound(getJumpSoundName());
        }
    }

    @Override
    public int getMaxHealth() {
        return super.getMaxHealth();
    }
}

