package com.EthanKnittel.audio;

import com.badlogic.gdx.audio.Sound;

public class SoundEntry {
    private final String name;
    private final Sound sound;

    public SoundEntry(String name, Sound sound) {
        this.name = name;
        this.sound = sound;
    }

    public String getName() {
        return name;
    }
    public Sound getSound() {
        return sound;
    }

}
