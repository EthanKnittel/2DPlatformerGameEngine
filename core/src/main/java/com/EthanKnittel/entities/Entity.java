package com.EthanKnittel.entities;

import com.EthanKnittel.Evolving;
import com.EthanKnittel.game.GameScreen;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Classe de base abstraite représentant tout objet existant dans le monde du jeu.
 * <p>
 * Une entité possède une position, une vitesse, et une hitbox (rectangle de collision).
 * Elle implémente {@link Evolving}, ce qui signifie qu'elle doit avoir une méthode {@code update()}
 * appelée à chaque frame.
 * </p>
 */
public abstract class Entity implements Evolving {
    /** Position actuelle (Coin inférieur gauche) dans le monde. */
    private Vector2 position;

    /** Vitesse actuelle (X, Y) en unités/seconde. */
    private Vector2 velocity= new Vector2();

    /** Rectangle de collision (Hitbox). Doit toujours être synchronisé avec la position. */
    private Rectangle bounds; // pour la hitbox

    // --- FLAGS (Drapeaux de configuration) ---

    /** Si true, cette entité bloque les mouvements (ex: un Mur). */
    private boolean collision = false;

    /** Si true, la gravité s'applique à cette entité à chaque frame. */
    private boolean affectedByGravity = false;

    /**
     * Gravité globale du monde.
     * Statique car elle s'applique de la même façon à toutes les entités..
     */
    private static float defaultGravity = -980f/ GameScreen.getPixelsPerBlocks();

    // --- TYPE IDENTIFIERS ---
    private boolean isAgent = false;      // Est-ce un être vivant (général) ?
    private boolean isEnemy = false;      // Est-ce un méchant ?
    private boolean isPlayer = false;     // Est-ce le joueur ?
    private boolean isProjectile = false; // Est-ce une flèche/balle ?

    /** Si true, l'entité sera supprimée de la liste au prochain cycle de nettoyage. */
    private boolean canBeRemove = false;


    /**
     * Constructeur de base.
     *
     * @param x      Position X initiale.
     * @param y      Position Y initiale.
     * @param width  Largeur de la hitbox.
     * @param height Hauteur de la hitbox.
     */
    public Entity(float x, float y,  float width, float height) {
        // On initialise le vecteur de position LibGDX
        this.position = new Vector2(x, y);
        // On crée la hitbox aux mêmes coordonnées
        this.bounds = new Rectangle(x, y, width, height);
    }

    /**
     * Méthode appelée à chaque frame pour mettre à jour la logique de l'entité.
     * (Déplacement, IA, Animation...).
     *
     * @param deltaTime Temps écoulé depuis la dernière frame (en secondes).
     */
    @Override
    public abstract void update(float deltaTime);

    // --- GETTERS & SETTERS (Accesseurs) ---

    public boolean getIsEnemy(){
        return isEnemy;
    }
    public void setIsEnemy(boolean isEnemy){
        this.isEnemy = isEnemy;
    }

    public boolean getIsAgent() {
        return isAgent;
    }
    public void setIsAgent(boolean isAgent) {
        this.isAgent = isAgent;
    }

    public void setIsPlayer(boolean isPlayer) {
        this.isPlayer = isPlayer;
    }
    public boolean getIsPlayer(){
        return isPlayer;
    }

    public boolean getIsProjectile() {return isProjectile;}
    public void setIsProjectile(boolean isProjectile) {this.isProjectile = isProjectile;}

    public float getX() {
        return  position.x;
    }
    public float getY() {
        return  position.y;
    }

    public Rectangle getbounds() {
        return bounds;
    }

    /**
     * Met à jour la position de l'entité ET de sa hitbox.
     * <p>
     * <b>Important :</b> Ne modifiez jamais {@code position.x} directement sans mettre à jour
     * {@code bounds.x}. Utilisez toujours cette méthode pour garantir la synchronisation.
     * </p>
     */
    public void setPosXY(float x, float y) {
        position.set(x, y);
        bounds.setPosition(x,y);
    }
    public Vector2 getPosition() {return position;}

    public Vector2 getVelocity() {
        return velocity;
    }
    public void setVelocityX(float x) {
        velocity.x = x;
    }
    public void setVelocityY(float y) {
        velocity.y = y;
    }
    public void setVelocity(float x, float y) {
        velocity.x = x;
        velocity.y = y;
    }

    public boolean getCollision() {
        return collision;
    }
    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public boolean getAffectedByGravity() {
        return affectedByGravity;
    }
    public void setAffectedByGravity(boolean affectedByGravity) {
        this.affectedByGravity = affectedByGravity;
    }

    // Gestion de la gravité statique
    public void setGravity(float gravity) {
        defaultGravity = gravity;
    }
    public static float getGravity() {
        return defaultGravity;
    }

    // Gestion du cycle de vie (pour libérer de la mémoire)
    public void setCanBeRemove(boolean canBeRemove) {
        this.canBeRemove = canBeRemove;
    }
    public boolean getCanBeRemove() {
        return this.canBeRemove;
    }


}
