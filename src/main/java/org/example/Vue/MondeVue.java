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

    private final Monde monde;
    private final CelluleVue[][] cellules = new CelluleVue[10][10];

    private final Label tourLabel = new Label();
    private final Label robotCourantLabel = new Label();
    private final Label messageLabel = new Label();
    private final VBox panneauInfos = new VBox(8);

    private int indexRobotCourant = 0;

    public MondeVue(Monde monde) {
        this.monde = monde;

        setPadding(new Insets(10));
        setStyle("-fx-background-color: #f5f5f5;");

        setCenter(creerGrille());
        setRight(creerPanneau());

        rafraichir();
    }

    private GridPane creerGrille() {
        GridPane grille = new GridPane();
        grille.setHgap(2);
        grille.setVgap(2);

        grille.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        for (int i = 0; i < 10; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(10);
            grille.getColumnConstraints().add(col);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(10);
            grille.getRowConstraints().add(row);
        }

        Secteur[][] secteurs = monde.getGrille();

        for (int l = 0; l < 10; l++) {
            for (int c = 0; c < 10; c++) {
                CelluleVue cellule = new CelluleVue(secteurs[l][c]);
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

        Button nord = new Button("↑ Nord");
        Button sud = new Button("↓ Sud");
        Button est = new Button("→ Est");
        Button ouest = new Button("← Ouest");
        Button recolter = new Button("Récolter (R)");
        Button deposer = new Button("Déposer (D)");

        nord.setMaxWidth(Double.MAX_VALUE);
        sud.setMaxWidth(Double.MAX_VALUE);
        est.setMaxWidth(Double.MAX_VALUE);
        ouest.setMaxWidth(Double.MAX_VALUE);
        recolter.setMaxWidth(Double.MAX_VALUE);
        deposer.setMaxWidth(Double.MAX_VALUE);

        nord.setOnAction(e -> jouerDeplacement(Directions.NORD));
        sud.setOnAction(e -> jouerDeplacement(Directions.SUD));
        est.setOnAction(e -> jouerDeplacement(Directions.EST));
        ouest.setOnAction(e -> jouerDeplacement(Directions.OUEST));
        recolter.setOnAction(e -> jouerRecolter());
        deposer.setOnAction(e -> jouerDeposer());

        box.getChildren().addAll(
                tourLabel,
                robotCourantLabel,
                messageLabel,
                new Separator(),
                nord,
                sud,
                est,
                ouest,
                recolter,
                deposer,
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

        if (!robots.isEmpty()) {
            indexRobotCourant = (indexRobotCourant + 1) % robots.size();
        }
    }

    private void jouerDeplacement(Directions direction) {
        Robot robot = getRobotCourant();

        if (robot == null) {
            afficherMessage("Aucun robot disponible.", false);
            return;
        }

        boolean ok = robot.avancer(direction, monde.getGrille());

        if (ok) {
            monde.jouerTour();
            passerAuRobotSuivant();
            afficherMessage("Action réussie.", true);
        } else {
            afficherMessage("Action invalide : déplacement impossible.", false);
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
            monde.jouerTour();
            passerAuRobotSuivant();
            afficherMessage("Récolte réussie.", true);
        } else {
            afficherMessage("Action invalide : récolte impossible.", false);
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
            monde.jouerTour();
            passerAuRobotSuivant();
            afficherMessage("Dépôt réussi.", true);
        } else {
            afficherMessage("Action invalide : dépôt impossible.", false);
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
            robotCourantLabel.setText("À jouer : R" + courant.getNumero());
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
            panneauInfos.getChildren().add(new Label(r.toString()));
        }

        panneauInfos.getChildren().add(new Separator());
        panneauInfos.getChildren().add(new Label("COMPOSANTS"));

        for (int l = 0; l < 10; l++) {
            for (int c = 0; c < 10; c++) {
                Composantes comp = monde.getGrille()[l][c].getComposante();

                if (comp instanceof Mine mine) {
                    panneauInfos.getChildren().add(new Label(mine.toString()));
                }

                if (comp instanceof Entrepot entrepot) {
                    String typeStr = entrepot.getType() == TypeMinerai.OR ? "OR" : "NI";
                    panneauInfos.getChildren().add(new Label(
                            "E" + entrepot.getNumero()
                                    + " " + typeStr
                                    + " stock : " + entrepot.getStock()
                    ));
                }
            }
        }
    }
}