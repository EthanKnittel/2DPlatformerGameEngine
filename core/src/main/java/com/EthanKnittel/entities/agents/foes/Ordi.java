package com.EthanKnittel.entities.agents.foes;

import com.EthanKnittel.ai.ChaseStrategy;
import com.EthanKnittel.ai.PatrolStrategy;
import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.entities.artifacts.Wall;
import com.EthanKnittel.game.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Ordi extends Foe {
    private TextureAtlas atlas;
    private Animation<TextureRegion> idleAnim, runAnim, hitAnim, fallAnim, jumpAnim;
    private enum State {Patrol, Chase};
    private Ordi.State currentState;

    // valeurs défini pour changer de stratégies / la garder
    private float detectionRadius = 10f;
    private float looseRadius = 15f;
    private float lostSightTimer = 0f;
    private float lostSightCooldown = 2.0f;
    private Array<Entity> allentities;

    public Ordi(float x, float y, Player target, Array<Entity> allentities) {
        super(x,y,32f/ GameScreen.getPixelsPerBlocks(), 32f/GameScreen.getPixelsPerBlocks(), 50, 1, target);
        this.allentities = allentities;
        this.currentState = Ordi.State.Patrol;
        this.setStrategy(new PatrolStrategy());
        this.setHitStunDuration(0.4f);
        this.setInvincibilityDuration(0.1f);
        this.setVisualHitDuration(0.4f);

        loadAnimations();

        this.setMoveSpeed(150f/GameScreen.getPixelsPerBlocks());
        this.setJumpSpeed(400f/GameScreen.getPixelsPerBlocks());
    }

    private void loadAnimations(){
        try{
            atlas = new TextureAtlas(Gdx.files.internal("Ennemies/ordi/Ordi.atlas"));

            idleAnim = new Animation<>(0.1f, atlas.findRegions("IDLE"), Animation.PlayMode.LOOP);
            runAnim = new Animation<>(0.1f, atlas.findRegions("RUNNING"), Animation.PlayMode.LOOP);
            hitAnim = new Animation<>(0.1f, atlas.findRegions("HIT"), Animation.PlayMode.LOOP);
            fallAnim = new Animation<>(0.1f, atlas.findRegions("FALLING"), Animation.PlayMode.LOOP);
            jumpAnim = new Animation<>(0.1f, atlas.findRegions("JUMPING"), Animation.PlayMode.LOOP);

            setAnimation(idleAnim);
        } catch(Exception e){
            Gdx.app.error("Ordi", "Erreur de chargement de l'atlas", e);
        }
    }

    @Override
    public void update(float deltaTime){
        if (isHit()){
            setAnimation(hitAnim);
            super.update(deltaTime);
            return;
        }
        updateAI(deltaTime);
        super.update(deltaTime);
        if (getVisualHitActive()) {
            setAnimation(hitAnim);
        }else if (!getGrounded() && getVelocity().y < 0) {
            setAnimation(fallAnim);
        } else  if (!getGrounded() && getVelocity().y > 0) {
            setAnimation(jumpAnim);
        } else if (getVelocity().x !=0){
            setAnimation(runAnim);
        } else {
            setAnimation(idleAnim);
        }
    }

    private void updateAI(float deltaTime){
        Player player = this.getTarget();
        if (!player.isAlive()) {
            if (currentState != Ordi.State.Patrol){
                currentState= Ordi.State.Patrol;
                this.setStrategy(new PatrolStrategy());
            }
            return;
        };
        float distance = Vector2.dst(this.getX(), this.getY(), player.getX(), player.getY());
        boolean canSeePlayer = hasLineOfSight(player);

        if (currentState == Ordi.State.Patrol) {
            if (distance < detectionRadius && canSeePlayer){
                currentState = Ordi.State.Chase;
                this.setStrategy(new ChaseStrategy());
                lostSightTimer = 0f;
            }
        } else if (currentState == Ordi.State.Chase) {
            if (distance > looseRadius || !canSeePlayer){
                lostSightTimer += deltaTime;
                if (lostSightTimer >= lostSightCooldown){
                    currentState = Ordi.State.Patrol;
                    this.setStrategy(new PatrolStrategy());
                    lostSightTimer = 0f;
                }
            } else {
                lostSightTimer = 0f;
            }
        }
    }

    public boolean hasLineOfSight(Player player){
        if (allentities == null){
            return true;
        }
        // Centre de Cactus
        Vector2 start = new Vector2(getX()+ getbounds().width / 2, getY() + getbounds().height / 2);
        // Centre du joueur
        Vector2 end = new Vector2(player.getX() + player.getbounds().width / 2, player.getY() + player.getbounds().height / 2);

        for (Entity entity : allentities){
            if (entity.getClass().equals(Wall.class)){
                if (Intersector.intersectSegmentRectangle(start, end, entity.getbounds())){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void dispose(){
        atlas.dispose();
    }
}

