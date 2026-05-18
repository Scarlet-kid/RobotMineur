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

    // Méthode pour la première ligne du secteur (bâtiments et haut de l'eau)
    public String getAffichageHaut() {
        if (estEau) {
            return "XX"; // Le haut des vagues
        }

        if (composante instanceof Mine) {
            // "M" + 1 donne "M1" (ce qui fait bien 2 caractères)
            return "M" + composante.getNumero();
        }

        if (composante instanceof Entrepot) {
            // "E" + 1 donne "E1"
            return "E" + composante.getNumero();
        }

        // S'il n'y a ni eau, ni mine, ni entrepôt, on renvoie 2 espaces vides
        return "  ";
    }

    // Méthode pour la deuxième ligne du secteur (robots et bas de l'eau)
    public String getAffichageBas() {
        if (estEau) {
            return "XX"; // Le bas des vagues
        }

        if (robot != null) {
            // "R" + 1 donne "R1"
            return "R" + robot.getNumero();
        }

        // S'il n'y a ni eau, ni robot, on renvoie 2 espaces vides
        return "  ";
    }

    public String afficher() {
        if (estEau) return " X ";
        if (robot != null) return "R" + robot.getNumero() + (robot.getNumero() < 10 ? " " : "");
        if (composante instanceof Mine) return "M" + composante.getNumero() + (composante.getNumero() < 10 ? " " : "");
        if (composante instanceof Entrepot)
            return "E" + composante.getNumero() + (composante.getNumero() < 10 ? " " : "");

        return "   "; // 3 espaces
    }
    }