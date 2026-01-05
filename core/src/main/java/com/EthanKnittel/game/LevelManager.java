package com.EthanKnittel.game;

import com.badlogic.gdx.utils.Array;
import java.util.Random;

public class LevelManager {

    private Array<String> mapFiles;
    private String currentMap;
    private Random random;

    public LevelManager() {
        random = new Random();
        mapFiles = new Array<>();

        mapFiles.add("TiledLevels/1.tmx");
        mapFiles.add("TiledLevels/2.tmx");
        mapFiles.add("TiledLevels/3.tmx");
        mapFiles.add("TiledLevels/4.tmx");
    }

    public String getNextMapPath(){
        if (mapFiles.size == 0){
            return "TiledLevels/1.tmx";
        }
        if (mapFiles.size == 1){
            return mapFiles.get(0);
        }

        String nextMap;
        do {
            int index = random.nextInt(mapFiles.size);
            nextMap = mapFiles.get(index);
        } while (nextMap.equals(currentMap));

        currentMap = nextMap;
        return nextMap;
    }

    public void setCurrentMap(String path){
        this.currentMap = path;
    }
}
