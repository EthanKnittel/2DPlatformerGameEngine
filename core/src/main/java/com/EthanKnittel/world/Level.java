package com.EthanKnittel.world;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Array;
import com.EthanKnittel.entities.Entity;

/**
 * Classe de base abstraite représentant un niveau de jeu (Carte).
 * <p>
 * Un {@code Level} est responsable de la création et du chargement initial de toutes les entités
 * qui composent le monde (Murs, Ennemis, Objets, Point de départ du joueur).
 * </p>
 * <p>
 * Elle implémente l'interface {@link Disposable} de LibGDX, ce qui impose à tous les niveaux
 * de fournir une méthode pour nettoyer leurs ressources (ex: décharger la TiledMap) quand on change de niveau.
 * </p>
 * <p>
 * Voir les implémentations :
 * <ul>
 * <li>{@link com.EthanKnittel.world.TiledLevel} : Pour les niveaux créés avec l'éditeur Tiled (.tmx).</li>
 * <li>{@link com.EthanKnittel.world.TestLevel} : Pour les niveaux de test générés manuellement via du code pur.</li>
 * </ul>
 * </p>
 */
public abstract class Level implements Disposable {

    /**
     * Charge le contenu du niveau et retourne la liste des entités à ajouter au monde.
     * <p>
     * Cette méthode est appelée par {@link com.EthanKnittel.world.systems.Environment}
     * au moment de l'initialisation du niveau.
     * </p>
     *
     * @return Une {@link Array} contenant toutes les entités (Murs, Spawners, etc.) prêtes à l'emploi.
     */
    public abstract Array<Entity> load();

    // Note : La méthode void dispose() est héritée de l'interface Disposable.
    // C'est le contrat qui force les sous-classes (TiledLevel) à le faire.
}
