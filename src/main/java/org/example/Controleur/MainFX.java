package org.example.Controleur;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.Model.*;
import org.example.Vue.MondeVue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainFX extends Application {

    private final Random random = new Random();

    private Monde monde;
    private Secteur[][] grille;
    private List<Mine> mines     = new ArrayList<>();
    private List<Entrepot> entrepots = new ArrayList<>();
    private List<Robot> robots   = new ArrayList<>();

    private MondeVue vue;
    private Timeline auto;

    @Override
    public void start(Stage stage) {
        monde = new Monde(null, 0);
        monde.initialiser();
        grille = monde.getGrille();

        ajouterMine(1, TypeMinerai.OR);
        ajouterMine(2, TypeMinerai.NICKEL);
        ajouterEntrepot(1, TypeMinerai.OR);
        ajouterEntrepot(2, TypeMinerai.NICKEL);
        ajouterRobot(1, TypeMinerai.OR);
        ajouterRobot(2, TypeMinerai.NICKEL);

        vue = new MondeVue(monde);

        Button btnAuto = new Button("▶ Lancer mode auto");
        btnAuto.setStyle("-fx-background-color: #2A7A2A; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        btnAuto.setOnAction(e -> demarrerModeAuto(btnAuto));

        Button btnReset = new Button("↺ Reinitialiser");
        btnReset.setStyle("-fx-background-color: #7A2A2A; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        btnReset.setOnAction(e -> {
            if (auto != null) auto.stop();
            stage.close();
            try { start(new Stage()); } catch (Exception ex) { ex.printStackTrace(); }
        });

        HBox barreBoutons = new HBox(10, btnAuto, btnReset);
        barreBoutons.setAlignment(Pos.CENTER);
        barreBoutons.setPadding(new Insets(10));
        barreBoutons.setStyle("-fx-background-color: #1E2A3A;");

        BorderPane root = new BorderPane();
        root.setCenter(vue);
        root.setBottom(barreBoutons);

        Scene scene = new Scene(root, 1600, 900);
        vue.ajouterControleClavier(scene);

        stage.setTitle("Robots mineurs - Mode autonome (A*)");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setResizable(true);
        stage.show();

        vue.requestFocus();
    }

    private void demarrerModeAuto(Button btn) {
        if (auto != null && auto.getStatus() == javafx.animation.Animation.Status.RUNNING) {
            auto.stop();
            btn.setText("▶ Lancer mode auto");
            return;
        }

        auto = new Timeline(new KeyFrame(Duration.millis(600), ev -> {
            Platform.runLater(() -> {
                if (toutesMinesVides()) {
                    auto.stop();
                    btn.setText("✓ Terminé !");
                    System.out.println("=== Fin ! Tours : " + monde.getTourActuel() + " ===");
                    return;
                }

                for (Robot robot : robots) {
                    robot.agirAutonome(grille, mines, entrepots);
                }

                // incrementerTour() au lieu de jouerTour()
                // → pas de Scanner, pas de fuite mémoire
                monde.incrementerTour();
                vue.rafraichir();
            });
        }));

        auto.setCycleCount(Timeline.INDEFINITE);
        auto.play();
        btn.setText("⏸ Arreter mode auto");
    }

    private boolean toutesMinesVides() {
        for (Mine m : mines) {
            if (!m.estVide()) return false;
        }
        return true;
    }

    private void ajouterMine(int numero, TypeMinerai type) {
        Secteur secteur = getSecteurLibre();
        Mine mine = new Mine(numero, secteur, type, 50 + random.nextInt(51));
        secteur.setComposante(mine);
        mines.add(mine);
    }

    private void ajouterEntrepot(int numero, TypeMinerai type) {
        Secteur secteur = getSecteurLibre();
        Entrepot entrepot = new Entrepot(type, numero, secteur);
        secteur.setComposante(entrepot);
        entrepots.add(entrepot);
    }

    private void ajouterRobot(int numero, TypeMinerai type) {
        Secteur secteur = getSecteurLibre();
        Robot robot = new Robot(
                numero, secteur, type,
                5 + random.nextInt(5),
                1 + random.nextInt(3)
        );
        secteur.setRobot(robot);
        robots.add(robot);
    }

    private Secteur getSecteurLibre() {
        Secteur secteur;
        do {
            int ligne   = random.nextInt(10);
            int colonne = random.nextInt(10);
            secteur = grille[ligne][colonne];
        } while (secteur.EstEau() || secteur.getComposante() != null || secteur.getRobot() != null);
        return secteur;
    }

    public static void main(String[] args) {
        launch(args);
    }
}