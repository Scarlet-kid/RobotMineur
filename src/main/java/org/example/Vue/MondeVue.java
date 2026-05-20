package org.example.Vue;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

    private final VBox panneauInfos = new VBox(12);
    private final Label tourLabel = new Label();
    private final Label robotActuelLabel = new Label();
    private final Label messageLabel = new Label();

    private int robotActuel = 0;
    private int actionsTour = 0;

    public MondeVue(Monde monde) {
        this.monde = monde;

        setStyle("-fx-background-color: #e9e9e9;");

        grille.setHgap(0);
        grille.setVgap(0);
        grille.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        creerGrille();

        VBox panneau = creerPanneau();
        panneau.setPrefWidth(430);
        panneau.setMinWidth(430);
        panneau.setMaxWidth(430);

        setCenter(grille);
        setRight(panneau);

        rafraichirInfos();
    }

    private void creerGrille() {
        grille.getChildren().clear();
        grille.getColumnConstraints().clear();
        grille.getRowConstraints().clear();

        for (int i = 0; i < 10; i++) {
            ColumnConstraints colonne = new ColumnConstraints();
            colonne.setPercentWidth(10);
            colonne.setHgrow(Priority.ALWAYS);
            colonne.setFillWidth(true);

            RowConstraints ligne = new RowConstraints();
            ligne.setPercentHeight(10);
            ligne.setVgrow(Priority.ALWAYS);
            ligne.setFillHeight(true);

            grille.getColumnConstraints().add(colonne);
            grille.getRowConstraints().add(ligne);
        }

        Secteur[][] secteurs = monde.getGrille();

        for (int ligne = 0; ligne < 10; ligne++) {
            for (int colonne = 0; colonne < 10; colonne++) {
                CelluleVue cellule = new CelluleVue(secteurs[ligne][colonne]);
                cellule.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                GridPane.setHgrow(cellule, Priority.ALWAYS);
                GridPane.setVgrow(cellule, Priority.ALWAYS);

                grille.add(cellule, colonne, ligne);
            }
        }
    }

    private VBox creerPanneau() {
        VBox panneau = new VBox(14);
        panneau.setPadding(new Insets(16));
        panneau.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #F6E6C8, #D7B07A);
                -fx-border-color: #7A4E24;
                -fx-border-width: 2;
                """);

        tourLabel.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #3A2410;");
        robotActuelLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #183A59;");

        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(8));
        messageLabel.setStyle("""
                -fx-background-color: #EFE2C6;
                -fx-background-radius: 8;
                -fx-border-color: #8A6338;
                -fx-border-radius: 8;
                -fx-font-weight: bold;
                """);

        Label commandes = carteTexte(
                "COMMANDES CLAVIER",
                "↑ : Nord\n↓ : Sud\n← : Ouest\n→ : Est\n\nR : Récolter\nD : Déposer",
                "#FFF3D8"
        );

        VBox header = new VBox(8, tourLabel, robotActuelLabel, messageLabel);
        header.setPadding(new Insets(12));
        header.setStyle("""
                -fx-background-color: #FFF8E8;
                -fx-background-radius: 12;
                -fx-border-color: #8A6338;
                -fx-border-radius: 12;
                -fx-border-width: 1.5;
                """);

        panneauInfos.setFillWidth(true);

        ScrollPane scroll = new ScrollPane(panneauInfos);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox.setVgrow(scroll, Priority.ALWAYS);

        panneau.getChildren().addAll(header, commandes, scroll);

        return panneau;
    }

    private Label carteTexte(String titre, String contenu, String couleurFond) {
        Label label = new Label(titre + "\n\n" + contenu);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(10));
        label.setStyle("""
                -fx-background-color: %s;
                -fx-background-radius: 10;
                -fx-border-color: #8A6338;
                -fx-border-radius: 10;
                -fx-font-size: 13;
                -fx-text-fill: #2F2418;
                """.formatted(couleurFond));
        return label;
    }

    private Label badge(String texte, String couleurFond, String couleurTexte) {
        Label label = new Label(texte);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(10));
        label.setStyle("""
                -fx-background-color: %s;
                -fx-background-radius: 10;
                -fx-border-color: #7A4E24;
                -fx-border-radius: 10;
                -fx-border-width: 1;
                -fx-font-size: 12.5;
                -fx-font-weight: bold;
                -fx-text-fill: %s;
                """.formatted(couleurFond, couleurTexte));
        return label;
    }

    private Label titreSection(String texte) {
        Label label = new Label(texte);
        label.setPadding(new Insets(6, 0, 2, 0));
        label.setStyle("""
                -fx-font-size: 15;
                -fx-font-weight: bold;
                -fx-text-fill: #3A2410;
                """);
        return label;
    }

    private List<Robot> recupererRobots() {
        List<Robot> robots = new ArrayList<>();

        for (int ligne = 0; ligne < 10; ligne++) {
            for (int colonne = 0; colonne < 10; colonne++) {
                Robot robot = monde.getGrille()[ligne][colonne].getRobot();

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

            boolean actionValide;

            if (event.getCode() == KeyCode.UP) {
                actionValide = robot.avancer(Directions.NORD, monde.getGrille());
            } else if (event.getCode() == KeyCode.DOWN) {
                actionValide = robot.avancer(Directions.SUD, monde.getGrille());
            } else if (event.getCode() == KeyCode.LEFT) {
                actionValide = robot.avancer(Directions.OUEST, monde.getGrille());
            } else if (event.getCode() == KeyCode.RIGHT) {
                actionValide = robot.avancer(Directions.EST, monde.getGrille());
            } else if (event.getCode() == KeyCode.R) {
                actionValide = robot.recolter();
            } else if (event.getCode() == KeyCode.D) {
                actionValide = robot.deposer();
            } else {
                return;
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
            } else {
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
        } else if (robot.getType() == TypeMinerai.OR) {
            robotActuelLabel.setText("À jouer : robot_or");
        } else {
            robotActuelLabel.setText("À jouer : robot_nickel");
        }

        panneauInfos.getChildren().clear();

        panneauInfos.getChildren().add(titreSection("ROBOTS"));

        for (Robot r : recupererRobots()) {
            String nom = r.getType() == TypeMinerai.OR ? "robot_or" : "robot_nickel";
            String couleur = r.getType() == TypeMinerai.OR ? "#FFD166" : "#74B9E7";

            String texte = nom
                    + "\nStockage : " + r.getNbMinerais() + " / " + r.getCapaciteStockage();
            Composantes comp = r.getSecteur().getComposante();

            if (comp instanceof Mine mine) {
                texte += mine.getType() == TypeMinerai.OR
                        ? "\nActuellement sur : mine_or"
                        : "\nActuellement sur : mine_nickel";
            }

            if (comp instanceof Entrepot entrepot) {
                texte += entrepot.getType() == TypeMinerai.OR
                        ? "\nActuellement sur : entrepot_or"
                        : "\nActuellement sur : entrepot_nickel";
            }

            panneauInfos.getChildren().add(badge(texte, couleur, "#2B2114"));
        }

        panneauInfos.getChildren().add(new Separator());
        panneauInfos.getChildren().add(titreSection("COMPOSANTS"));

        for (int ligne = 0; ligne < 10; ligne++) {
            for (int colonne = 0; colonne < 10; colonne++) {
                Composantes composante = monde.getGrille()[ligne][colonne].getComposante();

                if (composante instanceof Mine mine) {
                    String nom = mine.getType() == TypeMinerai.OR ? "mine_or" : "mine_nickel";
                    String couleur = mine.getType() == TypeMinerai.OR ? "#D4A017" : "#5DA9D6";

                    panneauInfos.getChildren().add(
                            badge(
                                    nom
                                            + "\nMinerais : "
                                            + mine.getNbMinerais()
                                            + " / "
                                            + mine.getCapaciteInitiale(),
                                    couleur,
                                    "#2B2114"
                            )
                    );
                }

                if (composante instanceof Entrepot entrepot) {
                    String nom = entrepot.getType() == TypeMinerai.OR ? "entrepot_or" : "entrepot_nickel";
                    String couleur = entrepot.getType() == TypeMinerai.OR ? "#F7C948" : "#9AD1D4";

                    panneauInfos.getChildren().add(
                            badge(
                                    nom + "\nStock : " + entrepot.getStock(),
                                    couleur,
                                    "#2B2114"
                            )
                    );
                }
            }
        }
    }
}