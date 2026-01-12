package com.EthanKnittel.audio;

import com.badlogic.gdx.audio.Sound;

/**
 * Conteneur simple associant un objet Son (Bruitage) à un nom (clé).
 * <p>
 * Cette classe permet de stocker les effets sonores (chargés en RAM via {@link Sound})
 * avec un identifiant lisible (ex: "JumpEffect", "Explosion").
 * </p>
 */

public class SoundEntry {

    /** Le nom clé utilisé pour jouer le son (ex: "Shoot"). */
    private final String name;

    /** L'objet technique LibGDX qui contient les données audio décompressées en mémoire. */
    private final Sound sound;

    /**
     * Crée une nouvelle entrée.
     *
     * @param name  Le nom identifiant.
     * @param sound L'instance du son chargé.
     */
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
