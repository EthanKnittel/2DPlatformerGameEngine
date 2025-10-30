package com.EthanKnittel.inputs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

import java.util.Arrays;

public class MouseInput implements InputProcessor {
    // LibGDX gère 5 touches: 0=gauche / 1=droite / 2=milieu / 3=arrière et 4=avant
    private static final int Max_Buttons = 5;

    // liste pour gérer les touches enfoncées
    private final boolean[] buttonsDown = new boolean[Max_Buttons]; // enfoncé
    private final boolean[] buttonsDownNow = new boolean[Max_Buttons]; // vient d'être enfoncé lors de la frame

    // Position actuelle de la souris
    private int PosX;
    private int PosY;

    //Position de la souris à la frame précédente
    private int LastPosX;
    private int LastPosY;

    // état du scroll
    private int scrollX;
    private int scrollY;

    public MouseInput() {
        Arrays.fill(buttonsDown, false);    // On initialise les listes
        Arrays.fill(buttonsDownNow, false); // avec des boutons relâchés
        // on initialise toutes les variables à 0
        this.PosX = 0;
        this.PosY = 0;
        this.LastPosX = 0;
        this.LastPosY = 0;
        this.scrollX = 0;
        this.scrollY = 0;
    }

    public void update(){
        Arrays.fill(buttonsDownNow, false); // nouvelle frame tout à false
        // on remet le scroll à 0
        this.scrollX=0;
        this.scrollY=0;
    }

    private void updatePosition(int screenX, int screenY){
        // On enregistre l'ancienne position
        this.LastPosX = this.PosX;
        this.LastPosY = this.PosY;
        // on met en place la nouvelle position
        this.PosX = screenX;
        this.PosY = screenY;
    }

    // méthode qui gère les clics de souris
    public boolean isButtonDown(int button){
        // simple prévention pour éviter un crash si une touche
        // ne fait pas partie de celles prises en compte
        if (button < 0 || button >= Max_Buttons) {
            return false;
        }
        return buttonsDown[button];
    }

    public boolean isButtonDownNow(int button){
        if (button < 0 || button >= Max_Buttons) {
            return false;
        }
        return buttonsDownNow[button];

    }

    // pour si on veut faire un ctrl + clic par exemple
    // on n'utilise pas notre KeyboardInput car on les veut indépendant
    public boolean isButtonDownMods(int button, int modKey){
        return isButtonDown(button) && Gdx.input.isKeyPressed(modKey);
    }

    public boolean isButtonDownNowMods(int button, int modKey){
        return isButtonDownNow(button) && Gdx.input.isKeyPressed(modKey);
    }

    // Getter
    public int GetPosX(){
        return this.PosX;
    }
    public int GetPosY(){
        return this.PosY;
    }
    public int GetLastPosX(){
        return this.LastPosX;
    }
    public int GetLastPosY(){
        return this.LastPosY;
    }
    public int GetScrollX(){
        return this.scrollX;
    }
    public int GetScrollY(){
        return this.scrollY;
    }

    // Override des méthode de l'interface

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        updatePosition(screenX, screenY);
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
            buttonsDown[button] = false;
        }
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
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
        this.scrollX = (int) amountX; // on force les paramètres à être des int
        this.scrollY = (int) amountY;
        return true;
    }

    // méthodes lié au clavier non pris en compte ici
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

}
