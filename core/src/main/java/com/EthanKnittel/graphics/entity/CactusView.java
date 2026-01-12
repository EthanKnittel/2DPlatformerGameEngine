package com.EthanKnittel.graphics.entity;

import com.EthanKnittel.entities.agents.foes.Cactus;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Vue graphique spécifique pour l'ennemi {@link Cactus}.
 * <p>
 * Cette classe gère l'affichage des animations du Cactus (Idle, Course, Saut, Chute, Coup reçu).
 * Elle fait le lien entre l'état logique de l'ennemi (ex: {@code cactus.getVelocity().x})
 * et les assets graphiques (Fichier .atlas).
 * </p>
 */
public class CactusView extends AnimatedEntityView {

    /** Référence vers l'entité logique (Modèle). */
    private Cactus cactus;

    // --- ANIMATIONS ---
    // On stocke les différentes séquences d'animation possibles.
    private Animation<TextureRegion> idleAnim;
    private Animation<TextureRegion> runAnim;
    private Animation<TextureRegion> hitAnim;
    private Animation<TextureRegion> fallAnim;
    private Animation<TextureRegion> jumpAnim;

    /**
     * Constructeur.
     * <p>
     * Charge l'atlas de texture spécifique au Cactus ("Ennemies/cactus/Cactus.atlas")
     * via le constructeur parent.
     * </p>
     *
     * @param cactus L'instance de l'ennemi à afficher.
     */
    public CactusView(Cactus cactus) {
        // On passe le chemin du fichier .atlas au parent (AnimatedEntityView) qui se charge du chargement technique
        super(cactus, Gdx.files.internal("Ennemies/cactus/Cactus.atlas").path());
        this.cactus = cactus;
    }

    /**
     * Charge et découpe les animations depuis l'atlas.
     * <p>
     * Cette méthode est appelée automatiquement par le constructeur parent.
     * On utilise les noms de régions définis dans TexturePacker (ex: "IDLE", "RUNNING").
     * </p>
     *
     * @param atlas L'atlas chargé contenant toutes les images.
     */
    @Override
    protected void loadAnimations(TextureAtlas atlas) {
        // 0.1f = 10 images par seconde (Vitesse standard)
        idleAnim = new Animation<>(0.1f, atlas.findRegions("IDLE"), Animation.PlayMode.LOOP);
        runAnim = new Animation<>(0.1f, atlas.findRegions("RUNNING"), Animation.PlayMode.LOOP);

        // HIT est souvent joué une seule fois, mais en LOOP ça marche aussi tant que l'état "isHit" est vrai
        hitAnim = new Animation<>(0.1f, atlas.findRegions("HIT"), Animation.PlayMode.LOOP);

        fallAnim = new Animation<>(0.1f, atlas.findRegions("FALLING"), Animation.PlayMode.LOOP);
        jumpAnim = new Animation<>(0.1f, atlas.findRegions("JUMPING"), Animation.PlayMode.LOOP);
    }

    /**
     * Machine à états visuelle (Visual State Machine).
     * <p>
     * Décide quelle animation afficher à l'instant T en fonction des propriétés du Cactus.
     * L'ordre des 'if' détermine la priorité visuelle (ex: "Être touché" est plus important que "Courir").
     * </p>
     *
     * @return L'animation correspondante.
     */
    @Override
    protected Animation<TextureRegion> getAnimationForState() {
        // PRIORITÉ 1 : Dégâts ou Mort
        // Si le cactus vient de prendre un coup (visuel hit actif) OU est mort
        if (cactus.getVisualHitActive() || !cactus.getAlive()) {
            return hitAnim;
        }

        // PRIORITÉ 2 : En l'air (Physique verticale)
        else if (!cactus.getGrounded() && cactus.getVelocity().y < 0) {
            return fallAnim;
        }
        else if (cactus.getGrounded() && cactus.getVelocity().y > 0) {
            // Idem, vitesse positive au sol = début du saut
            return jumpAnim;
        }

        // PRIORITÉ 3 : Mouvement horizontal (Course)
        else if (cactus.getVelocity().x != 0) {
            return runAnim;
        }

        // PAR DÉFAUT : Immobile
        else {
            return idleAnim;
        }
    }

    /**
     * Indique si le sprite doit être retourné horizontalement.
     * <p>
     * On délègue cette info à l'entité logique qui sait dans quelle direction elle regarde.
     * </p>
     */
    @Override
    protected boolean getIsFacingLeft() {
        return cactus.getFacingLeft();
    }
}
