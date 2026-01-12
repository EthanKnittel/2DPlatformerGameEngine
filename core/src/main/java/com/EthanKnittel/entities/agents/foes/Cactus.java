package com.EthanKnittel.entities.agents.foes;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.game.GameScreen;
import com.badlogic.gdx.utils.Array;

/**
 * Entité représentant l'ennemi "Cactus".
 * <p>
 * Contrairement à l'{@link Ordi}, le Cactus est un ennemi plus puissant :
 * il possède le même nombre de PV mais inflige des dégâts plus importants (15 PV par coup).
 * </p>
 */
public class Cactus extends Foe {

    /**
     * Crée une nouvelle instance de l'ennemi Cactus.
     *
     * @param x           Position X initiale.
     * @param y           Position Y initiale.
     * @param target      Le joueur (cible pour l'IA).
     * @param allentities Liste des entités du monde (pour la vision de l'IA).
     */
    public Cactus(float x, float y, Player target, Array<Entity> allentities) {
        // Appel au constructeur parent (Foe).
        // Paramètres : x, y, Largeur (32px), Hauteur (32px), PV Max (50), Dégâts (15)
        // Note : Dégâts plus élevés (15). Un joueur (100 PV) meurt en 7 coups.
        super(x,y, 32f/ GameScreen.getPixelsPerBlocks(), 32f/GameScreen.getPixelsPerBlocks(), 50, 15, target, allentities);

        // --- CONFIGURATION DU RESSENTI (Game Feel) ---
        // Configuration standard pour les ennemis : récupération rapide pour permettre les combos du joueur.
        this.setHitStunDuration(0.4f);
        this.setInvincibilityDuration(0.1f);
        this.setVisualHitDuration(0.4f);

        // --- IDENTITÉ & RÉCOMPENSES ---
        // Score plus faible (50) que l'Ordi.
        setScoreValue(50);
        setEnemyName("Cactus");

        // --- PHYSIQUE ---
        // Même mobilité que les autres ennemis de base.
        this.setMoveSpeed(150f/GameScreen.getPixelsPerBlocks());
        this.setJumpSpeed(400f/GameScreen.getPixelsPerBlocks());
    }
}
