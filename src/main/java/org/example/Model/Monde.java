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

    public void afficher() {

        System.out.println("Tour : " + tourActuel);

        for (int ligne = 0; ligne < 10; ligne++) {

            for (int colonne = 0; colonne < 10; colonne++) {

                System.out.print(
                        "[" + grille[ligne][colonne].afficher() + "]"
                );
            }

            System.out.println();
        }
    }
}