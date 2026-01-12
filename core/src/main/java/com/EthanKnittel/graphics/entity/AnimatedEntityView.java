package com.EthanKnittel.graphics.entity;

import com.EthanKnittel.entities.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Classe de base abstraite pour toutes les vues utilisant des animations par sprites.
 * <p>
 * Cette classe simplifie la gestion graphique en prenant en charge :
 * <ul>
 * <li>Le chargement du {@link TextureAtlas} (fichier .atlas contenant toutes les images).</li>
 * <li>Le suivi du temps d'animation ({@code stateTime}).</li>
 * <li>La sélection de la frame courante à afficher.</li>
 * <li>Le retournement horizontal (Flip X) automatique si l'entité regarde à gauche.</li>
 * </ul>
 * </p>
 * <p>
 * Les classes filles (ex: {@code PlayerView}) n'ont plus qu'à définir les règles de changement d'état.
 * </p>
 */
public abstract class AnimatedEntityView implements EntityView {

    /** L'entité logique liée (le Modèle). */
    private Entity entity;

    /** L'atlas de texture contenant toutes les images packées. */
    private TextureAtlas atlas;

    /** L'animation en cours de lecture (ex: Run, Idle). */
    private Animation<TextureRegion> currentAnimation;

    /** Temps écoulé pour l'animation actuelle (permet de savoir quelle frame afficher). */
    private float stateTime = 0f;

    /**
     * Constructeur de base.
     *
     * @param entity    L'entité à représenter.
     * @param atlasPath Le chemin interne vers le fichier .atlas (généré par TexturePacker).
     */
    public AnimatedEntityView(Entity entity, String atlasPath) {
        this.entity = entity;
        try {
            // Chargement de l'atlas en mémoire
            this.atlas = new TextureAtlas(atlasPath);
            // Appel à la méthode abstraite pour que l'enfant charge ses animations spécifiques
            loadAnimations(this.atlas);
        } catch (Exception e) {
            Gdx.app.error("Erreur du chargement de l'atlas: ", atlasPath, e);
        }
    }

    /**
     * Charge les animations spécifiques à partir de l'atlas.
     * <p>
     * À implémenter par les sous-classes pour extraire les régions (ex: "RUN", "JUMP")
     * et créer les objets {@link Animation}.
     * </p>
     *
     * @param atlas L'atlas chargé, prêt à être interrogé.
     */
    protected abstract void loadAnimations(TextureAtlas atlas);

    /**
     * Détermine quelle animation doit être jouée actuellement.
     * <p>
     * C'est ici que réside la logique visuelle (Machine à états).
     * Ex: Si l'entité saute -> Retourner {@code jumpAnim}.
     * </p>
     *
     * @return L'objet Animation correspondant à l'état de l'entité.
     */
    protected abstract Animation<TextureRegion> getAnimationForState();

    /**
     * Indique si le sprite doit être retourné horizontalement (Miroir).
     *
     * @return {@code true} si l'entité regarde vers la gauche.
     */
    protected abstract boolean getIsFacingLeft();

    /**
     * Méthode de rendu principale.
     * Gère automatiquement le temps, le changement d'animation et le dessin.
     */
    @Override
    public void render(SpriteBatch batch, float delta) {
        // 1. Mise à jour du temps d'animation
        stateTime = stateTime + delta;

        // 2. Récupération de l'animation voulue pour cet instant
        Animation<TextureRegion> nextAnimation = getAnimationForState();

        // 3. Détection de changement d'état
        // Si on passe de "Courir" à "Sauter", on remet le timer à 0 pour commencer l'anim de saut au début.
        if (currentAnimation != nextAnimation) {
            currentAnimation = nextAnimation;
            stateTime = 0f;
        }

        // 4. Dessin
        if (currentAnimation != null) {
            // getKeyFrame sélectionne l'image précise en fonction du temps écoulé
            // le paramètre 'true' active le bouclage (Looping) si l'animation est configurée ainsi
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

            // Gestion du Flip (Regarder à gauche ou droite)
            boolean flip = getIsFacingLeft();

            // Logique de flip :
            // Si on veut flip (gauche) mais que l'image n'est pas flippée -> On flip.
            // Si on ne veut pas flip (droite) mais que l'image est flippée -> On re-flip.
            // Note : TextureRegion garde l'état du flip en mémoire, il faut faire attention à ne pas le laisser inversé.
            if (flip && !currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            } else if (!flip && currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            }

            // Affichage à l'écran aux coordonnées de l'entité
            batch.draw(currentFrame, entity.getX(), entity.getY(), entity.getbounds().width, entity.getbounds().height);
        }
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    /**
     * Libère l'atlas de texture.
     * <p>
     * Très important : les textures prennent beaucoup de mémoire vidéo.
     * </p>
     */
    @Override
    public void dispose() {
        atlas.dispose();
    }

    // --- GETTERS & SETTERS (Utiles pour des effets spéciaux ou debug) ---

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }
}
