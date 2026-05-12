package org.example;

import org.example.Model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static Random random = new Random();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        Monde monde = new Monde(null, 0);
        monde.initialiser();
        Secteur[][] grille = monde.getGrille();

        List<Mine> mines = new ArrayList<>();

        Secteur sMine1 = getSecteurLibre(grille);
        Mine mine1 = new Mine(1, sMine1, TypeMinerai.OR, 50 + random.nextInt(51));
        sMine1.setComposante(mine1);
        mines.add(mine1);

        Secteur sMine2 = getSecteurLibre(grille);
        Mine mine2 = new Mine(2, sMine2, TypeMinerai.NICKEL, 50 + random.nextInt(51));
        sMine2.setComposante(mine2);
        mines.add(mine2);

        List<Entrepot> entrepots = new ArrayList<>();

        Secteur sEntrepot1 = getSecteurLibre(grille);
        Entrepot entrepot1 = new Entrepot(TypeMinerai.OR, 1, sEntrepot1);
        sEntrepot1.setComposante(entrepot1);
        entrepots.add(entrepot1);

        Secteur sEntrepot2 = getSecteurLibre(grille);
        Entrepot entrepot2 = new Entrepot(TypeMinerai.NICKEL, 2, sEntrepot2);
        sEntrepot2.setComposante(entrepot2);
        entrepots.add(entrepot2);

        List<Robot> robots = new ArrayList<>();

        Secteur sRobot1 = getSecteurLibre(grille);
        Robot robot1 = new Robot(1, sRobot1, TypeMinerai.OR, 5 + random.nextInt(5), 1 + random.nextInt(3));
        sRobot1.setRobot(robot1);
        robots.add(robot1);

        Secteur sRobot2 = getSecteurLibre(grille);
        Robot robot2 = new Robot(2, sRobot2, TypeMinerai.NICKEL, 5 + random.nextInt(5), 1 + random.nextInt(3));
        sRobot2.setRobot(robot2);
        robots.add(robot2);

        System.out.println("=== SAE 2.01 - Robots Mineurs ===");
        System.out.println("Le jeu se termine quand toutes les mines sont vides.\n");

        while (!toutesVides(mines)) {

            monde.afficher();
            afficherInfos(mines, entrepots, robots, monde.getTourActuel());

            for (Robot robot : robots) {
                robot.choisirAction(grille, scanner);
            }

            monde.jouerTour();
        }

        monde.afficher();
        System.out.println("\n=== Toutes les mines sont vides ! Fin du jeu. ===");
        System.out.println("Nombre de tours : " + monde.getTourActuel());
        for (Entrepot e : entrepots) {
            String typeStr = (e.getType() == TypeMinerai.OR) ? "OR" : "NI";
            System.out.println("E" + e.getNumero()
                    + " " + e.getSecteur().getLigne()
                    + " " + e.getSecteur().getColonne()
                    + " " + typeStr
                    + " " + e.getStock());
        }

        scanner.close();
    }

    private static Secteur getSecteurLibre(Secteur[][] grille) {
        Secteur s;
        do {
            int l = random.nextInt(10);
            int c = random.nextInt(10);
            s = grille[l][c];
        } while (s.isEstEau() || s.getComposante() != null || s.getRobot() != null);
        return s;
    }

    private static boolean toutesVides(List<Mine> mines) {
        for (Mine m : mines) {
            if (!m.estVide()) return false;
        }
        return true;
    }

    private static void afficherInfos(List<Mine> mines, List<Entrepot> entrepots,
                                      List<Robot> robots, int tour) {
        System.out.println("\ntour " + tour);
        for (Mine m : mines)         System.out.println(m);
        for (Entrepot e : entrepots) {
            String typeStr = (e.getType() == TypeMinerai.OR) ? "OR" : "NI";
            System.out.println("E" + e.getNumero()
                    + " " + e.getSecteur().getLigne()
                    + " " + e.getSecteur().getColonne()
                    + " " + typeStr
                    + " " + e.getStock());
        }
        for (Robot r : robots)       System.out.println(r);
        System.out.println();
    }
}