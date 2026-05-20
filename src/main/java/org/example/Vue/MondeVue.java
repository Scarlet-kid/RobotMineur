package org.example.Vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
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

        setStyle("-fx-background-color: #111111;");

        grille.setHgap(0);
        grille.setVgap(0);
        grille.setSnapToPixel(false);
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
            grille.getColumnConstraints().add(colonne);

            RowConstraints ligne = new RowConstraints();
            ligne.setPercentHeight(10);
            grille.getRowConstraints().add(ligne);
        }

        Secteur[][] secteurs = monde.getGrille();

        for (int ligne = 0; ligne < 10; ligne++) {
            for (int colonne = 0; colonne < 10; colonne++) {
                CelluleVue cellule = new CelluleVue(secteurs[ligne][colonne]);
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
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setAlignment(Pos.CENTER);
        // Style par défaut (neutre)
        messageLabel.setStyle("""
                -fx-background-color: #EFE2C6;
                -fx-background-radius: 8;
                -fx-border-color: #8A6338;
                -fx-border-radius: 8;
                -fx-font-weight: bold;
                -fx-font-size: 14;
                """);

        VBox header = new VBox(8, tourLabel, robotActuelLabel, messageLabel);
        header.setPadding(new Insets(12));
        header.setStyle("""
                -fx-background-color: #FFF8E8;
                -fx-background-radius: 12;
                -fx-border-color: #8A6338;
                -fx-border-radius: 12;
                -fx-border-width: 1.5;
                """);

        // CRITÈRE BASTIEN & SCAPIN : Contrôle Explicite (Boutons d'interface)
        VBox controles = creerBoutonsActions();

        panneauInfos.setFillWidth(true);
        ScrollPane scroll = new ScrollPane(panneauInfos);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        panneau.getChildren().addAll(header, controles, scroll);

        return panneau;
    }

    // Méthode pour générer les boutons cliquables
    private VBox creerBoutonsActions() {
        GridPane croixDir = new GridPane();
        croixDir.setAlignment(Pos.CENTER);
        croixDir.setHgap(5);
        croixDir.setVgap(5);

        Button btnHaut = creerBouton("↑ NORD", KeyCode.UP);
        Button btnBas = creerBouton("↓ SUD", KeyCode.DOWN);
        Button btnGauche = creerBouton("← OUEST", KeyCode.LEFT);
        Button btnDroite = creerBouton("→ EST", KeyCode.RIGHT);

        croixDir.add(btnHaut, 1, 0);
        croixDir.add(btnGauche, 0, 1);
        croixDir.add(btnBas, 1, 1);
        croixDir.add(btnDroite, 2, 1);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(10, 0, 0, 0));
        Button btnRecolter = creerBouton(" Récolter (R)", KeyCode.R);
        Button btnDeposer = creerBouton(" Déposer (D)", KeyCode.D);
        actions.getChildren().addAll(btnRecolter, btnDeposer);

        VBox conteneur = new VBox(5, new Label("CONTRÔLES MANUELS :"), croixDir, actions);
        conteneur.setAlignment(Pos.CENTER);
        conteneur.setPadding(new Insets(10));
        conteneur.setStyle("-fx-background-color: #FFF3D8; -fx-background-radius: 10; -fx-border-color: #8A6338; -fx-border-radius: 10;");
        return conteneur;
    }

    private Button creerBouton(String texte, KeyCode codeAssocie) {
        Button btn = new Button(texte);
        btn.setStyle("-fx-background-color: #EFE2C6; -fx-border-color: #8A6338; -fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand;");
        btn.setOnAction(e -> executerAction(codeAssocie)); // Relie le clic de souris à la même logique que le clavier
        return btn;
    }

    private Label badge(String texte, String couleurFond, String couleurTexte, boolean estActif) {
        Label label = new Label(texte);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(10));

        // CRITÈRE BASTIEN & SCAPIN : Charge de travail (Mise en évidence du robot actif)
        String bordure = estActif ? "-fx-border-color: #E74C3C; -fx-border-width: 3;" : "-fx-border-color: #7A4E24; -fx-border-width: 1;";

        label.setStyle("""
                -fx-background-color: %s;
                -fx-background-radius: 10;
                -fx-border-radius: 8;
                %s
                -fx-font-size: 12.5;
                -fx-font-weight: bold;
                -fx-text-fill: %s;
                """.formatted(couleurFond, bordure, couleurTexte));
        return label;
    }

    private Label titreSection(String texte) {
        Label label = new Label(texte);
        label.setPadding(new Insets(6, 0, 2, 0));
        label.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #3A2410;");
        return label;
    }

    private List<Robot> recupererRobots() {
        List<Robot> robots = new ArrayList<>();
        for (int ligne = 0; ligne < 10; ligne++) {
            for (int colonne = 0; colonne < 10; colonne++) {
                Robot robot = monde.getGrille()[ligne][colonne].getRobot();
                if (robot != null) { robots.add(robot); }
            }
        }
        robots.sort(Comparator.comparingInt(Robot::getNumero));
        return robots;
    }

    private Robot getRobotActuel() {
        List<Robot> robots = recupererRobots();
        if (robots.isEmpty()) return null;
        if (robotActuel >= robots.size()) robotActuel = 0;
        return robots.get(robotActuel);
    }

    public void ajouterControleClavier(Scene scene) {
        // Le clavier et les clics de souris passent maintenant par la même méthode
        scene.setOnKeyPressed(event -> executerAction(event.getCode()));
    }

    // Centralisation de la logique (Évite de dupliquer le code entre clavier et clics)
    private void executerAction(KeyCode code) {
        Robot robot = getRobotActuel();
        if (robot == null) return;

        boolean actionValide = false;
        boolean toucheReconnue = true;

        if (code == KeyCode.UP) { actionValide = robot.avancer(Directions.NORD, monde.getGrille()); }
        else if (code == KeyCode.DOWN) { actionValide = robot.avancer(Directions.SUD, monde.getGrille()); }
        else if (code == KeyCode.LEFT) { actionValide = robot.avancer(Directions.OUEST, monde.getGrille()); }
        else if (code == KeyCode.RIGHT) { actionValide = robot.avancer(Directions.EST, monde.getGrille()); }
        else if (code == KeyCode.R) { actionValide = robot.recolter(); }
        else if (code == KeyCode.D) { actionValide = robot.deposer(); }
        else { toucheReconnue = false; }

        if (!toucheReconnue) return;

        // CRITÈRE BASTIEN & SCAPIN : Gestion des erreurs (Changement visuel drastique)
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
            messageLabel.setStyle("-fx-background-color: #D4EDDA; -fx-border-color: #C3E6CB; -fx-text-fill: #155724; -fx-background-radius: 8; -fx-border-radius: 8; -fx-font-weight: bold; -fx-font-size: 14;");
            messageLabel.setText("✔ Action réussie");
        } else {
            messageLabel.setStyle("-fx-background-color: #F8D7DA; -fx-border-color: #F5C6CB; -fx-text-fill: #721C24; -fx-background-radius: 8; -fx-border-radius: 8; -fx-font-weight: bold; -fx-font-size: 14;");
            messageLabel.setText("❌ Action impossible");
        }

        rafraichir();
    }

    private void rafraichir() {
        creerGrille();
        rafraichirInfos();
    }

    private void rafraichirInfos() {
        tourLabel.setText("Tour : " + monde.getTourActuel());

        Robot robotDutour = getRobotActuel();

        if (robotDutour == null) {
            robotActuelLabel.setText("Aucun robot");
        } else if (robotDutour.getType() == TypeMinerai.OR) {
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
                    + "\nStockage : " + r.getNbMinerais() + " / " + r.getCapaciteStockage()
                    + "\nExtraction : " + r.getCapaciteExtraction() + " min/action";

            Composantes comp = r.getSecteur().getComposante();
            if (comp instanceof Mine mine) {
                texte += mine.getType() == TypeMinerai.OR ? "\nSur : mine_or" : "\nSur : mine_nickel";
            }
            if (comp instanceof Entrepot entrepot) {
                texte += entrepot.getType() == TypeMinerai.OR ? "\nSur : entrepot_or" : "\nSur : entrepot_nickel";
            }

            // On vérifie si c'est le tour de ce robot pour le mettre en évidence (Bordure rouge)
            boolean estActif = (r == robotDutour);
            panneauInfos.getChildren().add(badge(texte, couleur, "#2B2114", estActif));
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
                            badge(nom + "\nMinerais : " + mine.getNbMinerais() + " / " + mine.getCapaciteInitiale(), couleur, "#2B2114", false)
                    );
                }

                if (composante instanceof Entrepot entrepot) {
                    String nom = entrepot.getType() == TypeMinerai.OR ? "entrepot_or" : "entrepot_nickel";
                    String couleur = entrepot.getType() == TypeMinerai.OR ? "#F7C948" : "#9AD1D4";
                    panneauInfos.getChildren().add(
                            badge(nom + "\nStock : " + entrepot.getStock(), couleur, "#2B2114", false)
                    );
                }
            }
        }
    }
}