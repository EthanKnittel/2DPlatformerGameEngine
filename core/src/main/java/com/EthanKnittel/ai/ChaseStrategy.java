package com.EthanKnittel.ai;

import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.math.Vector2;

public class ChaseStrategy implements EnemyStategy {
    private final Vector2 output = new Vector2();
    private float walltimer = 0f;
    private static final float MaxWallTimer = 0.15f;


    @Override
    public Vector2 calculateMove(float currentX, float currentY, boolean isGrounded, boolean isTouchingWall, Player player, float deltaTime, float entitySpeed, float entityJumpForce) {
        output.set(0,0);
        if (player.getX() < currentX) {
            output.x = -entitySpeed;
        } else {
            output.x = entitySpeed;
        }

        if (isTouchingWall && isGrounded){
            walltimer += deltaTime;
            if (walltimer >= MaxWallTimer){
                output.y = entityJumpForce;
                walltimer = 0f;
            }
        } else {
            walltimer = 0f;
        }
        return output;
    }

}
