# 2DGameEngine

Ceci est un projet créé pour le cours de Programmation et conception orientée objet de l'université Côte d'Azur, 
utilisant la bibliothèque [libGDX](https://libgdx.com/), dont leur outil pour débuter rapidement ([gdx-liftoff](https://github.com/libgdx/gdx-liftoff)) 
a été utilisé.

Ce projet a été généré avec un modèle incluant un lanceur d'application simple et une extension `ApplicationAdapter`.

L'objectif de ce projet est d'être un moteur prévu pour des platformer/action en 2D.

## Programme

- `core`: Le module principal dans lequel se situe tout le code que j'ai écrit
- `assets`: Le fichier dans lequel est situé tous les sprites, cartes et sauvegardes.

## Compilation/Execution

Pour lancer l'application, vous avez à votre disposition le fichier "run.bat" si vous êtes sur Windows, ou "run.sh" 
si vous êtes sur Linux ou macOS.
Si vous souhaitez seulement compiler, puisque ce projet utilise Gradle il vous suffit, dans un terminal à la racine du projet, 
d'exécuter la commande "gradlew.bat build" si vous êtes sur Windows et "./gradlew build" si vous êtes sur Linux / macOS.

## Gradle

Ce projet utilise [Gradle](https://gradle.org/), simplifiant les étapes de compilation comme cité précédemment.
Ce dernier est présent depuis l'utilisation de l'outil de lancement rapide 
mis à disposition par libGDX: [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).
