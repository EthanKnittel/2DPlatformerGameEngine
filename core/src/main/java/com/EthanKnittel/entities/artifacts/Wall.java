package com.EthanKnittel.entities.artifacts;

import com.EthanKnittel.entities.Artifact;

/**
 * Entité représentant un obstacle statique (Mur, Sol, Plafond, Plateforme).
 * <p>
 * C'est l'élément de base du Level Design. Il bloque physiquement les agents.
 * Un mur peut être visible (avec une texture fixe, notamment pour le TestLevel) ou invisible (pour créer des murs invisibles
 * laissant visible les éléments de la carte provenant de Tiled).
 * </p>
 */
public class Wall extends Artifact{

    /**
     * Détermine si ce mur doit être dessiné à l'écran.
     * <p>
     * {@code true} : Le Renderer chargera et affichera une texture (ex: briques).<br>
     * {@code false} : Le mur existe physiquement (collisions) mais est totalement transparent.
     * </p>
     */
    private boolean visible;

    /**
     * Crée un nouveau Mur.
     *
     * @param x           Position X (Coin inférieur gauche).
     * @param y           Position Y (Coin inférieur gauche).
     * @param width       Largeur du mur.
     * @param height      Hauteur du mur.
     * @param loadTexture Si true, le mur sera visible. Si false, c'est un "mur invisible".
     */
    public Wall(float x, float y, float width, float height, boolean visible) {
        // On appelle le constructeur d'Artifact qui active automatiquement setCollision(true).
        super(x, y, width, height);

        this.visible = visible;
    }

    public boolean getVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Mise à jour du mur.
     * <p>
     * <b>Optimisation :</b> Cette méthode est volontairement vide.
     * Un mur est statique, il ne bouge pas, ne réfléchit pas et n'a pas d'animation.
     * Ne rien faire ici économise du CPU quand on a des centaines de murs dans un niveau.
     * </p>
     */
    @Override
    public void update(float delta) {
        // Rien à faire pour un objet statique.
    }
}
