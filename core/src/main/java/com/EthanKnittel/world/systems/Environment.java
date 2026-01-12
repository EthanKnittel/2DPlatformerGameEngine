package com.EthanKnittel.world.systems;

import com.EthanKnittel.Evolving;
import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.world.Level;
import com.badlogic.gdx.utils.Array;

/**
 * Environnement de jeu (Simulateur / Monde).
 * <p>
 * Cette classe représente l'état global du monde à un instant T.
 * Elle contient :
 * <ul>
 * <li>Le {@link Level} actuel (la carte, les décors).</li>
 * <li>La liste de toutes les {@link Entity} actives (Joueur, Ennemis, Projectiles).</li>
 * <li>Le {@link PhysicSystem} qui gère les interactions physiques entre ces entités.</li>
 * </ul>
 * </p>
 * <p>
 * C'est ici que se trouve la boucle de mise à jour logique principale (via la méthode {@link #update(float)}).
 * </p>
 */
public class Environment implements Evolving {

    /** Le niveau actuellement chargé (Carte + Règles de spawn). */
    private Level currentlevel;

    /** Liste dynamique de toutes les entités présentes dans le monde. */
    private Array<Entity> entities;

    /** Le moteur physique qui gère les déplacements et collisions. */
    private PhysicSystem physics;

    /**
     * Constructeur de l'environnement.
     * Initialise les listes et les systèmes de base.
     */
    public Environment() {
        entities = new Array<>();
        // On initialise le système physique (sans niveau pour l'instant)
        physics = new PhysicSystem(null);
    }

    /**
     * Change le niveau actif du jeu.
     * <p>
     * Cette méthode s'occupe de :
     * <ol>
     * <li>Nettoyer l'ancien niveau (mémoire).</li>
     * <li>Vider la liste des entités (sauf si on voulait garder le joueur, mais ici on recharge tout).</li>
     * <li>Charger les nouvelles entités (Murs, Spawners) définies par le nouveau niveau.</li>
     * <li>Réinitialiser le moteur physique avec le nouveau contexte.</li>
     * </ol>
     * </p>
     *
     * @param level Le nouveau niveau à charger.
     */
    public void setLevel(Level level) {
        // 1. Nettoyage de l'ancien niveau
        if (this.currentlevel != null) {
            this.currentlevel.dispose();
            entities.clear();
        }

        // 2. Installation du nouveau niveau
        this.currentlevel = level;

        // On lie le moteur physique au nouveau niveau (utile si le niveau contient des données physiques globales)
        physics = new PhysicSystem(this.currentlevel);

        // 3. Chargement initial des entités du niveau (Murs, Décors)
        Array<Entity> levelEntities = this.currentlevel.load();
        this.entities.addAll(levelEntities);
    }

    /**
     * Ajoute une entité dynamique à la simulation en cours de jeu.
     * <p>
     * Utilisé pour faire apparaître :
     * <ul>
     * <li>Le Joueur (au début).</li>
     * <li>Des Projectiles (quand on tire).</li>
     * <li>Des Ennemis (via les SpawnZones).</li>
     * </ul>
     * </p>
     *
     * @param entity L'entité à ajouter.
     */
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    /**
     * Boucle principale de simulation (Game Loop Logique).
     * Appelé à chaque frame.
     *
     * @param deltaTime Temps écoulé depuis la dernière frame.
     */
    @Override
    public void update(float deltaTime) {

        // ÉTAPE 1 : Nettoyage
        // On parcourt la liste À L'ENVERS (i--) pour pouvoir supprimer des éléments
        // sans décaler les index des éléments suivants ni provoquer d'erreurs.
        for (int i = entities.size - 1; i >= 0; i--) {
            // Si une entité est marquée "à supprimer" (ex: PV <= 0, Projectile hors écran)
            if (entities.get(i).getCanBeRemove()) {
                entities.removeIndex(i);
            }
        }

        // ÉTAPE 2 : Mise à jour individuelle (IA, Comportement)
        // Chaque entité "réfléchit" ou exécute ses animations internes.
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);
            entity.update(deltaTime);
        }

        // ÉTAPE 3 : Résolution Physique (Déplacements & Collisions)
        // Une fois que tout le monde a décidé où aller, le système physique applique les règles
        // et empêche les objets de traverser les murs.
        physics.update(deltaTime, entities);
    }

    // --- GETTERS ---

    /**
     * Récupère la liste de toutes les entités.
     * Utile pour le {@link com.EthanKnittel.graphics.WorldRenderer} qui doit tout dessiner.
     */
    public Array<Entity> getEntities() {
        return entities;
    }

    public Level getLevel() {
        return currentlevel;
    }
}
