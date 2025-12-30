package com.EthanKnittel.ai;

import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class PatrolStrategy implements EnemyStategy {
    private float timer = 0f;
    private float currentDirection = 1; // 1: droite, -1: gauche, 0: stop
    private Vector2 output = new Vector2();
    private float changeMove = 2f; // Temps avant changement naturel

    @Override
    public Vector2 calculateMove(float currentX, float currentY, boolean isGrounded, boolean isTouchingWall, boolean isTouchingAlly, Player player, float deltaTime, float entitySpeed, float entityJumpForce) {
        output.set(0, 0);
        timer += deltaTime;

        // Condition de changement
        if (timer > changeMove || (isTouchingWall && currentDirection != 0) || (isTouchingAlly && timer > 0.2f)) {

            if (isTouchingAlly && timer != 0) {
                // On force un choix aléatoire pour essayer de briser la superposition en mode Patrol, sans mettre de collisions
                // le but est de les "séparer" de manière naturelle.
                int choice = MathUtils.random(2);
                if (choice == 0) currentDirection = -1;
                else if (choice == 1) currentDirection = 1;
                else currentDirection = 0;
                // on donne ici un timer aléatoire pour (espérer) briser la superposition
                timer = -MathUtils.random(0.5f, 1.5f);

            } else {
                int choice = MathUtils.random(2);
                if (choice == 0) currentDirection = -1;
                else if (choice == 1) currentDirection = 1;
                else currentDirection = 0;
                timer = 0f;
            }

            changeMove = MathUtils.random(2f, 5f);
        }

        output.x = (entitySpeed * 0.5f) * currentDirection;
        return output;
    }

    @Override
    public boolean enableSeparation() {
        return false;
    }
}
