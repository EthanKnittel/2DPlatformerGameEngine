package com.EthanKnittel.audio;

import com.badlogic.gdx.audio.Music;

/**
 * Conteneur simple associant un objet Musique à un nom (clé).
 * <p>
 * Cette classe est utilisée par l'{@link AudioManager} pour stocker les musiques chargées
 * dans une liste et les retrouver facilement par leur nom (ex: "MenuTheme") plutôt que par leur index.
 * </p>
 */
public class MusicEntry {

    /** Le nom clé utilisé pour jouer la musique (ex: "BossBattle"). */
    private final String name;

    /** L'objet technique LibGDX qui gère le streaming audio. */
    private final Music music;

    /**
     * Crée une nouvelle entrée.
     *
     * @param name  Le nom identifiant.
     * @param music L'instance de la musique chargée.
     */
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
