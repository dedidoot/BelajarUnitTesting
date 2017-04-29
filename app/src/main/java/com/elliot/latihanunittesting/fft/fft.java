package com.elliot.latihanunittesting.fft;

/**
 * Created by TEAM on 4/29/2017.
 * Happy Coding
 */

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import android.app.Activity;
public class fft extends Activity {

    public void main(String args[])
    {
        double [] test = new double[20];
        double [] real_input = new double[20];
        DoubleFFT_1D ft= new DoubleFFT_1D(test.length);
        ft.realForward(real_input);
        ft.realInverse(real_input, true);
    }

}