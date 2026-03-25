package avmf.reserveSeat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReserveSeatGeneratedTests {

    @Test
    void test_0() {
        assertEquals(1, ReserveSeatTestable.reserveSeatTestable(0, -3, 10, 14, true));
    }

    @Test
    void test_1() {
        assertEquals(2, ReserveSeatTestable.reserveSeatTestable(3, -4, 29, 17, true));
    }

    @Test
    void test_2() {
        assertEquals(2, ReserveSeatTestable.reserveSeatTestable(1, 4, 26, 18, false));
    }

    @Test
    void test_3() {
        assertEquals(3, ReserveSeatTestable.reserveSeatTestable(3, 2, 25, 18, true));
    }

    @Test
    void test_4() {
        assertEquals(4, ReserveSeatTestable.reserveSeatTestable(5, 4, 0, 8, false));
    }

    @Test
    void test_5() {
        assertEquals(0, ReserveSeatTestable.reserveSeatTestable(3, 2, 2, 4, true));
    }

}
