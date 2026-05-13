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
        System.out.println();

        // 1. En-tête des colonnes (ajusté pour 7 espaces + 1 barre)
        System.out.print("    ");
        for (int j = 0; j < 10; j++) {
            System.out.print("   " + j + "    ");
        }
        System.out.println();

        dessinerLigneBordure();

        for (int i = 0; i < 10; i++) {
            // 2. Ligne de données (Contenu)
            System.out.print(" " + i + " |");
            for (int j = 0; j < 10; j++) {
                // On centre le symbole de 3 chars au milieu des 7 espaces
                // Format : 2 espaces + 3 chars + 2 espaces = 7 total
                System.out.print("  " + grille[i][j].afficher() + "  |");
            }
            System.out.println();

            // 3. Ligne de vide (Hauteur)
            System.out.print("   |");
            for (int j = 0; j < 10; j++) {
                System.out.print("       |"); // Exactement 7 espaces
            }
            System.out.println();

            dessinerLigneBordure();
        }
    }

    private void dessinerLigneBordure() {
        System.out.print("   +");
        for (int j = 0; j < 10; j++) {
            System.out.print("-------+"); // 7 tirets
        }
        System.out.println();
    }
}