package org.example.Model;

public class Mine extends Composantes {

    private TypeMinerai type;
    private int nbMinerais;
    private int capaciteInitiale;

    public Mine(int numero, Secteur secteur, TypeMinerai type, int capaciteInitiale) {
        super(numero, secteur);
        this.type = type;
        this.capaciteInitiale = capaciteInitiale;
        this.nbMinerais = capaciteInitiale;
    }

    public int extraire(int qte) {
        if (estVide()) return 0;
        int quantiteExtraite = Math.min(qte, nbMinerais);
        nbMinerais -= quantiteExtraite;
        return quantiteExtraite;
    }

    public boolean estVide() {
        return nbMinerais == 0;
    }

    public int getNbMinerais() {
        return nbMinerais;
    }

    public TypeMinerai getType() {
        return type;
    }

    @Override
    public String afficher() {
        return "M" + getNumero();
    }

    @Override
    public String toString() {
        String typeStr = (type == TypeMinerai.OR) ? "OR" : "NI";
        return "M" + getNumero()
                + " " + getSecteur().getLigne()
                + " " + getSecteur().getColonne()
                + " " + typeStr
                + " " + nbMinerais + " / " + capaciteInitiale;
    }
}


