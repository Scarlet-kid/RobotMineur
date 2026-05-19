package org.example.Vue;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.example.Model.*;

public class CelluleVue extends StackPane {

    private final ImageView fond = new ImageView();
    private final ImageView objet = new ImageView();
    private final ImageView robot = new ImageView();

    public CelluleVue(Secteur secteur) {
        setPrefSize(70, 70);

        configurerImage(fond);
        configurerImage(objet);
        configurerImage(robot);

        getChildren().addAll(fond, objet, robot);

        setStyle("-fx-border-color: #dddddd; -fx-border-width: 1;");

        rafraichir(secteur);
    }

    private void configurerImage(ImageView img) {
        img.setPreserveRatio(false);
        img.fitWidthProperty().bind(widthProperty());
        img.fitHeightProperty().bind(heightProperty());
    }

    private Image charger(String nom) {
        var flux = getClass().getResourceAsStream("/images/" + nom);

        if (flux == null) {
            throw new RuntimeException("Image introuvable : /images/" + nom);
        }

        return new Image(flux);
    }

    public void rafraichir(Secteur secteur) {
        fond.setImage(charger("terrain.png"));
        objet.setImage(null);
        robot.setImage(null);

        objet.fitWidthProperty().unbind();
        objet.fitHeightProperty().unbind();
        robot.fitWidthProperty().unbind();
        robot.fitHeightProperty().unbind();

        objet.setTranslateY(0);
        robot.setTranslateY(0);

        configurerImage(objet);
        configurerImage(robot);

        if (secteur.EstEau()) {
            fond.setImage(charger("eau.png"));
            return;
        }

        Composantes composante = secteur.getComposante();
        Robot r = secteur.getRobot();

        boolean aComposante = composante != null;
        boolean aRobot = r != null;

        if (aComposante) {
            if (composante instanceof Mine mine) {
                if (mine.getType() == TypeMinerai.OR) {
                    objet.setImage(charger("mine_or.jpg"));
                } else {
                    objet.setImage(charger("mine_ni.png"));
                }
            }

            if (composante instanceof Entrepot entrepot) {
                if (entrepot.getType() == TypeMinerai.OR) {
                    objet.setImage(charger("entrepot_or.jpg"));
                } else {
                    objet.setImage(charger("entrepot_ni.png"));
                }
            }
        }

        if (aRobot) {
            if (r.toString().contains("OR")) {
                robot.setImage(charger("robot_or.png"));
            } else {
                robot.setImage(charger("robot_ni.png"));
            }
        }

        if (aComposante && aRobot) {
            objet.fitHeightProperty().unbind();
            robot.fitHeightProperty().unbind();

            objet.setFitHeight(getHeight() / 2);
            robot.setFitHeight(getHeight() / 2);

            objet.fitWidthProperty().bind(widthProperty());
            robot.fitWidthProperty().bind(widthProperty());

            objet.setTranslateY(-getHeight() / 4);
            robot.setTranslateY(getHeight() / 4);
        }
    }
}