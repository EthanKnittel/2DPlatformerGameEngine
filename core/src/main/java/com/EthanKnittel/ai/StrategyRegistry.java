package com.EthanKnittel.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * Registre central des stratégies (Factory Pattern).
 * <p>
 * Cette classe sert de pont entre les données textuelles (venant de l'éditeur de niveau Tiled ou de JSON)
 * et le code Java. Elle permet d'instancier une classe de stratégie (ex: {@code PatrolStrategy})
 * à partir de son nom (ex: "Patrol").
 * </p>
 * <p>
 * C'est grâce à ce registre que vous pouvez ajouter une propriété personnalisée "Chase" sur un ennemi
 * dans Tiled, et que le jeu saura automatiquement lui donner le cerveau correspondant.
 * </p>
 */
public class StrategyRegistry {

    /** Liste 1 : Les noms (ex: "Patrol", "Chase") qui servent de clés de recherche. */
    private static Array<String> listeNoms = new Array<>();

    /** Liste 2 : Les classes Java correspondantes (ex: PatrolStrategy.class). */
    private static Array<Class> listeClasses = new Array<>();

    /*
     * Bloc statique d'initialisation.
     * C'est ici qu'on déclare les stratégies disponibles par défaut au démarrage du jeu.
     */
    static {
        register("Patrol", PatrolStrategy.class);
        register("Chase", ChaseStrategy.class);
        // Ajoutez vos futures stratégies ici
    }

    /**
     * Enregistre une nouvelle association Nom <-> Classe.
     * <p>
     * Cette méthode remplit les deux listes en parallèle.
     * </p>
     *
     * @param name      Le nom utilisé dans l'éditeur de niveau (ex: "Sniper").
     * @param className La classe Java qui doit être créée (ex: SniperStrategy.class).
     */
    public static void register(String name, Class className) {
        if (!exists(name)) {
            listeNoms.add(name);
            listeClasses.add(className);
        }
    }

    /**
     * Vérifie si une stratégie est connue dans le registre.
     *
     * @param name Le nom de la stratégie à tester.
     * @return {@code true} si le nom existe dans la liste, {@code false} sinon.
     */
    public static boolean exists(String name) {
        return listeNoms.contains(name, false);
    }

    /**
     * Crée une nouvelle instance de stratégie à partir de son nom (Factory Method).
     * <p>
     * Cette méthode utilise la <b>Réflexion Java</b> pour instancier la classe dynamiquement.
     * C'est l'équivalent de faire {@code new MaClasse()} mais sans connaître le nom de la classe à l'avance.
     * </p>
     *
     * @param name Le nom de la stratégie (ex: "Patrol").
     * @return Une nouvelle instance de {@link EnemyStrategy} prête à l'emploi, ou {@code null} en cas d'erreur.
     */
    public static EnemyStrategy create(String name) {
        // 1. On cherche à quel numéro (index) se trouve le nom dans la première liste
        int index = listeNoms.indexOf(name, false);

        // 2. Si on l'a trouvé (index n'est pas -1)
        if (index != -1) {
            // 3. On récupère la classe située au MÊME numéro dans l'autre liste
            Class laClasse = listeClasses.get(index);

            try {
                return (EnemyStrategy) laClasse.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                // En cas de crash (ex: la classe n'a pas de constructeur vide)
                Gdx.app.error("StrategyRegistry", "problème avec la classe de la Strategy", e);
            }
        }
        return null; // Rien trouvé ou erreur
    }
}
