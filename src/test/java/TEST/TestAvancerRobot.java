package TEST;
import org.example.Model.Directions;
import org.example.Model.Robot;
import static org.junit.jupiter.api.Assertions.*;

import org.example.Model.Secteur;
import org.example.Model.TypeMinerai;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestAvancerRobot
{
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
    void tearDown() throws Exception
    {
        grille = null;
    }
    @Test
    void test1()
    {
        Robot robot = new Robot(1,null,TypeMinerai.OR,4,2);
        assertEquals(false,robot.avancer(Directions.NORD,grille));
    }
    @Test
    void test2()
    {
        Secteur secteurDepart = grille[2][1];
        Robot robot = new Robot(1,secteurDepart,TypeMinerai.OR,4,2);
        secteurDepart.setRobot(robot);
        grille[1][1].setEstEau(true);
        assertEquals(false, robot.avancer(Directions.NORD, grille));

    }
    @Test
    void test3()
    {
        Secteur secteurDepart = grille[5][6];
        Robot robot = new Robot(1,secteurDepart,TypeMinerai.OR,4,2);
        secteurDepart.setRobot(robot);
        assertEquals(true, robot.avancer(Directions.NORD, grille));
    }
    @Test
    void test4()
    {
        Secteur secteurDepart = grille[2][5];
        Robot robot = new Robot(1,secteurDepart,TypeMinerai.OR,4,2);
        secteurDepart.setRobot(robot);
        Secteur secteurCible = grille[1][5];
        Robot robot2 = new Robot(2, secteurCible, TypeMinerai.OR, 4, 2);
        secteurCible.setRobot(robot2);
        assertEquals(false, robot.avancer(Directions.NORD, grille));
    }
    @Test
    void test5()
    {
        Secteur secteurDepart = grille[0][5];
        Robot robot = new Robot(1,secteurDepart,TypeMinerai.OR,4,2);
        secteurDepart.setRobot(robot);
        assertEquals(false, robot.avancer(Directions.NORD, grille));
    }
    @Test
    void test6()
    {
        Secteur secteurDepart = grille[1][1];
        Robot robot = new Robot(1, secteurDepart, TypeMinerai.OR, 4, 2);
        secteurDepart.setRobot(robot);
        assertEquals(false, robot.avancer(null, grille));
    }
    @Test
    void test7()
    {
        Secteur secteurDepart = grille[1][5];
        Robot robot = new Robot(1,secteurDepart,TypeMinerai.OR,4,2);
        secteurDepart.setRobot(robot);
        Secteur secteurCible = grille[2][5];
        Robot robot2 = new Robot(2, secteurCible, TypeMinerai.OR, 4, 2);
        secteurCible.setRobot(robot2);
        assertEquals(false, robot.avancer(Directions.SUD, grille));
    }
    @Test
    void test8()
    {
        Secteur secteurDepart = grille[1][4];
        Robot robot = new Robot(1,secteurDepart,TypeMinerai.OR,4,2);
        secteurDepart.setRobot(robot);
        Secteur secteurCible = grille[1][5];
        Robot robot2 = new Robot(2, secteurCible, TypeMinerai.OR, 4, 2);
        secteurCible.setRobot(robot2);
        assertEquals(false, robot.avancer(Directions.EST, grille));
    }
    @Test
    void test9()
    {
        Secteur secteurDepart = grille[1][0];
        Robot robot = new Robot(1,secteurDepart,TypeMinerai.OR,4,2);
        secteurDepart.setRobot(robot);
        assertEquals(false, robot.avancer(Directions.OUEST, grille));
    }


}
