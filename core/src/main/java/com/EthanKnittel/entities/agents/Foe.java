package com.EthanKnittel.entities.agents;

import com.EthanKnittel.ai.EnemyStategy;
import com.EthanKnittel.ai.PatrolStrategy;
import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.save.SaveManager;
import com.EthanKnittel.score.ScoreManager;
import com.badlogic.gdx.math.Vector2;

public abstract class Foe extends Agent {
    private EnemyStategy strategy;
    private Player target;
    private boolean touchingAlly = false;

    private String enemyName = "Unknown";

    private int scoreValue = 100; // valeur par défaut
    private boolean scoreAwarded = false; // pour éviter de donner plusieurs fois les scores

    public Foe(float x, float y, float width, float height, int maxHealth, int damage, Player target) {
        super(x, y, width, height, maxHealth, damage);
        this.target = target;
        this.setIsEnemy(true);
        this.setStrategy(new PatrolStrategy()); // strategy par défaut
    }

    public void setTouchingAlly(boolean isTouching) {
        this.touchingAlly = isTouching;
    }

    // 3. Le Getter (optionnel, si besoin ailleurs)
    public boolean isTouchingAlly() {
        return this.touchingAlly;
    }

    public void setStrategy(EnemyStategy strategy){
        this.strategy = strategy;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (!isAlive()) {
            setInvincibilityDuration(99f); // pour ne pas refaire l'animation de dégats
            // Si l'animation de dégâts est terminée, on supprime l'ennemi
            if (!isHit()) {
                this.setCanBeRemove(true);
            }
            // Tant qu'il est "touché" (l'animation HIT joue encore), on ne fait rien.
            if (!scoreAwarded) { // si on a pas encore augmenté le score, on l'augmente
                if (ScoreManager.instance != null){
                    ScoreManager.instance.addScore(scoreValue);
                }

                if (SaveManager.instance != null) {
                    SaveManager.instance.addKillCount(enemyName);
                }
                scoreAwarded = true;
            }
            // Le return empêche l'IA de continuer à bouger le cadavre.
            return;
        }

        if (strategy !=null && target != null){
            Vector2 command = strategy.calculateMove(getX(),getY(),getGrounded(), getTouchingWall(),this.touchingAlly,target,deltaTime, getMoveSpeed(), getJumpSpeed());
            this.setVelocityX(command.x);
            if (command.y !=0){
                this.setVelocityY(command.y);
            }
        }
        if (getVelocity().x < 0){
            setFacingLeft(true);
        } else if (getVelocity().x > 0){
            setFacingLeft(false);
        }
    }

    public Player getTarget() {
        return target;
    }
    public boolean shouldUseRepulsion() {
        if (this.strategy != null) {
            return this.strategy.enableSeparation();
        }
        return true;
    }

    public int getScoreValue() {
        return scoreValue;
    }
    public void setScoreValue(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public void setEnemyName(String enemyName) {
        this.enemyName = enemyName;
    }
}
