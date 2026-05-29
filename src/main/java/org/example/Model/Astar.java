package org.example.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class Astar {

    // La grille du monde (10x10 de Secteurs)
    private final Secteur[][] grille;

    private static final int[][] VOISINS = {
            {-1,  0},
            { 1,  0},
            { 0,  1},
            { 0, -1}
    };

    public Astar(Secteur[][] grille) {
        this.grille = grille;
    }



    public List<Secteur> trouverChemin(Secteur depart, Secteur arrivee, Robot robotCourant) {

        // openList = les noeuds qu'on doit encore explorer
        // PriorityQueue = elle trie automatiquement par f croissant grâce à Noeud.compareTo()
        // Donc openList.poll() nous donne toujours le noeud avec le plus petit f
        PriorityQueue<Noeud> openList = new PriorityQueue<>();

        Map<String, Integer> meilleurG = new HashMap<>();

        int hDepart = heuristique(depart, arrivee);
        Noeud noeudDepart = new Noeud(depart, null, 0, hDepart);

        openList.add(noeudDepart);
        meilleurG.put(cleDeSection(depart), 0);

        while (!openList.isEmpty()) {

            // On prend le noeud avec le f le plus petit
            // (PriorityQueue le fait automatiquement grâce à compareTo)
            Noeud courant = openList.poll();

            // Si on est arrivé à destination → on reconstruit et retourne le chemin
            if (courant.getSecteur() == arrivee) {
                return reconstruireChemin(courant);
            }

            // Sinon on explore les 4 voisins du noeud courant
            for (int[] delta : VOISINS) {

                // Calcul des coordonnées du voisin
                int nouvelleLigne   = courant.getSecteur().getLigne()   + delta[0];
                int nouvelleColonne = courant.getSecteur().getColonne() + delta[1];

                // On vérifie que le voisin est bien dans la grille 10x10
                if (!estDansGrille(nouvelleLigne, nouvelleColonne)) {
                    continue;
                }

                Secteur voisin = grille[nouvelleLigne][nouvelleColonne];

                if (!estFranchissable(voisin, arrivee, robotCourant)) {
                    continue;
                }

                // Le coût pour aller au voisin = coût du courant + 1 déplacement
                int nouveauG = courant.getG() + 1;
                String cleVoisin = cleDeSection(voisin);

                if (!meilleurG.containsKey(cleVoisin) || nouveauG < meilleurG.get(cleVoisin)) {

                    // On mémorise ce nouveau meilleur g pour ce voisin
                    meilleurG.put(cleVoisin, nouveauG);

                    // On calcule h pour ce voisin
                    int h = heuristique(voisin, arrivee);

                    // On crée le noeud voisin avec le noeud courant comme parent
                    Noeud noeudVoisin = new Noeud(voisin, courant, nouveauG, h);

                    // On l'ajoute à explorer
                    openList.add(noeudVoisin);
                }
            }
        }

        return new ArrayList<>();
    }


    // HEURISTIQUE : distance de Manhattan

    /**
     * On utilise la distance Manhattan : |ligneA - ligneB| + |colonneA - colonneB|
     * Exemple : secteur (2,3) vers secteur (5,7)
     *   → |2-5| + |3-7| = 3 + 4 = 7 cases minimum
     */
    private int heuristique(Secteur a, Secteur b) {
        return Math.abs(a.getLigne()   - b.getLigne())
                + Math.abs(a.getColonne() - b.getColonne());
    }

    // VÉRIFICATIONS

    private boolean estFranchissable(Secteur s, Secteur arrivee, Robot robotCourant) {
        // Règle 1 : pas d'eau
        if (s.EstEau()) {
            return false;
        }
        // Règle 2 : pas un autre robot dessus (sauf si c'est la destination)
        if (s != arrivee && s.getRobot() != null && s.getRobot() != robotCourant) {
            return false;
        }
        return true;
    }

    /**
     * Vérifie que les coordonnées sont bien dans la grille 10x10.
     * Evite les IndexOutOfBoundsException.
     */
    private boolean estDansGrille(int ligne, int colonne) {
        return ligne   >= 0 && ligne   < grille.length
                && colonne >= 0 && colonne < grille[0].length;
    }


    // RECONSTRUCTION DU CHEMIN

    private List<Secteur> reconstruireChemin(Noeud noeudArrivee) {
        List<Secteur> chemin = new ArrayList<>();
        Noeud courant = noeudArrivee;

        // On remonte de l'arrivée vers le départ via les parents
        while (courant != null) {
            chemin.add(courant.getSecteur());
            courant = courant.getParent();
        }

        Collections.reverse(chemin);
        return chemin;
    }
    /**
     * Utilisé comme clé dans le HashMap meilleurG.
     */
    private String cleDeSection(Secteur s) {
        return s.getLigne() + "," + s.getColonne();
    }
}