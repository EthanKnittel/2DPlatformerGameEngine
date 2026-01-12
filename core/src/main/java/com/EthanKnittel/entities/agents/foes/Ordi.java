package com.EthanKnittel.entities.agents.foes;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.game.GameScreen;
import com.badlogic.gdx.utils.Array;

/**
 * Entité représentant l'ennemi "Ordi".
 * <p>
 * C'est un ennemi standard, souvent utilisé comme "sbire" de base.
 * Il possède des caractéristiques équilibrées (Vitesse moyenne, Dégâts faibles, HP moyens).
 * </p>
 */
public class Ordi extends Foe {

    /**
     * Crée une nouvelle instance de l'ennemi Ordi.
     *
     * @param x           Position X initiale.
     * @param y           Position Y initiale.
     * @param target      Le joueur (cible pour l'IA).
     * @param allentities Liste des entités du monde (pour la vision de l'IA).
     */
    public Ordi(float x, float y, Player target, Array<Entity> allentities) {
        // Appel au constructeur parent (Foe) pour initialiser la physique de base.
        // Paramètres : x, y, Largeur (32px), Hauteur (32px), PV Max (50), Dégâts (5)
        // Note : On divise par PixelsPerBlocks pour convertir les pixels en mètres/blocs.
        super(x, y, 32f / GameScreen.getPixelsPerBlocks(), 32f / GameScreen.getPixelsPerBlocks(), 50, 5, target, allentities);

        // --- CONFIGURATION DU RESSENTI (Game Feel) ---

        // Durée pendant laquelle l'Ordi est figé après avoir pris un coup.
        // 0.4s permet au joueur d'enchaîner quelques coups sans riposte immédiate.
        this.setHitStunDuration(0.4f);

        // Durée d'invincibilité après un coup.
        // Très court (0.1s) pour permettre au joueur de "spammer" ses attaques s'il le souhaite.
        this.setInvincibilityDuration(0.1f);

        // Durée de l'animation de prise de dégats.
        this.setVisualHitDuration(0.4f);

        // --- IDENTITÉ & RÉCOMPENSES ---
        // Rapporte plus de points qu'un Cactus car il y en a été décidé ainsi.
        setScoreValue(150);
        setEnemyName("Ordi"); // Nom clé pour les statistiques (SaveManager)

        // --- PHYSIQUE SPÉCIFIQUE ---
        // Vitesse de marche standard
        this.setMoveSpeed(150f / GameScreen.getPixelsPerBlocks());

        // Capacité de saut standard (assez pour passer un bloc)
        this.setJumpSpeed(400f / GameScreen.getPixelsPerBlocks());
    }
}
