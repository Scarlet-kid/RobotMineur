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
        Secteur secteurActuel = getSecteur();
        int ligne = secteurActuel.getLigne();
        int colonne = secteurActuel.getColonne();

        int nouvelleLigne = ligne;
        int nouvelleColonne = colonne;

        switch (dir) {
            case NORD:  nouvelleLigne  = ligne - 1;  break;
            case SUD:   nouvelleLigne  = ligne + 1;  break;
            case EST:   nouvelleColonne = colonne + 1; break;
            case OUEST: nouvelleColonne = colonne - 1; break;
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
            System.out.println("Action invalide : un autre robot est deja la !");
            return;
        }

        secteurActuel.setComposante(null);
        cible.setComposante(this);
        System.out.println("Robot " + getNumero() + " avance vers " + dir);
    }

     void recolter() {
         Composantes c = getSecteur().getComposante();
         if (!(c instanceof Mine)) {
             System.out.println("Action invalide : pas de mine sur ce secteur !");
             return;
         }

        if (nbMinerais >= capaciteStockage) {
            System.out.println("Action invalide : stockage plein !");
            return;
        }

        int espaceDisponible = capaciteStockage - nbMinerais;
        int qteARecolter = Math.min(capaciteExtraction, espaceDisponible);
        int qteReelle = mine.extraire(qteARecolter);
        nbMinerais += qteReelle;
        System.out.println("Robot " + getNumero() + " a recolte " + qteReelle + " minerai(s). Stock : "
                + nbMinerais + "/" + capaciteStockage);
    }

    public void deposer() {
        Secteur secteurActuel = getSecteur();
        Entrepot entrepot = secteurActuel.getEntrepot();

        if (entrepot == null) {
            System.out.println("Action invalide : pas d'entrepot sur ce secteur !");
            return;
        }
        if (entrepot.getType() != type) {
            System.out.println("Action invalide : ce n'est pas le bon entrepot !");
            return;
        }
        if (nbMinerais == 0) {
            System.out.println("Action invalide : le robot ne porte rien !");
            return;
        }

        entrepot.deposer(nbMinerais);
        System.out.println("Robot " + getNumero() + " a depose " + nbMinerais + " minerai(s).");
        nbMinerais = 0;
    }

    /**
     * Demande à l'utilisateur quelle action effectuer (SAÉ 2.01).
     * Affiche le menu, lit le choix et exécute l'action correspondante.
     * @param grille la grille du monde
     * @param scanner le scanner pour lire l'entrée utilisateur
     */
    public void choisirAction(Secteur[][] grille, Scanner scanner) {
        System.out.println("\n--- Robot R" + getNumero()
                + " (" + (type == TypeMinerai.OR ? "OR" : "NI") + ")"
                + "  Minerais : " + nbMinerais + "/" + capaciteStockage
                + "  Position : (" + getSecteur().getLigne() + "," + getSecteur().getColonne() + ")");
        System.out.println("1-NORD  2-SUD  3-EST  4-OUEST  5-RECOLTER  6-DEPOSER  0-PASSER");
        System.out.print("Votre choix : ");

        int choix = scanner.nextInt();

        switch (choix) {
            case 1: avancer(Directions.NORD, grille);  break;
            case 2: avancer(Directions.SUD, grille);   break;
            case 3: avancer(Directions.EST, grille);   break;
            case 4: avancer(Directions.OUEST, grille); break;
            case 5: recolter(); break;
            case 6: deposer();  break;
            case 0: System.out.println("Robot " + getNumero() + " passe son tour."); break;
            default: System.out.println("Choix invalide, le robot passe son tour."); break;
        }
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public TypeMinerai getType() { return type; }
    public int getNbMinerais()   { return nbMinerais; }
    public int getCapaciteStockage()   { return capaciteStockage; }
    public int getCapaciteExtraction() { return capaciteExtraction; }

    @Override
    public String afficher() {
        return "R" + getNumero();
    }

    @Override
    public String toString() {
        String typeStr = (type == TypeMinerai.OR) ? "OR" : "NI";
        return "R" + getNumero()
                + " " + getSecteur().getLigne()
                + " " + getSecteur().getColonne()
                + " " + typeStr
                + " " + nbMinerais + " / " + capaciteStockage;
    }
}

