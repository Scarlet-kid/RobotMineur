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

    public String getAffichageHaut() {

        if (estEau) return "X X";

        if (composante instanceof Mine)
            return "M " + composante.getNumero();

        if (composante instanceof Entrepot)
            return "E " + composante.getNumero();

        return "   ";
<<<<<<< HEAD
    }
=======
>>>>>>> 23faaedfed3b822b8939246c85f6884b3c821a13

    public String getAffichageBas() {

        if (estEau) return "X X";

        if (robot != null)
            return "R " + robot.getNumero();

        return "   ";
    }
    }