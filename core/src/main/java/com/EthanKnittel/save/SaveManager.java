package com.EthanKnittel.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class SaveManager {
    public static SaveManager instance;

    private GameStats gameStats;
    private final String SaveFilePath= "saves/GameStats.json"; // le lieu où est enregistré le fichier de sauvegarde
    private Json json;

    public SaveManager() {
        instance = this;
        json = new Json();
        json.setUsePrototypes(false);

        load();
    }

    public void load(){
        FileHandle fileHandle = Gdx.files.local(SaveFilePath); // on enregistre localement car c'est un simple projet

        if (fileHandle.exists()){
            try {
                gameStats = json.fromJson(GameStats.class, fileHandle.readString());
            } catch (Exception e) {
                Gdx.app.error("Load GameStats", "fichier corrompu", e);
                gameStats = new GameStats();
            }
        } else {
            gameStats = new GameStats();
        }
    }

    public void save(){
        FileHandle fileHandle = Gdx.files.local(SaveFilePath);
        fileHandle.writeString(json.prettyPrint(gameStats), false);
    }

    public void saveSessionStats(float sessionTime, int sessionScore) {

        gameStats.totalPlaytime += sessionTime;

        if (sessionTime > gameStats.longestSession) {
            gameStats.longestSession = sessionTime;
        }

        if (sessionScore > gameStats.highScore) {
            gameStats.highScore = sessionScore;
        }

        save();
    }

    public void addKillCount(String enemyName) {
        boolean found  = false;

        for (int i=0; i<gameStats.enemyKills.size; i++) {
            EnemyKillStat stat = gameStats.enemyKills.get(i);
            if (stat.getEnemyName().equals(enemyName)){
                stat.setKillCount(stat.getKillCount() + 1);
                found = true;
                break;
            }
        }

        if (!found){
            gameStats.enemyKills.add(new EnemyKillStat(enemyName, 1));
        }
    }

    public int getKillCount(String enemyName) {
        for (int i=0; i<gameStats.enemyKills.size; i++) {
            EnemyKillStat stat = gameStats.enemyKills.get(i);
            if (stat.getEnemyName().equals(enemyName)) {
                return stat.getKillCount();
            }
        }
        return 0;
    }

    public int getHighScore() {
        return gameStats.highScore;
    }

    public float getTotalPlaytime() {
        return gameStats.totalPlaytime;
    }

    public float getLongestSession() {
        return gameStats.longestSession;
    }

}
