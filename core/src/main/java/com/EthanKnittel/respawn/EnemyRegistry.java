package com.EthanKnittel.respawn;

import com.badlogic.gdx.utils.Array;

/**
 * Registre central des types d'ennemis (Pattern Registry).
 * <p>
 * Cette classe maintient une liste statique de toutes les associations connues entre
 * un Nom (String) et une Usine (Factory).
 * </p>
 * <p>
 * Elle permet au {@link com.EthanKnittel.game.LevelManager} ou au {@link com.EthanKnittel.world.TiledLevel}
 * de demander "Donne-moi l'usine pour fabriquer un 'Cactus'", sans avoir à connaître la classe {@code CactusFactory}.
 * </p>
 */
public class EnemyRegistry {

    // Liste statique contenant toutes les paires (Nom <-> Usine)
    private static final Array<EnemyAssociation> data = initializeList();

    /**
     * Initialise la liste des ennemis disponibles dans le jeu.
     * <p>
     * C'est ici qu'on "déclare" les monstres. Si on crée un nouveau monstre (ex: Zombie),
     * on doit l'ajouter ici pour qu'il soit reconnu par le moteur.
     * </p>
     *
     * @return La liste initialisée.
     */
    private static Array<EnemyAssociation> initializeList(){
        Array<EnemyAssociation> list = new Array<>();

        // Enregistrement des monstres
        list.add(new EnemyAssociation("Cactus", new CactusFactory()));
        list.add(new EnemyAssociation("Ordi", new OrdiFactory()));
        // Ajoutez des futurs monstres ici :
        // list.add(new EnemyAssociation("Zombie", new ZombieFactory())); par exemple

        return list;
    }

    /**
     * Recherche et renvoie l'usine correspondant à un nom donné.
     *
     * @param name Le nom de l'ennemi (ex: "Cactus").
     * @return L'usine correspondante (ex: instance de {@link CactusFactory}), ou {@code null} si inconnu.
     */
    public static EnemyFactory getFactory(String name){
        for (int i=0; i<data.size; i++){
            EnemyAssociation association = data.get(i);
            if (association.getName().equals(name)){
                return association.getFactory();
            }
        }
        return null;
    }

    /**
     * Renvoie une usine par défaut (généralement la première de la liste).
     * <p>
     * Utile pour éviter les crashs si un spawner est mal configuré :
     * "Je ne sais pas ce que tu veux, tiens prends un Cactus c'est mieux que rien."
     * </p>
     *
     * @return Une usine valide, ou {@code null} si la liste est vide.
     */
    public static EnemyFactory getDefaultFactory(){
        if (data.size > 0){
            return data.get(0).getFactory(); // Par défaut : Le premier (Cactus)
        }
        return null;
    }

    /**
     * Renvoie la liste complète de tous les ennemis connus.
     * <p>
     * Utilisé par le {@link com.EthanKnittel.world.TiledLevel} pour scanner la carte
     * à la recherche de tous les types de spawns possibles.
     * </p>
     */
    public static Array<EnemyAssociation> getAllAssociations() {
        return data;
    }
}
