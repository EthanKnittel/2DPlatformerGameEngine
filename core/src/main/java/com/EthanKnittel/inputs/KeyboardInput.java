package com.EthanKnittel.inputs;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import java.util.Arrays;


public class KeyboardInput implements InputProcessor {

    // Nombre de touches max géré par LibGDX
    private static final int MAX_KEYS = Input.Keys.MAX_KEYCODE + 1;
    // Liste de booléen pour savoir si chaque touche est enfoncé
    private final boolean[] keysDown = new boolean[MAX_KEYS];
    // Liste de booléen pour savoir si chaque touche vient d'être enfoncé lors de la frame
    private final boolean[] keysDownNow = new boolean[MAX_KEYS];

    public KeyboardInput() {
        // Tout est initialisé comme étant "non enfoncé"
        Arrays.fill(keysDown, false);
        Arrays.fill(keysDownNow, false);
    }

    public void update() {
        // On réinitialise le tableau "keysDownNow" à chaque frames
        Arrays.fill(keysDownNow, false);
    }

    // méthode vérifiant si une touche est enfoncé
    public boolean isKeyDown(int keyCode) {
        // simple prévention pour éviter un crash si une touche
        // ne fait pas partie de celles prises en compte
        if (keyCode < 0 || keyCode >= MAX_KEYS) {
            return false;
        }
        return keysDown[keyCode];
    }

    // méthode vérifiant si une touche est enfoncé lors de la frame en cours
    public boolean isKeyDownNow(int keyCode) {
        if (keyCode < 0 || keyCode >= MAX_KEYS) {
            return false;
        }
        return keysDownNow[keyCode];
    }

    //méthode pour gérer quand on presse une touche
    @Override
    public boolean keyDown(int keyCode) {
        if (keyCode >= 0 && keyCode < MAX_KEYS) {
            keysDown[keyCode] = true;
            keysDownNow[keyCode] = true; // pour cette frame
        }
        return true;
    }

    //méthode pour gérer quand on relâche une touche
    @Override
    public boolean keyUp(int keyCode) {
        if (keyCode >= 0 && keyCode < MAX_KEYS) {
            keysDown[keyCode] = false; // on a "définitivement" relâché la touche
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false; // on ne le gèrera pas
    }

    //les méthodes suivantes sont lié à la souris, non géré ici
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
