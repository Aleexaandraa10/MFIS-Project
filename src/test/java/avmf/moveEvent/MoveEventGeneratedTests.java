package avmf.moveEvent;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MoveEventGeneratedTests {

    @Test
    void test_0() {
        assertEquals(0, MoveEventTestable.moveEventTestable(3, 41, 1));
    }

    @Test
    void test_1() {
        assertEquals(1, MoveEventTestable.moveEventTestable(4, -33, 5));
    }

    @Test
    void test_2() {
        assertEquals(2, MoveEventTestable.moveEventTestable(1, -84, -2));
    }

    @Test
    void test_3() {
        assertEquals(3, MoveEventTestable.moveEventTestable(2, 3, -2));
    }

    @Test
    void test_4() {
        assertEquals(4, MoveEventTestable.moveEventTestable(1, 172, 1));
    }

}
