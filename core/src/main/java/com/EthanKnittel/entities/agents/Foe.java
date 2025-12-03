package com.EthanKnittel.entities.agents;

import com.EthanKnittel.ai.EnemyStategy;
import com.EthanKnittel.ai.PatrolStrategy;
import com.EthanKnittel.entities.Agent;
import com.badlogic.gdx.math.Vector2;

public abstract class Foe extends Agent {
    private EnemyStategy strategy;
    private Player target;

    public Foe(float x, float y, float width, float height, int maxHealth, int damage, Player target){
        super(x, y, width, height, maxHealth, damage);
        this.target = target;
        this.setIsEnemy(true);
        this.setStrategy(new PatrolStrategy()); // strategy par défaut
    }

    public void setStrategy(EnemyStategy strategy){
        this.strategy = strategy;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (strategy !=null && target != null && this.isAlive()){
            Vector2 command = strategy.calculateMove(getX(),getY(),getGrounded(), getTouchingWall(),target,deltaTime, getMoveSpeed(), getJumpSpeed());
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
}
