package org.example.Model;

import java.util.Scanner;

public class Robot extends Composantes {

    private TypeMinerai type;
    private int nbMinerais;
    private final int capaciteStockage;
    private final int capaciteExtraction;

    public Robot(int numero,
                 Secteur secteur,
                 TypeMinerai type,
                 int capaciteStockage,
                 int capaciteExtraction) {
        super(numero, secteur);
        this.type = type;
        this.nbMinerais = 0;
        this.capaciteStockage = capaciteStockage;
        this.capaciteExtraction = capaciteExtraction;
    }
    public void SetNbMinerais(int nb)
    {
        this.nbMinerais = nb;
    }

    public int getCapaciteExtraction() {
        return capaciteExtraction;
    }



    public boolean avancer(Directions dir, Secteur[][] grille) {

        if (secteur == null) {
            System.out.println("Action invalide : le robot n’est pas placé sur le plateau.");
            return false;
        }

        int ligne   = secteur.getLigne();
        int colonne = secteur.getColonne();

        int nouvelleLigne   = ligne;
        int nouvelleColonne = colonne;

        if (dir == Directions.NORD) {
            nouvelleLigne--;
        } else if (dir == Directions.SUD) {
            nouvelleLigne++;
        } else if (dir == Directions.EST) {
            nouvelleColonne++;
        } else if (dir == Directions.OUEST) {
            nouvelleColonne--;
        }

        int maxLignes = grille.length;
        int maxCols   = grille[0].length;

        if (nouvelleLigne < 0 || nouvelleLigne >= maxLignes
                || nouvelleColonne < 0 || nouvelleColonne >= maxCols) {
            System.out.println("Action invalide : hors du monde !");
            return false;
        }

        Secteur cible = grille[nouvelleLigne][nouvelleColonne];

        if (cible.EstEau()) {
            System.out.println("Action invalide : plan d’eau !");
            return false;
        }

        if (cible.getRobot() != null) {
            System.out.println("Action invalide : secteur déjà occupé par un robot !");
            return false;
        }

        secteur.setRobot(null);
        cible.setRobot(this);
        this.secteur = cible;

        System.out.printf("Robot %d avance vers %s%n", numero, dir);
        return true;
    }

    public boolean recolter() {
        Composantes c = secteur.getComposante();

        if (!(c instanceof Mine)) {
            System.out.println("Action invalide : pas de mine sur ce secteur !");
            return false;
        }

        Mine mine = (Mine) c;

        if (mine.getType() != type) {
            System.out.println("Action invalide : ce n'est pas votre type de minerai !");
            return false;
        }
        if (mine.estVide()) {
            System.out.println("Action invalide : la mine est vide !");
            return false;
        }
        if (nbMinerais >= capaciteStockage) {
            System.out.println("Action invalide : stockage plein !");
            return false;
        }

        int espaceDisponible = capaciteStockage - nbMinerais;
        int qteARecolter     = Math.min(capaciteExtraction, espaceDisponible);
        int qteReelle        = mine.extraire(qteARecolter);
        nbMinerais += qteReelle;

        System.out.println("Robot " + numero + " a recolte " + qteReelle
                + " minerai(s). Stock : " + nbMinerais + "/" + capaciteStockage);
        return true;
    }

    public boolean deposer() {
        Composantes c = secteur.getComposante();

        if (!(c instanceof Entrepot)) {
            System.out.println("Action invalide : pas d'entrepot sur ce secteur !");
            return false;
        }

        Entrepot entrepot = (Entrepot) c;

        if (entrepot.getType() != type) {
            System.out.println("Action invalide : ce n'est pas le bon entrepot !");
            return false;
        }
        if (nbMinerais == 0) {
            System.out.println("Action invalide : le robot ne porte rien !");
            return false;
        }

        entrepot.deposer(nbMinerais);
        System.out.println("Robot " + numero + " a depose " + nbMinerais + " minerai(s).");
        nbMinerais = 0;
        return true;
    }

    public void choisirAction(Secteur[][] grille, Scanner scanner) {
        boolean actionEffectuee = false;

        while (!actionEffectuee) {
            System.out.println("\n--- Robot R" + numero
                    + " (" + (type == TypeMinerai.OR ? "OR" : "NI") + ")"
                    + "  Minerais : " + nbMinerais + "/" + capaciteStockage
                    + "  Position : (" + secteur.getLigne() + "," + secteur.getColonne() + ")");
            System.out.println("1-NORD  2-SUD  3-EST  4-OUEST  5-RECOLTER  6-DEPOSER  0-PASSER");
            System.out.print("Votre choix : ");

            int choix = scanner.nextInt();

            switch (choix) {
                case 1:
                    actionEffectuee = avancer(Directions.NORD, grille);
                    break;
                case 2:
                    actionEffectuee = avancer(Directions.SUD, grille);
                    break;
                case 3:
                    actionEffectuee = avancer(Directions.EST, grille);
                    break;
                case 4:
                    actionEffectuee = avancer(Directions.OUEST, grille);
                    break;
                case 5:
                    actionEffectuee = recolter();
                    break;
                case 6:
                    actionEffectuee = deposer();
                    break;
                case 0:
                    System.out.println("Robot " + numero + " passe son tour.");
                    actionEffectuee = true;
                    break;
                default:
                    System.out.println("Choix invalide, le robot passe son tour.");
                    actionEffectuee = true;
                    break;
            }
        }
    }

    public int getNumero(){ return numero; }

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

    public TypeMinerai getType() {
        return type;
    }

    public int getNbMinerais() {
        return nbMinerais;
    }

    public int getCapaciteStockage() {
        return capaciteStockage;
    }
}
