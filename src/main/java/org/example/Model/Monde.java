package org.example.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Monde {

    private Secteur[][] grille;
    private int tourActuel;
    private final List<Robot> robots = new ArrayList<>();

    public Monde(Secteur[][] grille, int tourActuel) {
        this.grille = grille;
        this.tourActuel = tourActuel;
    }

    public Secteur[][] getGrille()      { return this.grille; }
    public int getTourActuel()         { return this.tourActuel; }
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
            if (!grille[ligne][colonne].EstEau()) {
                grille[ligne][colonne].setEstEau(true);
                nbEau++;
            }
        }
    }

    public void jouerTour() {
        tourActuel++;
        System.out.println("\n=== Tour " + tourActuel + " ===");

        for (Robot r : robots) {
            boolean actionOk = false;
            while (!actionOk) {
                Scanner scanner = new Scanner(System.in);
                System.out.print(
                        "Robot " + r.getNumero() + " – choisissez une direction (N/E/S/O) : ");
                String line = scanner.nextLine().trim(); //.trim pour la supression des espaces au début et en fin de caractère.

                if (line.isEmpty()) {
                    System.out.println("Entrée vide – réessayez.");
                    continue;
                }

                char c = Character.toUpperCase(line.charAt(0));
                Directions dir = switch (c) {
                    case 'N' -> Directions.NORD;
                    case 'S' -> Directions.SUD;
                    case 'E' -> Directions.EST;
                    case 'O' -> Directions.OUEST;
                    default -> {
                        System.out.println("Direction inconnue – utilisez N, E, S ou O.");
                        yield null;
                    }
                };
                if (dir == null) {
                    continue;
                }
                actionOk = r.avancer(dir, grille);
            }
        }
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
                System.out.print("  " + grille[i][j].getAffichageHaut() + "   |");
            }
            System.out.println();
            System.out.print("   |");
            for (int j = 0; j < 10; j++) {
                System.out.print("  " + grille[i][j].getAffichageBas() + "   |");
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
