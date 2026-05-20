package org.example.Vue;

import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.example.Model.*;

public class CelluleVue extends StackPane {

    private final Secteur secteur;

    public CelluleVue(Secteur secteur) {
        this.secteur = secteur;
        setMinSize(0, 0);
        rafraichir();
    }

    public void rafraichir() {
        getChildren().clear();

        // Fond
        ImageView fond = new ImageView(
                secteur.EstEau() ? charger("eau.png") : charger("terrain.png")
        );
        reglerImage(fond);
        getChildren().add(fond);

        Composantes composante = secteur.getComposante();
        String infoTooltip = secteur.EstEau() ? "Eau (Infranchissable)" : "Plaine";

        // Mines
        if (composante instanceof Mine mine) {
            ImageView mineVue = new ImageView(
                    mine.getType() == TypeMinerai.OR
                            ? charger("mine_or.png")
                            : charger("mine_ni.png")
            );
            reglerImage(mineVue);
            getChildren().add(mineVue);
            infoTooltip = "Mine " + mine.getType() + "\nReste : " + mine.getNbMinerais();
        }

        // Entrepôts
        if (composante instanceof Entrepot entrepot) {
            ImageView entrepotVue = new ImageView(
                    entrepot.getType() == TypeMinerai.OR
                            ? charger("entrepot_or.jpg")
                            : charger("entrepot_ni.png")
            );
            reglerImage(entrepotVue);
            getChildren().add(entrepotVue);
            infoTooltip = "Entrepôt " + entrepot.getType() + "\nStock : " + entrepot.getStock();
        }

        Robot robot = secteur.getRobot();

        // Robots
        if (robot != null) {
            ImageView robotVue = new ImageView(
                    robot.getType() == TypeMinerai.OR
                            ? charger("robot_or.png")
                            : charger("robot_ni.png")
            );
            reglerImage(robotVue);
            StackPane.setAlignment(robotVue, Pos.CENTER);
            getChildren().add(robotVue);
            infoTooltip = "Robot " + robot.getType() + "\nTransporte : " + robot.getNbMinerais();
        }

        // CRITÈRE BASTIEN & SCAPIN : Guidage (Information immédiate)
        Tooltip tooltip = new Tooltip(infoTooltip);
        tooltip.setShowDelay(Duration.millis(200)); // S'affiche rapidement
        tooltip.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Tooltip.install(this, tooltip);
    }

    private void reglerImage(ImageView imageView) {
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