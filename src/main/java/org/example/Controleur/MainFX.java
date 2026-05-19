package org.example.Controleur;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.Model.*;
import org.example.Vue.MondeVue;

import java.util.Random;

public class MainFX extends Application {

    private final Random random = new Random();

    @Override
    public void start(Stage stage) {

        Monde monde = new Monde(null, 0);
        monde.initialiser();

        Secteur[][] grille = monde.getGrille();

        ajouterMine(grille, 1, TypeMinerai.OR);
        ajouterMine(grille, 2, TypeMinerai.NICKEL);

        ajouterEntrepot(grille, 1, TypeMinerai.OR);
        ajouterEntrepot(grille, 2, TypeMinerai.NICKEL);

        ajouterRobot(grille, 1, TypeMinerai.OR);
        ajouterRobot(grille, 2, TypeMinerai.NICKEL);

        MondeVue vue = new MondeVue(monde);

        Scene scene = new Scene(vue, 1000, 700);
        vue.ajouterControleClavier(scene);

        stage.setScene(scene);
        stage.setTitle("Robots mineurs");
        stage.show();
    }

    private void ajouterMine(Secteur[][] grille, int numero, TypeMinerai type) {
        Secteur secteur = getSecteurLibre(grille);
        Mine mine = new Mine(numero, secteur, type, 50 + random.nextInt(51));
        secteur.setComposante(mine);
    }

    private void ajouterEntrepot(Secteur[][] grille, int numero, TypeMinerai type) {
        Secteur secteur = getSecteurLibre(grille);
        Entrepot entrepot = new Entrepot(type, numero, secteur);
        secteur.setComposante(entrepot);
    }

    private void ajouterRobot(Secteur[][] grille, int numero, TypeMinerai type) {
        Secteur secteur = getSecteurLibre(grille);
        Robot robot = new Robot(
                numero,
                secteur,
                type,
                5 + random.nextInt(5),
                1 + random.nextInt(3)
        );
        secteur.setRobot(robot);
    }

    private Secteur getSecteurLibre(Secteur[][] grille) {
        Secteur secteur;

        do {
            int ligne = random.nextInt(10);
            int colonne = random.nextInt(10);
            secteur = grille[ligne][colonne];
        } while (
                secteur.EstEau()
                        || secteur.getComposante() != null
                        || secteur.getRobot() != null
        );

        return secteur;
    }

    public static void main(String[] args) {
        launch(args);
    }
}