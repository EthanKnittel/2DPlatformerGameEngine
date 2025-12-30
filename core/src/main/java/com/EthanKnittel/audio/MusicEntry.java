package com.EthanKnittel.audio;

import com.badlogic.gdx.audio.Music;

public class MusicEntry {
    private final String name;
    private final Music music;

    public MusicEntry(String name, Music music) {
        this.name = name;
        this.music = music;
    }

    public Music getMusic() {
        return music;
    }
    public String getName() {
        return name;
    }
}
