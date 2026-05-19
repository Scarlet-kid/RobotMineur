package org.example.Vue;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import org.example.Model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MondeVue extends BorderPane {

    private static final int TAILLE_CASE = 72;

    private final Monde monde;
    private final CelluleVue[][] cellules = new CelluleVue[10][10];

    private final Label tourLabel = new Label();
    private final Label robotCourantLabel = new Label();
    private final Label messageLabel = new Label();
    private final VBox panneauInfos = new VBox(8);

    private int indexRobotCourant = 0;
    private int nbActionsValidesDansTour = 0;

    public MondeVue(Monde monde) {
        this.monde = monde;

        setPadding(new Insets(8));
        setStyle("-fx-background-color: #f5f5f5;");

        setCenter(creerGrille());
        setRight(creerPanneau());

        rafraichir();
    }

    private GridPane creerGrille() {
        GridPane grille = new GridPane();
        grille.setHgap(0);
        grille.setVgap(0);
        grille.setMaxWidth(10 * TAILLE_CASE);
        grille.setMaxHeight(10 * TAILLE_CASE);

        for (int i = 0; i < 10; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPrefWidth(TAILLE_CASE);
            col.setMinWidth(TAILLE_CASE);
            col.setMaxWidth(TAILLE_CASE);

            RowConstraints row = new RowConstraints();
            row.setPrefHeight(TAILLE_CASE);
            row.setMinHeight(TAILLE_CASE);
            row.setMaxHeight(TAILLE_CASE);

            grille.getColumnConstraints().add(col);
            grille.getRowConstraints().add(row);
        }

        Secteur[][] secteurs = monde.getGrille();

        for (int l = 0; l < 10; l++) {
            for (int c = 0; c < 10; c++) {
                CelluleVue cellule = new CelluleVue(secteurs[l][c]);

                cellule.setPrefSize(TAILLE_CASE, TAILLE_CASE);
                cellule.setMinSize(TAILLE_CASE, TAILLE_CASE);
                cellule.setMaxSize(TAILLE_CASE, TAILLE_CASE);

                cellules[l][c] = cellule;
                grille.add(cellule, c, l);
            }
        }

        return grille;
    }

    private VBox creerPanneau() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setPrefWidth(280);
        box.setStyle("-fx-background-color: white; -fx-border-color: #cccccc;");

        tourLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        robotCourantLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #243b6b;");
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold;");

        Label aide = new Label(
                "COMMANDES CLAVIER\n\n" +
                        "↑ : Nord\n" +
                        "↓ : Sud\n" +
                        "← : Ouest\n" +
                        "→ : Est\n\n" +
                        "R : Récolter\n" +
                        "D : Déposer"
        );

        aide.setWrapText(true);
        aide.setStyle("-fx-font-size: 13; -fx-text-fill: #444444;");

        box.getChildren().addAll(
                tourLabel,
                robotCourantLabel,
                messageLabel,
                new Separator(),
                aide,
                new Separator(),
                panneauInfos
        );

        return box;
    }

    private List<Robot> recupererRobots() {
        List<Robot> robots = new ArrayList<>();

        for (int l = 0; l < 10; l++) {
            for (int c = 0; c < 10; c++) {
                Robot r = monde.getGrille()[l][c].getRobot();

                if (r != null) {
                    robots.add(r);
                }
            }
        }

        robots.sort(Comparator.comparingInt(Robot::getNumero));
        return robots;
    }

    private Robot getRobotCourant() {
        List<Robot> robots = recupererRobots();

        if (robots.isEmpty()) {
            return null;
        }

        if (indexRobotCourant >= robots.size()) {
            indexRobotCourant = 0;
        }

        return robots.get(indexRobotCourant);
    }

    private void passerAuRobotSuivant() {
        List<Robot> robots = recupererRobots();

        if (robots.isEmpty()) {
            return;
        }

        nbActionsValidesDansTour++;

        if (nbActionsValidesDansTour >= robots.size()) {
            monde.jouerTour();
            nbActionsValidesDansTour = 0;
        }

        indexRobotCourant = (indexRobotCourant + 1) % robots.size();
    }

    private void jouerDeplacement(Directions direction) {
        Robot robot = getRobotCourant();

        if (robot == null) {
            afficherMessage("Aucun robot disponible.", false);
            return;
        }

        boolean ok = robot.avancer(direction, monde.getGrille());

        if (ok) {
            passerAuRobotSuivant();
            afficherMessage("Déplacement réussi. Robot suivant.", true);
        } else {
            afficherMessage("Action invalide : déplacement impossible. Le même robot doit rejouer.", false);
        }

        rafraichir();
    }

    private void jouerRecolter() {
        Robot robot = getRobotCourant();

        if (robot == null) {
            afficherMessage("Aucun robot disponible.", false);
            return;
        }

        boolean ok = robot.recolter();

        if (ok) {
            passerAuRobotSuivant();
            afficherMessage("Récolte réussie. Robot suivant.", true);
        } else {
            afficherMessage("Action invalide : récolte impossible. Le même robot doit rejouer.", false);
        }

        rafraichir();
    }

    private void jouerDeposer() {
        Robot robot = getRobotCourant();

        if (robot == null) {
            afficherMessage("Aucun robot disponible.", false);
            return;
        }

        boolean ok = robot.deposer();

        if (ok) {
            passerAuRobotSuivant();
            afficherMessage("Dépôt réussi. Robot suivant.", true);
        } else {
            afficherMessage("Action invalide : dépôt impossible. Le même robot doit rejouer.", false);
        }

        rafraichir();
    }

    private void afficherMessage(String texte, boolean succes) {
        messageLabel.setText(texte);

        if (succes) {
            messageLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: green;");
        } else {
            messageLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: red;");
        }
    }

    public void ajouterControleClavier(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) {
                jouerDeplacement(Directions.NORD);
            } else if (e.getCode() == KeyCode.DOWN) {
                jouerDeplacement(Directions.SUD);
            } else if (e.getCode() == KeyCode.LEFT) {
                jouerDeplacement(Directions.OUEST);
            } else if (e.getCode() == KeyCode.RIGHT) {
                jouerDeplacement(Directions.EST);
            } else if (e.getCode() == KeyCode.R) {
                jouerRecolter();
            } else if (e.getCode() == KeyCode.D) {
                jouerDeposer();
            }
        });
    }

    public void rafraichir() {
        tourLabel.setText("Tour : " + monde.getTourActuel());

        Robot courant = getRobotCourant();

        if (courant != null) {
            robotCourantLabel.setText("À jouer : robot_" + typeRobot(courant));
        } else {
            robotCourantLabel.setText("Aucun robot");
        }

        for (int l = 0; l < 10; l++) {
            for (int c = 0; c < 10; c++) {
                cellules[l][c].rafraichir(monde.getGrille()[l][c]);
            }
        }

        panneauInfos.getChildren().clear();

        panneauInfos.getChildren().add(new Label("ROBOTS"));

        for (Robot r : recupererRobots()) {
            panneauInfos.getChildren().add(new Label(
                    "robot_" + typeRobot(r)
                            + " : "
                            + r.getSecteur().getLigne()
                            + ","
                            + r.getSecteur().getColonne()
            ));
        }

        panneauInfos.getChildren().add(new Separator());
        panneauInfos.getChildren().add(new Label("COMPOSANTS"));

        for (int l = 0; l < 10; l++) {
            for (int c = 0; c < 10; c++) {
                Composantes comp = monde.getGrille()[l][c].getComposante();

                if (comp instanceof Mine mine) {
                    String nom = mine.getType() == TypeMinerai.OR ? "mine_or" : "mine_nickel";

                    panneauInfos.getChildren().add(new Label(
                            nom + " : "
                                    + mine.getSecteur().getLigne()
                                    + ","
                                    + mine.getSecteur().getColonne()
                    ));
                }

                if (comp instanceof Entrepot entrepot) {
                    String nom = entrepot.getType() == TypeMinerai.OR ? "entrepot_or" : "entrepot_nickel";

                    panneauInfos.getChildren().add(new Label(
                            nom + " stock : " + entrepot.getStock()
                    ));
                }
            }
        }
    }

    private String typeRobot(Robot r) {
        return r.toString().contains("OR") ? "or" : "nickel";
    }
}