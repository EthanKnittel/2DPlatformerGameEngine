package com.EthanKnittel.graphics.entity;

import com.EthanKnittel.entities.agents.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Vue graphique spécifique pour le {@link Player} (Joueur).
 * <p>
 * Cette classe est responsable de l'affichage du joueur.
 * Elle gère une grande variété d'animations correspondant aux nombreuses capacités du joueur :
 * <ul>
 * <li>Mouvements de base : Idle, Marche, Course.</li>
 * <li>Aérien : Saut, Double Saut, Chute.</li>
 * <li>Interactions : Glissade sur mur (Wall Slide), Dégâts (Hit).</li>
 * </ul>
 * </p>
 * <p>
 * Elle fait le lien entre l'état logique complexe du joueur (ex: {@code jumpCount}, {@code isTouchingWall})
 * et le rendu visuel.
 * </p>
 */
public class PlayerView extends AnimatedEntityView {

    /** Référence au modèle logique du joueur pour lire ses états. */
    private Player player;

    // --- ANIMATIONS ---
    private Animation<TextureRegion> idleAnim;       // Attente
    private Animation<TextureRegion> walkAnim;       // Marche (vitesse normale)
    private Animation<TextureRegion> runAnim;        // Course (rapide + Shift)
    private Animation<TextureRegion> jumpAnim;       // Saut simple
    private Animation<TextureRegion> wallSlideAnim;  // Glissade contre un mur
    private Animation<TextureRegion> hitAnim;        // Dégâts / Mort
    private Animation<TextureRegion> fallAnim;       // Chute (vitesse Y < 0)
    private Animation<TextureRegion> doubleJumpAnim; // Double saut (Air Jump)

    /**
     * Constructeur.
     * Charge l'atlas du joueur ("Player1/Player1.atlas").
     *
     * @param player L'instance du joueur à afficher.
     */
    public PlayerView(Player player) {
        super(player, Gdx.files.internal("Player1/Player1.atlas").path());
        this.player = player;
    }

    /**
     * Charge et configure toutes les animations du joueur.
     * <p>
     * Notez les différences de vitesse (frame duration) :
     * La course (0.05s) est jouée deux fois plus vite que la marche (0.1s) pour donner une impression de vitesse.
     * </p>
     */
    @Override
    protected void loadAnimations(TextureAtlas atlas) {
        idleAnim = new Animation<>(0.1f, atlas.findRegions("IDLE"), Animation.PlayMode.LOOP);
        walkAnim = new Animation<>(0.1f, atlas.findRegions("RUNNING"), Animation.PlayMode.LOOP);

        // Course plus rapide visuellement (0.05s par frame)
        runAnim = new Animation<>(0.05f, atlas.findRegions("RUNNING"), Animation.PlayMode.LOOP);

        jumpAnim = new Animation<>(0.1f, atlas.findRegions("JUMPING"), Animation.PlayMode.LOOP);
        wallSlideAnim = new Animation<>(0.1f, atlas.findRegions("WALLJUMPING"), Animation.PlayMode.LOOP);
        hitAnim = new Animation<>(0.1f, atlas.findRegions("HIT"), Animation.PlayMode.LOOP);
        fallAnim = new Animation<>(0.1f, atlas.findRegions("FALLING"), Animation.PlayMode.LOOP);
        doubleJumpAnim = new Animation<>(0.1f, atlas.findRegions("DOUBLEJUMP"), Animation.PlayMode.LOOP);
    }

    /**
     * Arbre de décision pour choisir l'animation (Visual State Machine).
     * <p>
     * L'ordre des conditions est crucial (Priorité) :
     * <ol>
     * <li><b>Dégâts/Mort :</b> Priorité absolue.</li>
     * <li><b>Wall Slide :</b> Prioritaire si on est en l'air contre un mur.</li>
     * <li><b>Aérien (Saut/Chute) :</b> Si on ne touche pas le sol.</li>
     * <li><b>Sol (Course/Marche/Idle) :</b> Comportement par défaut.</li>
     * </ol>
     * </p>
     *
     * @return L'animation à jouer pour la frame actuelle.
     */
    @Override
    protected Animation<TextureRegion> getAnimationForState() {

        // 1. PRIORITÉ ABSOLUE : Dégâts ou Mort
        if (player.getVisualHitActive() || !player.getAlive()) {
            return hitAnim;
        }

        // 2. WALL SLIDE (Glissade murale)
        // Conditions : Toucher un mur + Être en l'air + Tomber (vitesse Y < 0)
        else if (player.getTouchingWall() && !player.getGrounded() && player.getVelocity().y < 0) {
            return wallSlideAnim;
        }

        // 3. AÉRIEN (Saut ou Chute libre)
        else if (!player.getGrounded()) {
            if (player.getVelocity().y < 0) {
                // On tombe
                return fallAnim;
            } else {
                // On monte (Saut)
                // On distingue le saut normal du double saut
                if (player.getJumpCount() > 1) {
                    return doubleJumpAnim;
                } else {
                    return jumpAnim;
                }
            }
        }

        // 4. SOL (Mouvement horizontal)
        else if (player.getVelocity().x != 0) {
            if (player.getRunning()) {
                return runAnim; // Course rapide
            } else {
                return walkAnim; // Marche normale
            }
        }

        // 5. PAR DÉFAUT : Immobile
        else {
            return idleAnim;
        }
    }

    /**
     * Indique si le sprite doit être inversé horizontalement.
     * @return {@code true} si le joueur regarde vers la gauche.
     */
    @Override
    protected boolean getIsFacingLeft() {
        return player.getFacingLeft();
    }
}
