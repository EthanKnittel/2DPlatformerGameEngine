package com.EthanKnittel.world;

import com.EthanKnittel.Evolving;
import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.entities.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Environment implements Disposable, Evolving {

    private Level currentlevel;
    private Array<Entity> entities;

    public Environment() {
        entities = new Array<>();
    }

    public void setLevel(Level level) {
        if (this.currentlevel != null) {
            this.currentlevel.dispose();
        }
        this.currentlevel = level;
        this.currentlevel.load(this);
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
            if (entity.GetAffectedByGravity()) { // on applique la gravité
                entity.GetVelocity().y += Entity.GetGravity() * deltaTime;
            }
            if (entity instanceof  Agent) {
                if (((Agent) entity).IsTouchingWall() && !((Agent) entity).getGrounded() && entity.GetVelocity().y < 0){
                    entity.SetVelocityY(Math.max(entity.GetVelocity().y, ((Agent) entity).GetWallSlideSpeed()));
                }
            }

            if (entity.GetVelocity().x == 0 && entity.GetVelocity().y == 0) {
                continue; // si l'entité ne bouge pas, on la skip
            }

            float potentialDeltaX = entity.GetVelocity().x * deltaTime;
            float potentialDeltaY = entity.GetVelocity().y * deltaTime;

            // On dit ne pas être au sol (corrigé plus tard si besoin)
            if (entity.GetAffectedByGravity() && entity instanceof Agent) {
                ((Agent) entity).setGrounded(false);
            }

            if (entity instanceof  Agent) {
                ((Agent) entity).SetIsTouchingWall(false,false);
            }

            // On vérifie les collisions des entités "obstacles" (pas le joueur)
            if (!entity.GetCollision()) {
                // on vérifie ses collisions avec toutes les autres entités
                for (int j = 0; j < entities.size; j++) {
                    // sauf elle-même
                    if (i == j) {
                        continue;
                    }

                    Entity other = entities.get(j);

                    // on ne vérifie que contre les entités "solides"
                    if (!other.GetCollision()) {
                        continue;
                    }

                    Rectangle entityBounds = entity.GetBounds();
                    Rectangle otherBounds = other.GetBounds();

                    Rectangle futureBoundsX = new Rectangle(entityBounds.x + potentialDeltaX, entityBounds.y, entityBounds.width, entityBounds.height);

                    if (potentialDeltaX != 0 && futureBoundsX.overlaps(otherBounds)) {
                        boolean wallIsOnLeft = false;
                        // Collision en cours sur X:
                        if (potentialDeltaX > 0) { // en allant vers la droite
                            entity.SetPosXY(otherBounds.x - entityBounds.width, entityBounds.y);
                            wallIsOnLeft = false;
                        }
                        else if (potentialDeltaX < 0) { // en allant vers la gauche
                            entity.SetPosXY(otherBounds.x + otherBounds.width, entityBounds.y);
                            wallIsOnLeft = true;
                        }
                        if (entity instanceof Agent) {
                            ((Agent) entity).SetIsTouchingWall(true, wallIsOnLeft);
                        }
                        entity.GetVelocity().x = 0; // on stoppe le mouvement à cause de la collision
                        potentialDeltaX = 0; // on annule le déplacement pour cette frame
                    }
                    // on met à jour la hitbox sur l'axe des X avant de tester celle sur Y
                    entityBounds.x = entity.GetX();

                    Rectangle futureBoundsY = new Rectangle(entityBounds.x, entityBounds.y + potentialDeltaY, entityBounds.width, entityBounds.height);

                    if (potentialDeltaY != 0 && futureBoundsY.overlaps(otherBounds)) {
                        // Collision sur l'axe des Y !
                        if (potentialDeltaY < 0) {
                            entity.SetPosXY(entityBounds.x, otherBounds.y + otherBounds.height);
                            if (entity instanceof Agent){
                                ((Agent) entity).setGrounded(true); // on reset le saut puisqu'on est au sol
                            }
                        }
                        else if (potentialDeltaY > 0) { // si on saute (ou se fait balancer vers le haut)
                            entity.SetPosXY(entityBounds.x, otherBounds.y - entityBounds.height);
                        }
                        entity.GetVelocity().y = 0; // on arrete le mouvement vertical
                        potentialDeltaY = 0; // on annule le déplacement pour cette frame
                    }
                }
            }

            if (potentialDeltaX != 0 || potentialDeltaY != 0) { // on applique le mouvement
                entity.SetPosXY(entity.GetX() + potentialDeltaX, entity.GetY() + potentialDeltaY);
            }
        }
    }

    public void  render(SpriteBatch batch) {
        for(Entity entity : entities){
            entity.render(batch);
        }
    }

    @Override
    public void dispose() {
        if (currentlevel != null) {
            currentlevel.dispose();
        }
        for(Entity entity : entities){
            entity.dispose();
        }
    }
}
