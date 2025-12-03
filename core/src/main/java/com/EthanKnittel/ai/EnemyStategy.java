package com.EthanKnittel.ai;

import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.math.Vector2;

public interface EnemyStategy {
    Vector2 calculateMove(float currentX, float currentY, boolean isGrounded, boolean isTouchingWall, Player player, float deltaTime, float entitySpeed, float entityJumpForce);
}
