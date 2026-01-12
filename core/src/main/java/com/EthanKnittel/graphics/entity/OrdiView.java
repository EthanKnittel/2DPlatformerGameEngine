package com.EthanKnittel.graphics.entity;

import com.EthanKnittel.entities.agents.foes.Ordi;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Vue graphique spécifique pour l'ennemi {@link Ordi}.
 * <p>
 * Cette classe est responsable de l'affichage du sprite de l'Ordi.
 * Elle hérite de {@link AnimatedEntityView} pour bénéficier de la gestion automatique
 * du temps d'animation et du chargement de l'atlas.
 * </p>
 * <p>
 * L'Ordi possède des animations distinctes pour :
 * <ul>
 * <li>L'attente (Idle)</li>
 * <li>La course (Run)</li>
 * <li>Le saut (Jump) et la chute (Fall)</li>
 * <li>La réaction aux dégâts (Hit)</li>
 * </ul>
 * </p>
 */
public class OrdiView extends AnimatedEntityView {

    /** Référence vers l'entité logique (le Modèle). */
    private Ordi ordi;

    // --- ANIMATIONS ---
    private Animation<TextureRegion> idleAnim;
    private Animation<TextureRegion> runAnim;
    private Animation<TextureRegion> hitAnim;
    private Animation<TextureRegion> fallAnim;
    private Animation<TextureRegion> jumpAnim;

    /**
     * Constructeur.
     *
     * @param ordi L'instance de l'ennemi à représenter graphiquement.
     */
    public OrdiView(Ordi ordi) {
        // Chargement de l'atlas spécifique à l'Ordi via le constructeur parent
        super(ordi, Gdx.files.internal("Ennemies/ordi/Ordi.atlas").path());
        this.ordi = ordi;
    }

    /**
     * Charge et configure les animations à partir du fichier Atlas.
     * <p>
     * Les noms des régions ("IDLE", "RUNNING", etc.) doivent correspondre exactement
     * aux noms définis dans le fichier .atlas (généré par TexturePacker).
     * </p>
     *
     * @param atlas L'atlas chargé contenant toutes les sprites.
     */
    @Override
    protected void loadAnimations(TextureAtlas atlas) {
        // Création des animations avec une vitesse de 0.1s par frame (10 FPS)
        idleAnim = new Animation<>(0.1f, atlas.findRegions("IDLE"), Animation.PlayMode.LOOP);
        runAnim = new Animation<>(0.1f, atlas.findRegions("RUNNING"), Animation.PlayMode.LOOP);
        hitAnim = new Animation<>(0.1f, atlas.findRegions("HIT"), Animation.PlayMode.LOOP);
        fallAnim = new Animation<>(0.1f, atlas.findRegions("FALLING"), Animation.PlayMode.LOOP);
        jumpAnim = new Animation<>(0.1f, atlas.findRegions("JUMPING"), Animation.PlayMode.LOOP);
    }

    /**
     * Détermine l'animation à jouer en fonction de l'état actuel de l'Ordi.
     * <p>
     * C'est une machine à états visuelle simple : on vérifie les conditions
     * par ordre de priorité (Dégâts > Air > Sol).
     * </p>
     *
     * @return L'animation correspondant à l'action en cours.
     */
    @Override
    protected Animation<TextureRegion> getAnimationForState() {
        // 1. PRIORITÉ MAXIMALE : Dégâts ou Mort
        if (ordi.getVisualHitActive() || !ordi.getAlive()) {
            return hitAnim;
        }

        // 2. PRIORITÉ PHYSIQUE : En l'air
        else if (!ordi.getGrounded() && ordi.getVelocity().y < 0) {
            return fallAnim;
        }
        // Si on touche le sol ET qu'on a une vitesse positive (impulsion de saut)
        else if (ordi.getGrounded() && ordi.getVelocity().y > 0) {
            return jumpAnim;
        }

        // 3. PRIORITÉ MOUVEMENT : Course au sol
        else if (ordi.getVelocity().x != 0) {
            return runAnim;
        }

        // 4. PAR DÉFAUT : Immobile
        else {
            return idleAnim;
        }
    }

    /**
     * Indique si le sprite doit être inversé horizontalement (Flip X).
     *
     * @return {@code true} si l'entité regarde vers la gauche.
     */
    @Override
    protected boolean getIsFacingLeft() {
        return ordi.getFacingLeft();
    }
}
