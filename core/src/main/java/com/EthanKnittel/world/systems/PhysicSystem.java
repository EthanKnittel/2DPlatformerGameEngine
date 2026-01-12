package com.EthanKnittel.world.systems;

import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.entities.artifacts.Projectile;
import com.EthanKnittel.entities.artifacts.Wall;
import com.EthanKnittel.game.GameScreen;
import com.EthanKnittel.world.Level;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Système gérant la physique et les collisions du jeu.
 * <p>
 * Ce système est appelé à chaque frame pour mettre à jour la position de toutes les entités
 * en fonction de leur vitesse, de la gravité et des obstacles environnants.
 * </p>
 * <p>
 * Il utilise une détection de collision de type <b>AABB</b> (Axis-Aligned Bounding Box),
 * c'est-à-dire des rectangles qui ne pivotent pas. C'est la méthode standard pour les jeux de plateforme 2D.
 * </p>
 */
public class PhysicSystem {
    private Level level; // Référence au niveau (peu utilisée ici, mais peut servir pour des requêtes globales)

    /**
     * Initialise le système physique.
     * @param level Le niveau actuel.
     */
    public PhysicSystem(Level level){
        this.level = level;
    }

    /**
     * Boucle principale de la physique.
     * <p>
     * Pour chaque entité, on calcule son déplacement potentiel, on vérifie s'il est valide
     * (pas de mur), puis on applique le déplacement final.
     * </p>
     *
     * @param deltaTime Temps écoulé depuis la dernière frame.
     * @param entities  Liste de toutes les entités actives du monde.
     */
    public void update(float deltaTime, Array<Entity> entities){
        // On parcourt toutes les entités une par une
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);

            // 1. Application des forces verticales (Gravité, Glissade sur mur)
            applyGravityAndAgentLogic(entity, deltaTime);

            // 2. Calcul du déplacement théorique (Vitesse * Temps)
            float potentialDeltaX = entity.getVelocity().x * deltaTime;
            float potentialDeltaY = entity.getVelocity().y * deltaTime;

            // 3. Gestion de la séparation des ennemis (pour éviter qu'ils ne s'empilent)
            if (entity.getIsEnemy()) {
                potentialDeltaX += calculateSeparation((Foe) entity, entities, i, deltaTime);
            }

            // 4. Cas spécial : Projectiles (Collisions "Trigger")
            // Les projectiles traversent l'air mais s'arrêtent au premier impact.
            if (entity.getIsProjectile()) {
                handleProjectileCollision((Projectile) entity, entities, i, deltaTime);
                continue; // Le projectile gère son propre mouvement dans sa classe, on passe au suivant.
            }

            // Optimisation : Si l'entité ne bouge pas, inutile de calculer les collisions
            if (potentialDeltaX == 0 && potentialDeltaY == 0) {
                continue;
            }

            // 5. Résolution des collisions physiques (Solides)
            if (!entity.getCollision()) { // Si ce n'est pas un mur (donc c'est un mob ou joueur)
                // Si ce n'est pas un mur (donc c'est un acteur mobile)
                // Important : On traite X et Y séparément pour permettre de glisser contre un mur
                // tout en tombant (plutôt que d'être bloqué net en diagonale).
                potentialDeltaX = checkCollisionsX(entity, entities, i, potentialDeltaX);
                potentialDeltaY = checkCollisionsY(entity, entities, i, potentialDeltaY);
            }

