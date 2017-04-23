package com.elliot.latihanunittesting;

import com.elliot.latihanunittesting.logic.MathCalculator;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by TEAM on 4/23/2017.
 * Happy Coding
 */

public class MatchCalculatorTest {

    private MathCalculator mathCalculator;

    @Before
    public void setup() {
        mathCalculator = new MathCalculator();
    }

    @Test
    public void testTambah() {
        org.junit.Assert.assertEquals("logic method tambah tidak benar", mathCalculator.tambah(5, 5), 10);
    }

    @Test
    public void testkurang() {
        org.junit.Assert.assertEquals("logic method kurang tidak benar", mathCalculator.kurang(5, 5), 0);
    }

    @Test
    public void testKali() {
        org.junit.Assert.assertEquals("logic method kali tidak benar", mathCalculator.kali(5, 5), 25);
    }

    @Test
    public void testBagi() {
        // parameter terakhir adalah maksimal value dari expected dan actual
        // contohnya 87 : 9 = 9.666666666 , maka dari itu parameter terakhir adalah 0.99
        org.junit.Assert.assertEquals("logic method bagi tidak benar", mathCalculator.bagi(87, 9), 9, 0.99);
    }

}
