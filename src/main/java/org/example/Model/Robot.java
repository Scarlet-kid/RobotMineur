package org.example.Model;

import java.util.List;
import java.util.Scanner;

public class Robot extends Composantes {

    private TypeMinerai type;
    private int nbMinerais;
    private final int capaciteStockage;
    private final int capaciteExtraction;

    // --- NOUVEAUX ATTRIBUTS POUR LA SAE 2.02 ---

    // Le chemin actuel : liste de Secteurs à traverser calculée par A*
    // null ou vide = pas encore de chemin, on en calculera un au prochain tour
    private List<Secteur> chemin;

    // La destination actuelle du robot (secteur d'une mine ou d'un entrepôt)
    private Secteur destination;

    // L'algorithme A* utilisé pour calculer les chemins
    private Astar astar;

    // ----------------------------------------------------------------
    // CONSTRUCTEUR
    // ----------------------------------------------------------------

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
        this.astar              = null; // sera initialisé quand on a la grille
    }

    // ----------------------------------------------------------------
    // MÉTHODE AUTONOME PRINCIPALE (SAE 2.02)
    // ----------------------------------------------------------------

    /**
     * Le robot décide seul de ce qu'il fait à chaque tour.
     *
     * Logique :
     *   Étape 1 : Si on est sur une mine de notre type → on récolte
     *   Étape 2 : Si on est sur notre entrepôt         → on dépose
     *   Étape 3 : Si on n'a pas de chemin              → on choisit une destination
     *             et on calcule le chemin avec A*
     *   Étape 4 : Si on a un chemin                    → on avance d'une case
     *
     * @param grille    la grille du monde
     * @param mines     la liste de toutes les mines
     * @param entrepots la liste de tous les entrepôts
     */
    public void agirAutonome(Secteur[][] grille, List<Mine> mines, List<Entrepot> entrepots) {

        // On initialise A* avec la grille si ce n'est pas encore fait
        if (astar == null) {
            astar = new Astar(grille);
        }

        Composantes c = secteur.getComposante();

        // --- ÉTAPE 1 : on est sur une mine de notre type → on récolte ---
        if (c instanceof Mine) {
            Mine m = (Mine) c;
            if (m.getType() == type && !m.estVide() && nbMinerais < capaciteStockage) {
                recolter();
                // On efface le chemin : on est arrivé, on recalculera au prochain tour
                chemin      = null;
                destination = null;
                return;
            }
        }

        // --- ÉTAPE 2 : on est sur notre entrepôt → on dépose ---
        if (c instanceof Entrepot) {
            Entrepot e = (Entrepot) c;
            if (e.getType() == type && nbMinerais > 0) {
                deposer();
                // On efface le chemin : on recalculera vers une mine
                chemin      = null;
                destination = null;
                return;
            }
        }

        // --- ÉTAPE 3 : pas de chemin → on choisit une destination et on calcule ---
        if (chemin == null || chemin.isEmpty()) {

            // Choix de la destination selon l'état du robot
            if (nbMinerais >= capaciteStockage || toutesMinesVides(mines)) {
                // Stockage plein ou plus de mines → aller déposer
                destination = trouverEntrepot(entrepots);
            } else {
                // Aller à la mine la plus proche
                destination = trouverMineLaPlusProche(mines, grille);
            }

            // Aucune destination trouvée → le robot attend ce tour
            if (destination == null) {
                System.out.println("Robot " + numero + " attend : pas de destination.");
                return;
            }

            // On calcule le chemin avec A* vers la destination
            // A* retourne une List<Secteur> : [depart, case1, case2, ..., arrivee]
            chemin = astar.trouverChemin(secteur, destination, this);

            // On enlève le premier secteur car c'est notre position actuelle
            if (!chemin.isEmpty()) {
                chemin.remove(0);
            }

            // Aucun chemin possible → on attend
            if (chemin.isEmpty()) {
                System.out.println("Robot " + numero + " bloqué : chemin impossible.");
                chemin = null;
                return;
            }
        }

        // --- ÉTAPE 4 : on suit le chemin, une case par tour ---
        // On récupère le prochain secteur à atteindre
        Secteur prochainSecteur = chemin.remove(0);

        // On détermine la direction à partir de notre position et du prochain secteur
        Directions direction = calculerDirection(secteur, prochainSecteur);

        if (direction == null) {
            // Ne devrait pas arriver, mais par sécurité on efface le chemin
            chemin = null;
            return;
        }

        boolean aAvance = avancer(direction, grille);

        // Si on est bloqué par un autre robot → on efface le chemin
        // et on recalculera un nouveau chemin au prochain tour
        if (!aAvance) {
            System.out.println("Robot " + numero + " bloqué, recalcul au prochain tour.");
            chemin = null;
        }
    }

    // ----------------------------------------------------------------
    // MÉTHODES UTILITAIRES PRIVÉES
    // ----------------------------------------------------------------

    /**
     * Calcule la direction à prendre pour aller de "depuis" vers "vers".
     * Les deux secteurs doivent être voisins (distance de 1).
     *
     * Ex : depuis=(3,4) et vers=(2,4) → on monte d'une ligne → NORD
     */
    private Directions calculerDirection(Secteur depuis, Secteur vers) {
        int dl = vers.getLigne()   - depuis.getLigne();
        int dc = vers.getColonne() - depuis.getColonne();

        if (dl == -1) return Directions.NORD;
        if (dl ==  1) return Directions.SUD;
        if (dc ==  1) return Directions.EST;
        if (dc == -1) return Directions.OUEST;

        return null; // ne devrait pas arriver
    }

    /**
     * Trouve la mine de notre type la plus proche.
     * On utilise A* pour calculer la vraie distance (pas une estimation).
     * On appelle A* une fois par mine, mais comme la grille est petite (10x10)
     * et qu'il y a au max 2 mines par type, c'est acceptable.
     *
     * @param mines  liste de toutes les mines
     * @param grille la grille du monde
     * @return le secteur de la mine la plus proche, ou null si aucune
     */
    private Secteur trouverMineLaPlusProche(List<Mine> mines, Secteur[][] grille) {
        Secteur meilleure = null;
        int distMin = Integer.MAX_VALUE;

        for (Mine m : mines) {
            // On ignore les mines du mauvais type ou vides
            if (m.getType() != type || m.estVide()) continue;

            // On calcule le vrai chemin avec A* vers cette mine
            List<Secteur> cheminVers = astar.trouverChemin(secteur, m.getSecteur(), this);

            // Mine inaccessible → on skip
            if (cheminVers.isEmpty()) continue;

            // La longueur du chemin = la vraie distance
            int dist = cheminVers.size();

            if (dist < distMin) {
                distMin   = dist;
                meilleure = m.getSecteur();
            }
        }

        return meilleure;
    }

    /**
     * Trouve l'entrepôt correspondant au type du robot.
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
     * Retourne true si toutes les mines du type du robot sont vides.
     */
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
        if (secteur == null) {
            System.out.println("Action invalide : le robot n'est pas placé sur le plateau.");
            return false;
        }

        int ligne         = secteur.getLigne();
        int colonne       = secteur.getColonne();
        int nouvelleLigne   = ligne;
        int nouvelleColonne = colonne;

        if (dir == Directions.NORD)       nouvelleLigne--;
        else if (dir == Directions.SUD)   nouvelleLigne++;
        else if (dir == Directions.EST)   nouvelleColonne++;
        else if (dir == Directions.OUEST) nouvelleColonne--;

        int maxLignes = grille.length;
        int maxCols   = grille[0].length;

        if (nouvelleLigne < 0 || nouvelleLigne >= maxLignes
                || nouvelleColonne < 0 || nouvelleColonne >= maxCols) {
            System.out.println("Action invalide : hors du monde !");
            return false;
        }

        Secteur cible = grille[nouvelleLigne][nouvelleColonne];

        if (cible.EstEau()) {
            System.out.println("Action invalide : plan d'eau !");
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
                case 1: actionEffectuee = avancer(Directions.NORD,  grille); break;
                case 2: actionEffectuee = avancer(Directions.SUD,   grille); break;
                case 3: actionEffectuee = avancer(Directions.EST,   grille); break;
                case 4: actionEffectuee = avancer(Directions.OUEST, grille); break;
                case 5: actionEffectuee = recolter();                         break;
                case 6: actionEffectuee = deposer();                          break;
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

    // ----------------------------------------------------------------
    // GETTERS / SETTERS
    // ----------------------------------------------------------------

    public int         getNumero()             { return numero;             }
    public TypeMinerai getType()               { return type;               }
    public int         getNbMinerais()         { return nbMinerais;         }
    public int         getCapaciteStockage()   { return capaciteStockage;   }
    public int         getCapaciteExtraction() { return capaciteExtraction; }

    public void SetNbMinerais(int nb) { this.nbMinerais = nb; }

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