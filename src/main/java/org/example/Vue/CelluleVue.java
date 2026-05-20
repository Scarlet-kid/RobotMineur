package org.example.Vue;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.example.Model.*;

public class CelluleVue extends StackPane {

    private static final int TAILLE = 70;
    private final Secteur secteur;

    public CelluleVue(Secteur secteur) {
        this.secteur = secteur;

        setPrefSize(TAILLE, TAILLE);
        setMinSize(TAILLE, TAILLE);
        setMaxSize(TAILLE, TAILLE);

        rafraichir();
    }

    public void rafraichir() {
        getChildren().clear();

        ImageView fond = new ImageView(
                secteur.EstEau() ? charger("eau.png") : charger("terrain.png")
        );

        reglerImage(fond, TAILLE, TAILLE);
        getChildren().add(fond);

        Composantes composante = secteur.getComposante();

        if (composante instanceof Mine mine) {
            ImageView mineVue = new ImageView(
                    mine.getType() == TypeMinerai.OR
                            ? charger("mine_or.png")
                            : charger("mine_ni.png")
            );

            reglerImage(mineVue, TAILLE, TAILLE);
            getChildren().add(mineVue);
        }

        if (composante instanceof Entrepot entrepot) {
            ImageView entrepotVue = new ImageView(
                    entrepot.getType() == TypeMinerai.OR
                            ? charger("entrepot_or.jpg")
                            : charger("entrepot_ni.png")
            );

            reglerImage(entrepotVue, TAILLE, TAILLE);
            getChildren().add(entrepotVue);
        }

        Robot robot = secteur.getRobot();

        if (robot != null) {
            ImageView robotVue = new ImageView(
                    robot.getType() == TypeMinerai.OR
                            ? charger("robot_or.png")
                            : charger("robot_ni.png")
            );

            reglerImage(robotVue, TAILLE, TAILLE);
            StackPane.setAlignment(robotVue, Pos.CENTER);

            getChildren().add(robotVue);
        }
    }

    private void reglerImage(ImageView imageView, int largeur, int hauteur) {
        imageView.setFitWidth(largeur);
        imageView.setFitHeight(hauteur);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
    }

    private Image charger(String nom) {
        var flux = getClass().getResourceAsStream("/images/" + nom);

        if (flux == null) {
            throw new RuntimeException("Image introuvable : /images/" + nom);
        }

        return new Image(flux);
    }
}