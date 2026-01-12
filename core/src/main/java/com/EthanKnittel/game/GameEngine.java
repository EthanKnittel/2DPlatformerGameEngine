package com.EthanKnittel.game;

import com.badlogic.gdx.Game;

/**
 * Moteur principal du jeu (Entry Point).
 * <p>
 * Cette classe hérite de {@link Game}, une classe utilitaire de LibGDX qui gère
 * automatiquement la délégation des méthodes de cycle de vie (render, resize, pause...)
 * vers l'écran actif ({@link com.badlogic.gdx.Screen}).
 * </p>
 * <p>
 * C'est le chef d'orchestre global qui persiste pendant toute la durée de vie de l'application,
 * alors que les écrans (MainMenu, GameScreen) sont créés et détruits selon les besoins.
 * </p>
 */
public class GameEngine extends Game {

    /**
     * Appelé une seule fois au démarrage de l'application.
     * <p>
     * C'est ici qu'on initialise les ressources globales lourdes (si nécessaire)
     * et qu'on définit quel sera le tout premier écran affiché au joueur.
     * </p>
     */
    @Override
    public void create() {
        // On lance le menu principal dès le démarrage
        setScreen(new MainMenuScreen());
    }

    /**
     * Méthode de rendu global.
     * <p>
     * <b>Note importante :</b> La classe parente {@link Game} implémente déjà cette méthode
     * pour appeler {@code currentScreen.render(delta)}.
     * On ne la surcharge pas ici, car le comportement par défaut nous convient parfaitement.
     * </p>
     */

    // @Override public void render() { super.render(); }

    /**
     * Appelé à la fermeture de l'application.
     * <p>
     * Permet de nettoyer les ressources globales qui auraient été allouées dans {@code create()}.
     * </p>
     */
    @Override
    public void dispose() {
        // Il est important d'appeler super.dispose() car la classe Game
        // se charge d'appeler .dispose() sur l'écran qui était actif.
        super.dispose();
    }
}
