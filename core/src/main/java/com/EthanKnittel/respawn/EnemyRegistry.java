package com.EthanKnittel.respawn;

import com.badlogic.gdx.utils.Array;

public class EnemyRegistry {
    private static final Array<EnemyAssociation> data = initializeList();

    private static Array<EnemyAssociation> initializeList(){
        Array<EnemyAssociation> list = new Array<>();
        list.add(new EnemyAssociation("Cactus", new CactusFactory()));
        list.add(new EnemyAssociation("Ordi", new OrdiFactory()));

        return list;
    }

    public static EnemyFactory getFactory(String name){
        for (int i=0; i<data.size; i++){
            EnemyAssociation association = data.get(i);
            if (association.getName().equals(name)){
                return association.getFactory();
            }
        }
        return null;
    }

    public static EnemyFactory getDefaultFactory(){
        if (data.size > 0){
            return data.get(0).getFactory();
        }
        return null;
    }

    public static Array<EnemyAssociation> getAllAssociations() {
        return data;
    }
}
