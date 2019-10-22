package com.example.android.audiopinions;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Chronometer chrono;
    private long pauseOffset;
    private boolean recording;
    private boolean recorded;
    private boolean playing;
    private ImageButton centralButton;
    private Button leftButton;
    private Button rightButton;

    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    private MediaRecorder audioRecorder = new MediaRecorder();
    MediaPlayer mediaPlayer = new MediaPlayer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();
        if(!checkRecordingPermission()) requestRecordingPermission();
        else{
            setupAudioRecorder();
            setupAudioPlayer();
        }
    }

    public void centralButton(View v){
        if(!recording && !recorded){
            startChrono();
            startRecording();
        }
        else if (recording && !recorded){
            stopChrono();
            stopRecording();
        }
        else if (!recording && recorded){
            if (!playing) playAudio();
            else stopAudio();
        }
    }

    private void startChrono(){
        chrono.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        chrono.start();
    }

    private void stopChrono(){
        chrono.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chrono.getBase();
    }

    private void resetChrono(){
        chrono.setBase(SystemClock.elapsedRealtime());
        pauseOffset=0;
    }

    private void startRecording(){
        recording = true;
        try {
            audioRecorder.prepare();
            audioRecorder.start();
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        changeCentralButtonIcon("stop");
    }

    private void stopRecording(){
        recording = false;
        recorded = true;
        audioRecorder.stop();
        audioRecorder.reset();
        setupAudioRecorder();
        changeCentralButtonIcon("play");
        activateLateralButtons();
    }


    private void playAudio(){
        try {
            mediaPlayer.setDataSource(getFilesDir()+"/audio.aac");
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        resetChrono();
        startChrono();
        playing = true;
        changeCentralButtonIcon("stop");
        deactivateLateralButtons();
    }

    private void stopAudio(){
        mediaPlayer.stop();
        mediaPlayer.reset();
        chrono.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chrono.getBase();
        playing = false;
        changeCentralButtonIcon("play");
        activateLateralButtons();
    }

    private void changeCentralButtonIcon(String c){
        switch (c){
            case "play": centralButton.setImageResource(R.drawable.play_icon);
                break;
            case "stop": centralButton.setImageResource(R.drawable.stop_icon);
                break;
            case "mic": centralButton.setImageResource(R.drawable.mic_icon);
                break;
        }
    }

    private void activateLateralButtons(){
        leftButton.setVisibility(View.VISIBLE);
        rightButton.setVisibility(View.VISIBLE);
    }

    private void deactivateLateralButtons(){
        leftButton.setVisibility(View.INVISIBLE);
        rightButton.setVisibility(View.INVISIBLE);
    }

    private void setupAudioRecorder(){
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        audioRecorder.setAudioEncodingBitRate(128000);
        audioRecorder.setAudioSamplingRate(96000);
        audioRecorder.setOutputFile(getFilesDir()+"/audio.aac");
    }

    private void setupAudioPlayer(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });
    }

    private void setViews(){
        chrono = findViewById(R.id.chronometer);
        centralButton = findViewById(R.id.centralButton);
        rightButton = findViewById(R.id.rightButton);
        leftButton = findViewById(R.id.leftButton);
    }

    private boolean checkRecordingPermission(){
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestRecordingPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
    }

    public void leftButton(View v){
        new AlertDialog.Builder(this)
                .setMessage("¿Seguro que quiere eliminar la grabación?")
                .setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAudio();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }
                })
                .show();
    }

    private void deleteAudio(){
        resetChrono();
        deactivateLateralButtons();
        changeCentralButtonIcon("mic");
        recorded = false;
        deleteFile("audio.aac");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_RECORD_AUDIO){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupAudioRecorder();
                setupAudioPlayer();
            }
            else finish();
        }
    }
}

