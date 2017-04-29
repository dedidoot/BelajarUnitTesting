package com.elliot.latihanunittesting.fft;

/**
 * Created by TEAM on 4/29/2017.
 * Happy Coding
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.elliot.latihanunittesting.R;

/**
 * dokumentasi FFT
 * https://sites.google.com/site/androidstem/engineering/mobile-labs/lab-2-fourier-transform-on-speech-signal-processing
 * http://introcs.cs.princeton.edu/java/97data/FFT.java.html
 * **/

public class FftprocessActivity extends Activity {
    /** Called when the activity is first created. */
    ImageButton btnStart,btnExit;
    ImageButton s1record, s1stop,s1play;
    ImageButton s2record, s2stop,s2play;
    SurfaceView view, viewfft;
    TextView status;
    MediaRecorder m1=null;
    MediaPlayer m2=null;

    FFTSound sample_1 = new FFTSound();
    double sed1=0.0;
    FFTSound sample_2 = new FFTSound();
    double sed2=0.0;
    FFTSound SoundWave=new FFTSound();
    double sed=0.0;

    static final int sampleRate = 8000;// Sample Rate 8000Hz to record human voice
    static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    static final int xMax = 16;
    static final int xMin = 2;
    static final int yMax = 30;
    static final int yMin = 1;

