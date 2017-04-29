package com.elliot.latihanunittesting.fft;

/**
 * Created by TEAM on 4/29/2017.
 * Happy Coding
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;
import java.lang.reflect.Array;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioRecord;
import android.util.Log;
import android.view.SurfaceView;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class FFTSound {
    //private ArrayList<short[]> inBuf = new ArrayList<short[]>();
    private short [] inBuf;
    private double [] fftBuf;
    private double SED=0.0;//spectral energy density
    private boolean isRecording = false;// Make judgment if it is recording now

    public int rateX = 4;  //X shrink rate

    public int rateY = 4;  //Y shrink rate

    public int baseLine = 0;//Baseline for Y

    public double getSED()
    {
        return SED;
    }

    public void initFFTSound(int rateX, int rateY, int baseLine) { //Initialize
        this.rateX = rateX;
        this.rateY = rateY;
        this.baseLine = baseLine;
    }

    // On Start
    public void Start(AudioRecord audioRecord, int recBufSize, SurfaceView view,
                      SurfaceView viewfft, Paint mPaint) {
        isRecording = true;
        new RecordThread(audioRecord, recBufSize).start();// Start Recording
        new DrawThread(view, mPaint).start();// Draw Wave Graph of recording sound
        new DrawThreadfft(viewfft, mPaint).start();// Draw FFT Graph of recorded sound
    }

    //Stop
    public void Stop() {
        isRecording = false;
        //inBuf.clear();// clean up input buffer
    }

    //Get Frequency Intensity
    public double GetFrequencyIntensity(double re, double im)
    {
        return Math.sqrt((re*re)+(im*im));
    }
    /*
     * for(int j = 0; j < org_buffer.length; j++)
             {
                 finall[j] = (byte) GetFrequencyIntensity(real_input[j], img_input[j]);
             }
     */


    //Save data from MIC to buffer

    class RecordThread extends Thread {
        private int recBufSize;
        private AudioRecord audioRecord;

        public RecordThread(AudioRecord record, int recBufSize) {
            this.audioRecord = record;
            this.recBufSize = recBufSize;
        }
        public void run() {
            try {
                short[] buffer = new short[recBufSize];
                audioRecord.startRecording();// start recording

                // save MIC data to buffer
                int bufferReadResult = audioRecord.read(buffer, 0,
                        recBufSize);
                short[] tmpBuf = new short[bufferReadResult / rateX];
                for (int i = 0, ii = 0; i < tmpBuf.length; i++, ii = i* rateX)
                {
                    tmpBuf[i] = buffer[ii];
                }
                inBuf = tmpBuf;
                audioRecord.stop();
            } catch (Throwable t) {
            }
        }
    };
    //Draw original sound
    class DrawThread extends Thread {
        private int oldX = 0;//previous X
        private int oldY = 0;//previous Y
        private SurfaceView view;//
        private int X_index = 0;// x position
        private Paint mPaint;// paint
        public DrawThread(SurfaceView view, Paint mPaint) {
            this.view = view;
            this.mPaint = mPaint;
        }
        public void run() {
            short[] buf = inBuf;
            short[] tmpBuf = buf;
            SimpleDraw(X_index, tmpBuf, rateY, baseLine);
            if(tmpBuf==null){
                return;
            }
            X_index = X_index + tmpBuf.length;
            if (X_index > view.getWidth()) {
                X_index = 0;
            }

        }
        //Draw
        void SimpleDraw(int start, short[] buffer, int rate, int baseLine) {
            if (start == 0)
                oldX = 0;
            Log.wtf("start","=> "+start);
            Log.wtf("rate","=> "+rate);
            Log.wtf("baseLine","=> "+baseLine);
            Log.wtf("view","=> "+view);
            if(buffer==null){
                return;
            }
            Log.wtf("buffer","=> "+buffer);
            Canvas canvas = view.getHolder().lockCanvas(
                    new Rect(start, 0, start + buffer.length, view.getHeight()));// get panel
            canvas.drawColor(Color.BLACK);// clean background
            int y;
            for (int i = 0; i < buffer.length; i++) {// draw
                int x = i + start;
                y = buffer[i] / rate + baseLine;// adjust picture
                canvas.drawLine(oldX, oldY, x, y, mPaint);
                oldX = x;
                oldY = y;
            }
            view.getHolder().unlockCanvasAndPost(canvas);// unlock panel
        }
    }

    //Draw FFT
    class DrawThreadfft extends Thread {
        private int oldX = 0;// previous X
        private int oldY = 0;// previous Y
        private SurfaceView sfv;// Panel
        private int X_index = 0;// X position
        private Paint mPaint;// paint
        public DrawThreadfft(SurfaceView sfv, Paint mPaint) {
            this.sfv = sfv;
            this.mPaint = mPaint;
        }
        public void run() {
            short[] buf= inBuf;
            short[] temp = buf;
            if(temp==null){
                return;
            }
            fftBuf = new double[temp.length];
            for(int j = 0; j < temp.length; j++)
            {
                fftBuf[j] = (double) temp[j];
            }
            DoubleFFT_1D ft = new DoubleFFT_1D(fftBuf.length);
            ft.realForward(fftBuf);
            //some filtering here
            //ft.realInverse(input, true);
            short[] fina = new short[temp.length];
            for(int j = 0; j < temp.length; j++)
            {
                fftBuf[j] = Math.abs(fftBuf[j]);
                fftBuf[j] = (Math.log10(fftBuf[j]))*100;
                SED+=fftBuf[j]*fftBuf[j];
                fina[j] = (short) fftBuf[j];
            }

            buf = fina;
            short[] tmpBuf = buf;
            SimpleDraw(X_index, tmpBuf, rateY, baseLine);// draw data from buffer
            X_index = X_index + tmpBuf.length;
            if (X_index > sfv.getWidth()) {
                X_index = 0;
            }
        }

        void SimpleDraw(int start, short[] buffer, int rate, int baseLine) {
            if (start == 0)
                oldX = 0;
            Canvas canvas = sfv.getHolder().lockCanvas(
                    new Rect(start, 0, start + buffer.length, sfv.getHeight()));
            canvas.drawColor(Color.BLACK);
            int y;
            for (int i = 0; i < buffer.length; i++) {
                int x = i + start;
                y = buffer[i] / rate + baseLine;
                canvas.drawLine(oldX, oldY, x, y, mPaint);
                oldX = x;
                oldY = y;
            }
            sfv.getHolder().unlockCanvasAndPost(canvas);
        }
    }
}