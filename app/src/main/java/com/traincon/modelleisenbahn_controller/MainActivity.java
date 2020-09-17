package com.traincon.modelleisenbahn_controller;

import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Eingabefelder einrichten
        final TextInputEditText ipEntry = findViewById(R.id.ipEntry);
        final TextInputEditText portEntry = findViewById(R.id.portEntry);
        final TextInputEditText devIdEntry = findViewById(R.id.devIdEntry);
        //Letzten stand laden
        Button loadLast = findViewById(R.id.loadLast);
        loadLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadLastConectedBoard(ipEntry, portEntry);
            }
        });
        //Startbutton
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //Daten auslesen
                    String host = Objects.requireNonNull(ipEntry.getText()).toString();
                    int port = Integer.parseInt(Objects.requireNonNull(portEntry.getText()).toString());
                    String devId = Objects.requireNonNull(devIdEntry.getText()).toString();
                    if (devId.equals("")) {
                        devId = "2";
                    }
                    //Bei richtiger eingabe das Hauptprogramm starten
                    if (devId.equals("1") || devId.equals("2") || devId.equals("3") || devId.equals("4")) {
                        Intent intent = new Intent(getBaseContext(), FullscreenActivity.class);
                        intent.putExtra("host", host);
                        intent.putExtra("port", port);
                        intent.putExtra("deviceId", devId);
                        saveLastConnectedBoard(host, port);
                        startActivity(intent);
                    } else {
                        Snackbar.make(view, "Gerätenummer muss eine ganze Zahl zwischen 1 und 4 sein!", Snackbar.LENGTH_INDEFINITE)
                                .show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Snackbar.make(view, "Fehler bei der Eingabe", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void saveLastConnectedBoard(String host, int port) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("lastConnectedBoard", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastConnectedHost", host);
        editor.putString("lastUsedPort", Integer.toString(port));
        editor.apply();
    }

    private void loadLastConectedBoard(TextInputEditText ipEntry, TextInputEditText portEntry) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("lastConnectedBoard", MODE_PRIVATE);
        ipEntry.setText(sharedPreferences.getString("lastConnectedHost", null));
        portEntry.setText(sharedPreferences.getString("lastUsedPort", null));
    }
}