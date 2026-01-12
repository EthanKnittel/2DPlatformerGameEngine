package com.EthanKnittel.entities;

import com.EthanKnittel.game.GameScreen;

/**
 * Classe abstraite représentant une entité vivante (Agent).
 * <p>
 * Un Agent ajoute à l'Entité de base des caractéristiques biologiques et physiques avancées :
 * <ul>
 * <li><b>Santé :</b> Points de vie (HP), dégâts reçus, mort.</li>
 * <li><b>Combat :</b> Périodes d'invincibilité, étourdissement (Hit Stun), recul (Knockback).</li>
 * <li><b>Physique avancée :</b> Détection du sol (Grounded), glissade sur les murs (Wall Slide).</li>
 * </ul>
 * Cette classe sert de parent commun au {@link com.EthanKnittel.entities.agents.Player} et aux {@link com.EthanKnittel.entities.agents.Foe}.
 * </p>
 */
public abstract class Agent extends Entity {

    // --- STATISTIQUES DE VIE ---
    private int maxHealth;
    private int currenthealth;
    private int damage; // Dégâts infligés au contact (pour les ennemis)

    // --- ÉTATS PHYSIQUES ---
    /** Vrai si l'agent a les pieds sur un sol solide. */
    private boolean isGrounded = false;

    /** Vrai si l'agent est collé contre un mur (gauche ou droite). */
    private boolean isTouchingWall = false;

    /** Indique le côté du mur touché (utile pour savoir dans quel sens faire le Wall Jump). */
    private boolean isWallOnLeft = false;

    /** Vitesse de glissade le long d'un mur (frottement). */
    private static float wallSlideSpeed = -200f/ GameScreen.getPixelsPerBlocks();

    // --- DÉPLACEMENT ---
    private float moveSpeed = 150f/ GameScreen.getPixelsPerBlocks();
    private float jumpSpeed = 400f/ GameScreen.getPixelsPerBlocks();
    private boolean facingLeft = false; // Pour orienter le sprite

    // --- SYSTÈME DE DÉGÂTS (Combat) ---
    /** Temps restant pendant lequel l'agent ne peut pas prendre de nouveaux dégâts. */
    private float invincibilityTimer = 0f;
    private float invincibilityDuration = 1.0f;

    /** Vrai si l'agent vient de se faire taper et est "sonné". */
    private boolean isHit = false;

    /** Durée pendant laquelle l'agent est figé après un coup (Hit Stun). */
    private float hitStunDuration = 0.4f;
    private float hitTimer = 0f;

    /** Timer pour l'effet visuel lors d'un impact. */
    private float visualHitTimer = 0f;
    private float visualHitDuration = 0.2f;

    // --- AUDIO ---
    private String jumpSoundName = null;


    /**
     * Constructeur d'Agent.
     * Active par défaut la gravité et le flag `isAgent`.
     */
    public Agent(float x, float y, float width, float height, int maxHealth, int damage) {
        super(x, y,  width, height);
        this.maxHealth = maxHealth;
        this.currenthealth = maxHealth;
        this.damage = damage;

        // Un agent est toujours soumis à la gravité par défaut
        this.setAffectedByGravity(true);
        this.setIsAgent(true);
    }


    /**
     * Applique des dégâts à l'agent.
     * Gère automatiquement l'invincibilité, le stun et le recul (Knockback).
     *
     * @param amount Quantité de points de vie à retirer.
     */
    public void takeDamage(int amount) {
        if (invincibilityTimer > 0) {
            return; // Si invincible, on ignore l'attaque
        }

        this.currenthealth -= amount;
        if (this.currenthealth < 0) {
            this.currenthealth = 0;
        }

        // Activation du "Hit Stun" (l'agent est figé de douleur)
        if (hitStunDuration > 0){
            this.isHit=true;
            this.hitTimer = hitStunDuration;
        }

        // Activation des effets visuels et de l'invincibilité temporaire
        this.visualHitTimer = visualHitDuration;
        this.invincibilityTimer = invincibilityDuration;

        // Effet de Knockback (Recul) : On éjecte légèrement l'agent vers le haut et la droite
        this.setVelocityY(100f / GameScreen.getPixelsPerBlocks());
        this.setVelocityX(100f / GameScreen.getPixelsPerBlocks());
    }

    @Override
    public void update(float deltaTime) {
        // Gestion du compteur d'invincibilité
        if (invincibilityTimer > 0) {
            invincibilityTimer -= deltaTime;
        }

        // Gestion du Hit Stun (État "Sonné")
        if (isHit) {
            hitTimer -= deltaTime;
            if (hitTimer <= 0) {
                isHit = false; // L'agent reprend ses esprits
            }
        }

        // Gestion de l'effet visuel de dégâts
        if (visualHitTimer > 0) {
            visualHitTimer -= deltaTime;
        }
    }

    // --- GETTERS & SETTERS ---
    public void setJumpSoundName(String jumpSoundName) {
        this.jumpSoundName = jumpSoundName;
    }
    public String getJumpSoundName() {
        return jumpSoundName;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setJumpSpeed(float jumpSpeed) {
        this.jumpSpeed = jumpSpeed;
    }
    public float getJumpSpeed() {
        return jumpSpeed;
    }

    public void setFacingLeft(boolean facingLeft) {
        this.facingLeft = facingLeft;
    }
    public boolean getFacingLeft() {
        return facingLeft;
    }

    public int getCurrenthealth() {
        return this.currenthealth;
    }
    public void setCurrenthealth(int currenthealth) {this.currenthealth = currenthealth;}
    public int  getDamage() {
        return this.damage;
    }
    public int getMaxHealth() {
        return this.maxHealth;
    }
    public void setMaxHealth(int maxHealth) {this.maxHealth = maxHealth;}

    /** @return true si l'agent a encore des PV > 0. */
    public boolean getAlive() {
        return this.currenthealth > 0;
    }
    public void setGrounded(boolean grounded) {
        isGrounded = grounded;
    }
    public boolean getGrounded() {
        return isGrounded;
    }


    /**
     * Défini l'état de contact avec un mur.
     * @param touching Vrai si contact.
     * @param isWallOnLeft Vrai si le mur est à gauche, Faux s'il est à droite.
     */
    public void setIsTouchingWall(boolean touching, boolean isWallOnLeft) {
        this.isTouchingWall = touching;
        if (touching){ // on met à jour le côté touché
            this.isWallOnLeft = isWallOnLeft;
        }
    }
    public boolean getTouchingWall() {
        return isTouchingWall;
    }
    public boolean getWallOnLeft() {
        return isWallOnLeft;
    }
    public float getWallSlideSpeed() {
        return wallSlideSpeed;
    }
    public void setWallSlideSpeed(float speed) {
        wallSlideSpeed = speed;
    }

    // --- GESTION DES TIMERS DE COMBAT ---

    public boolean isHit() {
        return isHit;
    }

    public boolean getVisualHitActive() {
        return visualHitTimer > 0;
    }
    public void setVisualHitDuration(float duration) {
        this.visualHitDuration = duration;
    }

    public void setHitStunDuration(float hitStunDuration) {
        this.hitStunDuration = hitStunDuration;
    }
    public void setInvincibilityDuration(float invincibilityDuration) {
        this.invincibilityDuration = invincibilityDuration;
    }
}
