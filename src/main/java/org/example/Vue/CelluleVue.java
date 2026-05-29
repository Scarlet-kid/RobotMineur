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

    // ----------------------------------------------------------------
    // IMAGES STATIQUES : chargées UNE SEULE FOIS pour toute l'appli
    // static = partagées entre toutes les CelluleVue
    // ----------------------------------------------------------------
    private static final Image IMG_EAU       = chargerStatic("eau.png");
    private static final Image IMG_TERRAIN   = chargerStatic("terrain.png");
    private static final Image IMG_MINE_OR   = chargerStatic("mine_or.png");
    private static final Image IMG_MINE_NI   = chargerStatic("mine_ni.png");
    private static final Image IMG_ENTREPOT_OR = chargerStatic("entrepot_or.jpg");
    private static final Image IMG_ENTREPOT_NI = chargerStatic("entrepot_ni.png");
    private static final Image IMG_ROBOT_OR  = chargerStatic("robot_or.png");
    private static final Image IMG_ROBOT_NI  = chargerStatic("robot_ni.png");

    public CelluleVue(Secteur secteur) {
        this.secteur = secteur;
        setMinSize(0, 0);
        rafraichir();
    }

    public void rafraichir() {
        getChildren().clear();

        // Fond : on réutilise l'image déjà chargée, pas de new Image() !
        ImageView fond = new ImageView(secteur.EstEau() ? IMG_EAU : IMG_TERRAIN);
        reglerImage(fond);
        getChildren().add(fond);

        Composantes composante = secteur.getComposante();
        String infoTooltip = secteur.EstEau() ? "Eau (Infranchissable)" : "Plaine";

        // Mines
        if (composante instanceof Mine mine) {
            ImageView mineVue = new ImageView(
                    mine.getType() == TypeMinerai.OR ? IMG_MINE_OR : IMG_MINE_NI
            );
            reglerImage(mineVue);
            getChildren().add(mineVue);
            infoTooltip = "Mine " + mine.getType() + "\nReste : " + mine.getNbMinerais();
        }

        // Entrepôts
        if (composante instanceof Entrepot entrepot) {
            ImageView entrepotVue = new ImageView(
                    entrepot.getType() == TypeMinerai.OR ? IMG_ENTREPOT_OR : IMG_ENTREPOT_NI
            );
            reglerImage(entrepotVue);
            getChildren().add(entrepotVue);
            infoTooltip = "Entrepôt " + entrepot.getType() + "\nStock : " + entrepot.getStock();
        }

        // Robots
        Robot robot = secteur.getRobot();
        if (robot != null) {
            ImageView robotVue = new ImageView(
                    robot.getType() == TypeMinerai.OR ? IMG_ROBOT_OR : IMG_ROBOT_NI
            );
            reglerImage(robotVue);
            StackPane.setAlignment(robotVue, Pos.CENTER);
            getChildren().add(robotVue);
            infoTooltip = "Robot " + robot.getType() + "\nTransporte : " + robot.getNbMinerais();
        }

        Tooltip tooltip = new Tooltip(infoTooltip);
        tooltip.setShowDelay(Duration.millis(200));
        tooltip.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Tooltip.install(this, tooltip);
    }

    private void reglerImage(ImageView imageView) {
        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.fitHeightProperty().bind(this.heightProperty());
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
    }

    // Méthode statique pour charger une image une seule fois
    private static Image chargerStatic(String nom) {
        var flux = CelluleVue.class.getResourceAsStream("/images/" + nom);
        if (flux == null) {
            throw new RuntimeException("Image introuvable : /images/" + nom);
        }
        return new Image(flux);
    }
}