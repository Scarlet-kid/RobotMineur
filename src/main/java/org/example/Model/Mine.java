package org.example.Model;

public class Mine extends Composantes {

    private TypeMinerai type;
    private int nbMinerais;
    private int capaciteInitiale;

    public Mine(int numero, Secteur secteur, TypeMinerai type, int capaciteInitiale) {
        super(numero, secteur);
        if (capaciteInitiale < 0 || capaciteInitiale > 100) {
            throw new IllegalStateException("Erreur : La capacité initiale de la mine doit être entre 0 et 100.");
        }
        this.type = type;
        this.capaciteInitiale = capaciteInitiale;
        this.nbMinerais = capaciteInitiale;
    }

    public int extraire(int qte) {
        if (qte <= 0 || qte > 3) {
            throw new IllegalArgumentException("Erreur : La quantité demandée doit être comprise entre 1 et 3.");
        }
        if (estVide()) return 0;
        int quantiteExtraite = Math.min(qte, nbMinerais);
        nbMinerais -= quantiteExtraite;
        return quantiteExtraite;
    }

    public boolean estVide() {
        return nbMinerais == 0;
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
        String typeStr;
        if (type == TypeMinerai.OR) {
            typeStr = "OR";
        } else {
            typeStr = "NI";
        }
        return "M" + getNumero()
                + " " + getSecteur().getLigne()
                + " " + getSecteur().getColonne()
                + " " + typeStr
                + " " + nbMinerais + " / " + capaciteInitiale;
    }
}


