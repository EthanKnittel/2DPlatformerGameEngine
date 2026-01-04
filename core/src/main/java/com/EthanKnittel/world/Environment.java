package com.EthanKnittel.world;

import com.EthanKnittel.Evolving;
import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.entities.artifacts.FireArrow;
import com.EthanKnittel.entities.artifacts.Wall;
import com.EthanKnittel.game.GameScreen;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Environment implements Evolving {

    private Level currentlevel;
    private Array<Entity> entities;

    public Environment() {
        entities = new Array<>();
    }

    public void setLevel(Level level) {
        if (this.currentlevel != null) {
            this.currentlevel.dispose();
            entities.clear();
        }
        this.currentlevel = level;
        Array<Entity> levelEntities = this.currentlevel.load();
        this.entities.addAll(levelEntities);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    @Override
    public void update(float deltaTime) {
        // On met les entités à jour

        for (int i = entities.size - 1; i >= 0; i--) {
            if (entities.get(i).getCanBeRemove()) {
                entities.removeIndex(i);
            }
        }
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);
            entity.update(deltaTime);
        }

        // Gestion de la physique / collisions
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);

            // Gravité et gestion des états des mobs Agents
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

            // Calcul du déplacement
            float potentialDeltaX = entity.getVelocity().x * deltaTime;
            float potentialDeltaY = entity.getVelocity().y * deltaTime;

            // gestion de séparation des monstres (pour éviter la superposition)
            if (entity.getIsEnemy()) {
                Foe currentFoe = (Foe) entity;
                currentFoe.setTouchingAlly(false);

                for (int k = 0; k < entities.size; k++) {
                    if (i == k) continue;
                    Entity other = entities.get(k);

                    if (other.getIsEnemy()) {

                        // On calcul la séparation des mobs avec un "coeur" (pour qu'il ne soit pas parfaitement superposés)
                        float diffX = entity.getX() - other.getX();
                        float dist = Math.abs(diffX);
                        float threshold = 16f / GameScreen.getPixelsPerBlocks(); // Taille du coeur

                        // Si on est dans le "coeur" de l'autre -> superposé
                        if (dist < threshold) {
                            // On indique qu'il y a superposition -> les Strategy géreront les changements de directions si besoin (comme Patrol)
                            currentFoe.setTouchingAlly(true);

                            // Pour les stratégies de type Chase où l'on doit continuer sur une même direction, on effectue une séparation forcée
                            if (currentFoe.shouldUseRepulsion()) {

                                float pushStrength = 10f / GameScreen.getPixelsPerBlocks();
                                float pushAmount = pushStrength * deltaTime;

                                if (dist < 0.01f) {
                                    potentialDeltaX += (i > k ? pushAmount : -pushAmount);
                                } else {
                                    potentialDeltaX += (diffX > 0 ? pushAmount : -pushAmount);
                                }
                            }
                        }
                    }
                }
            }

            if (entity.getClass().equals(FireArrow.class)) {
                FireArrow projectile = (FireArrow) entity;

                for (int j = 0; j < entities.size; j++) {
                    if (i == j) continue;
                    Entity other = entities.get(j);

                    if (projectile.getbounds().overlaps(other.getbounds())) {

                        // Si touche un wall -> destruction
                        if (other.getClass().equals(Wall.class)) {
                            projectile.setCanBeRemove(true);
                            break;
                        }

                        // Si touche un mob -> dégâts + destruction
                        if (other.getIsEnemy()) {
                            ((Foe) other).takeDamage(projectile.getDamage());
                            projectile.setCanBeRemove(true);
                            break;
                        }
                    }
                }
                continue; // le projectile n'est pas affecté par le reste des mises à jour de collisions
            }

            // Si après tout ça, l'entité ne bouge toujours pas, on passe (pour accélérer et opti un peu le programme)
            if (potentialDeltaX == 0 && potentialDeltaY == 0) {
                continue;
            }

            // Collisions avec le monde et les autres entités
            if (!entity.getCollision()) { // Si ce n'est pas un mur (donc c'est un mob, un npc ou un joueur)

                for (int j = 0; j < entities.size; j++) {
                    if (i == j) continue;

                    Entity other = entities.get(j);
                    Rectangle entityBounds = entity.getbounds();
                    Rectangle otherBounds = other.getbounds();

                    // Dégâts (si il y a simplement superposition)
                    if (entityBounds.overlaps(otherBounds)) {
                        if (entity.getIsEnemy() && other.getIsPlayer()) {
                            ((Player) other).takeDamage(((Foe) entity).getDamage());
                        } else if (entity.getIsPlayer() && other.getIsEnemy()) {
                            ((Player) entity).takeDamage(((Foe) other).getDamage());
                        }
                    }

                    // Physique des wall
                    if (!other.getCollision()) {
                        continue; // On ne teste la physique que contre les murs (petite optimisation)
                    }

                    // Test de l'axe X
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
                        potentialDeltaX = 0; // Le mur annule tout mouvement
                    }

                    // Mise à jour temporaire pour tester l'axe des Y correctement
                    entityBounds.x = entity.getX();

                    // Test Axe Y
                    Rectangle futureBoundsY = new Rectangle(entityBounds.x, entityBounds.y + potentialDeltaY, entityBounds.width, entityBounds.height);

                    if (potentialDeltaY != 0 && futureBoundsY.overlaps(otherBounds)) {
                        if (potentialDeltaY < 0) {
                            entity.setPosXY(entityBounds.x, otherBounds.y + otherBounds.height);
                            if (entity instanceof Agent) {
                                ((Agent) entity).setGrounded(true);
                            }
                        } else if (potentialDeltaY > 0) {
                            entity.setPosXY(entityBounds.x, otherBounds.y - entityBounds.height);
                        }
                        entity.getVelocity().y = 0;
                        potentialDeltaY = 0;
                    }
                }
            }

            // Application finale du mouvement
            if (potentialDeltaX != 0 || potentialDeltaY != 0) {
                entity.setPosXY(entity.getX() + potentialDeltaX, entity.getY() + potentialDeltaY);
            }
        }
    }

    public Array<Entity> getEntities() {
        return entities;
    }

    public Level getLevel() {
        return currentlevel;
    }
}
