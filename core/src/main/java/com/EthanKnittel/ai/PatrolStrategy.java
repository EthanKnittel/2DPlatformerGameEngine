package com.EthanKnittel.ai;

import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class PatrolStrategy implements EnemyStategy{
    private float timer=0f;
    private float currentDirection=1; // 1 pour droite, -1 pour gauche
    private Vector2 output = new Vector2();

    @Override
    public Vector2 calculateMove(float currentX, float currentY, boolean isGrounded, boolean isTouchingWall, Player player, float deltaTime, float entitySpeed, float entityJumpForce) {
        output.set(0,0);
        timer+=deltaTime;
        if (timer > 2f) {
            if (MathUtils.randomBoolean()) {
                currentDirection = 1;
            } else {
                currentDirection = -1;
            }
            timer=0f;
        }

        output.x= (entitySpeed * 0.7f) * currentDirection;

        return output;
    }
}
