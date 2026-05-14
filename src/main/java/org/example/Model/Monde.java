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

    public void initialiser() {

        grille = new Secteur[10][10];
        tourActuel = 0;

        for (int ligne = 0; ligne < 10; ligne++) {
            for (int colonne = 0; colonne < 10; colonne++) {
                grille[ligne][colonne] = new Secteur(ligne, colonne, false);
            }
        }

        int nbEau = 0;
        java.util.Random rnd = new java.util.Random();

        while (nbEau < 8) {
            int ligne   = rnd.nextInt(10);
            int colonne = rnd.nextInt(10);

            if (!grille[ligne][colonne].isEstEau()) {
                grille[ligne][colonne].setEstEau(true);
                nbEau++;
            }
        }
    }

    public void jouerTour() {
        tourActuel++;
        System.out.println("=== Tour " + tourActuel + " ===");
    }

    public void afficher() {
        System.out.println();

        System.out.print("    ");
        for (int j = 0; j < 10; j++) {
            System.out.print("   " + j + "    ");
        }
        System.out.println();

        dessinerLigneBordure();

        for (int i = 0; i < 10; i++) {

            System.out.print(" " + i + " |");
            for (int j = 0; j < 10; j++) {
                System.out.print("  " + grille[i][j].getAffichageHaut() + "  |");
            }
            System.out.println();

            System.out.print("   |");
            for (int j = 0; j < 10; j++) {
                System.out.print("  " + grille[i][j].getAffichageBas() + "  |");
            }
            System.out.println();

            dessinerLigneBordure();
        }
    }

    private void dessinerLigneBordure() {
        System.out.print("   +");
        for (int j = 0; j < 10; j++) {
            System.out.print("-------+");
        }
        System.out.println();
    }
}