# SAE 2.01 - Simulation de Robots Mineurs

Ce projet est une application Java permettant de simuler l'activité de robots mineurs dans un environnement en deux dimensions. Le monde est représenté sous la forme d'une grille 10x10 où les robots interagissent avec différents éléments tels que des mines (or, nickel) et des entrepôts.

## 🚀 Fonctionnalités Principales

- **Génération du monde** : Création d'une grille 10x10 gérant les collisions et le positionnement des entités.
- **Logique métier orientée objet** : Utilisation de l'héritage (Robot, Mines, Entrepôts) et des énumérations (Directions, TypeMinerai) pour structurer les comportements.
- **Interface Graphique (JavaFX)** : Visualisation complète de la simulation.
- **Ergonomie** : L'interface a été conçue en respectant les critères ergonomiques de Bastien et Scapin.

## 🏷️ Gestion des Versions (Tags Git)

Le développement a été structuré de manière itérative. Les professeurs peuvent naviguer entre les grandes étapes du projet grâce aux tags Git fournis dans l'historique :

* `v1.0-console` : Version initiale contenant la logique métier (modèle) fonctionnelle avec un affichage textuel.
* `v2.0-gui` : Version finale intégrant l'interface graphique JavaFX complète.

## 🛠️ Prérequis et Outils

* **Langage** : Java (JDK 21 recommandé)
* **Gestionnaire de dépendances** : Maven
* **Interface Graphique** : JavaFX (géré via Maven et `module-info.java`)
* **IDE Recommandé** : IntelliJ IDEA

## ⚙️ Comment lancer le projet ?

Pour des raisons de compatibilité entre les différents environnements (Windows/Linux) et pour éviter les erreurs liées à l'empaquetage de JavaFX, **le lancement direct via l'IDE est privilégié**.

### Via IntelliJ IDEA :
1.  Cloner ce dépôt ou extraire l'archive du projet.
2.  Ouvrir le dossier du projet dans IntelliJ IDEA (`File > Open`).
3.  Attendre que Maven télécharge les dépendances (JavaFX) en arrière-plan.
4.  Ouvrir le fichier principal : `src/main/java/org/example/Controleur/Launcher.java` (ou `MainFX.java`).
5.  Cliquer sur le bouton **Run** (flèche verte).

## 📁 Architecture du Projet (Modèle MVC)

Le code source respecte scrupuleusement l'architecture Modèle-Vue-Contrôleur (MVC) :

* 📁 `Annexes_Doc/` : Contient la documentation technique (ex: `Diagramme_UML_SAE_pdf.pdf`).
* 📁 `src/main/java/org/example/`
  * 📂 `Controleur/` : Gère le lancement de l'application et les interactions (`Launcher`, `MainFX`).
  * 📂 `Model/` : Contient toute la logique métier et les entités (`Monde`, `Robot`, `Secteur`, `Mine`, etc.).
  * 📂 `Vue/` : Gère l'affichage graphique JavaFX (`MondeVue`, `CelluleVue`).
* 📁 `src/main/ressources/` : Contient les assets graphiques nécessaires à l'interface.
* 📁 `src/test/` : Contient les tests unitaires du projet.
* 📄 `pom.xml` : Fichier de configuration Maven pour la gestion des dépendances.

---
**Auteur :** Sosthene Toviekou, Khémiry Chahd, Lordevy Billy, Kawther Bouchnak, Reine Sorgho.
