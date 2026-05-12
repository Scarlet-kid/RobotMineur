package org.example.Model;

import java.util.Scanner;

public class Robot extends Composantes {

    private TypeMinerai type;
    private int nbMinerais;
    private int capaciteStockage;
    private int capaciteExtraction;

    public Robot(int numero, Secteur secteur, TypeMinerai type, int capaciteStockage, int capaciteExtraction) {
        super(numero, secteur);
        this.type = type;
        this.nbMinerais = 0;
        this.capaciteStockage = capaciteStockage;
        this.capaciteExtraction = capaciteExtraction;
    }

    public void avancer(Directions dir, Secteur[][] grille) {
        int ligne    = secteur.getLigne();
        int colonne  = secteur.getColonne();

        int nouvelleLigne   = ligne;
        int nouvelleColonne = colonne;

        switch (dir) {
            case NORD:  nouvelleLigne--;   break;
            case SUD:   nouvelleLigne++;   break;
            case EST:   nouvelleColonne++; break;
            case OUEST: nouvelleColonne--; break;
        }

        if (nouvelleLigne < 0 || nouvelleLigne >= 10
                || nouvelleColonne < 0 || nouvelleColonne >= 10) {
            System.out.println("Action invalide : hors du monde !");
            return;
        }

        Secteur cible = grille[nouvelleLigne][nouvelleColonne];

        if (cible.isEstEau()) {
            System.out.println("Action invalide : plan d'eau !");
            return;
        }

        if (!cible.estLibre()) {
            System.out.println("Action invalide : secteur occupe !");
            return;
        }

        secteur.setRobot(null);
        cible.setRobot(this);
        secteur = cible;
        System.out.println("Robot " + numero + " avance vers " + dir);
    }

    public void recolter() {
        Composantes c = secteur.getComposante();

        if (!(c instanceof Mine)) {
            System.out.println("Action invalide : pas de mine sur ce secteur !");
            return;
        }

        Mine mine = (Mine) c;

        if (mine.getType() != type) {
            System.out.println("Action invalide : ce n'est pas votre type de minerai !");
            return;
        }
        if (mine.estVide()) {
            System.out.println("Action invalide : la mine est vide !");
            return;
        }
        if (nbMinerais >= capaciteStockage) {
            System.out.println("Action invalide : stockage plein !");
            return;
        }

        int espaceDisponible = capaciteStockage - nbMinerais;
        int qteARecolter     = Math.min(capaciteExtraction, espaceDisponible);
        int qteReelle        = mine.extraire(qteARecolter);
        nbMinerais += qteReelle;
        System.out.println("Robot " + numero + " a recolte " + qteReelle
                + " minerai(s). Stock : " + nbMinerais + "/" + capaciteStockage);
    }

    public void deposer() {
        Composantes c = secteur.getComposante();

        if (!(c instanceof Entrepot)) {
            System.out.println("Action invalide : pas d'entrepot sur ce secteur !");
            return;
        }

        Entrepot entrepot = (Entrepot) c;

        if (entrepot.getType() != type) {
            System.out.println("Action invalide : ce n'est pas le bon entrepot !");
            return;
        }
        if (nbMinerais == 0) {
            System.out.println("Action invalide : le robot ne porte rien !");
            return;
        }

        entrepot.deposer(nbMinerais);
        System.out.println("Robot " + numero + " a depose " + nbMinerais + " minerai(s).");
        nbMinerais = 0;
    }

    public void choisirAction(Secteur[][] grille, Scanner scanner) {
        System.out.println("\n--- Robot R" + numero
                + " (" + (type == TypeMinerai.OR ? "OR" : "NI") + ")"
                + "  Minerais : " + nbMinerais + "/" + capaciteStockage
                + "  Position : (" + secteur.getLigne() + "," + secteur.getColonne() + ")");
        System.out.println("1-NORD  2-SUD  3-EST  4-OUEST  5-RECOLTER  6-DEPOSER  0-PASSER");
        System.out.print("Votre choix : ");

        int choix = scanner.nextInt();

        switch (choix) {
            case 1: avancer(Directions.NORD,  grille); break;
            case 2: avancer(Directions.SUD,   grille); break;
            case 3: avancer(Directions.EST,   grille); break;
            case 4: avancer(Directions.OUEST, grille); break;
            case 5: recolter(); break;
            case 6: deposer();  break;
            case 0: System.out.println("Robot " + numero + " passe son tour."); break;
            default: System.out.println("Choix invalide, le robot passe son tour."); break;
        }
    }

    public TypeMinerai getType()           { return type; }
    public int getNbMinerais()             { return nbMinerais; }
    public int getCapaciteStockage()       { return capaciteStockage; }
    public int getCapaciteExtraction()     { return capaciteExtraction; }

    @Override
    public String afficher() {
        return "R" + numero;
    }

    @Override
    public String toString() {
        String typeStr = (type == TypeMinerai.OR) ? "OR" : "NI";
        return "R" + numero
                + " " + secteur.getLigne()
                + " " + secteur.getColonne()
                + " " + typeStr
                + " " + nbMinerais + " / " + capaciteStockage;
    }
}