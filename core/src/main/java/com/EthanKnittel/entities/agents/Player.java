package com.EthanKnittel.entities.agents;

import com.EthanKnittel.audio.AudioManager;
import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.game.GameScreen;
import com.EthanKnittel.world.systems.Environment;
import com.badlogic.gdx.math.Vector3;
import com.EthanKnittel.entities.artifacts.FireArrow;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * La classe représentant le joueur contrôlé par l'utilisateur.
 * <p>
 * Elle étend {@link Agent} pour bénéficier de la santé et de la physique, mais ajoute
 * des mécaniques spécifiques au gameplay de plateforme :
 * <ul>
 * <li><b>Double Saut :</b> Capacité de sauter à nouveau en l'air.</li>
 * <li><b>Wall Jump :</b> Rebondir contre les murs.</li>
 * <li><b>Combat à distance :</b> Tir de projectiles vers la souris.</li>
 * </ul>
 * </p>
 */
public class Player extends Agent {
    // --- ÉTATS DU JOUEUR ---
    private boolean running = false;
    private boolean jumping = false; // Utilisé pour déclencher le son

    // --- GESTION DES SAUTS ---
    /** Nombre de sauts effectués depuis le dernier contact avec le sol. */
    private int jumpCount = 0;

    /** Nombre maximum de sauts autorisés (ex: 2 pour le double saut). */
    private int jumpCountMax;

    // --- VITESSES (Constantes de Gameplay) ---
    // Note : On divise par PixelsPerBlocks pour convertir les pixels en "Unités Monde" (Mètres/Blocs)
    private final float walkSpeed = 200f/ GameScreen.getPixelsPerBlocks();
    private final float runSpeed = 300f/ GameScreen.getPixelsPerBlocks();
    private final float jumpSpeed = 400f/ GameScreen.getPixelsPerBlocks();

    // --- PARAMÈTRES DU WALL JUMP ---
    private final float wallJumpYSpeed = 500f/ GameScreen.getPixelsPerBlocks();
    private final float wallJumpXSpeed = 300f/ GameScreen.getPixelsPerBlocks();

    /** Timer bloquant les contrôles horizontaux après un Wall Jump. */
    private float wallJumpTimer = 0f;

    /** Durée (en secondes) de la perte de contrôle après un rebond mural.
     * Cela empêche le joueur de revenir immédiatement se coller au mur,
     * rendant l'escalade plus technique et fluide. */
    private final float wallJumpControl = 0.1f;

    // --- DÉPENDANCES ---
    /** Référence à l'environnement pour pouvoir y faire apparaître des flèches. */
    private Environment environment;
    /** Référence au viewport pour convertir les clics souris (pixels) en coordonnées monde. */
    private Viewport viewport;

    /**
     * Constructeur du Joueur.
     *
     * @param env      L'environnement (nécessaire pour spawn les projectiles).
     * @param viewport La caméra/vue (nécessaire pour viser).
     */
    public Player(float x, float y,float width, float height, int maxHealth, int damage, int jumpCountMax, Environment env, Viewport viewport) {
        super(x,y,width, height, maxHealth, damage);
        this.environment = env;
        this.viewport = viewport;
        this.jumpCountMax = jumpCountMax;

        // Configuration spécifique du joueur
        this.setCollision(false); // Le joueur ne bloque pas les autres entités physiquement (pas un mur)
        this.setIsPlayer(true);   // Tag pour l'IA des ennemis

        // Configuration Combat
        this.setHitStunDuration(0f); // Le joueur n'est jamais figé quand il prend un coup (meilleur Game Feel)
        this.setInvincibilityDuration(1.5f); // Invincibilité longue pour ne pas se prendre plusieurs coups sans pouvoir réagir
        this.setVisualHitDuration(0.3f);

        setJumpSoundName("jumpEffectSound");
    }

    // --- GETTERS ---

    public boolean getRunning() {
        return running;
    }
    public int getJumpCount() {
        return jumpCount;
    }

    @Override
    public void update(float deltaTime) {

        // 1. Si mort, on arrête tout mouvement horizontal et on laisse la gravité faire le reste
        if (!getAlive()){
            setVelocityX(0);
            super.update(deltaTime); // Joue l'animation de mort via Agent
            return;
        }
        // 2. Si touché (mais pas stun car duration = 0), on update quand même
        if (isHit()) {
            super.update(deltaTime);
            return;
        }

        // 3. Mise à jour standard (Physique, Gravité, Timers d'invincibilité)
        super.update(deltaTime);

        // 4. Reset du saut si on touche le sol
        if(getGrounded()){
            jumpCount = 0;
        }

        // 5. Gestion du cooldown de contrôle Wall Jump
        if (wallJumpTimer > 0){
            wallJumpTimer -= deltaTime;
        }
    }

