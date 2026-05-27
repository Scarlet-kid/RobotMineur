package org.example.Model;


import java.util.List;

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

    /**
     * Le robot agit tout seul a chaque tour :
     *  1. S'il porte des minerais -> va vers son entrepot
     *  2. Sinon -> va vers la mine de son type la plus proche
     *  3. S'il est sur sa destination -> recolte ou depose
     *  4. Sinon -> suit Dijkstra
     *
     * Ajoute cette methode dans Robot.java avec :
     *   import org.example.Algorithme.Dijkstra;
     *   import java.util.List;
     */
    public void agirAutonome(Secteur[][] grille, List<Mine> mines, List<Entrepot> entrepots) {

        Composantes c = secteur.getComposante();

        // 1. Si on est sur une mine de notre type et qu'on peut recolter
        if (c instanceof Mine) {
            Mine m = (Mine) c;
            if (m.getType() == type && !m.estVide() && nbMinerais < capaciteStockage) {
                recolter();
                return;
            }
        }

        // 2. Si on est sur notre entrepot
        if (c instanceof Entrepot) {
            Entrepot e = (Entrepot) c;
            if (e.getType() == type && nbMinerais > 0) {
                deposer();
                return;
            }
        }

        // 3. Determiner la destination
        Secteur destination = null;

        if (nbMinerais >= capaciteStockage || (nbMinerais > 0 && toutesMinesVides(mines))) {
            // Aller deposer
            destination = trouverEntrepot(entrepots);
        } else {
            // Aller recolter dans la mine la plus proche
            destination = trouverMineLaPlusProche(mines, grille);
        }

        if (destination == null) {
            System.out.println("Robot " + numero + " : pas de destination disponible.");
            return;
        }

        // 4. Calculer le chemin avec Dijkstra
        List<Directions> chemin = Dijkstra.calculerChemin(grille, secteur, destination);

        if (chemin.isEmpty()) {
            System.out.println("Robot " + numero + " : pas de chemin trouve.");
            return;
        }

        // 5. Avancer d'une case
        avancer(chemin.get(0), grille);
    }

    /**
     * Trouve la mine de notre type la plus proche (non vide).
     */
    private Secteur trouverMineLaPlusProche(List<Mine> mines, Secteur[][] grille) {
        Secteur meilleure = null;
        int distMin = Integer.MAX_VALUE;

        for (Mine m : mines) {
            if (m.getType() != type || m.estVide()) continue;

            List<Directions> chemin = Dijkstra.calculerChemin(grille, secteur, m.getSecteur());
            if (chemin.isEmpty()) continue;

            if (chemin.size() < distMin) {
                distMin = chemin.size();
                meilleure = m.getSecteur();
            }
        }

        return meilleure;
    }

    /**
     * Trouve l'entrepot de notre type.
     */
    private Secteur trouverEntrepot(List<Entrepot> entrepots) {
        for (Entrepot e : entrepots) {
            if (e.getType() == type) {
                return e.getSecteur();
            }
        }
        return null;
    }

    /**
     * Verifie si toutes les mines de notre type sont vides.
     */
    private boolean toutesMinesVides(List<Mine> mines) {
        for (Mine m : mines) {
            if (m.getType() == type && !m.estVide()) return false;
        }
        return true;
    }


}
