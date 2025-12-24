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

            if (isTouchingAlly) {
                // --- CAS SPÉCIAL : COLLISION ENTRE MONSTRES ---

                // 1. On brise la symétrie : On force un choix aléatoire (Gauche ou Droite)
                // Même si on bougeait déjà, on re-tente sa chance.
                // Ça permet parfois à deux monstres de partir dans la MEME direction (l'un chasse l'autre),
                // mais comme ils ont des vitesses légèrement différentes ou des timers différents, ils finiront par se séparer.
                // Surtout, ça évite l'effet miroir parfait.
                currentDirection = MathUtils.randomBoolean() ? 1 : -1;

                // 2. LE SECRET EST ICI : Décalage temporel
                // On donne un temps d'attente (immunité) ALÉATOIRE.
                // L'un va reprendre sa logique dans 0.5s, l'autre dans 1.2s.
                // Ce décalage suffit à briser la synchronisation "main dans la main".
                timer = -MathUtils.random(0.5f, 1.5f);

            } else {
                // --- CAS NORMAL ---
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
