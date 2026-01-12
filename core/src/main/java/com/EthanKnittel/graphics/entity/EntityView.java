package com.EthanKnittel.graphics.entity;

import com.EthanKnittel.entities.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Interface définissant la "Vue" d'une entité (Pattern MVC simplifié).
 * <p>
 * Chaque entité logique du jeu (ex: {@link com.EthanKnittel.entities.agents.Player}) possède
 * une vue correspondante (ex: {@link PlayerView}) qui s'occupe de :
 * <ul>
 * <li>Charger les textures et animations.</li>
 * <li>Calculer quelle frame afficher en fonction de l'état de l'entité (marche, saut, hit).</li>
 * <li>Dessiner le sprite à la bonne position écran via le {@link SpriteBatch}.</li>
 * </ul>
 * </p>
 */
public interface EntityView {

    /**
     * Dessine l'entité à l'écran.
     * <p>
     * Cette méthode est appelée à chaque frame par le {@link com.EthanKnittel.graphics.WorldRenderer}.
     * </p>
     *
     * @param batch Le gestionnaire de dessin LibGDX (doit avoir appelé {@code batch.begin()} avant).
     * @param delta Le temps écoulé depuis la dernière frame (utile pour avancer les animations).
     */
    void render(SpriteBatch batch, float delta);

    /**
     * Libère les ressources graphiques (Textures, Atlas) pour éviter les fuites de mémoire.
     * <p>
     * Appelée quand l'entité meurt ou quand on change de niveau.
     * </p>
     */
    void dispose();

    /**
     * Récupère l'entité logique associée à cette vue.
     * <p>
     * Permet au Renderer de vérifier si l'entité existe toujours dans le monde
     * avant de tenter de la dessiner.
     * </p>
     *
     * @return L'instance de {@link Entity} liée (le Modèle).
     */
    Entity getEntity();
}
