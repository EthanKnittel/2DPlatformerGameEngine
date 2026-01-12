package com.EthanKnittel.graphics.entity;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.artifacts.Wall;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Vue graphique pour l'entité {@link Wall}.
 * <p>
 * Cette classe est responsable de l'affichage des murs statiques.
 * Contrairement aux autres vues (PlayerView, CactusView), elle n'utilise pas d'animations
 * complexes mais une simple {@link Texture} fixe (image).
 * </p>
 * <p>
 * Elle gère aussi l'optimisation des "Murs Invisibles" : si le mur logique est marqué
 * comme invisible (ex: mur de collision sur une map Tiled), aucune texture n'est chargée
 * ni dessinée.
 * </p>
 */
public class WallView implements EntityView {

    /** L'entité logique associée (le Modèle). */
    private Wall wall;

    /** L'image du mur (peut être null si le mur est invisible). */
    private Texture texture;

    /**
     * Constructeur.
     * <p>
     * Charge la texture "wall.png" uniquement si le mur est configuré comme visible.
     * </p>
     *
     * @param wall L'instance du mur à afficher.
     */
    public WallView(Wall wall) {
        this.wall = wall;

        // On ne charge l'image que si nécessaire pour économiser la mémoire vidéo
        if (wall.getVisible()) {
            try {
                // Chargement direct d'une image simple (pas d'Atlas ici)
                texture = new Texture(Gdx.files.internal("wall.png"));
            } catch (Exception e) {
                Gdx.app.error("Wall", "Erreur lors du chargement de la texture wall.png", e);
            }
        }
    }

    /**
     * Dessine le mur à l'écran.
     *
     * @param batch Le SpriteBatch actif.
     * @param delta Le temps écoulé (inutilisé ici car pas d'animation).
     */
    @Override
    public void render(SpriteBatch batch, float delta) {
        // On ne dessine que si on a une texture chargée (Mur visible)
        if (texture != null) {
            // On dessine l'image étirée à la taille de la hitbox du mur
            batch.draw(texture, wall.getX(), wall.getY(), wall.getbounds().width, wall.getbounds().height);
        }
    }

    @Override
    public Entity getEntity() {
        return wall;
    }

    /**
     * Libère la texture de la mémoire graphique.
     * <p>
     * Important : Chaque instance de WallView possédant sa propre Texture (dans cette implémentation),
     * il est crucial de les dispose() pour éviter de saturer la VRAM.
     * </p>
     */
    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
