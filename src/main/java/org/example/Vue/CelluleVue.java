package org.example.Vue;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.example.Model.*;

public class CelluleVue extends StackPane {

    private static final int TAILLE = 85;

    private final ImageView fond = new ImageView();
    private final ImageView objet = new ImageView();
    private final ImageView robot = new ImageView();

    public CelluleVue(Secteur secteur) {
        setMinSize(TAILLE, TAILLE);
        setPrefSize(TAILLE, TAILLE);
        setMaxSize(TAILLE, TAILLE);

        getChildren().addAll(fond, objet, robot);

        setStyle("-fx-border-color: #dddddd; -fx-border-width: 1;");

        rafraichir(secteur);
    }

    private Image charger(String nom) {
        var flux = getClass().getResourceAsStream("/images/" + nom);

        if (flux == null) {
            throw new RuntimeException("Image introuvable : /images/" + nom);
        }

        return new Image(flux);
    }

    private void imagePleineCase(ImageView image) {
        image.setFitWidth(TAILLE);
        image.setFitHeight(TAILLE);
        image.setPreserveRatio(false);
        image.setTranslateY(0);
    }

    private void imageMoitieHaute(ImageView image) {
        image.setFitWidth(TAILLE);
        image.setFitHeight(TAILLE / 2.0);
        image.setPreserveRatio(false);
        image.setTranslateY(-TAILLE / 4.0);
    }

    private void imageMoitieBasse(ImageView image) {
        image.setFitWidth(TAILLE);
        image.setFitHeight(TAILLE / 2.0);
        image.setPreserveRatio(false);
        image.setTranslateY(TAILLE / 4.0);
    }

    public void rafraichir(Secteur secteur) {
        fond.setImage(charger("terrain.png"));
        imagePleineCase(fond);

        objet.setImage(null);
        robot.setImage(null);

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
                objet.setImage(mine.getType() == TypeMinerai.OR
                        ? charger("mine_or.jpg")
                        : charger("mine_ni.png"));
            }

            if (composante instanceof Entrepot entrepot) {
                objet.setImage(entrepot.getType() == TypeMinerai.OR
                        ? charger("entrepot_or.jpg")
                        : charger("entrepot_ni.png"));
            }
        }

        if (aRobot) {
            robot.setImage(r.toString().contains("OR")
                    ? charger("robot_or.png")
                    : charger("robot_ni.png"));
        }

        if (aComposante && aRobot) {
            imageMoitieHaute(objet);
            imageMoitieBasse(robot);
        } else {
            imagePleineCase(objet);
            imagePleineCase(robot);
        }
    }
}