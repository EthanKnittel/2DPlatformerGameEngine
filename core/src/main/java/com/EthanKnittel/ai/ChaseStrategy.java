package com.EthanKnittel.ai;

import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.math.Vector2;


public class ChaseStrategy implements EnemyStategy {
    private final Vector2 output = new Vector2();
    private float walltimer = 0f;
    private static final float maxWallTimer = 0.15f;
    private static final float deadZone = 0.5f;

    @Override
    public Vector2 calculateMove(float currentX, float currentY, boolean isGrounded, boolean isTouchingWall,boolean isTouchingAlly, Player player, float deltaTime, float entitySpeed, float entityJumpForce) {
        output.set(0,0);

        float distanceToPlayerX= player.getX() - currentX;
        float distanceToPlayerY= player.getY() - currentY;

        if (Math.abs(distanceToPlayerX) > deadZone) {
            if (distanceToPlayerX < 0){
                output.x = -entitySpeed;
            } else {
                output.x = entitySpeed;
            }
        } else {
            output.x = 0; // si le x est suffisament proche, on se stoppe pour éviter des flips constant du sprite et avoir une genre de "crise d'épilepsie" du monstre
        }

        boolean needToJump = false; // pour éviter des sauts inutiles (ou pour faire des sauts utiles)

        if (isTouchingWall && isGrounded && Math.abs(distanceToPlayerX)> 3.0f){
            walltimer += deltaTime;
            if (walltimer >= maxWallTimer){
                needToJump = true;
                walltimer = 0f;
            }
        } else {
            walltimer = 0f;
        }

        if (isGrounded && distanceToPlayerY > 1.5f && Math.abs(distanceToPlayerX)< 3.0f){
            needToJump = true;
        }

        if (needToJump) {
            output.y = entityJumpForce;
        }
        return output;
    }

    @Override
    public boolean enableSeparation() {
        return true;
    }
}
