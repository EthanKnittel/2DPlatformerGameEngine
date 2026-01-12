package com.EthanKnittel.inputs;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import java.util.Arrays;

/**
 * Gestionnaire d'entrée Clavier personnalisé.
 * <p>
 * Cette classe implémente {@link InputProcessor} pour intercepter les événements clavier de LibGDX.
 * Elle stocke l'état de chaque touche dans des tableaux de booléens pour permettre
 * une interrogation facile (Polling) depuis la boucle de jeu.
 * </p>
 * <p>
 * <b>Pourquoi ne pas utiliser Gdx.input.isKeyPressed() directement ?</b><br>
 * LibGDX gère très bien l'état "maintenu" (isKeyPressed), mais gère moins facilement l'événement "Just Pressed"
 * (appuyé à cette frame précise). Cette classe résout ce problème avec {@code keysDownNow}.
 * </p>
 */
public class KeyboardInput implements InputProcessor {

    // Nombre de touches max géré par LibGDX (256 codes touches)
    private static final int MAX_KEYS = Input.Keys.MAX_KEYCODE + 1;

    /** État persistant : True tant que la touche est maintenue enfoncée. */
    private final boolean[] keysDown = new boolean[MAX_KEYS];

    /** État instantané : True seulement pendant la frame où la touche a été enfoncée. */
    private final boolean[] keysDownNow = new boolean[MAX_KEYS];

    public KeyboardInput() {
        // Tout est initialisé comme étant "non enfoncé" au démarrage
        Arrays.fill(keysDown, false);
        Arrays.fill(keysDownNow, false);
    }

    /**
     * Méthode à appeler à la fin de chaque frame (dans le GameScreen).
     * <p>
     * Elle réinitialise le tableau "Just Pressed" (keysDownNow) pour s'assurer
     * qu'un appui touche n'est valide que pour une seule frame.
     * </p>
     */
    public void update() {
        Arrays.fill(keysDownNow, false);
    }

    // --- MÉTHODES D'INTERROGATION (API) ---

    /**
     * Vérifie si une touche est physiquement maintenue enfoncée.
     * <p>Utilisation : Déplacements continus (marcher, courir).</p>
     *
     * @param keyCode Le code de la touche (ex: {@code Input.Keys.SPACE}).
     * @return true si la touche est enfoncé.
     */
    public boolean isKeyDown(int keyCode) {
        // Sécurité : on évite un crash ArrayOutOfBounds si le code est invalide
        if (keyCode < 0 || keyCode >= MAX_KEYS) {
            return false;
        }
        return keysDown[keyCode];
    }

    /**
     * Vérifie si une touche vient d'être enfoncée à cette frame précise.
     * <p>Utilisation : Actions uniques (Sauter, Tirer, Ouvrir menu).</p>
     *
     * @param keyCode Le code de la touche.
     * @return true seulement lors de la première frame de l'appui.
     */
    public boolean isKeyDownNow(int keyCode) {
        if (keyCode < 0 || keyCode >= MAX_KEYS) {
            return false;
        }
        return keysDownNow[keyCode];
    }

    // --- CALLBACKS LIBGDX (InputProcessor) ---
    // Ces méthodes sont appelées automatiquement par LibGDX quand un événement OS survient.

    @Override
    public boolean keyDown(int keyCode) {
        if (keyCode >= 0 && keyCode < MAX_KEYS) {
            keysDown[keyCode] = true;      // On note qu'elle est maintenue
            keysDownNow[keyCode] = true;   // On note qu'elle vient d'être pressée (sera reset au prochain update)
        }
        return true; // true signifie "J'ai traité l'événement, ne le passez pas aux autres processeurs"
    }

    @Override
    public boolean keyUp(int keyCode) {
        if (keyCode >= 0 && keyCode < MAX_KEYS) {
            keysDown[keyCode] = false; // On a "définitivement" relâché la touche
            // Note : on ne touche pas à keysDownNow ici, car l'événement "Pressed" a déjà été consommé.
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false; // On ne gère pas la saisie de texte (ex: taper son nom)
    }

    // --- SOURIS (Non géré ici, voir MouseInput) ---

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
