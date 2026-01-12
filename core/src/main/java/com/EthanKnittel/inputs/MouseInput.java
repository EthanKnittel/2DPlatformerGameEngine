package com.EthanKnittel.inputs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

import java.util.Arrays;

/**
 * Gestionnaire d'entrée Souris personnalisé.
 * <p>
 * Cette classe intercepte et stocke l'état de la souris (Boutons, Position, Molette)
 * pour permettre une utilisation facile dans la boucle de jeu.
 * </p>
 * <p>
 * Elle gère :
 * <ul>
 * <li><b>Les Clics :</b> État maintenu vs Clic instantané.</li>
 * <li><b>La Position :</b> Coordonnées écran (pixels) actuelles et précédentes.</li>
 * <li><b>Le Scroll :</b> Défilement de la molette.</li>
 * </ul>
 * </p>
 */
public class MouseInput implements InputProcessor {

    // LibGDX gère 5 boutons : 0=Gauche, 1=Droite, 2=Milieu, 3=Arrière, 4=Avant
    private static final int Max_Buttons = 5;

    // --- ÉTATS DES BOUTONS ---
    /** Vrai tant que le bouton est maintenu enfoncé. */
    private final boolean[] buttonsDown = new boolean[Max_Buttons];

    /** Vrai uniquement lors de la frame où le bouton a été cliqué. */
    private final boolean[] buttonsDownNow = new boolean[Max_Buttons];

    // --- POSITION CURSEUR (Pixels Écran) ---
    // Origine (0,0) = Coin Haut-Gauche de la fenêtre.
    private int PosX;
    private int PosY;

    // Position à la frame précédente (Utile pour calculer la vitesse de la souris ou le "Drag")
    private int LastPosX;
    private int LastPosY;

    // --- MOLETTE (Scroll) ---
    private int scrollX;
    private int scrollY;

    public MouseInput() {
        Arrays.fill(buttonsDown, false);    // Initialisation : aucun bouton pressé
        Arrays.fill(buttonsDownNow, false);

        this.PosX = 0;
        this.PosY = 0;
        this.LastPosX = 0;
        this.LastPosY = 0;
        this.scrollX = 0;
        this.scrollY = 0;
    }

    /**
     * Méthode de mise à jour à appeler à chaque frame (fin de boucle).
     * <p>
     * Elle nettoie les événements uniques (Clics instantanés, Scroll) pour qu'ils ne soient
     * traités qu'une seule fois.
     * </p>
     */
    public void update(){
        Arrays.fill(buttonsDownNow, false); // Reset des clics "Just Pressed"

        // Reset du scroll (la molette ne "reste" pas roulée, c'est un événement ponctuel)
        this.scrollX = 0;
        this.scrollY = 0;
    }

    /**
     * Met à jour la position interne de la souris.
     * Appelé automatiquement par les événements LibGDX (touchDown, touchDragged, etc.).
     */
    private void updatePosition(int screenX, int screenY){
        // On sauvegarde l'ancienne position avant de la remplacer
        this.LastPosX = this.PosX;
        this.LastPosY = this.PosY;

        // Mise à jour actuelle
        this.PosX = screenX;
        this.PosY = screenY;
    }

    // --- API PUBLIQUE (Pour le jeu) ---

    /**
     * Vérifie si un bouton est maintenu enfoncé (ex: Tir automatique).
     * @param button Code du bouton (0=Gauche, 1=Droite...).
     */
    public boolean isButtonDown(int button){
        if (button < 0 || button >= Max_Buttons) {
            return false;
        }
        return buttonsDown[button];
    }

    /**
     * Vérifie si un bouton vient d'être cliqué (ex: Tir coup par coup, Sélection menu).
     * @param button Code du bouton.
     */
    public boolean isButtonDownNow(int button){
        if (button < 0 || button >= Max_Buttons) {
            return false;
        }
        return buttonsDownNow[button];
    }

    // Combinaisons Clavier + Souris (ex: Ctrl + Clic)
    // Utile pour des commandes avancées (glisser un objet par exemple).
    public boolean isButtonDownMods(int button, int modKey){
        return isButtonDown(button) && Gdx.input.isKeyPressed(modKey);
    }

    public boolean isButtonDownNowMods(int button, int modKey){
        return isButtonDownNow(button) && Gdx.input.isKeyPressed(modKey);
    }

    // Getters de Position
    public int GetPosX() { return this.PosX; }
    public int GetPosY() { return this.PosY; }
    public int GetLastPosX() { return this.LastPosX; }
    public int GetLastPosY() { return this.LastPosY; }

    // Getters de Scroll
    public int GetScrollX() { return this.scrollX; }
    public int GetScrollY() { return this.scrollY; }

    // --- CALLBACKS LIBGDX ---

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        updatePosition(screenX, screenY); // On met à jour la pos même au clic
        if (button >= 0 && button < Max_Buttons) {
            buttonsDown[button] = true;
            buttonsDownNow[button] = true;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        updatePosition(screenX, screenY);
        if (button >= 0 && button < Max_Buttons) {
            buttonsDown[button] = false; // Relâché
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        updatePosition(screenX, screenY);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        updatePosition(screenX, screenY);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        this.scrollX = (int) amountX;
        this.scrollY = (int) amountY;
        return true;
    }

    // Méthodes clavier ignorées (gérées par KeyboardInput)
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
}
