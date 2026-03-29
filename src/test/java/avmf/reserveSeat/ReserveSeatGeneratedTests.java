package avmf.reserveSeat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReserveSeatGeneratedTests {

    @Test
    void test_0() {
        assertEquals(1, ReserveSeatTestable.reserveSeatTestable(0, 0, 26, 16, true));
    }

    @Test
    void test_1() {
        assertEquals(2, ReserveSeatTestable.reserveSeatTestable(3, -3, 25, 7, true));
    }

    @Test
    void test_2() {
        assertEquals(2, ReserveSeatTestable.reserveSeatTestable(2, 2, 34, 7, false));
    }

    @Test
    void test_3() {
        assertEquals(3, ReserveSeatTestable.reserveSeatTestable(3, 2, 37, 12, false));
    }

    @Test
    void test_4() {
        assertEquals(4, ReserveSeatTestable.reserveSeatTestable(1, 0, 4, 10, false));
    }

    @Test
    void test_5() {
        assertEquals(0, ReserveSeatTestable.reserveSeatTestable(2, 1, 2, 4, true));
    }

}