    int recBufSize=sampleRate*30;//buffer size of recording
    AudioRecord audioRecord;
    Paint mPaint;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "Ready to start.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please first insert your SD Card", Toast.LENGTH_LONG).show();
            //return;
        }
        //Recording
        recBufSize = AudioRecord.getMinBufferSize(sampleRate,
                channelConfiguration, audioEncoding);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                channelConfiguration, audioEncoding, recBufSize);
        //Buttons
        btnStart = (ImageButton) this.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new ClickEvent());
        btnExit = (ImageButton) this.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new ClickEvent());

        //Set up status
        status=(TextView)findViewById(R.id.status1);
        //Buttons for sample 1
        s1record = (ImageButton) this.findViewById(R.id.sample1record);
        s1stop = (ImageButton) this.findViewById(R.id.sample1stop);
        s1stop.setEnabled(false);
        s1play = (ImageButton) this.findViewById(R.id.sample1play);
        s1play.setEnabled(false);

        //Buttons for sample 2
        s2record = (ImageButton) this.findViewById(R.id.sample2record);
        s2stop = (ImageButton) this.findViewById(R.id.sample2stop);
        s2stop.setEnabled(false);
        s2play = (ImageButton) this.findViewById(R.id.sample2play);
        s2play.setEnabled(false);

        //panel and brush
        view = (SurfaceView) this.findViewById(R.id.SurfaceView01);
        //view.setOnTouchListener(new TouchEvent());
        viewfft = (SurfaceView) this.findViewById(R.id.SurfaceView02);
        //viewfft.setOnTouchListener(new TouchEvent());
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);// brush color
        mPaint.setStrokeWidth(1);// brush size
        //sound wave
        SoundWave.initFFTSound(xMin, yMax, view.getHeight()/2);

        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                SoundWave.baseLine=view.getHeight()/2;
                SoundWave.Start(audioRecord,recBufSize,view,viewfft,mPaint);
            }});


        s1record.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //File sd = Environment.getExternalStorageDirectory();
                //File file = new  File(sd, "sample1.wav");

                //setTitle("=="+path.getAbsolutePath());
                status.setText("GET READY FOR RECORDING");
                //String fileName = "audio_1.mp4";
                sample_1.baseLine=view.getHeight()/2;
                sample_1.Start(audioRecord,recBufSize,view,viewfft,mPaint);
                sed1=sample_1.getSED();
                try
                {
                    m1=new MediaRecorder();
                    m1.setAudioSource(MediaRecorder.AudioSource.MIC);
                    m1.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
                    m1.setOutputFile("/sdcard/sample1.amr");
                    //m1.setOutputFile(path.getAbsolutePath());
                    m1.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    m1.setAudioChannels(1);
                    m1.setAudioSamplingRate(8000);

                    m1.prepare();
                    m1.start();
                }
                catch(Exception e)
                {
                    status.setText("ERROR IN RECORDING "+e.getMessage());
                }
                s1stop.setEnabled(true);
                s1play.setEnabled(true);
            }});

        s1stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                sample_1.Stop();
                try
                {
                    status.setText("RECORDING STOPPED");
                    m1.stop();
                    m1.reset();
                    m1.release();
                }
                catch(Exception e)
                {
                    status.setText("ERROR IN STOPRECORDING FUNCTION");
                }

            }});

        s1play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //audioRecord.stop();
                status.setText("RECORDED AUDIO PLAYING");
                try
                {
                    m2=new MediaPlayer();
                    m2.setDataSource("/sdcard/sample1.amr");
                    //m2.setDataSource(path.getAbsolutePath());
                    m2.prepare();
                    m2.start();
                }
                catch(Exception e)
                {
                    System.out.println("ERROR in playaudio FUNCTION");
                }


            }});

        s2record.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){

                status.setText("GET READY FOR RECORDING");
                sample_2.baseLine=view.getHeight()/2;
                sample_2.Start(audioRecord,recBufSize,view,viewfft,mPaint);
                sed2=sample_2.getSED();
                try
                {
                    m1=new MediaRecorder();
                    m1.setAudioSource(MediaRecorder.AudioSource.MIC);
                    m1.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
                    m1.setOutputFile("/sdcard/sample2.amr");
                    //m1.setOutputFile(path.getAbsolutePath());
                    m1.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    m1.setAudioChannels(1);
                    m1.setAudioSamplingRate(8000);

                    m1.prepare();
                    m1.start();
                }
                catch(Exception e)
                {
                    status.setText("ERROR IN RECORDING "+e.getMessage());
                }
                s2stop.setEnabled(true);
                s2play.setEnabled(true);
            }});

        s2stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                sample_2.Stop();
                try
                {
                    status.setText("RECORDING STOPPED");
                    m1.stop();
                    m1.reset();
                    m1.release();
                }
                catch(Exception e)
                {
                    status.setText("ERROR IN STOPRECORDING FUNCTION");
                }

            }});

        s2play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //audioRecord.stop();
                status.setText("RECORDED AUDIO PLAYING");
                try
                {
                    m2=new MediaPlayer();
                    m2.setDataSource("/sdcard/sample2.amr");
                    //m2.setDataSource(path.getAbsolutePath());
                    m2.prepare();
                    m2.start();
                }
                catch(Exception e)
                {
                    System.out.println("ERROR in playaudio FUNCTION");
                }


            }});

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    class ClickEvent implements View.OnClickListener {

        public void onClick(View v) {
            if (v == btnStart) {
                SoundWave.baseLine=view.getHeight()/2;
                SoundWave.Start(audioRecord,recBufSize,view,viewfft,mPaint);
                sed=SoundWave.getSED();
            } else if (v == btnExit) {
                //SoundWave.Stop();
                if ( ((sed-sed1)*(sed-sed1))>((sed-sed2)*(sed-sed2)))
                    status.setText("Voice Matches Sample2");
                else if ( ((sed-sed1)*(sed-sed1))<((sed-sed2)*(sed-sed2)))
                    status.setText("Voice Matches Sample1");
            }
        }
    }

    public static String getTempPath(String paths) {

        String path;

        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

            switch (paths) {
                case "media":
                    path = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.elliot.latihanunittesting/media/";
                    break;
                case "profile":
                    path = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.elliot.latihanunittesting/profile/";
                    break;
                case "crop":
                    path = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.elliot.latihanunittesting/crop/";
                    break;
                case "camera":
                    path = Environment.getExternalStorageDirectory().getPath() + "/elliot/";
                    break;
                case "resize":
                    path = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.elliot.latihanunittesting/resize/";
                    break;
                default:
                    path = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.elliot.latihanunittesting/default/";
                    break;
            }
        } else {
            switch (paths) {
                case "media":
                    path = Environment.getDataDirectory().getPath() + "/Android/data/com.elliot.latihanunittesting/media/";
                    break;
                case "profile":
                    path = Environment.getDataDirectory().getPath() + "/Android/data/com.elliot.latihanunittesting/profile/";
                    break;
                case "crop":
                    path = Environment.getDataDirectory().getPath() + "/Android/data/com.elliot.latihanunittesting/crop/";
                    break;
                case "camera":
                    path = Environment.getDataDirectory().getPath() + "/elliot/";
                    break;
                case "resize":
                    path = Environment.getDataDirectory().getPath() + "/Android/data/com.elliot.latihanunittesting/resize/";
                    break;
                default:
                    path = Environment.getDataDirectory().getPath() + "/Android/data/com.elliot.latihanunittesting/default/";
                    break;
            }
        }

        File dir = new File(path);
        if (!(dir.exists() && dir.isDirectory()))
            dir.mkdirs();

        return path;

    }

}