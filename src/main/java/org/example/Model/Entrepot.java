package org.example.Model;

public class Entrepot  extends Composantes {
    private TypeMinerai type;
    private int stock;

    public Entrepot(TypeMinerai type, int numero, Secteur secteur) {
        super(numero, secteur);
        this.type = type;
        this.stock = 0;
    }

    public void deposer(int qte) {
        this.stock += qte;
    }

    public int getStock() {
        return this.stock;
    }

    public TypeMinerai getType() {
        return this.type;
    }

    public String afficher() {
        return "Entrepot [type=" + type + ", stock=" + stock + "]";
    }

}