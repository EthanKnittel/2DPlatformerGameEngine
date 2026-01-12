package com.EthanKnittel.entities;

/**
 * Classe abstraite représentant les entités "inanimées" ou "objets" du monde.
 * <p>
 * Contrairement aux {@link Agent}s (qui ont une vie, une IA, etc.), un Artefact est un objet
 * purement physique ou logique.
 * </p>
 * <p>
 * Exemples d'artefacts :
 * <ul>
 * <li>{@link com.EthanKnittel.entities.artifacts.Wall} (Murs, sols, plateformes)</li>
 * <li>{@link com.EthanKnittel.entities.artifacts.Projectile} (Flèches, balles - bien que mobiles, ce ne sont pas des êtres vivants)</li>
 * <li>Coffres, pièges, leviers...</li>
 * </ul>
 * </p>
 */
public abstract class Artifact extends Entity{

    /**
     * Constructeur de base pour un objet inanimé.
     * <p>
     * Par défaut, un Artefact est considéré comme "solide" pour le moteur physique.
     * Cela signifie que les entités ne peuvent pas le traverser (sauf si configuré autrement, comme pour un projectile).
     * </p>
     *
     * @param x      Position X initiale.
     * @param y      Position Y initiale.
     * @param width  Largeur de la hitbox.
     * @param height Hauteur de la hitbox.
     */
    public Artifact (float x, float y, float width, float height) {
        super(x,y, width,height);

        // Configuration par défaut : Un objet a une collision physique.
        // C'est logique pour un Mur.
        // Pour un Projectile, ce paramètre sera souvent désactivé manuellement dans la sous-classe
        // pour gérer une collision "Trigger" (traverser sans pousser) plutôt que physique.
        setCollision(true);
    }
}
