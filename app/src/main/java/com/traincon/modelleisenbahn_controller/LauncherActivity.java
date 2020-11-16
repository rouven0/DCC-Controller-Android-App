package com.traincon.modelleisenbahn_controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class LauncherActivity extends AppCompatActivity {
    private EditText ipEntry;
    private EditText portEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        getSupportActionBar();

        //Init Entries
        ipEntry = findViewById(R.id.ipEntry);
        portEntry = findViewById(R.id.portEntry);

        //Load last values
        Button loadLast = findViewById(R.id.loadLast);
        loadLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadLastConnectedBoard(ipEntry, portEntry);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //Daten auslesen
                    String host = Objects.requireNonNull(ipEntry.getText()).toString();
                    int port = Integer.parseInt(Objects.requireNonNull(portEntry.getText()).toString());

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("host", host);
                    intent.putExtra("port", port);
                    saveLastConnectedBoard(host, port);
                    startActivity(intent);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    portEntry.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_shake));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
        } else if (itemId == R.id.action_console) {
            try {
                String host = Objects.requireNonNull(ipEntry.getText()).toString();
                int port = Integer.parseInt(Objects.requireNonNull(portEntry.getText()).toString());
                Intent intent = new Intent(getBaseContext(), ConsoleActivity.class);
                intent.putExtra("host", host);
                intent.putExtra("port", port);
                saveLastConnectedBoard(host, port);
                startActivity(intent);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                portEntry.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_shake));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveLastConnectedBoard(String host, int port) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("lastConnectedBoard", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastConnectedHost", host);
        editor.putString("lastUsedPort", Integer.toString(port));
        editor.apply();
    }

    private void loadLastConnectedBoard(EditText ipEntry, EditText portEntry) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("lastConnectedBoard", MODE_PRIVATE);
        ipEntry.setText(sharedPreferences.getString("lastConnectedHost", null));
        portEntry.setText(sharedPreferences.getString("lastUsedPort", null));
    }
}