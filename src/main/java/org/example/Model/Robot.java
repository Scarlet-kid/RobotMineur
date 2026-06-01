package org.example.Model;

import java.util.List;
import java.util.Scanner;

public class Robot extends Composantes {

    private TypeMinerai type;
    private int nbMinerais;
    private final int capaciteStockage;
    private final int capaciteExtraction;

    // Chemin calculé par Dijkstra : liste de directions à suivre
    private List<Directions> chemin;

    // Destination actuelle du robot
    private Secteur destination;

    // Compteur anti-blocage (Patience)
    private int toursBloques;
    private static final int MAX_TENTATIVES = 5;

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
        this.toursBloques       = 0;
    }

    // ----------------------------------------------------------------
    // MÉTHODE AUTONOME PRINCIPALE (Avec Dijkstra)
    // ----------------------------------------------------------------

    public void agirAutonome(Secteur[][] grille, List<Mine> mines, List<Entrepot> entrepots) {

        // ÉTAPE 0 : Si on a trop forcé et qu'on est bloqué, on passe son tour pour respirer
        if (toursBloques >= MAX_TENTATIVES) {
            System.out.println("Robot " + numero + " passe son tour (trop de tentatives).");
            chemin       = null;
            destination  = null;
            toursBloques = 0; // On réinitialise pour le tour prochain
            return;
        }

        int maLigne = secteur.getLigne();
        int maColonne = secteur.getColonne();

        // ÉTAPE 1 : Vérifier si on est sur une MINE (par coordonnées) -> on récolte
        for (Mine m : mines) {
            if (m.getSecteur().getLigne() == maLigne && m.getSecteur().getColonne() == maColonne) {
                if (m.getType() == type && !m.estVide() && nbMinerais < capaciteStockage) {

                    int espaceDisponible = capaciteStockage - nbMinerais;
                    int qteARecolter = Math.min(capaciteExtraction, espaceDisponible);
                    int qteReelle = m.extraire(qteARecolter);
                    nbMinerais += qteReelle;

                    System.out.println("Robot " + numero + " a recolte " + qteReelle + " minerai(s). Stock : " + nbMinerais + "/" + capaciteStockage);

                    chemin = null;
                    destination = null;
                    toursBloques = 0;
                    return; // Fin du tour
                }
            }
        }

        // ÉTAPE 2 : Vérifier si on est sur un ENTREPÔT (par coordonnées) -> on dépose
        for (Entrepot e : entrepots) {
            if (e.getSecteur().getLigne() == maLigne && e.getSecteur().getColonne() == maColonne) {
                if (e.getType() == type && nbMinerais > 0) {
                    e.deposer(nbMinerais);
                    System.out.println("Robot " + numero + " a depose " + nbMinerais + " minerai(s). Stock : 0");

                    nbMinerais = 0;
                    chemin = null;
                    destination = null;
                    toursBloques = 0;
                    return; // Fin du tour
                }
            }
        }

        // ÉTAPE 3 : Calculer la destination et le chemin (Seulement si on n'a pas de chemin en cours)
        if (chemin == null || chemin.isEmpty()) {

            if (nbMinerais >= capaciteStockage || (nbMinerais > 0 && toutesMinesVides(mines))) {
                destination = trouverEntrepot(entrepots);
            } else {
                destination = trouverMineLaPlusProche(mines); // Utilise la distance de Manhattan ultra-rapide
            }

            if (destination == null) {
                // Pas d'objectif disponible (tout est vide)
                toursBloques++;
                return;
            }

            // --- APPEL À DIJKSTRA ICI ---
            chemin = Dijkstra.calculerChemin(grille, secteur, destination);

            if (chemin == null || chemin.isEmpty()) {
                System.out.println("Robot " + numero + " : bloqué, pas de chemin possible vers l'objectif.");
                chemin = null;
                toursBloques++;
                return;
            }

            toursBloques = 0;
        }

        // ÉTAPE 4 : Avancer d'une case en suivant le chemin
        Directions prochaineDirection = chemin.get(0); // On lit la première direction de la liste
        boolean aAvance = avancer(prochaineDirection, grille);

        if (!aAvance) {
            // Collision (ex: un autre robot est sur la case) !
            chemin = null; // On vide le chemin pour forcer un recalcul de Dijkstra au tour suivant
            toursBloques++;
        } else {
            // Le pas est validé, on l'enlève de la liste de choses à faire
            chemin.remove(0);
            toursBloques = 0;
        }
    }

    // ----------------------------------------------------------------
    // MÉTHODES UTILITAIRES PRIVÉES
    // ----------------------------------------------------------------

    /**
     * Trouve la mine compatible la plus proche en utilisant la distance de Manhattan.
     * C'est instantané pour le processeur.
     */
    private Secteur trouverMineLaPlusProche(List<Mine> mines) {
        Secteur meilleure = null;
        int distMin = Integer.MAX_VALUE;

        for (Mine m : mines) {
            if (m.getType() != type || m.estVide()) continue;

            int distanceM = Math.abs(m.getSecteur().getLigne() - secteur.getLigne())
                    + Math.abs(m.getSecteur().getColonne() - secteur.getColonne());

            if (distanceM < distMin) {
                distMin = distanceM;
                meilleure = m.getSecteur();
            }
        }
        return meilleure;
    }

    private Secteur trouverEntrepot(List<Entrepot> entrepots) {
        for (Entrepot e : entrepots) {
            if (e.getType() == type) return e.getSecteur();
        }
        return null;
    }

    private boolean toutesMinesVides(List<Mine> mines) {
        for (Mine m : mines) {
            if (m.getType() == type && !m.estVide()) return false;
        }
        return true;
    }

    // ----------------------------------------------------------------
    // MÉTHODES SAE 2.01 INCHANGÉES
    // ----------------------------------------------------------------

    public boolean avancer(Directions dir, Secteur[][] grille) {
        if (secteur == null) return false;

        int nouvelleLigne   = secteur.getLigne();
        int nouvelleColonne = secteur.getColonne();

        if      (dir == Directions.NORD)  nouvelleLigne--;
        else if (dir == Directions.SUD)   nouvelleLigne++;
        else if (dir == Directions.EST)   nouvelleColonne++;
        else if (dir == Directions.OUEST) nouvelleColonne--;

        if (nouvelleLigne < 0 || nouvelleLigne >= grille.length
                || nouvelleColonne < 0 || nouvelleColonne >= grille[0].length) {
            System.out.println("Action invalide : hors du monde !");
            return false;
        }

        Secteur cible = grille[nouvelleLigne][nouvelleColonne];

        // RAPPEL : Assure-toi que cela correspond au nom de ta méthode (EstEau() ou estEau())
        if (cible.EstEau()) {
            System.out.println("Action invalide : plan d'eau !");
            return false;
        }

        if (cible.getRobot() != null) {
            return false; // Silencieux pour ne pas spammer la console lors des embouteillages
        }

        secteur.setRobot(null);
        cible.setRobot(this);
        this.secteur = cible;

        System.out.printf("Robot %d avance vers %s%n", numero, dir);
        return true;
    }

    public boolean recolter() {
        // Gardée pour compatibilité avec le mode manuel si besoin
        return false;
    }

    public boolean deposer() {
        // Gardée pour compatibilité avec le mode manuel si besoin
        return false;
    }

    public void choisirAction(Secteur[][] grille, Scanner scanner) {
        boolean actionEffectuee = false;
        while (!actionEffectuee) {
            System.out.println("\n--- Robot R" + numero
                    + " (" + (type == TypeMinerai.OR ? "OR" : "NI") + ")"
                    + "  Minerais : " + nbMinerais + "/" + capaciteStockage
                    + "  Position : (" + secteur.getLigne() + "," + secteur.getColonne() + ")");
            System.out.println("1-NORD  2-SUD  3-EST  4-OUEST  0-PASSER");
            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            switch (choix) {
                case 1: actionEffectuee = avancer(Directions.NORD,  grille); break;
                case 2: actionEffectuee = avancer(Directions.SUD,   grille); break;
                case 3: actionEffectuee = avancer(Directions.EST,   grille); break;
                case 4: actionEffectuee = avancer(Directions.OUEST, grille); break;
                case 0:
                    System.out.println("Robot " + numero + " passe son tour.");
                    actionEffectuee = true;
                    break;
                default:
                    System.out.println("Choix invalide.");
                    actionEffectuee = true;
                    break;
            }
        }
    }

    // ----------------------------------------------------------------
    // GETTERS / SETTERS
    // ----------------------------------------------------------------

    public int         getNumero()             { return numero;             }
    public TypeMinerai getType()               { return type;               }
    public int         getNbMinerais()         { return nbMinerais;         }
    public int         getCapaciteStockage()   { return capaciteStockage;   }
    public int         getCapaciteExtraction() { return capaciteExtraction; }
    public void        SetNbMinerais(int nb)   { this.nbMinerais = nb;      }

    @Override public String afficher() { return "R" + numero; }

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