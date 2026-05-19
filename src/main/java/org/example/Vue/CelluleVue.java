package org.example.Vue;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.example.Model.*;

public class CelluleVue extends StackPane {

    private final ImageView fond = new ImageView();
    private final ImageView objet = new ImageView();
    private final ImageView robot = new ImageView();
    private final Label texte = new Label();

    public CelluleVue(Secteur secteur) {
        setMinSize(70, 70);
        setPrefSize(70, 70);

        configurerImage(fond);
        configurerImage(objet);
        configurerImage(robot);

        texte.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: black;");
        StackPane.setAlignment(texte, Pos.BOTTOM_CENTER);

        getChildren().addAll(fond, objet, robot, texte);

        setStyle("-fx-border-color: #999999; -fx-border-width: 1;");
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
            System.out.println("Image introuvable : /images/" + nom);
            return null;
        }

        return new Image(flux);
    }

    public void rafraichir(Secteur secteur) {
        fond.setImage(charger("terrain.png"));
        objet.setImage(null);
        robot.setImage(null);
        texte.setText("");

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
                texte.setText("M" + mine.getNumero());
            }

            if (composante instanceof Entrepot entrepot) {
                if (entrepot.getType() == TypeMinerai.OR) {
                    objet.setImage(charger("entrepot_or.jpg"));
                } else {
                    objet.setImage(charger("entrepot_ni.png"));
                }
                texte.setText("E" + entrepot.getNumero());
            }
        }

        if (aRobot) {
            if (r.toString().contains("OR")) {
                robot.setImage(charger("robot_or.png"));
            } else {
                robot.setImage(charger("robot_ni.png"));
            }

            texte.setText(texte.getText() + " R" + r.getNumero());
        }

        if (aComposante && aRobot) {
            objet.fitHeightProperty().unbind();
            robot.fitHeightProperty().unbind();

            objet.setFitHeight(35);
            robot.setFitHeight(35);

            objet.setTranslateY(-17);
            robot.setTranslateY(17);
        }
    }
}