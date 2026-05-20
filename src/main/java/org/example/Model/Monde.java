package org.example.Model;

import java.util.Random;

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

        Random random = new Random();

        int nbEau = 0;

        while (nbEau < 8) {

            int ligne = random.nextInt(10);
            int colonne = random.nextInt(10);

            if (!grille[ligne][colonne].EstEau()) {

                grille[ligne][colonne].setEstEau(true);

                nbEau++;
            }
        }
    }

    public void jouerTour() {
        tourActuel++;
    }

    public void afficher() {

        for (int ligne = 0; ligne < 10; ligne++) {

            for (int colonne = 0; colonne < 10; colonne++) {

                Secteur secteur = grille[ligne][colonne];

                if (secteur.EstEau()) {
                    System.out.print("[EAU]");
                }

                else if (secteur.getRobot() != null) {
                    System.out.print("[R" +
                            secteur.getRobot().getNumero() + "]");
                }

                else if (secteur.getComposante() != null) {
                    System.out.print("[" +
                            secteur.getComposante().afficher() + "]");
                }

                else {
                    System.out.print("[   ]");
                }
            }

            System.out.println();
        }
    }
}