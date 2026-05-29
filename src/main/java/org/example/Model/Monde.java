package org.example.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Monde {

    private Secteur[][] grille;
    private int tourActuel;
    private final List<Robot> robots = new ArrayList<>();

    public Monde(Secteur[][] grille, int tourActuel) {
        this.grille     = grille;
        this.tourActuel = tourActuel;
    }

    public Secteur[][] getGrille()  { return this.grille;     }
    public int getTourActuel()      { return this.tourActuel; }

    public void initialiser() {
        grille     = new Secteur[10][10];
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
            if (!grille[ligne][colonne].EstEau()) {
                grille[ligne][colonne].setEstEau(true);
                nbEau++;
            }
        }
    }

    /**
     * Version CONSOLE : demande les actions à l'utilisateur via Scanner.
     * Utilisée uniquement dans Main.java (mode console).
     */
    public void jouerTour() {
        tourActuel++;
        System.out.println("\n=== Tour " + tourActuel + " ===");

        Scanner scanner = new Scanner(System.in); // UN SEUL scanner créé ici
        for (Robot r : robots) {
            boolean actionOk = false;
            while (!actionOk) {
                System.out.print("Robot " + r.getNumero() + " – direction (N/E/S/O) : ");
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) { System.out.println("Entrée vide."); continue; }
                char c = Character.toUpperCase(line.charAt(0));
                Directions dir = null;
                if      (c == 'N') dir = Directions.NORD;
                else if (c == 'S') dir = Directions.SUD;
                else if (c == 'E') dir = Directions.EST;
                else if (c == 'O') dir = Directions.OUEST;
                else { System.out.println("Direction inconnue."); continue; }
                actionOk = r.avancer(dir, grille);
            }
        }
    }

    /**
     * Version GRAPHIQUE / AUTONOME : incrémente juste le tour.
     * Pas de Scanner, pas de boucle infinie.
     * Utilisée dans MainFX.java.
     */
    public void incrementerTour() {
        tourActuel++;
        System.out.println("\n=== Tour " + tourActuel + " ===");
    }

    public void afficher() {
        System.out.println();
        System.out.print("    ");
        for (int j = 0; j < 10; j++) System.out.print("   " + j + "    ");
        System.out.println();
        dessinerLigneBordure();
        for (int i = 0; i < 10; i++) {
            System.out.print(" " + i + " |");
            for (int j = 0; j < 10; j++) System.out.print("  " + grille[i][j].getAffichageHaut() + "   |");
            System.out.println();
            System.out.print("   |");
            for (int j = 0; j < 10; j++) System.out.print("  " + grille[i][j].getAffichageBas() + "   |");
            System.out.println();
            dessinerLigneBordure();
        }
    }

    private void dessinerLigneBordure() {
        System.out.print("   +");
        for (int j = 0; j < 10; j++) System.out.print("-------+");
        System.out.println();
    }
}