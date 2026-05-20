package org.example.Model;

public abstract class Composantes {

    protected int numero;
    protected Secteur secteur;

    public Composantes(int numero, Secteur secteur) {
        this.numero = numero;
        this.secteur = secteur;
    }

    public int getNumero() {
        return numero;
    }

    public Secteur getSecteur() {
        return secteur;
    }

    public abstract String afficher();
}

