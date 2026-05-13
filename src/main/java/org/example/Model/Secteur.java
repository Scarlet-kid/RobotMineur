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
    public boolean estLibre() {

        if (estEau) {
            return false;
        }

        if (robot != null) {
            return false;
        }

        return true;
    }

    public String afficher() {
        if (estEau) return " X ";
        if (robot != null) return "R" + robot.getNumero() + (robot.getNumero() < 10 ? " " : "");
        if (composante instanceof Mine) return "M" + composante.getNumero() + (composante.getNumero() < 10 ? " " : "");
        if (composante instanceof Entrepot) return "E" + composante.getNumero() + (composante.getNumero() < 10 ? " " : "");

        return "   "; // 3 espaces
    }

    }


