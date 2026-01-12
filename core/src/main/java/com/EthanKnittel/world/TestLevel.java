package com.EthanKnittel.world;

import com.EthanKnittel.entities.Entity;
import com.EthanKnittel.entities.artifacts.Wall;
import com.badlogic.gdx.utils.Array;

/**
 * Niveau de test généré manuellement via le code.
 * <p>
 * Contrairement à {@link TiledLevel} qui lit un fichier, cette classe crée
 * des murs et des plateformes mathématiquement. C'est idéal pour :
 * <ul>
 * <li>Tester les mécaniques (Saut, Collisions) dans un environnement contrôlé.</li>
 * <li>Avoir un niveau de secours (Fallback) si le chargement de la carte échoue.</li>
 * </ul>
 * </p>
 */
public class TestLevel extends Level {

    /**
     * Constructeur par défaut.
     * <p>
     * Ne fait rien de particulier à l'instanciation, la génération se fait dans {@link #load()}.
     * </p>
     */
    public TestLevel() {
        // vide
    }

    /**
     * Génère les entités du niveau.
     * <p>
     * Crée une carte composée de murs : un sol, un plafond et deux murs latéraux ainsi qu'un "trou" au plafond contre le mur latéral droit.
     * </p>
     *
     * @return La liste des murs générés.
     */
    @Override
    public Array<Entity> load() {
        Array<Entity> generatedEntities = new Array<>();

        // Taille d'un bloc de mur (arbitraire pour ce test)
        float wallSize = 4f;

        // 1. Génération du SOL (10 blocs de large)
        for (int i=0; i<10; i++){
            // new Wall(x, y, w, h, visible=true)
            generatedEntities.add(new Wall(i * wallSize,0, wallSize,wallSize, true));
        }

        // 2. Génération du PLAFOND (5 blocs de large, situé à une hauteur de 4*wallSize)
        for (int i=0; i<5; i++){
            generatedEntities.add(new Wall(i * wallSize,4 *  wallSize,wallSize,wallSize, true));
        }

        // 3. Génération du MUR GAUCHE (Vertical)
        for (int i=0; i<5; i++){
            generatedEntities.add(new Wall(0, i * wallSize,wallSize,wallSize, true));
        }

        // 4. Génération du MUR DROIT (Très haut, 50 blocs)
        // Situé à X = 10 * wallSize (donc à la fin du sol généré plus haut)
        for (int i=0; i<50; i++){
            generatedEntities.add(new Wall(wallSize * 10, i * wallSize,wallSize,wallSize, true));
        }

        return generatedEntities;

    }

    /**
     * Libération des ressources.
     * <p>
     * Ce niveau n'utilisant pas de ressources externes lourdes (comme une TiledMap),
     * cette méthode est vide.
     * </p>
     */
    @Override
    public void dispose() {
        // Rien à nettoyer pour ce niveau procédural
    }
}
