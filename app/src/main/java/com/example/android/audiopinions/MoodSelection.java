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
        sendAlert("Happy");
    }

    public void neutralButton(View view){
        sendAlert("Neutral");
    }

    public void angryButton(View view){
        sendAlert("Angry");
    }

    private void sendAlert(final String type){
        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle(type);
        confirm.setPositiveButton(getString(R.string.send), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendAudio(type);
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
        File definitive = new File(getFilesDir(),type + "_" + currentDateandTime + ".aac");
        existing.renameTo(definitive);
        sendAndClose();
    }

    private void sendAndClose(){
        setResult(RESULT_OK);
        finish();
    }
}
