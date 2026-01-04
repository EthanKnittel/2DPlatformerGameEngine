package com.EthanKnittel.world.systems;

import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.entities.artifacts.FireArrow;
import com.EthanKnittel.entities.artifacts.Projectile;
import com.EthanKnittel.entities.artifacts.Wall;
import com.EthanKnittel.game.GameScreen;
import com.EthanKnittel.world.Level;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class PhysicSystem {
    private Level level; // pas utilisé mais on sait jamais

    public PhysicSystem(Level level){
        this.level = level;
    }

    public void update(float deltaTime, Array<Entity> entities){
        // Gestion de la physique / collisions
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);

            applyGravityAndAgentLogic(entity, deltaTime);

            // déplacement potentiel
            float potentialDeltaX = entity.getVelocity().x * deltaTime;
            float potentialDeltaY = entity.getVelocity().y * deltaTime;

            // séparation des monstres (un léger push)
            if (entity.getIsEnemy()) {
                potentialDeltaX += calculateSeparation((Foe) entity, entities, i, deltaTime);
            }

            // Si c'est un projectile, on gère sa collision spécifique et on passe au suivant
            if (entity.getIsProjectile()) {
                handleProjectileCollision((Projectile) entity, entities, i, deltaTime);
                continue;
            }

            // Si l'entité ne bouge pas, on passe (optimisation)
            if (potentialDeltaX == 0 && potentialDeltaY == 0) {
                continue;
            }

            // 5. Collisions physiques et Déplacement final
            if (!entity.getCollision()) { // Si ce n'est pas un mur (donc c'est un mob ou joueur)
                // On applique les collisions sur X et Y séparément
                potentialDeltaX = checkCollisionsX(entity, entities, i, potentialDeltaX);
                potentialDeltaY = checkCollisionsY(entity, entities, i, potentialDeltaY);
            }

            // Application finale du mouvement
            if (potentialDeltaX != 0 || potentialDeltaY != 0) {
                entity.setPosXY(entity.getX() + potentialDeltaX, entity.getY() + potentialDeltaY);
            }
        }
    }

    private void applyGravityAndAgentLogic(Entity entity, float deltaTime) {
        if (entity.getAffectedByGravity()) {
            entity.getVelocity().y += Entity.getGravity() * deltaTime;
        }
        if (entity.getIsAgent()) {
            Agent agent = (Agent) entity;
            if (agent.getTouchingWall() && !agent.getGrounded() && agent.getVelocity().y < 0) {
                agent.setVelocityY(Math.max(agent.getVelocity().y, agent.getWallSlideSpeed()));
            }
            if (agent.getAffectedByGravity()) {
                agent.setGrounded(false);
            }
            agent.setIsTouchingWall(false, false);
        }
    }

    private float calculateSeparation(Foe currentFoe, Array<Entity> entities, int currentIndex, float deltaTime) {
        float pushAdjustmentX = 0f;
        currentFoe.setTouchingAlly(false);

        for (int k = 0; k < entities.size; k++) {
            if (currentIndex == k) continue;
            Entity other = entities.get(k);

            if (other.getIsEnemy()) {
                float diffX = currentFoe.getX() - other.getX();
                float dist = Math.abs(diffX);
                float threshold = 16f / GameScreen.getPixelsPerBlocks();

                if (dist < threshold) {
                    currentFoe.setTouchingAlly(true);
                    if (currentFoe.shouldUseRepulsion()) {
                        float pushStrength = 10f / GameScreen.getPixelsPerBlocks();
                        float pushAmount = pushStrength * deltaTime;

                        if (dist < 0.01f) {
                            pushAdjustmentX += (currentIndex > k ? pushAmount : -pushAmount);
                        } else {
                            pushAdjustmentX += (diffX > 0 ? pushAmount : -pushAmount);
                        }
                    }
                }
            }
        }
        return pushAdjustmentX;
    }

    private void handleProjectileCollision(Projectile projectile, Array<Entity> entities, int currentIndex, float deltaTime) {
        for (int j = 0; j < entities.size; j++) {
            if (currentIndex == j) continue;
            Entity other = entities.get(j);

            if (projectile.getbounds().overlaps(other.getbounds())) {
                if (other.getClass().equals(Wall.class)) {
                    projectile.setCanBeRemove(true);
                    break;
                }
                if (other.getIsEnemy()) {
                    ((Foe) other).takeDamage(projectile.getDamage());
                    projectile.setCanBeRemove(true);
                    break;
                }
            }
        }
    }

    private float checkCollisionsX(Entity entity, Array<Entity> entities, int selfIndex, float potentialDeltaX) {
        Rectangle entityBounds = entity.getbounds();

        for (int j = 0; j < entities.size; j++) {
            if (selfIndex == j) continue;
            Entity other = entities.get(j);
            Rectangle otherBounds = other.getbounds();

            // Gestion Dégâts (overlaps)
            handleDamageOverlap(entity, other);

            // Physique (Murs uniquement)
            if (!other.getCollision()) continue;

            Rectangle futureBoundsX = new Rectangle(entityBounds.x + potentialDeltaX, entityBounds.y, entityBounds.width, entityBounds.height);

            if (potentialDeltaX != 0 && futureBoundsX.overlaps(otherBounds)) {
                boolean wallIsOnLeft = false;
                if (potentialDeltaX > 0) {
                    entity.setPosXY(otherBounds.x - entityBounds.width, entityBounds.y);
                    wallIsOnLeft = false;
                } else if (potentialDeltaX < 0) {
                    entity.setPosXY(otherBounds.x + otherBounds.width, entityBounds.y);
                    wallIsOnLeft = true;
                }
                if (entity instanceof Agent) {
                    ((Agent) entity).setIsTouchingWall(true, wallIsOnLeft);
                }
                entity.getVelocity().x = 0;
                potentialDeltaX = 0; // Stop mouvement
            }
        }
        return potentialDeltaX;
    }

    private float checkCollisionsY(Entity entity, Array<Entity> entities, int selfIndex, float potentialDeltaY) {
        Rectangle entityBounds = entity.getbounds();
        float currentX = entity.getX();

        for (int j = 0; j < entities.size; j++) {
            if (selfIndex == j) continue;
            Entity other = entities.get(j);
            Rectangle otherBounds = other.getbounds();

            if (!other.getCollision()) {
                continue;
            }

            Rectangle futureBoundsY = new Rectangle(currentX, entityBounds.y + potentialDeltaY, entityBounds.width, entityBounds.height);

            if (potentialDeltaY != 0 && futureBoundsY.overlaps(otherBounds)) {
                if (potentialDeltaY < 0) {
                    // On touche le sol
                    entity.setPosXY(currentX, otherBounds.y + otherBounds.height);

                    if (entity.getIsAgent()) {
                        ((Agent) entity).setGrounded(true);
                    }
                    if (entity.getVelocity().y < 0) {
                        entity.setVelocityY(0);
                    }

                } else if (potentialDeltaY > 0) {
                    // On tape le plafond
                    entity.setPosXY(currentX, otherBounds.y - entityBounds.height);
                    entity.setVelocityY(0);
                }
                potentialDeltaY = 0;
            }
        }
        return potentialDeltaY;
    }

    private void handleDamageOverlap(Entity entity, Entity other) {
        if (entity.getbounds().overlaps(other.getbounds())) {
            if (entity.getIsEnemy() && other.getIsPlayer()) {
                ((Player) other).takeDamage(((Foe) entity).getDamage());
            } else if (entity.getIsPlayer() && other.getIsEnemy()) {
                ((Player) entity).takeDamage(((Foe) other).getDamage());
            }
        }
    }
}
