package com.EthanKnittel.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Gestionnaire centralisé de l'audio (Sons et Musiques).
 * <p>
 * Cette classe permet de charger, jouer et gérer le volume de tous les assets audio du jeu.
 * Elle fait la distinction fondamentale entre :
 * <ul>
 * <li><b>Sound (Bruitages) :</b> Courts, chargés entièrement en RAM. Peuvent être joués plusieurs fois en même temps (ex: tir, saut).</li>
 * <li><b>Music (Musiques) :</b> Longues, lues en streaming depuis le disque. Une seule instance à la fois généralement (ex: musique de fond).</li>
 * </ul>
 * </p>
 */
public class AudioManager implements Disposable {

    /**
     * Instance statique globale (Singleton simplifié).
     * Permet d'accéder à l'AudioManager depuis n'importe où dans le code (ex: depuis le Player pour jouer un son de saut)
     * via {@code AudioManager.audioManager.playSound(...)}.
     */
    public static AudioManager instance;

    // Listes pour stocker les références vers les fichiers chargés
    private Array<SoundEntry> sounds;
    private Array<MusicEntry> musics;

    /** La musique en train d'être jouée actuellement (pour pouvoir l'arrêter ou changer son volume). */
    private Music currentMusic;

    // Volumes globaux (0.0 à 1.0)
    private float soundVolume = 1.0f;
    private float musicVolume = 0.1f;

    private AudioManager() {
        sounds = new Array<>();
        musics = new Array<>();
    }

    public static AudioManager getInstance() {
        if (AudioManager.instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    // --- GESTION DES BRUITAGES (SOUNDS) ---

    /**
     * Charge un effet sonore en mémoire.
     * Si le son existe déjà, il n'est pas rechargé.
     *
     * @param name Nom clé pour retrouver le son plus tard (ex: "jump").
     * @param path Chemin du fichier (ex: "audio/jump.wav").
     */
    public void loadSound(String name, String path) {
        if (getSound(name) == null) {
            // Gdx.audio.newSound charge tout le fichier en RAM (décompression immédiate).
            // Attention : Ne pas utiliser pour des musiques longues (> 10 sec)
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

    /**
     * Joue un bruitage avec le volume par défaut et un pitch normal.
     * @return L'ID du son joué, ou -1 en cas d'erreur.
     */
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

    // --- GESTION DES MUSIQUES (MUSIC) ---

    /**
     * Prépare une musique (Streaming).
     *
     * @param name Nom clé.
     * @param path Chemin du fichier.
     */
    public void loadMusic(String name, String path) {
        if (getMusic(name) == null) {
            // newMusic ne charge pas le fichier, il ouvre juste un flux de lecture.
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

    /**
     * Lance la lecture d'une musique de fond.
     * Gère automatiquement l'arrêt de la musique précédente.
     *
     * @param name Nom de la musique.
     * @param loop Si true, la musique recommencera au début une fois finie.
     */
    public void playMusic(String name, boolean loop) {
        Music music = getMusic(name);
        if (music != null) {
            // Si une autre musique joue, on l'arrête proprement
            if (currentMusic != null && currentMusic.isPlaying()) {
                currentMusic.stop();
            }
            currentMusic = music;
            currentMusic.setLooping(loop);
            currentMusic.setVolume(musicVolume); // On applique le volume global
            currentMusic.play();
        }
    }

    public void stopMusic(String name) {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    // --- VOLUME & REGLAGES ---

    public void setSoundVolume(float volume) {
        // Clamp (bornage) pour s'assurer que le volume reste entre 0% et 100%
        this.soundVolume = Math.max(0, Math.min(1, volume));
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0, Math.min(1, volume));
        // Si une musique joue déjà, on met à jour son volume en temps réel
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
    }

    /**
     * Nettoyage de la mémoire (Obligatoire dans LibGDX).
     * Appelé quand on quitte le jeu ou l'écran.
     */
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

        // On détruit la référence statique pour éviter les fuites si on recharge le jeu
        instance = null;
    }
}
