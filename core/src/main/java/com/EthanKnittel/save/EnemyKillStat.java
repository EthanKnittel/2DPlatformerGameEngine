package com.EthanKnittel.save;

/**
 * Modèle de données représentant une statistique de kill pour un type d'ennemi.
 * <p>
 * Cette classe est un simple conteneur (POJO) utilisé pour la sauvegarde JSON.
 * Elle stocke une paire "Nom de l'ennemi" <-> "Nombre de fois tué".
 * </p>
 * <p>
 * Exemple JSON généré : {@code { "enemyName": "Cactus", "killCount": 12 }}
 * </p>
 */
public class EnemyKillStat {

    /** Le nom de l'ennemi (doit correspondre au nom défini dans Foe ou EnemyRegistry). */
    private String enemyName;

    /** Le nombre total d'éliminations de cet ennemi par le joueur. */
    private int killCount;

    /**
     * Constructeur par défaut (Sans arguments).
     * <p>
     * <b>Important :</b> Ce constructeur est OBLIGATOIRE pour la désérialisation JSON (LibGDX Json).
     * Le moteur de sauvegarde crée d'abord l'objet vide, puis remplit les champs un par un.
     * Sans lui, le chargement de la sauvegarde planterait.
     * </p>
     */
    public EnemyKillStat() {
        // Laisser vide pour le JSON
    }

    /**
     * Constructeur d'initialisation.
     *
     * @param name  Nom de l'ennemi (ex: "Ordi").
     * @param count Nombre initial de kills.
     */
    public EnemyKillStat(String name, int count){
        this.enemyName = name;
        this.killCount = count;
    }

    // --- GETTERS & SETTERS (Accesseurs) ---
    // Nécessaires pour que le sérialiseur JSON puisse lire et écrire les valeurs privées.

    public void setEnemyName(String name){
        enemyName =  name;
    }

    public void setKillCount(int count){
        killCount = count;
    }

    public String getEnemyName(){
        return enemyName;
    }

    public int getKillCount(){
        return killCount;
    }
}
