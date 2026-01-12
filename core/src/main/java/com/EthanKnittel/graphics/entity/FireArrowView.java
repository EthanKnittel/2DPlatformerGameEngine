package com.EthanKnittel.graphics.entity;

import com.EthanKnittel.entities.artifacts.FireArrow;
import com.EthanKnittel.game.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Vue graphique spécifique pour le projectile {@link FireArrow}.
 * <p>
 * Cette classe hérite de {@link AnimatedEntityView} pour la gestion de l'atlas,
 * mais elle surcharge la méthode {@link #render(SpriteBatch, float)} pour gérer
 * la rotation fine du sprite.
 * </p>
 * <p>
 * Contrairement à un personnage qui regarde juste à Gauche ou à Droite (Flip),
 * une flèche doit pouvoir pointer dans n'importe quelle direction (0° à 360°).
 * </p>
 */
public class FireArrowView extends AnimatedEntityView {

    /** Référence vers l'entité logique (Modèle) pour lire sa rotation. */
    private FireArrow arrow;

    /** L'animation de vol (la flèche qui ondule ou brûle). */
    private Animation<TextureRegion> flyAnim;

    /**
     * Constructeur.
     * Charge l'atlas contenant les frames de la flèche de feu.
     *
     * @param arrow L'instance du projectile à afficher.
     */
    public FireArrowView(FireArrow arrow) {
        super(arrow, Gdx.files.internal("FireArrow/fire_arrow.atlas").path());
        this.arrow = arrow;
    }

    /**
     * Charge l'animation de vol depuis l'atlas.
     */
    @Override
    protected void loadAnimations(TextureAtlas atlas) {
        // Animation en boucle, 0.1s par frame
        flyAnim = new Animation<>(0.1f, atlas.findRegions("Fire Arrow_Frame"), Animation.PlayMode.LOOP);
    }

    /**
     * Retourne l'animation à jouer.
     * La flèche n'a qu'un seul état : elle vole.
     */
    @Override
    protected Animation<TextureRegion> getAnimationForState() {
        return flyAnim;
    }

    /**
     * Indique si on doit inverser l'image horizontalement.
     * <p>
     * Ici, on retourne toujours {@code false} car l'orientation n'est pas gérée par un miroir (Flip),
     * mais par une rotation complète dans la méthode render().
     * </p>
     */
    @Override
    protected boolean getIsFacingLeft() {
        return false;
    }

    /**
     * Surcharge de la méthode de rendu pour gérer la ROTATION.
     * <p>
     * La méthode parent {@code AnimatedEntityView.render()} est conçue pour des personnages debout (X/Y simples).
     * Ici, nous devons utiliser une version plus complexe de {@code batch.draw()} qui accepte un angle de rotation.
     * </p>
     */
    @Override
    public void render(SpriteBatch batch, float delta) {
        // 1. Mise à jour du temps d'animation (State Time)
        // On utilise les setters/getters du parent car le champ est privé
        setStateTime(getStateTime() + delta);

        // 2. Récupération de la frame actuelle
        TextureRegion currentFrame = flyAnim.getKeyFrame(getStateTime(), true);

        // 3. Calcul des dimensions d'affichage
        // Le sprite de la flèche fait 64x16 pixels. On convertit en unités du monde.
        float width = 64f / GameScreen.getPixelsPerBlocks();
        float height = 16f / GameScreen.getPixelsPerBlocks();

        // 4. Dessin avec Rotation
        // Arguments complexes de batch.draw() :
        // - TextureRegion : L'image à dessiner
        // - x, y : Position du coin inférieur gauche (ajustée pour centrer le sprite sur la hitbox)
        // - originX, originY : Point de pivot de la rotation (ici le centre de l'image : width/2, height/2)
        // - width, height : Taille affichée
        // - scaleX, scaleY : Échelle (1 = normal)
        // - rotation : L'angle en degrés
        batch.draw(currentFrame,
            arrow.getX() + arrow.getbounds().width / 2f - width / 2f,  // X centré
            arrow.getY() + arrow.getbounds().height / 2f - height / 2f, // Y centré
            width / 2f, height / 2f, // Point de pivot (Centre)
            width, height,           // Dimensions
            1, 1,                    // Échelle
            arrow.getRotation() + 180f); // Rotation (+180 car le sprite d'origine pointe peut-être vers la gauche -> problème à régler)
    }
}
