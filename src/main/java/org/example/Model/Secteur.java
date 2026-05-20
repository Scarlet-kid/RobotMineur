package org.example.Model;
public class Secteur {
    private int ligne;
    private int colonne;
    private boolean estEau;

    private Composantes composante;
    private Robot robot;

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

    public boolean EstEau() {
        return estEau;
    }

    public void setEstEau(boolean estEau) {
        this.estEau = estEau;
    }

    public Robot getRobot(){
        return robot;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public Composantes getComposante() {
        return composante;
    }

    public void setComposante(Composantes composante) {
        this.composante = composante;
    }

    public String getAffichageHaut() {
        if (estEau) {
            return "XX";
        }

        if (composante instanceof Mine) {
            return "M" + composante.getNumero();
        }

        if (composante instanceof Entrepot) {
            return "E" + composante.getNumero();
        }

        return "  ";
    }

    public String getAffichageBas() {
        if (estEau) {
            return "XX";
        }

        if (robot != null) {
            return "R" + robot.getNumero();
        }

        return "  ";
    }

}