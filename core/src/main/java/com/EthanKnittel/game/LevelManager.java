package com.EthanKnittel.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import java.util.Random;

/**
 * Gestionnaire de rotation des niveaux (Map Pool).
 * <p>
 * Cette classe est responsable de la sélection dynamique des cartes à jouer.
 * Au lieu d'avoir une liste fixe (Niveau 1 -> Niveau 2 -> Niveau 3), elle scanne
 * un dossier spécifique ("TiledLevels/") et choisit un niveau aléatoirement à chaque fin de partie.
 * </p>
 * <p>
 * Elle inclut une logique "Anti-Répétition" pour éviter que le joueur ne tombe
 * deux fois de suite sur la même carte.
 * </p>
 */
public class LevelManager {

    /** Liste de tous les chemins de fichiers .tmx trouvés dans le dossier. */
    private Array<String> mapFiles;

    /** Chemin du niveau actuellement joué (pour éviter de le re-piocher tout de suite). */
    private String currentMap;

    /** Générateur de nombres aléatoires. */
    private Random random;

    /**
     * Constructeur.
     * <p>
     * Scanne automatiquement le dossier interne "TiledLevels/" à la recherche de fichiers .tmx
     * et remplit la liste {@code mapFiles}.
     * </p>
     */
    public LevelManager() {
        random = new Random();
        mapFiles = new Array<>();

        // Gdx.files.internal pointe vers le dossier "assets" du projet (Android/Desktop).
        FileHandle dir = Gdx.files.internal("TiledLevels/");

        // On ne liste que les fichiers finissant par l'extension .tmx (Tiled Map XML)
        for (FileHandle entry : dir.list(".tmx")) {
            mapFiles.add(entry.path());
        }
    }

    /**
     * Sélectionne aléatoirement le prochain niveau à charger.
     * <p>
     * La méthode garantit (si possible) que le niveau retourné sera différent du niveau actuel.
     * </p>
     *
     * @return Le chemin complet du fichier (ex: "TiledLevels/map2.tmx").
     */
    public String getNextMapPath(){
        // Cas 1 : Aucun fichier trouvé (Erreur ou dossier vide)
        // On retourne un niveau par défaut codé en dur pour éviter le crash.
        if (mapFiles.size == 0){
            return "TiledLevels/1.tmx";
        }

        // Cas 2 : Un seul niveau disponible
        // Pas le choix, on retourne le seul existant (la boucle infinie serait impossible sinon).
        if (mapFiles.size == 1){
            return mapFiles.get(0);
        }

        // Cas 3 : Plusieurs niveaux
        // On boucle tant qu'on tombe sur le même niveau que celui en cours.
        String nextMap;
        do {
            int index = random.nextInt(mapFiles.size);
            nextMap = mapFiles.get(index);
        } while (nextMap.equals(currentMap));

        // On mémorise ce choix pour la prochaine fois
        currentMap = nextMap;
        return nextMap;
    }

    /**
     * Définit manuellement le niveau actuel.
     * <p>
     * Utile lors du premier lancement du jeu pour dire au manager :
     * "On commence sur cette map, ne la choisis pas tout de suite pour la suivante".
     * </p>
     *
     * @param path Le chemin du niveau chargé.
     */
    public void setCurrentMap(String path){
        this.currentMap = path;
    }
}
