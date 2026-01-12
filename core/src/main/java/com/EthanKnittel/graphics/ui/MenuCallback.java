package com.EthanKnittel.graphics.ui;

/**
 * Interface de rappel (Callback) pour gérer les interactions des menus en jeu.
 * <p>
 * Cette interface définit le "contrat" de communication entre l'interface utilisateur ({@link GameMenus})
 * et la logique du jeu ({@link com.EthanKnittel.game.GameScreen}).
 * </p>
 * <p>
 * <b>Pourquoi cette interface ?</b><br>
 * Pour découpler le code : la classe `GameMenus` gère l'affichage des boutons, mais elle ne sait pas
 * comment "Redémarrer le jeu" ou "Quitter". Elle se contente d'appeler ces méthodes quand on clique.
 * C'est le `GameScreen` qui implémente cette interface et effectue les vraies actions.
 * </p>
 */
public interface MenuCallback {

    /**
     * Appelé lorsque le joueur clique sur "Reprendre" dans le menu de pause.
     * <p>
     * L'implémentation doit cacher le menu et relancer la boucle de jeu.
     * </p>
     */
    void onResume();

    /**
     * Appelé lorsque le joueur clique sur "Recommencer" (souvent après la mort).
     * <p>
     * L'implémentation doit recharger le niveau actuel ou réinitialiser le monde.
     * </p>
     */
    void onRestart();

    /**
     * Appelé lorsque le joueur clique sur "Quitter" (Desktop).
     * <p>
     * L'implémentation doit fermer proprement l'application via {@code Gdx.app.exit()}.
     * </p>
     */
    void onQuit();

    /**
     * Appelé pour revenir au menu principal.
     * <p>
     * L'implémentation doit sauvegarder la progression (si nécessaire) et changer l'écran
     * vers {@link com.EthanKnittel.game.MainMenuScreen}.
     * </p>
     */
    void onGoToMainMenu();
}
