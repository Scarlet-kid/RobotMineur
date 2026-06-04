package Test2BoiteNoire;

import org.example.Model.Directions;
import org.example.Model.Robot;
import org.example.Model.Secteur;
import org.example.Model.TypeMinerai;
import org.example.Model.Dijkstra;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class DijkstraBoiteNoireTest {

    protected Secteur[][] grille;

    @BeforeEach
    void setUp() throws Exception {
        grille = new Secteur[10][10];
        for (int l = 0; l < 10; l++) {
            for (int c = 0; c < 10; c++) {
                grille[l][c] = new Secteur(l, c, false);
            }
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        grille = null;
    }

    @Test
    void test_T1_DepartHorsLimite() {
        Secteur departHorsLimite = new Secteur(-1, 8, false);
        Secteur arrivee = grille[5][5];


        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            Dijkstra.calculerChemin(grille, departHorsLimite, arrivee);
        });
    }

    @Test
    void test_T2_ArriveeHorsLimite() {
        Secteur depart = grille[0][0];
        Secteur arriveeHorsLimite = new Secteur(4, 11, false);


        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            Dijkstra.calculerChemin(grille, depart, arriveeHorsLimite);
        });
    }
    @Test
    void test_T3_GrilleVide() {
        Secteur depart  = grille[0][0];
        Secteur arrivee = grille[2][3];

        List<Directions> chemin = Dijkstra.calculerChemin(grille, depart, arrivee);

        assertNotNull(chemin);
        assertEquals(5, chemin.size());
        assertEquals(Directions.SUD, chemin.get(0));
        assertEquals(Directions.SUD, chemin.get(1));
        assertEquals(Directions.EST, chemin.get(2));
        assertEquals(Directions.EST, chemin.get(3));
        assertEquals(Directions.EST, chemin.get(4));
    }

    @Test
    void test_T4_MurEauBloquant() {
        // De l'eau sur toutes les cases de la ligne 4, une par une
        grille[4][0].setEstEau(true);
        grille[4][1].setEstEau(true);
        grille[4][2].setEstEau(true);
        grille[4][3].setEstEau(true);
        grille[4][4].setEstEau(true);
        grille[4][5].setEstEau(true);
        grille[4][6].setEstEau(true);
        grille[4][7].setEstEau(true);
        grille[4][8].setEstEau(true);
        grille[4][9].setEstEau(true);

        Secteur depart  = grille[2][2];
        Secteur arrivee = grille[6][2];

        List<Directions> chemin = Dijkstra.calculerChemin(grille, depart, arrivee);
        assertTrue(chemin == null || chemin.isEmpty());
    }

    @Test
    void test_T5_ContournementBlocEau() {
        grille[1][1].setEstEau(true);
        grille[1][2].setEstEau(true);
        grille[1][3].setEstEau(true);

        Secteur depart  = grille[0][2];
        Secteur arrivee = grille[2][2];

        List<Directions> chemin = Dijkstra.calculerChemin(grille, depart, arrivee);

        assertNotNull(chemin);
        assertEquals(6, chemin.size());
        assertEquals(Directions.EST,   chemin.get(0));
        assertEquals(Directions.EST,   chemin.get(1));
        assertEquals(Directions.SUD,   chemin.get(2));
        assertEquals(Directions.SUD,   chemin.get(3));
        assertEquals(Directions.OUEST, chemin.get(4));
        assertEquals(Directions.OUEST, chemin.get(5));
    }

    @Test
    void test_T6_MurAvecPorte() {

        grille[4][0].setEstEau(true);
        grille[4][1].setEstEau(true);
        grille[4][2].setEstEau(true);
        grille[4][3].setEstEau(true);
        grille[4][4].setEstEau(true);

        grille[4][6].setEstEau(true);
        grille[4][7].setEstEau(true);
        grille[4][8].setEstEau(true);
        grille[4][9].setEstEau(true);

        Secteur depart  = grille[2][5];
        Secteur arrivee = grille[5][5];

        List<Directions> chemin = Dijkstra.calculerChemin(grille, depart, arrivee);

        assertNotNull(chemin);
        assertEquals(3, chemin.size());
        assertEquals(Directions.SUD, chemin.get(0));
        assertEquals(Directions.SUD, chemin.get(1));
        assertEquals(Directions.SUD, chemin.get(2));
    }

    @Test
    void test_T7_ArriveeSurRobot() {

        Secteur secteurCible = grille[0][1];
        Robot robot2 = new Robot(2, secteurCible, TypeMinerai.OR, 4, 2);
        secteurCible.setRobot(robot2);

        Secteur depart  = grille[0][0];
        Secteur arrivee = grille[0][1];

        List<Directions> chemin = Dijkstra.calculerChemin(grille, depart, arrivee);

        assertNotNull(chemin);
        assertEquals(1, chemin.size());
        assertEquals(Directions.EST, chemin.get(0));
    }

    @Test
    void test_T8_ArriveeInaccessible() {

        Secteur secteurRobot = grille[0][1];
        Robot robot2 = new Robot(2, secteurRobot, TypeMinerai.OR, 4, 2);
        secteurRobot.setRobot(robot2);


        grille[1][0].setEstEau(true);

        Secteur depart  = grille[5][5];
        Secteur arrivee = grille[0][0];

        List<Directions> chemin = Dijkstra.calculerChemin(grille, depart, arrivee);
        assertTrue(chemin == null || chemin.isEmpty());
    }
}