package com.EthanKnittel.world;

import com.EthanKnittel.Evolving;
import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.entities.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Environment implements Disposable, Evolving {

    private Level currentlevel;
    private Array<Entity> entities;

    public Environment() {
        entities = new Array<>();
    }

    public void setLevel(Level level) {
        if (this.currentlevel != null) {
            this.currentlevel.dispose();
            entities.clear(); // on retire les entités du niveau précédent
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
        // mise à jour des entités
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);
            if (entity.getAffectedByGravity()) { // on applique la gravité
                entity.getVelocity().y += Entity.getGravity() * deltaTime;
            }
            if (entity.getIsAgent()) {
                Agent agent = (Agent) entity;
                if (agent.getTouchingWall() && !agent.getGrounded() && agent.getVelocity().y < 0) {
                    agent.setVelocityY(Math.max(agent.getVelocity().y, agent.getWallSlideSpeed()));
                }
                // On dit ne pas être au sol (corrigé plus tard si besoin)
                if (agent.getAffectedByGravity()) {
                    agent.setGrounded(false);
                }
                agent.setIsTouchingWall(false, false);
            }

            if (entity.getVelocity().x == 0 && entity.getVelocity().y == 0) {
                continue; // si l'entité ne bouge pas, on la skip
            }

            float potentialDeltaX = entity.getVelocity().x * deltaTime;
            float potentialDeltaY = entity.getVelocity().y * deltaTime;


            // On vérifie les collisions des entités "obstacles" (pas le joueur)
            if (!entity.getCollision()) {
                // on vérifie ses collisions avec toutes les autres entités
                for (int j = 0; j < entities.size; j++) {
                    // sauf elle-même
                    if (i == j) {
                        continue;
                    }

                    Entity other = entities.get(j);

                    // on ne vérifie que contre les entités "solides"
                    if (!other.getCollision()) {
                        continue;
                    }

                    Rectangle entityBounds = entity.getbounds();
                    Rectangle otherBounds = other.getbounds();

                    Rectangle futureBoundsX = new Rectangle(entityBounds.x + potentialDeltaX, entityBounds.y, entityBounds.width, entityBounds.height);

                    if (potentialDeltaX != 0 && futureBoundsX.overlaps(otherBounds)) {
                        boolean wallIsOnLeft = false;
                        // Collision en cours sur X:
                        if (potentialDeltaX > 0) { // en allant vers la droite
                            entity.setPosXY(otherBounds.x - entityBounds.width, entityBounds.y);
                            wallIsOnLeft = false;
                        } else if (potentialDeltaX < 0) { // en allant vers la gauche
                            entity.setPosXY(otherBounds.x + otherBounds.width, entityBounds.y);
                            wallIsOnLeft = true;
                        }
                        if (entity instanceof Agent) {
                            ((Agent) entity).setIsTouchingWall(true, wallIsOnLeft);
                        }
                        entity.getVelocity().x = 0; // on stoppe le mouvement à cause de la collision
                        potentialDeltaX = 0; // on annule le déplacement pour cette frame
                    }
                    // on met à jour la hitbox sur l'axe des X avant de tester celle sur Y
                    entityBounds.x = entity.getX();

                    Rectangle futureBoundsY = new Rectangle(entityBounds.x, entityBounds.y + potentialDeltaY, entityBounds.width, entityBounds.height);

                    if (potentialDeltaY != 0 && futureBoundsY.overlaps(otherBounds)) {
                        // Collision sur l'axe des Y !
                        if (potentialDeltaY < 0) {
                            entity.setPosXY(entityBounds.x, otherBounds.y + otherBounds.height);
                            if (entity instanceof Agent) {
                                ((Agent) entity).setGrounded(true); // on reset le saut puisqu'on est au sol
                            }
                        } else if (potentialDeltaY > 0) { // si on saute (ou se fait balancer vers le haut)
                            entity.setPosXY(entityBounds.x, otherBounds.y - entityBounds.height);
                        }
                        entity.getVelocity().y = 0; // on arrete le mouvement vertical
                        potentialDeltaY = 0; // on annule le déplacement pour cette frame
                    }
                }
            }

            if (potentialDeltaX != 0 || potentialDeltaY != 0) { // on applique le mouvement
                entity.setPosXY(entity.getX() + potentialDeltaX, entity.getY() + potentialDeltaY);
            }
        }
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        currentlevel.renderBackground(camera);
        batch.begin();
        for (Entity entity : entities) {
            entity.render(batch);
        }
        batch.end();
        currentlevel.renderAbove(camera);
    }

    @Override
    public void dispose() {
        if (currentlevel != null) {
            currentlevel.dispose();
        }
        for (Entity entity : entities) {
            entity.dispose();
        }
    }
}
