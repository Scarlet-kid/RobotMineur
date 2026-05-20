package org.example.Vue;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.example.Model.*;

public class CelluleVue extends StackPane {

    private static final int TAILLE = 78;

    private final ImageView fond = new ImageView();
    private final ImageView composant = new ImageView();
    private final ImageView robot = new ImageView();

    public CelluleVue(Secteur secteur) {

        setPrefSize(TAILLE, TAILLE);
        setMinSize(TAILLE, TAILLE);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        configurer(fond);
        configurer(composant);
        configurer(robot);

        getChildren().addAll(fond, composant, robot);

        setStyle("-fx-border-color: #d0d0d0; -fx-border-width: 0.5;");

        rafraichir(secteur);
    }

    private void configurer(ImageView img) {

        img.setFitWidth(TAILLE);
        img.setFitHeight(TAILLE);

        img.setPreserveRatio(false);
        img.setSmooth(true);
    }

    private Image charger(String nom) {

        var flux = getClass().getResourceAsStream("/images/" + nom);

        if (flux == null) {
            throw new RuntimeException("Image introuvable : " + nom);
        }

        return new Image(flux);
    }

    public void rafraichir(Secteur secteur) {

        fond.setImage(charger("terrain.png"));

        composant.setImage(null);
        robot.setImage(null);

        if (secteur.EstEau()) {

            fond.setImage(charger("eau.png"));
            return;
        }

        Composantes c = secteur.getComposante();

        if (c instanceof Mine mine) {

            if (mine.getType() == TypeMinerai.OR) {
                composant.setImage(charger("mine_or.jpg"));
            }
            else {
                composant.setImage(charger("mine_ni.png"));
            }
        }

        if (c instanceof Entrepot entrepot) {

            if (entrepot.getType() == TypeMinerai.OR) {
                composant.setImage(charger("entrepot_or.jpg"));
            }
            else {
                composant.setImage(charger("entrepot_ni.png"));
            }
        }

        Robot r = secteur.getRobot();

        if (r != null) {

            if (r.getType() == TypeMinerai.OR) {
                robot.setImage(charger("robot_or.png"));
            }
            else {
                robot.setImage(charger("robot_ni.png"));
            }
        }
    }
}