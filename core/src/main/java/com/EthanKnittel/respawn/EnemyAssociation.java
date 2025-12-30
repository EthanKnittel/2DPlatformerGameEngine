package com.EthanKnittel.respawn;

public class EnemyAssociation {
    private final String name;
    private final EnemyFactory factory;

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
