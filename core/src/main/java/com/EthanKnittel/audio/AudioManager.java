package com.EthanKnittel.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class AudioManager implements Disposable {
    public static AudioManager audioManager;

    private Array<SoundEntry> sounds;
    private Array<MusicEntry> musics;

    private Music currentMusic;
    private float soundVolume = 1.0f;
    private float musicVolume = 0.2f;

    public AudioManager() {
        sounds = new Array<>();
        musics = new Array<>();

        audioManager = this;
    }

    public void loadSound(String name, String path) {
        if (getSound(name) == null) {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
            sounds.add(new SoundEntry(name, sound));

        }
    }

    private Sound getSound(String name) {
        for (int i = 0; i < sounds.size; i++) {
            SoundEntry entry = (SoundEntry) sounds.get(i);
            if (entry.getName().equals(name)) {
                return entry.getSound();
            }
        }
        return null;
    }

    public long playSound(String name) {
        return playSound(name,1.0f);
    }

    public long playSound(String name, float pitch) {
        Sound sound = getSound(name);
        if (sound != null) {
            return sound.play(soundVolume, pitch, 0);
        }
        return -1;
    }

    public void loadMusic(String name, String path) {
        if (getMusic(name) == null) {
            Music music = Gdx.audio.newMusic(Gdx.files.internal(path));
            musics.add(new MusicEntry(name, music));
        }
    }

    private Music getMusic(String name) {
        for (int i = 0; i < musics.size; i++) {
            MusicEntry entry = (MusicEntry) musics.get(i);
            if (entry.getName().equals(name)) {
                return entry.getMusic();
            }
        }
        return null;
    }

    public void playMusic(String name, boolean loop) {
        Music music = getMusic(name);
        if (music != null) {
            if (currentMusic != null && currentMusic.isPlaying()) {
                currentMusic.stop();
            }
            currentMusic = music;
            currentMusic.setLooping(loop);
            currentMusic.setVolume(musicVolume);
            currentMusic.play();
        }
    }

    public void stopMusic(String name) {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void setSoundVolume(float volume) {
        this.soundVolume = Math.max(0, Math.min(1, volume));
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0, Math.min(1, volume));
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
    }

    @Override
    public void dispose() {
        for (int i = 0; i < sounds.size; i++) {
            sounds.get(i).getSound().dispose();
        }
        for (int i = 0; i < musics.size; i++) {
            musics.get(i).getMusic().dispose();
        }
        sounds.clear();
        musics.clear();
        audioManager = null;
    }
}
