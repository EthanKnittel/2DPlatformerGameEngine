package com.EthanKnittel.save;

public class EnemyKillStat {
    private String enemyName;
    private int killCount;

    public EnemyKillStat() {} // pour la lecture JSON

    public EnemyKillStat(String name, int count){
        this.enemyName = name;
        this.killCount = count;
    }

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
