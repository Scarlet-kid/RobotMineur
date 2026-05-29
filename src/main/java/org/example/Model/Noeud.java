package org.example.Model;


public class Noeud implements Comparable<Noeud> {

    // La case de la grille que ce noeud représente
    private final Secteur secteur;

    // Sert à retracer le chemin à la fin
    private Noeud parent;

    // g = nombre de cases parcourues depuis le départ jusqu'ici
    private int g;

    private int h;


    // Plus f est petit, plus A* explore ce noeud en premier
    private int f;

    public Noeud(Secteur secteur, Noeud parent, int g, int h) {
        this.secteur = secteur;
        this.parent  = parent;
        this.g       = g;
        this.h       = h;
        this.f       = g + h; // f se calcule automatiquement
    }

    /**
     * Permet à la PriorityQueue de trier les noeuds automatiquement.
     * Elle compare deux noeuds par leur f :
     *   - si this.f < autre.f → this passe en premier (retourne négatif)
     *   - si this.f > autre.f → autre passe en premier (retourne positif)
     *   - si égal → retourne 0
     */
    @Override
    public int compareTo(Noeud autre) {
        return Integer.compare(this.f, autre.f);
    }

    public Secteur getSecteur() { return secteur; }
    public Noeud   getParent()  { return parent;  }
    public int     getG()       { return g;        }
    public int     getH()       { return h;        }
    public int     getF()       { return f;        }



    public void setParent(Noeud parent) {
        this.parent = parent;
    }

    public void setG(int g) {
        this.g = g;
        this.f = g + h;
    }
}