package org.example.Model;

public class Monde {

    private Secteur[][] grille;
    private int tourActuel;

    public Monde(Secteur[][] grille, int tourActuel) {
        this.grille = grille;
        this.tourActuel = tourActuel;
    }

    public Secteur[][] getGrille() {
        return grille;
    }

    public int getTourActuel() {
        return tourActuel;
    }

    public void setGrille(Secteur[][] grille) {
        this.grille = grille;
    }

    public void setTourActuel(int tourActuel) {
        this.tourActuel = tourActuel;
    }

    public void initialiser() {

        grille = new Secteur[10][10];
        tourActuel = 0;

        for (int ligne = 0; ligne < 10; ligne++) {
            for (int colonne = 0; colonne < 10; colonne++) {

                grille[ligne][colonne] =
                        new Secteur(ligne, colonne, false);
            }
        }
    }

    public void jouerTour() {

        tourActuel++;

        System.out.println("=== Tour " + tourActuel + " ===");
    }

    public void afficher(String[][] grille) {
        System.out.println("-------------");

        for (String[] ligne : grille) {
            String ligneAffichage = "|";

            for (String cellule : ligne) {
                ligneAffichage += cellule + "|";
            }

            System.out.println(ligneAffichage);
            System.out.println("-------------");
        }

    }

}

//def afficher_grille(grille: list[list[str]]):
//    """Affiche la grille dans la console avec les jetons placés."""
//    print("-------------")  # Affiche la bordure supérieure
//    for ligne in grille:  # Parcourt chaque ligne de la grille
//        ligne_affichage = "|"  # Commence la ligne avec un |
//        for case in ligne:  # Parcourt chaque case de la ligne
//            ligne_affichage += case + "|"  # Ajoute la case et un | pour la séparation
//        print(ligne_affichage)  # Affiche la ligne complète
//        print("-------------")  # Affiche une bordure après la ligne