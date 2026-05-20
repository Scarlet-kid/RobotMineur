package org.example.Vue;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.example.Model.*;

public class CelluleVue extends StackPane {

    private final Secteur secteur;

    public CelluleVue(Secteur secteur) {
        this.secteur = secteur;

        // LA LIGNE VITALE : autorise la case à rétrécir pour s'adapter à l'écran
        // Sans ça, l'image fait exploser la grille !
        setMinSize(0, 0);

        rafraichir();
    }

    public void rafraichir() {
        getChildren().clear();

        ImageView fond = new ImageView(
                secteur.EstEau() ? charger("eau.png") : charger("terrain.png")
        );
        reglerImage(fond);
        getChildren().add(fond);

        Composantes composante = secteur.getComposante();

        if (composante instanceof Mine mine) {
            ImageView mineVue = new ImageView(
                    mine.getType() == TypeMinerai.OR
                            ? charger("mine_or.png")
                            : charger("mine_ni.png")
            );
            reglerImage(mineVue);
            getChildren().add(mineVue);
        }

        if (composante instanceof Entrepot entrepot) {
            ImageView entrepotVue = new ImageView(
                    entrepot.getType() == TypeMinerai.OR
                            ? charger("entrepot_or.jpg")
                            : charger("entrepot_ni.png")
            );
            reglerImage(entrepotVue);
            getChildren().add(entrepotVue);
        }

        Robot robot = secteur.getRobot();

        if (robot != null) {
            ImageView robotVue = new ImageView(
                    robot.getType() == TypeMinerai.OR
                            ? charger("robot_or.png")
                            : charger("robot_ni.png")
            );
            reglerImage(robotVue);
            StackPane.setAlignment(robotVue, Pos.CENTER);
            getChildren().add(robotVue);
        }
    }

    private void reglerImage(ImageView imageView) {
        // L'image colle aux bords de la case
        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.fitHeightProperty().bind(this.heightProperty());
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