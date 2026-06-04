package org.example.Model;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Robot extends Composantes {

    private TypeMinerai type;
    private int nbMinerais;
    private final int capaciteStockage;
    private final int capaciteExtraction;

    private List<Directions> chemin;
    private Secteur destination;

    // --- VARIABLES DE GESTION DES CONFLITS ---
    private int cooldownActuel = 0;       // Temps de pause du robot
    private int echecsConsecutifs = 0;    // Compteur de collisions
    private int toursDeFuite = 0;         // Temps passé à fuir un bouchon
    private static final Random rand = new Random();

    public Robot(int numero,
                 Secteur secteur,
                 TypeMinerai type,
                 int capaciteStockage,
                 int capaciteExtraction) {
        super(numero, secteur);
        this.type               = type;
        this.nbMinerais         = 0;
        this.capaciteStockage   = capaciteStockage;
        this.capaciteExtraction = capaciteExtraction;
        this.chemin             = null;
        this.destination        = null;
    }

    public void agirAutonome(Secteur[][] grille, List<Mine> mines, List<Entrepot> entrepots) {

        // ÉTAPE 0 : Gestion de la patience (Pause suite à une collision)
        if (cooldownActuel > 0) {
            cooldownActuel--;
            return;
        }

        int maLigne = secteur.getLigne();
        int maColonne = secteur.getColonne();

        // ÉTAPE 1 : Récolter
        for (Mine m : mines) {
            if (m.getSecteur().getLigne() == maLigne && m.getSecteur().getColonne() == maColonne) {
                if (m.getType() == type && !m.estVide() && nbMinerais < capaciteStockage) {
                    int espaceDisponible = capaciteStockage - nbMinerais;
                    int qteARecolter = Math.min(capaciteExtraction, espaceDisponible);
                    int qteReelle = m.extraire(qteARecolter);
                    nbMinerais += qteReelle;
                    System.out.println("Robot " + numero + " a recolte " + qteReelle + " minerai(s). Stock : " + nbMinerais + "/" + capaciteStockage);
                    resetTrajetEtConflits();
                    return;
                }
            }
        }

        // ÉTAPE 2 : Déposer
        for (Entrepot e : entrepots) {
            if (e.getSecteur().getLigne() == maLigne && e.getSecteur().getColonne() == maColonne) {
                if (e.getType() == type && nbMinerais > 0) {
                    e.deposer(nbMinerais);
                    System.out.println("Robot " + numero + " a depose " + nbMinerais + " minerai(s). Stock : 0");
                    nbMinerais = 0;
                    resetTrajetEtConflits();
                    return;
                }
            }
        }

        // ÉTAPE 3 : Choix de l'objectif et calcul du CHEMIN OPTIMAL (100% Dijkstra)
        if (chemin == null || chemin.isEmpty()) {

            if (toursDeFuite > 0) {
                // Le robot fuit un bouchon
                destination = trouverCaseVideAleatoire(grille);
                if (destination != null) chemin = Dijkstra.calculerChemin(grille, secteur, destination);

            } else if (nbMinerais >= capaciteStockage || (nbMinerais > 0 && toutesMinesVides(mines))) {
                // Cherche le vrai chemin le plus court vers un entrepôt
                chemin = chercherMeilleurCheminEntrepot(grille, entrepots);

            } else if (nbMinerais == 0 && toutesMinesVides(mines)) {
                // REGLE ANTI-CAMPING : Va se garer loin pour libérer l'espace
                destination = trouverCaseVideAleatoire(grille);
                if (destination != null) chemin = Dijkstra.calculerChemin(grille, secteur, destination);

            } else {
                // Cherche le vrai chemin le plus court vers une mine
                chemin = chercherMeilleurCheminMine(grille, mines);
            }

            // Si après tout ça, il n'y a toujours pas de chemin valide
            if (chemin == null || chemin.isEmpty()) {
                chemin = null;
                cooldownActuel = 2; // Attente simple
                return;
            }
        }

        // ÉTAPE 4 : Tenter d'avancer
        Directions prochaineDirection = chemin.get(0);
        boolean aAvance = avancer(prochaineDirection, grille);

        if (aAvance) {
            chemin.remove(0);
            echecsConsecutifs = 0;
            if (toursDeFuite > 0) toursDeFuite--; // Baisse le temps de fuite s'il fuyait
        } else {
            // COLLISION
            chemin = null;
            echecsConsecutifs++;

            if (echecsConsecutifs >= 3) {
                System.out.println("Robot " + numero + " est dans un bouchon ! Dégagement en cours.");
                echecsConsecutifs = 0;
                toursDeFuite = 5; // Force le robot à partir loin pendant 5 tours
                cooldownActuel = 0;
            } else {
                cooldownActuel = 1 + rand.nextInt(3); // Attente asymétrique (1 à 3 tours)
                System.out.println("Robot " + numero + " percute un obstacle. Pause de " + cooldownActuel + " tours.");
            }
        }
    }

    // --- NOUVELLES MÉTHODES 100% DIJKSTRA ---

    /**
     * Teste tous les chemins possibles vers les mines valides via Dijkstra
     * et conserve uniquement le trajet le plus court.
     */
    private List<Directions> chercherMeilleurCheminMine(Secteur[][] grille, List<Mine> mines) {
        List<Directions> meilleurChemin = null;
        int distanceMin = Integer.MAX_VALUE;

        for (Mine m : mines) {
            if (m.getType() != type || m.estVide()) continue;

            List<Directions> cheminTest = Dijkstra.calculerChemin(grille, secteur, m.getSecteur());

            if (cheminTest != null && !cheminTest.isEmpty() && cheminTest.size() < distanceMin) {
                distanceMin = cheminTest.size();
                meilleurChemin = cheminTest;
                destination = m.getSecteur(); // Met à jour la cible
            }
        }
        return meilleurChemin;
    }

    /**
     * Teste tous les chemins possibles vers les entrepôts valides via Dijkstra.
     */
    private List<Directions> chercherMeilleurCheminEntrepot(Secteur[][] grille, List<Entrepot> entrepots) {
        List<Directions> meilleurChemin = null;
        int distanceMin = Integer.MAX_VALUE;

        for (Entrepot e : entrepots) {
            if (e.getType() != type) continue;

            List<Directions> cheminTest = Dijkstra.calculerChemin(grille, secteur, e.getSecteur());

            if (cheminTest != null && !cheminTest.isEmpty() && cheminTest.size() < distanceMin) {
                distanceMin = cheminTest.size();
                meilleurChemin = cheminTest;
                destination = e.getSecteur();
            }
        }
        return meilleurChemin;
    }

    // --- MÉTHODES UTILITAIRES PRIVÉES ---

    private void resetTrajetEtConflits() {
        chemin = null;
        destination = null;
        echecsConsecutifs = 0;
        cooldownActuel = 0;
        toursDeFuite = 0;
    }

    private Secteur trouverCaseVideAleatoire(Secteur[][] grille) {
        int maxTentatives = 50;
        for (int i = 0; i < maxTentatives; i++) {
            int l = rand.nextInt(grille.length);
            int c = rand.nextInt(grille[0].length);
            Secteur s = grille[l][c];
            if (!s.EstEau() && s.getComposante() == null && s.getRobot() == null) {
                return s;
            }
        }
        return null;
    }

    private boolean toutesMinesVides(List<Mine> mines) {
        for (Mine m : mines) {
            if (m.getType() == type && !m.estVide()) return false;
        }
        return true;
    }

    // --- MÉTHODES SAE 2.01 INCHANGÉES ---

    public boolean avancer(Directions dir, Secteur[][] grille) {
        if (secteur == null) return false;
        int nouvelleLigne = secteur.getLigne();
        int nouvelleColonne = secteur.getColonne();

        if (dir == Directions.NORD) nouvelleLigne--;
        else if (dir == Directions.SUD) nouvelleLigne++;
        else if (dir == Directions.EST) nouvelleColonne++;
        else if (dir == Directions.OUEST) nouvelleColonne--;

        if (nouvelleLigne < 0 || nouvelleLigne >= grille.length || nouvelleColonne < 0 || nouvelleColonne >= grille[0].length) return false;

        Secteur cible = grille[nouvelleLigne][nouvelleColonne];
        if (cible.EstEau()) return false;
        if (cible.getRobot() != null) return false;

        secteur.setRobot(null);
        cible.setRobot(this);
        this.secteur = cible;

        System.out.printf("Robot %d avance vers %s%n", numero, dir);
        return true;
    }

    public boolean recolter() { return false; }
    public boolean deposer() { return false; }
    public void choisirAction(Secteur[][] grille, Scanner scanner) {}

    // --- GETTERS / SETTERS ---
    public int getNumero() { return numero; }
    public TypeMinerai getType() { return type; }
    public int getNbMinerais() { return nbMinerais; }
    public int getCapaciteStockage() { return capaciteStockage; }
    public int getCapaciteExtraction() { return capaciteExtraction; }
    public void SetNbMinerais(int nb) { this.nbMinerais = nb; }

    @Override public String afficher() { return "R" + numero; }

    @Override
    public String toString() {
        String typeStr = (type == TypeMinerai.OR) ? "OR" : "NI";
        return "R" + numero + " " + secteur.getLigne() + " " + secteur.getColonne() + " " + typeStr + " " + nbMinerais + " / " + capaciteStockage;
    }
}