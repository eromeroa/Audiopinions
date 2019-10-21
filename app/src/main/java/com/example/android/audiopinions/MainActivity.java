package com.example.android.audiopinions;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Chronometer chrono;
    private long pauseOffset;
    private boolean running;
    private boolean recorded;
    private ImageButton botonCentral;
    private Button botonIzquierdo;
    private Button botonDerecho;

    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    private MediaRecorder myAudioRecorder = new MediaRecorder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        chrono = findViewById(R.id.chronometer);
        botonCentral = findViewById(R.id.botonCentral);
        botonDerecho = findViewById(R.id.botonSend);
        botonIzquierdo = findViewById(R.id.botonDelete);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
        }

        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setAudioEncodingBitRate(128000);
        myAudioRecorder.setAudioSamplingRate(96000);
        myAudioRecorder.setOutputFile(getFilesDir()+"/audio.aac");

    }

    public void startChronometer(View v){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            if(!running && !recorded){
                chrono.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                chrono.start();
                running = true;
                botonCentral.setImageResource(R.drawable.stop_icon);
                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                } catch (IllegalStateException ise) {

                } catch (IOException ioe) {

                }
            }
            else if (running && !recorded){
                running = false;
                recorded = true;
                chrono.stop();
                pauseOffset = SystemClock.elapsedRealtime() - chrono.getBase();
                botonIzquierdo.setVisibility(View.VISIBLE);
                botonDerecho.setVisibility(View.VISIBLE);
                botonCentral.setImageResource(R.drawable.play_icon);
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;
            }
            else if (!running && recorded){
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(getFilesDir()+"/audio.aac");
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {

                }
            }
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_RECORD_AUDIO);
        }
    }

    public void deleteAudio(View v){

        new AlertDialog.Builder(this)
                .setTitle("Confirmar acción")
                .setMessage("¿Seguro que quiere borrar el audio?")
                .setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        chrono.setBase(SystemClock.elapsedRealtime());
                        pauseOffset=0;
                        running = false;
                        botonIzquierdo.setVisibility(View.INVISIBLE);
                        botonDerecho.setVisibility(View.INVISIBLE);
                        botonCentral.setImageResource(R.drawable.mic_icon);
                        deleteFile("audio.aac");
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setIcon(R.drawable.warning_icon)
                .show();
    }
}
