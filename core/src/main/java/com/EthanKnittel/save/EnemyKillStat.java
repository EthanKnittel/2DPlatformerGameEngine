package com.EthanKnittel.save;

public class EnemyKillStat {
    public String enemyName;
    public int killCount;

    public EnemyKillStat() {} // pour la lecture JSON

    public EnemyKillStat(String name, int count){
        this.enemyName = name;
        this.killCount = count;
    }
}
