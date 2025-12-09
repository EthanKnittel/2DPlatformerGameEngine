package com.EthanKnittel.ai;

import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class PatrolStrategy implements EnemyStategy{
    private float timer=0f;
    private float currentDirection=1; // 1 pour droite, -1 pour gauche
    private Vector2 output = new Vector2();
    private float changeMove = 2f;

    @Override
    public Vector2 calculateMove(float currentX, float currentY, boolean isGrounded, boolean isTouchingWall, Player player, float deltaTime, float entitySpeed, float entityJumpForce) {
        output.set(0,0);
        timer+=deltaTime;
        if (timer > changeMove || (isTouchingWall && currentDirection !=0)) {
            int choice = MathUtils.random(2);
            if (choice == 0) {
                currentDirection=-1; // on va à gauche
            } else if (choice == 1) {
                currentDirection = 1; // à droite
            } else {
                currentDirection = 0; // on s'immobilise
            }
            timer = 0f;
            changeMove = MathUtils.random(1f,5f);
        }

        output.x= (entitySpeed * 0.5f) * currentDirection;

        return output;
    }
}
