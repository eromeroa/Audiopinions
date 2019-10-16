package com.example.android.audiopinions;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class MainActivity extends AppCompatActivity {
    private Chronometer chrono;
    private long pauseOffset;
    private boolean running;
    private Button botonIzquierdo;
    private Button botonDerecho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chrono = findViewById(R.id.chronometer);
        botonDerecho = findViewById(R.id.botonSend);
        botonIzquierdo = findViewById(R.id.botonDelete);
    }

    public void startChronometer(View v){
        if(!running){
            chrono.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chrono.start();
            running = true;
        }
        else{
            chrono.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chrono.getBase();
            botonIzquierdo.setVisibility(View.VISIBLE);
            botonDerecho.setVisibility(View.VISIBLE);
        }
    }

    public void deleteAudio(View v){
        chrono.setBase(SystemClock.elapsedRealtime());
        pauseOffset=0;
        running = false;
        botonIzquierdo.setVisibility(View.INVISIBLE);
        botonDerecho.setVisibility(View.INVISIBLE);
    }
}
