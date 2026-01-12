package com.EthanKnittel.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * Gestionnaire responsable de l'écriture et de la lecture des fichiers de sauvegarde.
 * <p>
 * Cette classe utilise la librairie JSON de LibGDX pour sérialiser l'objet {@link GameStats}
 * en un fichier texte lisible (save.json). Elle agit comme une couche d'abstraction
 * entre le jeu et le système de fichiers.
 * </p>
 * <p>
 * Elle suit le pattern Singleton (via le champ statique instance) pour être accessible
 * facilement depuis les écrans de fin de jeu ou les menus.
 * </p>
 */
public class SaveManager {

    /** Instance globale accessible de partout (Singleton simplifié). */
    public static SaveManager instance;

    /** Le modèle de données contenant les statistiques (objet à sauvegarder). */
    private GameStats gameStats;

    /** Chemin du fichier de sauvegarde (relatif au dossier d'exécution ou au stockage local Android). */
    private final String SaveFilePath = "saves/GameStats.json";

    /** Outil de sérialisation JSON de LibGDX. */
    private Json json;

    /**
     * Constructeur du gestionnaire.
     * Initialise le sérialiseur et tente de charger une sauvegarde existante au démarrage.
     */
    private SaveManager() {
        json = new Json();

        // setUsePrototypes(false) assure que tout le JSON est écrit explicitement,
        // sans référencer des valeurs par défaut, ce qui est plus sûr pour les sauvegardes.
        json.setUsePrototypes(false);

        load();
    }

    public static SaveManager getInstance() {
        if (SaveManager.instance == null) {
            instance = new  SaveManager();
        }
        return instance;
    }

    /**
     * Charge les données depuis le disque.
     * <p>
     * Si le fichier existe, il est lu et converti en objet {@link GameStats}.
     * Si le fichier n'existe pas ou est corrompu, un nouvel objet vierge est créé.
     * </p>
     */
    public void load() {
        // Gdx.files.local stocke dans le dossier de l'app (Desktop) ou les données privées (Android)
        FileHandle fileHandle = Gdx.files.local(SaveFilePath);

        if (fileHandle.exists()) {
            try {
                // Désérialisation : Texte JSON -> Objet Java
                gameStats = json.fromJson(GameStats.class, fileHandle.readString());
            } catch (Exception e) {
                Gdx.app.error("Load GameStats", "Fichier de sauvegarde corrompu ou incompatible", e);
                // En cas d'erreur, on repart de zéro pour éviter le crash
                gameStats = new GameStats();
            }
        } else {
            // Pas de sauvegarde trouvée (premier lancement)
            gameStats = new GameStats();
        }
    }

    /**
     * Écrit l'état actuel des statistiques sur le disque.
     * <p>
     * Utilise {@code prettyPrint} pour rendre le fichier JSON lisible par un humain (utile pour le debug).
     * </p>
     */
    public void save() {
        FileHandle fileHandle = Gdx.files.local(SaveFilePath);
        fileHandle.writeString(json.prettyPrint(gameStats), false);
    }

    /**
     * Met à jour les statistiques globales à la fin d'une partie.
     *
     * @param sessionTime La durée de la partie qui vient de se terminer (en secondes).
     * @param sessionScore Le score obtenu lors de cette partie.
     */
    public void saveSessionStats(float sessionTime, int sessionScore) {
        // Mise à jour du temps total (via Getter/Setter)
        gameStats.setTotalTimePlayed(gameStats.getTotalTimePlayed() + sessionTime);

        // Mise à jour de la plus longue session si record battu
        if (sessionTime > gameStats.getLongestSession()) {
            gameStats.setLongestSession(sessionTime);
        }

        // Mise à jour du meilleur score si record battu
        if (sessionScore > gameStats.getHighScore()) {
            gameStats.setHighScore(sessionScore);
        }

        // On écrit immédiatement sur le disque pour ne pas perdre les données en cas de crash
        save();
    }

    /**
     * Incrémente le compteur de kills pour un type d'ennemi donné.
     * <p>
     * Si l'ennemi est déjà dans la liste, son compteur augmente.
     * Sinon, une nouvelle entrée est créée dans la liste.
     * </p>
     *
     * @param enemyName Le nom de l'ennemi (ex: "Cactus").
     */
    public void addKillCount(String enemyName) {
        boolean found = false;

        // On parcourt la liste des stats d'ennemis
        for (int i = 0; i < gameStats.getEnemyKillStats().size; i++) {
            EnemyKillStat stat = gameStats.getEnemyKillStats().get(i);

            if (stat.getEnemyName().equals(enemyName)) {
                stat.setKillCount(stat.getKillCount() + 1);
                found = true;
                break;
            }
        }

        // Si c'est la première fois qu'on tue cet ennemi, on l'ajoute à la liste
        if (!found) {
            gameStats.getEnemyKillStats().add(new EnemyKillStat(enemyName, 1));
        }
    }

    /**
     * Récupère le nombre total d'éliminations pour un type d'ennemi.
     *
     * @param enemyName Le nom de l'ennemi recherché.
     * @return Le nombre de kills, ou 0 si l'ennemi n'a jamais été tué.
     */
    public int getKillCount(String enemyName) {
        for (int i = 0; i < gameStats.getEnemyKillStats().size; i++) {
            EnemyKillStat stat = gameStats.getEnemyKillStats().get(i);
            if (stat.getEnemyName().equals(enemyName)) {
                return stat.getKillCount();
            }
        }
        return 0;
    }

    // --- ACCESSEURS DE LECTURE (Proxies vers GameStats) ---

    /** @return Le meilleur score enregistré. */
    public int getHighScore() {
        return gameStats.getHighScore();
    }

    /** @return Le temps total de jeu cumulé (en secondes). */
    public float getTotalPlaytime() {
        return gameStats.getTotalTimePlayed();
    }

    /** @return La durée de la plus longue session enregistrée (en secondes). */
    public float getLongestSession() {
        return gameStats.getLongestSession();
    }
}
