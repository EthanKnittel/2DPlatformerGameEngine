package com.EthanKnittel.respawn;

/**
 * Classe conteneur (Wrapper) associant un Nom à une Usine d'ennemis.
 * <p>
 * Elle sert de brique de base pour le {@link EnemyRegistry}.
 * Elle permet de dire : "Quand le jeu voit le texte 'Cactus' (name), il doit utiliser
 * l'usine {@link CactusFactory} (factory)."
 * </p>
 */
public class EnemyAssociation {

    /** Le nom identifiant l'ennemi (ex: "Cactus"). Doit correspondre à la propriété Tiled. */
    private final String name;

    /** L'usine capable de fabriquer cet ennemi. */
    private final EnemyFactory factory;

    /**
     * Crée une nouvelle association.
     *
     * @param name    Le nom clé.
     * @param factory L'instance de l'usine.
     */
    public EnemyAssociation(String name, EnemyFactory factory) {
        this.name = name;
        this.factory = factory;
    }

    public String getName() {
        return name;
    }

    public EnemyFactory getFactory() {
        return factory;
    }
}
