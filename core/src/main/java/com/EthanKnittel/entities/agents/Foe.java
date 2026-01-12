package com.EthanKnittel.entities.agents;

import com.EthanKnittel.ai.EnemyStrategy;
import com.EthanKnittel.ai.PatrolStrategy;
import com.EthanKnittel.entities.Agent;
import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.artifacts.Wall;
import com.EthanKnittel.save.SaveManager;
import com.EthanKnittel.score.ScoreManager;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Classe de base pour tous les ennemis (Opposants).
 * <p>
 * Un Foe (Ennemi) est un Agent piloté par une Intelligence Artificielle (Strategy Pattern).
 * Il possède une cible (le Joueur) et une liste d'entités pour analyser son environnement (vue).
 * </p>
 * <p>
 * Cette classe gère aussi les récompenses (Score) et les statistiques de kill lors de la mort.
 * </p>
 */
public abstract class Foe extends Agent {
    // --- IA & CERVEAU ---
    /** La stratégie actuelle qui contrôle les mouvements (Patrol, Chase, etc.). */
    private EnemyStrategy strategy;

    /** La cible principale de l'ennemi (généralement le Joueur). */
    private Player target;

    /** Liste de toutes les entités du monde. Nécessaire pour vérifier les obstacles (Ligne de vue). */
    private Array<Entity> allEntities;

    /** Vrai si l'ennemi touche un autre ennemi. Utilisé par l'IA pour la séparation. */
    private boolean touchingAlly = false;

    // --- IDENTITÉ & SCORE ---
    /** Nom utilisé pour les statistiques (ex: "Cactus", "Ordi"). */
    private String enemyName = "Unknown";

    /** Points par défaut donnés au joueur lors de l'élimination. */
    private int scoreValue = 100;

    /** Flag pour s'assurer qu'on ne donne les points qu'une seule fois, même si l'animation de mort dure plusieurs frames. */
    private boolean scoreAwarded = false;


    /**
     * Constructeur de base pour un Ennemi.
     *
     * @param x           Position X initiale.
     * @param y           Position Y initiale.
     * @param width       Largeur de la hitbox.
     * @param height      Hauteur de la hitbox.
     * @param target      Référence au joueur à pourchasser.
     * @param allEntities Référence au monde (pour voir les murs).
     */
    public Foe(float x, float y, float width, float height, int maxHealth, int damage, Player target, Array<Entity> allEntities) {
        super(x, y, width, height, maxHealth, damage);
        this.target = target;
        this.allEntities = allEntities;

        // Configuration par défaut
        this.setIsEnemy(true); // Tag pour le système de collisions (PhysicSystem)
        this.setStrategy(new PatrolStrategy()); // Comportement par défaut si aucun cerveau (BrainStrategy) n'est donné
    }

    // --- GESTION DE L'IA ---

    public void setStrategy(EnemyStrategy strategy){
        this.strategy = strategy;
    }

    public void setTouchingAlly(boolean isTouching) {
        this.touchingAlly = isTouching;
    }
    public boolean getTouchingAlly() {
        return this.touchingAlly;
    }


    /**
     * Vérifie si l'ennemi a une vue dégagée sur le joueur (Raycasting).
     * <p>
     * Cette méthode trace une ligne imaginaire entre le centre de l'ennemi et le centre du joueur.
     * Si cette ligne coupe un Mur (Wall), la vue est bloquée.
     * </p>
     *
     * @param player La cible à tester.
     * @return true si aucun mur ne bloque la vue, false sinon.
     */
    public boolean hasLineOfSight(Player player){
        if (allEntities == null){
            return true; // Si pas d'info sur le monde, on suppose qu'on voit tout (sécurité)
        }
        // 1. Calcul du point de départ (Centre de l'ennemi)
        Vector2 start = new Vector2(getX()+ getbounds().width / 2, getY() + getbounds().height / 2);

        // 2. Calcul du point d'arrivée (Centre du joueur)
        Vector2 end = new Vector2(player.getX() + player.getbounds().width / 2, player.getY() + player.getbounds().height / 2);

        // 3. Test d'intersection avec tous les murs
        for (Entity entity : allEntities){
            if (entity.getClass().equals(Wall.class)){
                // Intersector est un outil mathématique de LibGDX
                if (Intersector.intersectSegmentRectangle(start, end, entity.getbounds())){
                    return false; // Un mur coupe la ligne -> Pas de vue
                }
            }
        }
        return true; // Aucun obstacle trouvé
    }


    /**
     * Boucle principale de mise à jour de l'ennemi.
     */
    @Override
    public void update(float deltaTime) {
        // 1. Mise à jour des status d'Agent (Invincibilité, Hit Stun...)
        super.update(deltaTime);

        // 2. GESTION DE LA MORT
        if (!getAlive()) {
            setInvincibilityDuration(99f); // Empêche de rejouer l'animation "Hit" pendant l'agonie

            // A. Suppression différée : On attend la fin de l'animation de mort
            // (La classe Vue utilisera isHit() ou une anim spécifique pour savoir quand finir)
            if (!isHit()) {
                this.setCanBeRemove(true); // Signal pour pouvoir supprimer l'entité
            }

            // B. Attribution des récompenses (une seule fois)
            if (!scoreAwarded) {
                if (ScoreManager.getScoreManager() != null){
                    ScoreManager.getScoreManager().addScore(scoreValue);
                }

                if (SaveManager.instance != null) {
                    SaveManager.instance.addKillCount(enemyName);
                }
                scoreAwarded = true; // On verrouille pour ne pas redonner 100 points à la frame suivante
            }
            // C. Arrêt total : Un cadavre ne réfléchit plus et ne bouge plus.
            return;
        }

        // 3. EXÉCUTION DE L'IA (Si vivant)
        if (strategy !=null && target != null){
            // On demande au cerveau : "Où dois-je aller ?"
            Vector2 command = strategy.calculateMove(this,deltaTime);

            // On applique la volonté du cerveau à la physique
            this.setVelocityX(command.x);

            // Si le cerveau veut sauter (y != 0), on applique. Sinon on laisse la gravité gérer le Y.
            if (command.y !=0){
                this.setVelocityY(command.y);
            }
        }

        // 4. Orientation du sprite (Feedback visuel)
        if (getVelocity().x < 0){
            setFacingLeft(true);
        } else if (getVelocity().x > 0){
            setFacingLeft(false);
        }
    }

    /**
     * Demande à la stratégie si la répulsion physique entre ennemis doit être active.
     * (Ex: Oui en chasse pour ne pas s'empiler, Non en patrouille pour se croiser fluidement).
     */
    public boolean shouldUseRepulsion() {
        if (this.strategy != null) {
            return this.strategy.enableSeparation();
        }
        return true;
    }

    // --- GETTERS & SETTERS SCORE ---

    public Player getTarget() {
        return target;
    }

    public int getScoreValue() {
        return scoreValue;
    }
    public void setScoreValue(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public void setEnemyName(String enemyName) {
        this.enemyName = enemyName;
    }
}
