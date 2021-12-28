package com.traincon.modelleisenbahn_controller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.tabs.TabLayout;
import com.traincon.CBusMessage.CBusMessage;
import com.traincon.modelleisenbahn_controller.BoardManager;
import com.traincon.modelleisenbahn_controller.Cab;
import com.traincon.modelleisenbahn_controller.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FragmentContainerView[] controllerContainers;
    private Fragment[] controllers;
    private Handler handler;
    private Runnable cbusUpdateRunnable;

    /**
     * @see BoardManager
     */
    private BoardManager boardManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String host = intent.getStringExtra("host");
        int port = intent.getIntExtra("port", 0);

        boardManager = new BoardManager(host, port);
        boardManager.connect();
        boardManager.onStartFrameListener();

        handler = boardManager.getHandler();
        initLayout();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Loco
        if (item.getItemId() == R.id.action_locoList) {
            startActivity(new Intent(getBaseContext(), LocoConfigActivity.class));
        }

        //Reset
        if(item.getItemId() == R.id.action_reset){
            Cab.reset(boardManager);
        }

        //Reconnect
        if (item.getItemId() == R.id.action_reconnect) {
            boardManager.disconnect();
            boardManager.connect();
        }

        if (item.getItemId() == R.id.action_estop) {
            Cab.estop(boardManager);
        }

        if (item.getItemId() == R.id.action_exit) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        for (Fragment fragment : controllers){
            ((ControllerFragment) fragment).getCab().releaseSession();
        }
        //Wait until all sessions are released
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handler.removeCallbacks(cbusUpdateRunnable);
        boardManager.onStopFrameListener();
        boardManager.disconnect();
        super.onDestroy();
    }

    private void initLayout() {
        String[] controllerTags = new String[]{"c1", "c2", "c3"};
        controllers = new Fragment[controllerTags.length];

        int[] containerTags = new int[]{R.id.con1, R.id.con2, R.id.con3};
        controllerContainers = new FragmentContainerView[containerTags.length];

        for (int i = 0; i < controllers.length; i++) {
            controllers[i] = getSupportFragmentManager().findFragmentByTag(controllerTags[i]);
            Bundle bundle = new Bundle();
            bundle.putParcelable("boardManager", boardManager);
            assert controllers[i] != null;
            controllers[i].setArguments(bundle);

            controllerContainers[i] = findViewById(containerTags[i]);
        }
        initTabs();
        initUpdates();
    }

    /**
     * This shows a TabLayout in portrait mode to choose a controller
     */
    private void initTabs(){
        TabLayout cabSelection = findViewById(R.id.cabSelection);
        cabSelection.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showCab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void showCab(int cab){
        for(int i = 0; i< controllerContainers.length; i++){
            if(i==cab){
                controllerContainers[i].setVisibility(View.VISIBLE);
                controllerContainers[i].startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.nav_default_enter_anim));
            } else {
                controllerContainers[i].setVisibility(View.GONE);
            }
        }
    }

    private void initUpdates() {
        cbusUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                processCbusMessages();
                handler.postDelayed(this, 200);
            }
        };
        handler.post(cbusUpdateRunnable);
    }

    /**
     * All received messages will be processed here
     * @see CBusMessage
     */
    private void processCbusMessages(){
        List<CBusMessage> receivedMessages = boardManager.getReceivedMessages();
        for (CBusMessage cbusMessage : receivedMessages) {
            switch (cbusMessage.getEvent()) {
                case "ESTOP":
                    for (Fragment fragment : controllers) {
                        ((ControllerFragment) fragment).displayEstop();
                    }
                    break;
                case "PLOC":
                    for (Fragment fragment : controllers) {
                        ((ControllerFragment) fragment).onSessionAllocated(cbusMessage);
                    }
                    break;

                case "ERR":
                    if(cbusMessage.getData()[2].equals("08")){
                        for(Fragment fragment :  controllers){
                            if(((ControllerFragment) fragment).onSessionCancelled(cbusMessage)){
                                break;
                            }
                        }
                    }
                    break;
            }
        }
        receivedMessages.clear();
    }
}