    /**
     * Arrête le mouvement horizontal.
     * <p>
     * <b>Condition :</b> Cette action est ignorée si le joueur est en plein milieu d'un rebond mural
     * (wallJumpTimer > 0), car on ne peut pas s'arrêter en l'air pendant la propulsion.
     * </p>
     */
    public void stopMovingX(){
        if (wallJumpTimer <= 0){
            setVelocityX(0);
            running = false;
        }
    }

    /**
     * Déplace le joueur vers la gauche.
     * Bloqué si en cours de Wall Jump.
     */
    public void moveLeft(boolean run){
        if  (wallJumpTimer <= 0){
            float speed = walkSpeed;
            if (run) {
                speed = runSpeed;
            }
            setVelocityX(-speed);
            setFacingLeft(true);
            running = run;
        }
    }

    /**
     * Déplace le joueur vers la droite.
     * Bloqué si en cours de Wall Jump.
     */
    public void moveRight(boolean run){
        if  (wallJumpTimer <= 0){
            float speed = walkSpeed;
            if (run) {
                speed = runSpeed;
            }
            setVelocityX(speed);
            setFacingLeft(false);
            running = run;
        }
    }

    /**
     * Logique centrale du saut. Gère 3 cas différents :
     * <ol>
     * <li><b>Saut normal :</b> Depuis le sol.</li>
     * <li><b>Wall Jump :</b> Depuis un mur (si pas au sol).</li>
     * <li><b>Air Jump :</b> En l'air (Double saut).</li>
     * </ol>
     */
    public void jump() {
        jumping = false;

        // CAS 1 : Saut depuis le sol
        if (getGrounded()) {
            setVelocityY(jumpSpeed);
            setGrounded(false);
            jumpCount = 1; // 1er saut consommé
            jumping = true;
            playsound();

            // CAS 2 : Wall Jump (Prioritaire sur le double saut si contre un mur)
        } else if (getTouchingWall() && !getGrounded() && jumpCount < jumpCountMax) {
            setVelocityY(wallJumpYSpeed);

            // On propulse le joueur dans la direction OPPOSÉE au mur
            if (getWallOnLeft()) {
                setVelocityX(wallJumpXSpeed); // Mur à gauche -> Vers la droite
            } else {
                setVelocityX(-wallJumpXSpeed); // Mur à droite -> Vers la gauche
            }
            // On bloque les contrôles temporairement pour valider la propulsion
            wallJumpTimer = wallJumpControl;

            setGrounded(false);
            jumpCount = jumpCount + 1; // Consomme un saut
            jumping = true;
            playsound();

            // CAS 3 : Double Saut (En l'air, sans mur)
        } else if (jumpCount < jumpCountMax) {
            setVelocityY(jumpSpeed);

            // Si on tombe d'une plateforme sans sauter, jumpCount est à 0.
            // Donc le premier saut est en l'air et doit donc être compté comme le 2ème saut (double saut de sauvetage).
            if (jumpCount == 0) {
                jumpCount = 2;
            } else {
                jumpCount = jumpCount + 1;
            }
            jumping = true;
            playsound();
        }
    }

    /**
     * Tire une flèche en direction de la souris.
     *
     * @param screenX Position X de la souris sur l'écran (pixels).
     * @param screenY Position Y de la souris sur l'écran (pixels).
     */
    public void shoot(int screenX, int screenY) {
        // 1. Conversion des coordonnées : Écran (2D plat) -> Monde (Caméra, Zoom, Position)
        // LibGDX utilise Vector3 pour unproject, le Z est ignoré en 2D.
        Vector3 worldPos = new Vector3(screenX, screenY, 0);
        viewport.unproject(worldPos);

        // 2. Calcul du point de départ (Centre du joueur)
        float startX = getX() + getbounds().width / 2f;
        float startY = getY() + getbounds().height / 2f;

        // 3. Création et ajout du projectile dans le monde
        FireArrow arrow = new FireArrow(startX, startY, worldPos.x, worldPos.y);
        environment.addEntity(arrow);
    }

    private void playsound() {
        if (jumping && getJumpSoundName() != null && AudioManager.getInstance() != null){
            AudioManager.getInstance().playSound(getJumpSoundName());
        }
    }

    @Override
    public int getMaxHealth() {
        return super.getMaxHealth();
    }
}

