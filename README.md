# SAE 2.01 - Simulation de Robots Mineurs

Ce projet est une application Java permettant de simuler l'activité de robots mineurs dans un environnement en deux dimensions. Le monde est représenté sous la forme d'une grille 10x10 où les robots interagissent avec différents éléments tels que des mines (or, nickel) et des entrepôts.

## 🚀 Fonctionnalités Principales

- **Génération du monde** : Création d'une grille 10x10 gérant les collisions et le positionnement des entités.
- **Logique métier orientée objet** : Utilisation de l'héritage (Robot, Mines, Entrepôts) et des interfaces pour structurer les comportements.
- **Interface Graphique (JavaFX)** : Visualisation complète de la simulation.
- **Ergonomie** : L'interface a été conçue en respectant les critères ergonomiques de Bastien et Scapin.

## 🏷️ Gestion des Versions (Tags Git)

Le développement a été structuré de manière itérative. Les professeurs peuvent naviguer entre les grandes étapes du projet grâce aux tags Git fournis dans l'historique :

* `v1.0-console` : Version initiale contenant la logique métier (modèle) 100% fonctionnelle avec un affichage textuel dans le terminal.
* `v2.0-gui` : Version finale intégrant l'interface graphique JavaFX complète.

## 🛠️ Prérequis et Outils

* **Langage** : Java (JDK 21 recommandé)
* **Gestionnaire de dépendances** : Maven
* **Interface Graphique** : JavaFX (géré via Maven)
* **IDE Recommandé** : IntelliJ IDEA

## ⚙️ Comment lancer le projet ?

Pour des raisons de compatibilité entre les différents environnements (Windows/Linux) et pour éviter les erreurs JNI liées à l'empaquetage de JavaFX, **le lancement direct via l'IDE est privilégié** par rapport à l'exécution d'un fichier `.jar`.

### Via IntelliJ IDEA :
1.  Cloner ce dépôt ou extraire l'archive du projet.
2.  Ouvrir le dossier du projet dans IntelliJ IDEA (`File > Open`).
3.  Attendre que Maven télécharge les dépendances nécessaires (JavaFX) en arrière-plan.
4.  Ouvrir le fichier principal contenant la méthode `main` (ex: `MainFX.java` ou `Launcher.java`).
5.  Cliquer sur le bouton **Run** (flèche verte) dans la gouttière de l'éditeur ou en haut à droite.

## 📁 Architecture du Projet

* `src/main/java/` : Contient l'ensemble du code source Java, séparé en packages (Modèle, Vue, Contrôleur).
* `src/main/resources/` : Contient les assets graphiques (images) nécessaires à l'interface JavaFX.
* `pom.xml` : Fichier de configuration Maven pour la gestion des dépendances.

---
Auteurs:Sosthene Toviekou,Khémiry Chahd,Lordevy Billy Bichall Taty, Kawther Bouchnak,Reine Sorgho.
