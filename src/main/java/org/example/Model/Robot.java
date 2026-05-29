package org.example.Model;

import java.util.List;
import java.util.Scanner;

public class Robot extends Composantes {

    private TypeMinerai type;
    private int nbMinerais;
    private final int capaciteStockage;
    private final int capaciteExtraction;

    // Chemin calculé par A* : liste de secteurs à traverser
    private List<Secteur> chemin;

    // Destination actuelle du robot
    private Secteur destination;

    // L'algo A*
    private Astar astar;

    // Compteur anti-blocage
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
        this.astar              = null;
        this.toursBloques       = 0;
    }

    // ----------------------------------------------------------------
    // MÉTHODE AUTONOME PRINCIPALE
    // ----------------------------------------------------------------

    public void agirAutonome(Secteur[][] grille, List<Mine> mines, List<Entrepot> entrepots) {

        if (astar == null) {
            astar = new Astar(grille);
        }

        Composantes c = secteur.getComposante();

        // ÉTAPE 1 : on est sur une mine → on récolte
        if (c instanceof Mine) {
            Mine m = (Mine) c;
            if (m.getType() == type && !m.estVide() && nbMinerais < capaciteStockage) {
                recolter();
                chemin       = null;
                destination  = null;
                toursBloques = 0;
                return;
            }
        }

        // ÉTAPE 2 : on est sur notre entrepôt → on dépose
        if (c instanceof Entrepot) {
            Entrepot e = (Entrepot) c;
            if (e.getType() == type && nbMinerais > 0) {
                deposer();
                chemin       = null;
                destination  = null;
                toursBloques = 0;
                return;
            }
        }

        // ÉTAPE 3 : trop bloqué → on passe le tour
        if (toursBloques >= MAX_TENTATIVES) {
            System.out.println("Robot " + numero + " passe son tour.");
            chemin       = null;
            destination  = null;
            toursBloques = 0;
            return;
        }

        // ÉTAPE 4 : pas de chemin → on calcule UNE SEULE FOIS avec A*
        if (chemin == null || chemin.isEmpty()) {

            if (nbMinerais >= capaciteStockage || toutesMinesVides(mines)) {
                // Aller déposer
                destination = trouverEntrepot(entrepots);
                if (destination == null) {
                    toursBloques++;
                    return;
                }
                chemin = astar.trouverChemin(secteur, destination, this);

            } else {
                // Trouver la mine la plus proche avec UN SEUL appel A* par mine
                // On garde directement le chemin le plus court trouvé
                chemin      = null;
                destination = null;
                int distMin = Integer.MAX_VALUE;

                for (Mine m : mines) {
                    if (m.getType() != type || m.estVide()) continue;

                    // UN appel A* vers cette mine
                    List<Secteur> cheminVers = astar.trouverChemin(secteur, m.getSecteur(), this);

                    if (!cheminVers.isEmpty() && cheminVers.size() < distMin) {
                        distMin     = cheminVers.size();
                        destination = m.getSecteur();
                        chemin      = cheminVers; // on garde directement ce chemin
                    }
                }
            }

            // Aucun chemin trouvé
            if (chemin == null || chemin.isEmpty()) {
                System.out.println("Robot " + numero + " : pas de chemin.");
                chemin       = null;
                destination  = null;
                toursBloques++;
                return;
            }

            // On enlève le premier secteur (position actuelle)
            if (!chemin.isEmpty()) {
                chemin.remove(0);
            }

            // Après suppression, chemin vide = déjà à destination
            if (chemin.isEmpty()) {
                chemin = null;
                return;
            }

            toursBloques = 0;
        }

        // ÉTAPE 5 : avancer d'une case
        Secteur prochainSecteur = chemin.remove(0);
        Directions direction    = calculerDirection(secteur, prochainSecteur);

        if (direction == null) {
            chemin = null;
            toursBloques++;
            return;
        }

        boolean aAvance = avancer(direction, grille);

        if (!aAvance) {
            chemin = null; // recalcul au prochain tour
            toursBloques++;
        } else {
            toursBloques = 0;
        }
    }

    // ----------------------------------------------------------------
    // MÉTHODES UTILITAIRES PRIVÉES
    // ----------------------------------------------------------------

    private Directions calculerDirection(Secteur depuis, Secteur vers) {
        int dl = vers.getLigne()   - depuis.getLigne();
        int dc = vers.getColonne() - depuis.getColonne();

        if (dl == -1) return Directions.NORD;
        if (dl ==  1) return Directions.SUD;
        if (dc ==  1) return Directions.EST;
        if (dc == -1) return Directions.OUEST;

        return null;
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

        if (cible.EstEau()) {
            System.out.println("Action invalide : plan d'eau !");
            return false;
        }

        if (cible.getRobot() != null) {
            System.out.println("Action invalide : secteur occupé !");
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
            System.out.println("Action invalide : pas de mine !");
            return false;
        }
        Mine mine = (Mine) c;
        if (mine.getType() != type) {
            System.out.println("Action invalide : mauvais type !");
            return false;
        }
        if (mine.estVide()) {
            System.out.println("Action invalide : mine vide !");
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
            System.out.println("Action invalide : pas d'entrepot !");
            return false;
        }
        Entrepot entrepot = (Entrepot) c;
        if (entrepot.getType() != type) {
            System.out.println("Action invalide : mauvais entrepot !");
            return false;
        }
        if (nbMinerais == 0) {
            System.out.println("Action invalide : rien à déposer !");
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