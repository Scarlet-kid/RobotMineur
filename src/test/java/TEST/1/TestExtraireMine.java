package TEST;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.Model.Mine;
import org.example.Model.TypeMinerai;



public class TestExtraireMine {
    protected Mine mine;

    @BeforeEach
    void setUp() throws Exception {
        mine = new Mine(1, null, TypeMinerai.OR, 50);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mine = null;
    }
    @Test
    void test1() {
        mine = new Mine(1, null, TypeMinerai.OR, 0);
        assertEquals(0, mine.extraire(2));
    }
    @Test
    void test2() {
        mine = new Mine(1, null, TypeMinerai.OR, 1);
        assertEquals(1, mine.extraire(1));
    }
    @Test
    void test3() {
        mine = new Mine(1, null, TypeMinerai.OR, 1);
        assertEquals(1, mine.extraire(2));
    }
    @Test
    void test4() {
        mine = new Mine(1, null, TypeMinerai.OR, 1);
        assertEquals(1, mine.extraire(3));
    }
    @Test
    void test5() {
        mine = new Mine(1, null, TypeMinerai.OR, 99);
        assertEquals(1, mine.extraire(1));
    }
    @Test
    void test6() {
        mine = new Mine(1, null, TypeMinerai.OR, 99);
        assertEquals(2, mine.extraire(2));
    }
    @Test
    void test7() {
        mine = new Mine(1, null, TypeMinerai.OR, 99);
        assertEquals(3, mine.extraire(3));
    }

    @Test
    void test8() {
        mine = new Mine(1, null, TypeMinerai.OR, 100);
        assertEquals(1, mine.extraire(1));
    }

    @Test
    void test9() {
        mine = new Mine(1, null, TypeMinerai.OR, 100);
        assertEquals(2, mine.extraire(2));
    }

    @Test
    void test10() {
        mine = new Mine(1, null, TypeMinerai.OR, 100);
        assertEquals(3, mine.extraire(3));
    }
    @Test
    void test11() {
        assertThrows(IllegalStateException.class, () -> {
            mine = new Mine(1, null, TypeMinerai.OR, -1);
            mine.extraire(2);
        });
    }

    @Test
    void test12() {
        assertThrows(IllegalArgumentException.class, () -> {
            mine.extraire(-1);
        });
    }

    @Test
    void test14() {
        assertThrows(IllegalArgumentException.class, () -> {
            mine.extraire(0);
        });
    }

    @Test
    void test15() {
        mine = new Mine(1, null, TypeMinerai.OR, 1);
        assertThrows(IllegalArgumentException.class, () -> {
            mine.extraire(4);
        });
    }

    @Test
    void test16() {
        mine = new Mine(1, null, TypeMinerai.OR, 99);
        assertThrows(IllegalArgumentException.class, () -> {
            mine.extraire(4);
        });
    }

    @Test
    void test17() {
        mine = new Mine(1, null, TypeMinerai.OR, 100);
        assertThrows(IllegalArgumentException.class, () -> {
            mine.extraire(4);
        });
    }

    @Test
    void test18() {
        assertThrows(IllegalStateException.class, () -> {
            mine = new Mine(1, null, TypeMinerai.OR, 101);
            mine.extraire(1);
        });
    }

    @Test
    void test19() {
        assertThrows(IllegalStateException.class, () -> {
            mine = new Mine(1, null, TypeMinerai.OR, 101);
            mine.extraire(2);
        });
    }

    @Test
    void test20() {
        assertThrows(IllegalStateException.class, () -> {
            mine = new Mine(1, null, TypeMinerai.OR, 101);
            mine.extraire(3);
        });
    }

    @Test
    void test21() {
        assertThrows(IllegalStateException.class, () -> {
            mine = new Mine(1, null, TypeMinerai.OR, 101);
            mine.extraire(4);
        });
    }
}
