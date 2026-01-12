package com.EthanKnittel.score;

import com.EthanKnittel.Evolving;

/**
 * Gestionnaire responsable du suivi du score et du temps de survie.
 * <p>
 * Cette classe implémente {@link Evolving} pour être mise à jour à chaque frame.
 * Elle gère :
 * <ul>
 * <li>Le score actuel du joueur (points gagnés en tuant des ennemis).</li>
 * <li>Le temps de survie (chronomètre).</li>
 * <li>Un système optionnel de gain de points passif par seconde.</li>
 * </ul>
 * </p>
 * <p>
 * Elle suit le pattern <b>Singleton</b> pour permettre un accès facile depuis n'importe où
 * (ex: depuis un Ennemi pour ajouter des points à sa mort).
 * </p>
 */
public class ScoreManager implements Evolving {

    /** Instance unique. */
    private static ScoreManager instance;

    /** Le score actuel de la partie. */
    private int score;

    /** Le temps écoulé depuis le début de la partie (en secondes). */
    private float timeSurvived;

    /**
     * Nombre de points gagnés automatiquement chaque seconde.
     * <p>
     * Par défaut à 2.
     * </p>
     */
    private int pointsPerSecond = 2;

    /**
     * Accumulateur interne pour gérer le gain de points par seconde.
     * Stocke le temps écoulé (delta) jusqu'à atteindre 1.0 seconde.
     */
    private float scoreAccumulator= 0f;

    /**
     * Constructeur du ScoreManager.
     * Initialise les compteurs à 0.
     */
    private ScoreManager(){
        this.score = 0;
        this.timeSurvived = 0;
    }

    /**
     * instance unique ScoreManager.
     * Design Pattern Singleton.
     */
    public static ScoreManager getScoreManager(){
        if (ScoreManager.instance == null){
            instance = new ScoreManager();
        }
        return instance;
    }

    /**
     * Ajoute des points au score du joueur.
     *
     * @param score Le montant à ajouter (doit être positif).
     */
    public void addScore(int score){
        if (score > 0) {
            this.score += score;
        }
    }

    /**
     * Met à jour le chronomètre et le score passif.
     * Appelé à chaque frame par le GameScreen.
     *
     * @param deltaTime Temps écoulé depuis la dernière frame (en secondes).
     */
    public void update(float deltaTime) {
        // 1. Mise à jour du temps de survie
        timeSurvived += deltaTime;

        // 2. Gestion des points par seconde (si activé)
        scoreAccumulator += deltaTime;
        // Si une seconde complète est passée
        if (scoreAccumulator >= 1.0f) {
            score += pointsPerSecond;
            scoreAccumulator = 0f; // On revient à 0
        }
    }

    /**
     * Convertit le temps de survie (float) en une chaîne formatée "MM:SS".
     * <p>
     * Exemple : 125.5 secondes devient "02:05".
     * </p>
     *
     * @return Une chaîne de caractères représentant le temps (ex: "01:30").
     */
    public String getFormattedTime(){
        int minutes = (int) timeSurvived / 60;
        int seconds = (int) timeSurvived % 60;

        String minuteString;
        String secondString;

        if (minutes < 10) {
            // Astuce : On concatène une chaîne vide "" + nombre pour convertir int en String
            // On ajoute un "0" devant si le chiffre est inférieur à 10 (ex: "05" au lieu de "5")
            minuteString = "0" + minutes;
        } else {
            minuteString = "" + minutes;
        }

        if (seconds < 10) {
            secondString = "0" + seconds;
        } else  {
            secondString = "" + seconds;
        }

        return minuteString + ":" + secondString;
    }

    /**
     * Réinitialise les statistiques (Score et Temps).
     */
    public void reset(){
        score = 0;
        timeSurvived = 0;
        scoreAccumulator = 0f;
    }

    // --- GETTERS & SETTERS ---

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Récupère le temps de survie exact en secondes.
     * @return Le temps sous forme de nombre à virgule (ex: 12.456).
     */
    public float getTimeSurvived() {
        return timeSurvived;
    }


    public int getPointsPerSecond() {
        return pointsPerSecond;
    }

    /**
     * Définit le nombre de points gagnés passivement chaque seconde.
     *
     * @param pointsPerSecond Le nombre de points (doit être >= 0).
     */
    public void setPointsPerSecond(int pointsPerSecond) {
        if (pointsPerSecond >= 0) {
            this.pointsPerSecond = pointsPerSecond;
        }
    }
}