            // 6. Application finale de la nouvelle position validée
            if (potentialDeltaX != 0 || potentialDeltaY != 0) {
                entity.setPosXY(entity.getX() + potentialDeltaX, entity.getY() + potentialDeltaY);
            }
        }
    }

    /**
     * Applique la gravité et met à jour les états physiques des Agents (Sol, Mur).
     */
    private void applyGravityAndAgentLogic(Entity entity, float deltaTime) {
        // Application de la gravité simple
        if (entity.getAffectedByGravity()) {
            entity.getVelocity().y += Entity.getGravity() * deltaTime;
        }

        // Logique spécifique aux êtres vivants (Player, Foe)
        if (entity.getIsAgent()) {
            Agent agent = (Agent) entity;

            // Friction murale (Wall Slide) : Si on tombe en frottant un mur, on tombe moins vite
            if (agent.getTouchingWall() && !agent.getGrounded() && agent.getVelocity().y < 0) {
                // On limite la vitesse de chute à 'wallSlideSpeed' (ex: -200 au lieu de -980)
                agent.setVelocityY(Math.max(agent.getVelocity().y, agent.getWallSlideSpeed()));
            }
            // Réinitialisation des états avant les tests de collision de cette frame
            if (agent.getAffectedByGravity()) {
                agent.setGrounded(false); // On suppose qu'on est en l'air jusqu'à preuve du contraire (collision sol)
            }
            agent.setIsTouchingWall(false, false);
        }
    }

    /**
     * Calcule une force de répulsion pour éviter que les ennemis ne se chevauchent.
     * <p>
     * C'est une implémentation simplifiée du comportement "Separation" des Boids.
     * </p>
     *
     * @return Un ajustement de déplacement sur l'axe X.
     */
    private float calculateSeparation(Foe currentFoe, Array<Entity> entities, int currentIndex, float deltaTime) {
        float pushAdjustmentX = 0f;
        currentFoe.setTouchingAlly(false); // Reset du flag

        for (int k = 0; k < entities.size; k++) {
            if (currentIndex == k) {
                continue; // On ne se teste pas soi-même
            }
            Entity other = entities.get(k);

            // On ne se sépare que des autres ennemis
            if (other.getIsEnemy()) {
                float diffX = currentFoe.getX() - other.getX();
                float dist = Math.abs(diffX);
                float threshold = 16f / GameScreen.getPixelsPerBlocks(); // Distance min (1 bloc)

                // Si trop proche
                if (dist < threshold) {
                    currentFoe.setTouchingAlly(true);

                    // On demande à l'IA si elle accepte la répulsion (ex: Oui en Chasse, Non en Patrouille)
                    if (currentFoe.shouldUseRepulsion()) {
                        float pushStrength = 10f / GameScreen.getPixelsPerBlocks();
                        float pushAmount = pushStrength * deltaTime;

                        // Si superposition parfaite (dist ~ 0), on pousse arbitrairement selon l'index
                        if (dist < 0.01f) {
                            pushAdjustmentX += (currentIndex > k ? pushAmount : -pushAmount);
                        } else {
                            // Sinon on pousse dans la direction opposée à l'autre
                            pushAdjustmentX += (diffX > 0 ? pushAmount : -pushAmount);
                        }
                    }
                }
            }
        }
        return pushAdjustmentX;
    }

    /**
     * Gère les impacts de projectiles (Flèches, Balles).
     * Vérifie si le projectile touche un mur ou un ennemi.
     */
    private void handleProjectileCollision(Projectile projectile, Array<Entity> entities, int currentIndex, float deltaTime) {
        for (int j = 0; j < entities.size; j++) {
            if (currentIndex == j) continue;
            Entity other = entities.get(j);

            // Test de superposition simple (AABB)
            if (projectile.getbounds().overlaps(other.getbounds())) {
                // Cas 1 : Mur -> Le projectile se plante et disparaît
                if (other.getClass().equals(Wall.class)) {
                    projectile.setCanBeRemove(true);
                    break;
                }
                // Cas 2 : Ennemi -> Dégâts + Disparition
                if (other.getIsEnemy()) {
                    ((Foe) other).takeDamage(projectile.getDamage());
                    projectile.setCanBeRemove(true);
                    break;
                }
                // Note : On ignore le joueur (Friendly Fire désactivé)
            }
        }
    }

    /**
     * Vérifie les collisions sur l'axe horizontal (X).
     * Gère aussi les dégâts au contact (corps à corps).
     *
     * @return Le déplacement X corrigé (0 si bloqué par un mur).
     */
    private float checkCollisionsX(Entity entity, Array<Entity> entities, int selfIndex, float potentialDeltaX) {
        Rectangle entityBounds = entity.getbounds();

        for (int j = 0; j < entities.size; j++) {
            if (selfIndex == j) {
                continue;
            }
            Entity other = entities.get(j);
            Rectangle otherBounds = other.getbounds();

            // Vérification : Est-ce qu'on se touche ? (Hitbox vs Hitbox)
            // Cette méthode gère les dégâts si "entity" et "other" sont Joueur/Ennemi
            handleDamageOverlap(entity, other);

            // Si l'autre objet n'est pas solide (ex: un bonus, ou un autre ennemi traversable), on continue
            if (!other.getCollision()) {
                continue;
            }

            // Prédiction : Où sera ma hitbox si j'applique le mouvement X ?
            Rectangle futureBoundsX = new Rectangle(entityBounds.x + potentialDeltaX, entityBounds.y, entityBounds.width, entityBounds.height);

            // Si collision prédite
            if (potentialDeltaX != 0 && futureBoundsX.overlaps(otherBounds)) {
                boolean wallIsOnLeft = false;

                // Correction de position : On se colle parfaitement contre le mur
                if (potentialDeltaX > 0) {
                    // On allait à droite -> On se colle au bord gauche du mur
                    entity.setPosXY(otherBounds.x - entityBounds.width, entityBounds.y);
                    wallIsOnLeft = false;
                } else if (potentialDeltaX < 0) {
                    // On allait à gauche -> On se colle au bord droit du mur
                    entity.setPosXY(otherBounds.x + otherBounds.width, entityBounds.y);
                    wallIsOnLeft = true;
                }

                // Notification à l'Agent (pour le Wall Jump)
                if (entity instanceof Agent) {
                    ((Agent) entity).setIsTouchingWall(true, wallIsOnLeft);
                }
                // On stoppe net
                entity.getVelocity().x = 0;
                potentialDeltaX = 0;
            }
        }
        return potentialDeltaX;
    }

    /**
     * Vérifie les collisions sur l'axe vertical (Y).
     * Gère le sol (Grounded) et le plafond.
     *
     * @return Le déplacement Y corrigé.
     */
    private float checkCollisionsY(Entity entity, Array<Entity> entities, int selfIndex, float potentialDeltaY) {
        Rectangle entityBounds = entity.getbounds();
        float currentX = entity.getX(); // On utilise le X actuel (déjà validé ou corrigé par checkCollisionsX)

        for (int j = 0; j < entities.size; j++) {
            if (selfIndex == j) continue;
            Entity other = entities.get(j);
            Rectangle otherBounds = other.getbounds();

            if (!other.getCollision()) {
                continue;
            }

            // Prédiction Y
            Rectangle futureBoundsY = new Rectangle(currentX, entityBounds.y + potentialDeltaY, entityBounds.width, entityBounds.height);

            if (potentialDeltaY != 0 && futureBoundsY.overlaps(otherBounds)) {
                if (potentialDeltaY < 0) {
                    // On descendait -> On touche le SOL
                    entity.setPosXY(currentX, otherBounds.y + otherBounds.height);

                    if (entity.getIsAgent()) {
                        ((Agent) entity).setGrounded(true); // Flag important pour le saut !
                    }
                    // On annule la vitesse de chute (sinon la gravité continue d'accumuler de la vitesse infinie)
                    if (entity.getVelocity().y < 0) {
                        entity.setVelocityY(0);
                    }

                } else if (potentialDeltaY > 0) {
                    // On montait -> On se cogne la tête (PLAFOND)
                    entity.setPosXY(currentX, otherBounds.y - entityBounds.height);
                    entity.setVelocityY(0); // Le saut est stoppé net
                }
                potentialDeltaY = 0; // Mouvement validé = 0
            }
        }
        return potentialDeltaY;
    }

    /**
     * Vérifie si deux entités se touchent et applique les dégâts si nécessaire.
     * (Joueur touche Ennemi ou Ennemi touche Joueur).
     */
    private void handleDamageOverlap(Entity entity, Entity other) {
        // overlaps() vérifie si les rectangles s'intersectent
        if (entity.getbounds().overlaps(other.getbounds())) {

            // Cas A : Ennemi fonce sur Joueur
            if (entity.getIsEnemy() && other.getIsPlayer()) {
                ((Player) other).takeDamage(((Foe) entity).getDamage());
            }
            // Cas B : Joueur fonce sur Ennemi (le joueur prend des dégâts aussi par contact)
            else if (entity.getIsPlayer() && other.getIsEnemy()) {
                ((Player) entity).takeDamage(((Foe) other).getDamage());
            }
        }
    }
}
