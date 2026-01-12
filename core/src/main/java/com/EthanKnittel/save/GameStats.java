package com.EthanKnittel.save;

import com.badlogic.gdx.utils.Array;

/**
 * Conteneur racine des statistiques de jeu.
 * <p>
 * C'est cet objet qui est directement transformé en fichier JSON (save.json).
 * Il regroupe toutes les données persistantes : temps global, records, et statistiques de combat.
 * </p>
 * <p>
 * Cette classe ne contient aucune logique complexe (POJO), elle sert uniquement de structure de données
 * pour la sauvegarde et le chargement.
 * </p>
 */
public class GameStats {

    /** Temps total passé en jeu (en secondes), cumulé sur toutes les parties. */
    private float totalTimePlayed;

    /** Durée de la plus longue session de jeu continue (en secondes). */
    private float longestSession;

    /** Le meilleur score jamais atteint par le joueur. */
    private int highScore;

    /** Liste des statistiques par type d'ennemi (ex: [{Cactus: 12}, {Ordi: 5}]). */
    private Array<EnemyKillStat> enemyKillStats;

    /**
     * Constructeur par défaut.
     * <p>
     * Initialise les listes et les valeurs par défaut pour éviter les erreurs
     * si l'objet est utilisé avant d'être chargé depuis un fichier.
     * </p>
     */
    public GameStats() {
        this.enemyKillStats = new Array<>();
        this.totalTimePlayed = 0;
        this.longestSession = 0;
        this.highScore = 0;
    }

    // --- GETTERS & SETTERS (Encapsulation) ---

    public float getTotalTimePlayed() {
        return totalTimePlayed;
    }

    public void setTotalTimePlayed(float totalTimePlayed) {
        this.totalTimePlayed = totalTimePlayed;
    }

    public float getLongestSession() {
        return longestSession;
    }

    public void setLongestSession(float longestSession) {
        this.longestSession = longestSession;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public Array<EnemyKillStat> getEnemyKillStats() {
        return enemyKillStats;
    }

    public void setEnemyKillStats(Array<EnemyKillStat> enemyKillStats) {
        this.enemyKillStats = enemyKillStats;
    }
}
