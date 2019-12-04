package com.example.android.audiopinions;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MoodSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_selection);
    }

    public void happyButton(View view){
        sendAlert("happy");
    }

    public void neutralButton(View view){
        sendAlert("neutral");
    }

    public void angryButton(View view){
        sendAlert("angry");
    }

    private void sendAlert(final String type){
        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        switch(type){
            case "happy":
                confirm.setTitle(getString(R.string.happy));
                break;
            case "neutral":
                confirm.setTitle(getString(R.string.neutral));
                break;
            case "angry":
                confirm.setTitle(getString(R.string.angry));
                break;
        }
        confirm.setPositiveButton(getString(R.string.send), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String audioType = type;
                sendAudio(audioType);
            }
        });
        confirm.setMessage(getString(R.string.send_recording));
        confirm.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) { }
        });
        confirm.show();
    }

    private void sendAudio(String type){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        File existing = new File(getFilesDir(),"audio.aac");
        File definitive = null;
        switch(type){
            case "happy":
                definitive = new File(getFilesDir(),"happy_" + currentDateandTime + ".aac");
                break;
            case "neutral":
                definitive = new File(getFilesDir(),"neutral_" + currentDateandTime + ".aac");
                break;
            case "angry":
                definitive = new File(getFilesDir(),"angry_" + currentDateandTime + ".aac");
                break;
        }
        existing.renameTo(definitive);
        sendAndClose();
    }

    private void sendAndClose(){
        setResult(RESULT_OK);
        finish();
    }
}
