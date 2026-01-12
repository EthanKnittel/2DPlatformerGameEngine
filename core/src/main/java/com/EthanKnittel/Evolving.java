package com.EthanKnittel;

/**
 * Interface fondamentale pour tout objet devant évoluer dans le temps (Game Loop).
 * <p>
 * Tout objet implémentant cette interface sera mis à jour à chaque frame du jeu.
 * C'est le cœur de la simulation : déplacement physique, animation, calcul de l'IA,
 * augmentation du score, etc.
 * </p>
 * <p>
 * Exemples d'implémentations :
 * <ul>
 * <li>{@link com.EthanKnittel.entities.Entity} (et donc Joueur, Ennemis, Projectiles)</li>
 * <li>{@link com.EthanKnittel.score.ScoreManager} (pour compter le temps de survie)</li>
 * <li>{@link com.EthanKnittel.world.systems.Environment} (pour gérer le monde entier)</li>
 * </ul>
 * </p>
 */
public interface Evolving {

    /**
     * Méthode appelée automatiquement à chaque tour de boucle (Frame).
     *
     * @param deltaTime Le temps écoulé (en secondes) depuis la dernière frame.
     * <p>
     * <b>Pourquoi utiliser deltaTime ?</b><br>
     * Pour rendre le jeu indépendant du framerate (FPS).
     * Si le jeu tourne à 60 FPS, deltaTime vaudra environ 0.016s.
     * Si le jeu ralentit à 30 FPS, deltaTime vaudra environ 0.033s.
     * <br>
     * En multipliant les vitesses par deltaTime (ex: {@code x += speed * deltaTime}),
     * un personnage parcourra la même distance en 1 seconde, que l'ordinateur soit puissant ou lent.
     * </p>
     */
    void update(float deltaTime);
}
