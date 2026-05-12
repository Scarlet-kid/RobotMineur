package org.example.Model;

public class Secteur {
    private int ligne;
    private int colonne;
    private boolean estEau;

    public Secteur(int ligne, int colonne, boolean estEau) {
        this.ligne = ligne;
        this.colonne = colonne;
        this.estEau = estEau;
    }

    public int getLigne() {
        return ligne;
    }

    public int getColonne() {
        return colonne;
    }

    public boolean isEstEau() {
        return estEau;
    }

    public void setColonne(int colonne) {
        this.colonne = colonne;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }

    public void setEstEau(boolean estEau) {
        this.estEau = estEau;
    }

}


