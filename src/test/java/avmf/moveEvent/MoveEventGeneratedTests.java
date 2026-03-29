package avmf.moveEvent;

import avmf.moveEvent.MoveEventTestable;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MoveEventGeneratedTests {

    @Test
    void test_0() {
        assertEquals(1, MoveEventTestable.moveEventTestable(-2, 163, 4));
    }

    @Test
    void test_1() {
        assertEquals(1, MoveEventTestable.moveEventTestable(4, -31, -2));
    }

    @Test
    void test_2() {
        assertEquals(2, MoveEventTestable.moveEventTestable(3, -155, 0));
    }

    @Test
    void test_3() {
        assertEquals(3, MoveEventTestable.moveEventTestable(2, 82, 0));
    }

    @Test
    void test_4() {
        assertEquals(3, MoveEventTestable.moveEventTestable(1, 59, 0));
    }

    @Test
    void test_5() {
        assertEquals(4, MoveEventTestable.moveEventTestable(1, 124, 1));
    }

    @Test
    void test_6() {
        assertEquals(0, MoveEventTestable.moveEventTestable(1, 189, 3));
    }

}
