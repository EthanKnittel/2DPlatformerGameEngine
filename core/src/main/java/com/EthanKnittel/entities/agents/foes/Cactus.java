package com.EthanKnittel.entities.agents.foes;

import com.EthanKnittel.ai.ChaseStrategy;
import com.EthanKnittel.ai.PatrolStrategy;
import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.game.GameScreen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Cactus extends Foe {

    private enum State {Patrol, Chase};
    private State currentState;

    // valeurs défini pour changer de stratégies / la garder
    private float detectionRadius = 10f;
    private float looseRadius = 15f;
    private float lostSightTimer = 0f;
    private float lostSightCooldown = 2.0f;

    public Cactus(float x, float y, Player target, Array<Entity> allentities) {
        super(x,y,32f/ GameScreen.getPixelsPerBlocks(), 32f/GameScreen.getPixelsPerBlocks(), 50, 25, target, allentities);
        this.currentState = State.Patrol;
        this.setStrategy(new PatrolStrategy());
        this.setHitStunDuration(0.4f);
        this.setInvincibilityDuration(0.1f);
        this.setVisualHitDuration(0.4f);
        setScoreValue(50);
        setEnemyName("Cactus");

        this.setMoveSpeed(150f/GameScreen.getPixelsPerBlocks());
        this.setJumpSpeed(400f/GameScreen.getPixelsPerBlocks());
    }

    @Override
    public void update(float deltaTime){
        if (isHit()){
            super.update(deltaTime);
            return;
        }
        updateAI(deltaTime);
        super.update(deltaTime);
    }

    private void updateAI(float deltaTime){
        Player player = this.getTarget();
        if (player == null || !player.getAlive()) {
            if (currentState != State.Patrol){
                currentState=State.Patrol;
                this.setStrategy(new PatrolStrategy());
            }
            return;
        };
        float distance = Vector2.dst(this.getX(), this.getY(), player.getX(), player.getY());
        boolean canSeePlayer = hasLineOfSight(player);

        if (currentState == State.Patrol) {
            if (distance < detectionRadius && canSeePlayer){
                currentState = State.Chase;
                this.setStrategy(new ChaseStrategy());
                lostSightTimer = 0f;
            }
        } else if (currentState == State.Chase) {
            if (distance > looseRadius || !canSeePlayer){
                lostSightTimer += deltaTime;
                if (lostSightTimer >= lostSightCooldown){
                    currentState = State.Patrol;
                    this.setStrategy(new PatrolStrategy());
                    lostSightTimer = 0f;
                }
            } else {
                lostSightTimer = 0f;
            }
        }
    }
}
