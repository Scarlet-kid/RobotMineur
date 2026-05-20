package org.example.Vue;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.example.Model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MondeVue extends BorderPane {

    private final Monde monde;

    private final GridPane grille = new GridPane();

    private final Label tourLabel = new Label();
    private final Label robotActuelLabel = new Label();
    private final Label messageLabel = new Label();

    private final Label robotsLabel = new Label();
    private final Label composantsLabel = new Label();

    private int robotActuel = 0;
    private int actionsTour = 0;

    public MondeVue(Monde monde) {

        this.monde = monde;

        setStyle("-fx-background-color: #e9e9e9;");

        grille.setHgap(0);
        grille.setVgap(0);

        creerGrille();

        VBox panneau = creerPanneau();

        panneau.setPrefWidth(320);

        setCenter(grille);
        setRight(panneau);

        BorderPane.setMargin(grille, Insets.EMPTY);

        grille.prefWidthProperty().bind(widthProperty().subtract(320));
        grille.prefHeightProperty().bind(heightProperty());

        rafraichirInfos();
    }

    private void creerGrille() {

        grille.getChildren().clear();
        grille.getColumnConstraints().clear();
        grille.getRowConstraints().clear();

        for (int i = 0; i < 10; i++) {

            ColumnConstraints colonne = new ColumnConstraints();
            colonne.setPercentWidth(10);

            RowConstraints ligne = new RowConstraints();
            ligne.setPercentHeight(10);

            grille.getColumnConstraints().add(colonne);
            grille.getRowConstraints().add(ligne);
        }

        Secteur[][] secteurs = monde.getGrille();

        for (int ligne = 0; ligne < 10; ligne++) {

            for (int colonne = 0; colonne < 10; colonne++) {

                CelluleVue cellule =
                        new CelluleVue(secteurs[ligne][colonne]);

                cellule.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                GridPane.setHgrow(cellule, Priority.ALWAYS);
                GridPane.setVgrow(cellule, Priority.ALWAYS);

                grille.add(cellule, colonne, ligne);
            }
        }
    }

    private VBox creerPanneau() {

        VBox panneau = new VBox(12);

        panneau.setPadding(new Insets(15));

        panneau.setStyle("""
                -fx-background-color: white;
                -fx-border-color: #cccccc;
                """);

        tourLabel.setStyle("""
                -fx-font-size: 30;
                -fx-font-weight: bold;
                """);

        robotActuelLabel.setStyle("""
                -fx-font-size: 20;
                -fx-font-weight: bold;
                -fx-text-fill: #102c6b;
                """);

        messageLabel.setWrapText(true);

        Label commandes = new Label("""
                COMMANDES CLAVIER

                ↑ : Nord
                ↓ : Sud
                ← : Ouest
                → : Est

                R : Récolter
                D : Déposer
                """);

        panneau.getChildren().addAll(
                tourLabel,
                robotActuelLabel,
                messageLabel,
                new Separator(),
                commandes,
                new Separator(),
                robotsLabel,
                new Separator(),
                composantsLabel
        );

        return panneau;
    }

    private List<Robot> recupererRobots() {

        List<Robot> robots = new ArrayList<>();

        for (int ligne = 0; ligne < 10; ligne++) {

            for (int colonne = 0; colonne < 10; colonne++) {

                Robot robot =
                        monde.getGrille()[ligne][colonne].getRobot();

                if (robot != null) {
                    robots.add(robot);
                }
            }
        }

        robots.sort(Comparator.comparingInt(Robot::getNumero));

        return robots;
    }

    private Robot getRobotActuel() {

        List<Robot> robots = recupererRobots();

        if (robots.isEmpty()) {
            return null;
        }

        if (robotActuel >= robots.size()) {
            robotActuel = 0;
        }

        return robots.get(robotActuel);
    }

    public void ajouterControleClavier(Scene scene) {

        scene.setOnKeyPressed(event -> {

            Robot robot = getRobotActuel();

            if (robot == null) {
                return;
            }

            boolean actionValide = false;

            if (event.getCode() == KeyCode.UP) {
                actionValide =
                        robot.avancer(Directions.NORD, monde.getGrille());
            }

            else if (event.getCode() == KeyCode.DOWN) {
                actionValide =
                        robot.avancer(Directions.SUD, monde.getGrille());
            }

            else if (event.getCode() == KeyCode.LEFT) {
                actionValide =
                        robot.avancer(Directions.OUEST, monde.getGrille());
            }

            else if (event.getCode() == KeyCode.RIGHT) {
                actionValide =
                        robot.avancer(Directions.EST, monde.getGrille());
            }

            else if (event.getCode() == KeyCode.R) {
                actionValide = robot.recolter();
            }

            else if (event.getCode() == KeyCode.D) {
                actionValide = robot.deposer();
            }

            if (actionValide) {

                actionsTour++;

                if (actionsTour >= recupererRobots().size()) {

                    monde.jouerTour();
                    actionsTour = 0;
                }

                robotActuel++;

                if (robotActuel >= recupererRobots().size()) {
                    robotActuel = 0;
                }

                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("Action réussie");
            }

            else {

                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Action invalide");
            }

            rafraichir();
        });
    }

    private void rafraichir() {

        creerGrille();
        rafraichirInfos();
    }

    private void rafraichirInfos() {

        tourLabel.setText("Tour : " + monde.getTourActuel());

        Robot robot = getRobotActuel();

        if (robot == null) {
            robotActuelLabel.setText("Aucun robot");
        }

        else {

            if (robot.getType() == TypeMinerai.OR) {
                robotActuelLabel.setText("À jouer : robot_or");
            }
            else {
                robotActuelLabel.setText("À jouer : robot_nickel");
            }
        }

        StringBuilder robots = new StringBuilder("ROBOTS\n\n");

        for (Robot r : recupererRobots()) {

            String nom;

            if (r.getType() == TypeMinerai.OR) {
                nom = "robot_or";
            }
            else {
                nom = "robot_nickel";
            }

            robots.append(nom)
                    .append(" : ")
                    .append(r.getSecteur().getLigne())
                    .append(",")
                    .append(r.getSecteur().getColonne())
                    .append("\n");
        }

        robotsLabel.setText(robots.toString());

        StringBuilder composants =
                new StringBuilder("COMPOSANTS\n\n");

        for (int ligne = 0; ligne < 10; ligne++) {

            for (int colonne = 0; colonne < 10; colonne++) {

                Composantes composante =
                        monde.getGrille()[ligne][colonne]
                                .getComposante();

                if (composante instanceof Mine mine) {

                    if (mine.getType() == TypeMinerai.OR) {
                        composants.append("mine_or");
                    }
                    else {
                        composants.append("mine_nickel");
                    }

                    composants.append(" : ")
                            .append(ligne)
                            .append(",")
                            .append(colonne)
                            .append("\n");
                }

                if (composante instanceof Entrepot entrepot) {

                    if (entrepot.getType() == TypeMinerai.OR) {
                        composants.append("entrepot_or");
                    }
                    else {
                        composants.append("entrepot_nickel");
                    }

                    composants.append(" stock : ")
                            .append(entrepot.getStock())
                            .append("\n");
                }
            }
        }

        composantsLabel.setText(composants.toString());
    }
}