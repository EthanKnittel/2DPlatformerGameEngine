package com.EthanKnittel.game;

import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.inputs.KeyboardInput;
import com.EthanKnittel.inputs.MouseInput;
import com.badlogic.gdx.Input;

/**
 * Contrôleur du Joueur (Pattern Controller).
 * <p>
 * Cette classe fait le lien entre les périphériques d'entrée (Clavier, Souris)
 * et l'entité {@link Player}.
 * </p>
 * <p>
 * Elle sépare la logique d'input ("Quelle touche est appuyée ?") de la logique de jeu
 * ("Le personnage doit sauter"). Cela permet de changer facilement les contrôles
 * sans toucher au code du personnage.
 * </p>
 */
public class PlayerController {

    /** Le personnage contrôlé. */
    private Player player;

    /** Gestionnaire des entrées clavier. */
    private KeyboardInput keyboard;

    /** Gestionnaire des entrées souris. */
    private MouseInput mouse;

    /**
     * Constructeur.
     *
     * @param player   L'instance du joueur à piloter.
     * @param keyboard Le gestionnaire clavier actif.
     * @param mouse    Le gestionnaire souris actif.
     */
    public PlayerController(Player player, KeyboardInput keyboard, MouseInput mouse) {
        this.player = player;
        this.keyboard = keyboard;
        this.mouse = mouse;
    }

    /**
     * Met à jour les commandes du joueur.
     * Appelé à chaque frame par le {@link GameScreen}.
     *
     * @param delta Temps écoulé depuis la dernière frame (inutilisé ici mais souvent requis par convention).
     */
    public void update(float delta) {
        // On sépare la gestion du mouvement et des actions pour plus de clarté
        processMovementInput();
        processActionInput();
    }

    /**
     * Gère les déplacements horizontaux (Gauche / Droite).
     */
    private void processMovementInput() {
        // Vérification de la touche "Shift" pour courir
        boolean running = keyboard.isKeyDown(Input.Keys.SHIFT_LEFT);

        // 1. Gestion de l'arrêt (Si aucune touche ou les deux en même temps)
        if (!keyboard.isKeyDown(Input.Keys.A) && !keyboard.isKeyDown(Input.Keys.D)) {
            player.stopMovingX();
        }
        // Note : Si on appuie sur A et D en même temps, le dernier 'if' exécuté gagnera.
        // Ici, D gagnera car il est testé après. Pour un comportement plus strict (arrêt si A+D),
        // il faudrait ajouter une condition spécifique.

        // 2. Déplacement Gauche (Touche Q ou A selon clavier, ici A pour Qwerty/Azerty adapté)
        if (keyboard.isKeyDown(Input.Keys.A)) {
            player.moveLeft(running);
        }

        // 3. Déplacement Droite (Touche D)
        if (keyboard.isKeyDown(Input.Keys.D)) {
            player.moveRight(running);
        }
    }

    /**
     * Gère les actions ponctuelles (Saut, Tir).
     */
    private void processActionInput() {
        // SAUT (Espace)
        // On utilise isKeyDownNow() pour que le saut ne se déclenche qu'une seule fois
        // même si la touche reste enfoncée (pas de "saut mitraillette").
        if (keyboard.isKeyDownNow(Input.Keys.SPACE)) {
            player.jump();
        }

        // TIR (Clic Gauche - Bouton 0)
        // Idem, on utilise isButtonDownNow() pour du coup par coup (semi-auto).
        // Si on voulait une mitraillette, on utiliserait isButtonDown().
        if (mouse.isButtonDownNow(0)) {
            // On passe la position de la souris au joueur pour qu'il sache où viser
            player.shoot(mouse.GetPosX(), mouse.GetPosY());
        }
    }
}
