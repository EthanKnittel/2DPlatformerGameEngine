package com.EthanKnittel.world;

import com.EthanKnittel.Evolving;
import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.game.GameScreen;
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
        // 1. Mise à jour de la logique (IA, animations...)

        for (int i = entities.size - 1; i >= 0; i--) {
            if (entities.get(i).getCanBeRemove()) {
                entities.get(i).dispose();
                entities.removeIndex(i);
            }
        }
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);
            entity.update(deltaTime);
        }

        // 2. Physique et Collisions
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);

            // --- A. Gravité et état Agent ---
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

            // Calcul du déplacement voulu (Vitesse normale)
            float potentialDeltaX = entity.getVelocity().x * deltaTime;
            float potentialDeltaY = entity.getVelocity().y * deltaTime;

            // --- B. Anti-Stacking (Séparation des Monstres) ---
            // On le fait AVANT de vérifier les murs pour que le déplacement total soit testé
            if (entity instanceof Foe) {
                Foe currentFoe = (Foe) entity; // Cast pour accéder aux méthodes Foe
                currentFoe.setTouchingAlly(false);

                for (int k = 0; k < entities.size; k++) {
                    if (i == k) continue;
                    Entity other = entities.get(k);

                    if (other instanceof Foe) {

                        // 1. Calculer la distance / le "coeur"
                        float diffX = entity.getX() - other.getX();
                        float dist = Math.abs(diffX);
                        float threshold = 16f / GameScreen.getPixelsPerBlocks(); // Taille du coeur

                        // Si on est dans le "coeur" de l'autre
                        if (dist < threshold) {

                            // ETAPE A : INFORMATION (Pour PatrolStrategy)
                            // On prévient le monstre qu'il touche quelqu'un.
                            // PatrolStrategy utilisera ça pour changer de direction au prochain frame.
                            currentFoe.setTouchingAlly(true);


                            // ETAPE B : RÉACTION PHYSIQUE (Pour ChaseStrategy)
                            // On ne pousse QUE si la stratégie le demande
                            if (currentFoe.shouldUseRepulsion()) {

                                float pushStrength = 150f / GameScreen.getPixelsPerBlocks();
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

            if (entity instanceof com.EthanKnittel.entities.artifacts.FireArrow) {
                com.EthanKnittel.entities.artifacts.FireArrow arrow = (com.EthanKnittel.entities.artifacts.FireArrow) entity;

                for (int j = 0; j < entities.size; j++) {
                    if (i == j) continue;
                    Entity other = entities.get(j);

                    if (arrow.getbounds().overlaps(other.getbounds())) {

                        // Si touche un Mur -> Destruction
                        if (other instanceof com.EthanKnittel.entities.artifacts.Wall) {
                            arrow.setCanBeRemove(true);
                            break;
                        }

                        // Si touche un Monstre -> Dégâts + Destruction
                        if (other instanceof Foe) {
                            ((Foe) other).takeDamage(arrow.getDamage());
                            arrow.setCanBeRemove(true);
                            break;
                        }
                    }
                }
                continue; // La flèche n'a pas besoin de la physique de collision standard (bloquante)
            }

            // Si après tout ça, l'entité ne bouge toujours pas, on passe (optimisation)
            if (potentialDeltaX == 0 && potentialDeltaY == 0) {
                continue;
            }

            // --- C. Collisions avec le monde et les autres ---
            if (!entity.getCollision()) { // Si ce n'est pas un mur (donc c'est un acteur)

                for (int j = 0; j < entities.size; j++) {
                    if (i == j) continue;

                    Entity other = entities.get(j);
                    Rectangle entityBounds = entity.getbounds();
                    Rectangle otherBounds = other.getbounds();

                    // 1. Dégâts (Overlap simple)
                    if (entityBounds.overlaps(otherBounds)) {
                        if (entity.getIsEnemy() && other.getIsPlayer()) {
                            ((Player) other).takeDamage(((Foe) entity).getDamage());
                        } else if (entity.getIsPlayer() && other.getIsEnemy()) {
                            ((Player) entity).takeDamage(((Foe) other).getDamage());
                        }
                    }

                    // 2. Physique Solide (Murs)
                    if (!other.getCollision()) {
                        continue; // On ne teste la physique que contre les murs
                    }

                    // Test Axe X (avec le potentialDeltaX qui contient la poussée !)
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
                        potentialDeltaX = 0; // Le mur annule tout mouvement (y compris la poussée)
                    }

                    // Mise à jour temp pour tester Y correctement
                    entityBounds.x = entity.getX(); // Important: on utilise la position réelle potentiellement corrigée

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

            // --- D. Application finale du mouvement ---
            if (potentialDeltaX != 0 || potentialDeltaY != 0) {
                entity.setPosXY(entity.getX() + potentialDeltaX, entity.getY() + potentialDeltaY);
            }
        }
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        if (currentlevel != null) {
            currentlevel.renderBackground(camera);
        }
        batch.begin();
        for (Entity entity : entities) {
            entity.render(batch);
        }
        batch.end();
        if (currentlevel != null) {
            currentlevel.renderAbove(camera);
        }
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

    public Array<Entity> getEntities() {
        return entities;
    }
}
