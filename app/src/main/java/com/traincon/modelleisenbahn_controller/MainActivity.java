package com.traincon.modelleisenbahn_controller;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.view.View;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextInputEditText ipEntry = findViewById(R.id.ipEntry);
        final TextInputEditText portEntry = findViewById(R.id.portEntry);
        final TextInputEditText devIdEntry = findViewById(R.id.devIdEntry);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String host = Objects.requireNonNull(ipEntry.getText()).toString();
                int port = Integer.parseInt(Objects.requireNonNull(portEntry.getText()).toString());
                String devId = Objects.requireNonNull(devIdEntry.getText()).toString();
                if(devId.equals("")){devId="2";}

                if(devId.equals("1") || devId.equals("2") || devId.equals("3") || devId.equals("4")) {
                    Intent intent = new Intent(getBaseContext(), FullscreenActivity.class);
                    intent.putExtra("host", host);
                    intent.putExtra("port", port);
                    intent.putExtra("deviceId", devId);
                    startActivity(intent);
                }

                else{
                    Snackbar.make(view, "Gerätenummer muss eine ganze Zahl zwischen 1 und 4 sein!", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });



    }

}
