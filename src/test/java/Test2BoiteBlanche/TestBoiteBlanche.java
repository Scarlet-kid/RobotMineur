package Test2BoiteBlanche;

import org.example.Model.Dijkstra;
import org.example.Model.Directions;
import org.example.Model.Robot;
import org.example.Model.Secteur;
import org.example.Model.TypeMinerai;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestBoiteBlanche {

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
    void test1_cheminDirect() {
        Secteur depart = grille[1][1];
        Secteur arrivee = grille[1][2];

        List<Directions> resultat = Dijkstra.calculerChemin(grille, depart, arrivee);

        assertEquals(List.of(Directions.EST), resultat);
    }

    @Test
    void test2_departEgalArrivee() {
        Secteur depart = grille[1][1];

        List<Directions> resultat = Dijkstra.calculerChemin(grille, depart, depart);

        assertTrue(resultat.isEmpty());
    }

    @Test
    void test3_horsLimites() {
        Secteur depart = grille[0][0];
        Secteur arrivee = grille[0][1];

        List<Directions> resultat = Dijkstra.calculerChemin(grille, depart, arrivee);

        assertEquals(List.of(Directions.EST), resultat);
    }

    @Test
    void test4_eau() {
        Secteur depart = grille[1][0];
        Secteur arrivee = grille[1][2];

        grille[1][1] = new Secteur(1, 1, true);

        List<Directions> resultat = Dijkstra.calculerChemin(grille, depart, arrivee);

        assertFalse(resultat.isEmpty());
        assertNotEquals(List.of(Directions.EST, Directions.EST), resultat);
    }

    @Test
    void test5_cheminImpossible() {
        Secteur depart = grille[0][0];
        Secteur arrivee = grille[1][1];

        grille[0][1] = new Secteur(0, 1, true);
        grille[1][0] = new Secteur(1, 0, true);
        grille[1][2] = new Secteur(1, 2, true);
        grille[2][1] = new Secteur(2, 1, true);

        List<Directions> resultat = Dijkstra.calculerChemin(grille, depart, arrivee);

        assertTrue(resultat.isEmpty());
    }

    @Test
    void test6_caseDejaVisitee() {
        Secteur depart = grille[0][0];
        Secteur arrivee = grille[2][2];

        List<Directions> resultat = Dijkstra.calculerChemin(grille, depart, arrivee);

        assertFalse(resultat.isEmpty());
        assertEquals(4, resultat.size());
    }

    @Test
    void test7_grilleNullException() {
        assertThrows(NullPointerException.class, () -> {
            Dijkstra.calculerChemin(null, null, null);
        });
    }

    @Test
    void test8_departNullException() {
        assertThrows(NullPointerException.class, () -> {
            Dijkstra.calculerChemin(grille, null, grille[1][1]);
        });
    }

    @Test
    void test9_arriveeNullException() {
        assertThrows(NullPointerException.class, () -> {
            Dijkstra.calculerChemin(grille, grille[0][0], null);
        });
    }
}