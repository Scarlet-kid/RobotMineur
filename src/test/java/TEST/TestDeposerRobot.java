package TEST;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.example.Model.Robot;
import org.example.Model.Secteur;
import org.example.Model.Mine;
import org.example.Model.Entrepot;
import org.example.Model.TypeMinerai;

public class TestDeposerRobot {
    protected Secteur secteur;
    protected Robot robot;

    @BeforeEach
    void setUp() throws Exception {

        secteur = new Secteur(1,1,false);
    }
    @AfterEach
    public void tearDown() throws Exception {
        secteur = null;
        robot = null;
    }
    @Test
    void test1()
    {
        secteur.setComposante(new Mine(1, secteur, TypeMinerai.OR, 50));
        robot = new Robot(2,secteur,TypeMinerai.OR,55,2);
        assertEquals(false, robot.deposer());
    }
    @Test
    void test2()
    {
        secteur.setComposante(new Entrepot(TypeMinerai.NICKEL,1,secteur));
        robot = new Robot(2,secteur,TypeMinerai.OR,55,2);
        assertEquals(false, robot.deposer());

    }
    @Test
    void test3()
    {
        secteur.setComposante(new Entrepot(TypeMinerai.OR,1,secteur));
        robot = new Robot(2,secteur,TypeMinerai.OR,55,0);
        assertEquals(false, robot.deposer());

    }
    @Test
    void test4()
    {
        secteur.setComposante(new Entrepot(TypeMinerai.OR,2,secteur));
        robot = new Robot(2,secteur,TypeMinerai.OR,55,1);
        robot.SetNbMineris(1);
        assertEquals(true, robot.deposer());
    }
}
