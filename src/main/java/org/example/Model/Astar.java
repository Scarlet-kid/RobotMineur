package org.example.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Astar {

    private final Secteur[][] grille;

    // Limite max de noeuds explorés par appel A*
    // Sur une grille 10x10 = 100 cases max, 200 est largement suffisant
    private static final int MAX_NOEUDS = 200;

    private static final int[][] VOISINS = {
            {-1,  0},
            { 1,  0},
            { 0,  1},
            { 0, -1}
    }; // les voisins.

    public Astar(Secteur[][] grille) {
        this.grille = grille;
    } // le constructeur.

    public List<Secteur> trouverChemin(Secteur depart, Secteur arrivee, Robot robotCourant) {

        if (depart == arrivee) {
            return new ArrayList<>(); // une liste vide.
        }

        PriorityQueue<Noeud> openList = new PriorityQueue<>();
        Map<String, Integer> meilleurG = new HashMap<>(); // un dictionnaire.

        // Compteur de noeuds explorés
        int nbNoeudsExplores = 0; // techniquement un noeud c'est un sommet et dans notre contexte.

        int hDepart = heuristique(depart, arrivee);
        Noeud noeudDepart = new Noeud(depart, null, 0, hDepart);

        openList.add(noeudDepart);
        meilleurG.put(cleDeSection(depart), 0);

        while (!openList.isEmpty()) {

            // Si on a trop exploré → on arrête pour ne pas saturer la mémoire
            if (nbNoeudsExplores >= MAX_NOEUDS) { // je pense on pourrait diminuer le nb de noeud max.
                return new ArrayList<>();
            }

            Noeud courant = openList.poll();
            nbNoeudsExplores++;

            if (courant.getSecteur() == arrivee) {
                return reconstruireChemin(courant);
            }

            for (int[] delta : VOISINS) {

                int nouvelleLigne   = courant.getSecteur().getLigne()   + delta[0];
                int nouvelleColonne = courant.getSecteur().getColonne() + delta[1];

                if (!estDansGrille(nouvelleLigne, nouvelleColonne)) {
                    continue;
                }

                Secteur voisin = grille[nouvelleLigne][nouvelleColonne];

                if (!estFranchissable(voisin, arrivee, robotCourant)) {
                    continue;
                }

                int nouveauG = courant.getG() + 1;
                String cleVoisin = cleDeSection(voisin);

                if (!meilleurG.containsKey(cleVoisin) || nouveauG < meilleurG.get(cleVoisin)) {
                    meilleurG.put(cleVoisin, nouveauG);
                    int h = heuristique(voisin, arrivee);
                    Noeud noeudVoisin = new Noeud(voisin, courant, nouveauG, h);
                    openList.add(noeudVoisin);
                }
            }
        }

        return new ArrayList<>();
    }

    private int heuristique(Secteur a, Secteur b) {
        return Math.abs(a.getLigne()   - b.getLigne())
                + Math.abs(a.getColonne() - b.getColonne());
    }

    private boolean estFranchissable(Secteur s, Secteur arrivee, Robot robotCourant) {
        if (s.EstEau()) return false;
        if (s != arrivee && s.getRobot() != null && s.getRobot() != robotCourant) return false;
        return true;
    }

    private boolean estDansGrille(int ligne, int colonne) {
        return ligne   >= 0 && ligne   < grille.length
                && colonne >= 0 && colonne < grille[0].length;
    }

    private List<Secteur> reconstruireChemin(Noeud noeudArrivee) {
        List<Secteur> chemin = new ArrayList<>();
        Noeud courant = noeudArrivee;
        while (courant != null) {
            chemin.add(courant.getSecteur());
            courant = courant.getParent();
        }
        Collections.reverse(chemin);
        return chemin;
    }

    private String cleDeSection(Secteur s) {
        return s.getLigne() + "," + s.getColonne();
    }
}