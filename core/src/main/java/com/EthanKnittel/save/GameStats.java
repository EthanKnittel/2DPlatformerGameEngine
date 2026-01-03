package com.EthanKnittel.save;

import com.badlogic.gdx.utils.Array;

public class GameStats {
    public float totalPlaytime = 0f;
    public float longestSession = 0f;
    public int highScore = 0;

    public Array<EnemyKillStat> enemyKills = new Array<>();

    public GameStats(){}
}
