package com.maxchehab.remotelinuxunlocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ComputerListActivity extends AppCompatActivity {

    private static final Random rnd = new Random();
    private SwipeRefreshLayout swipeContainer;
    boolean commanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer_list);

        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        registerReceiver(unlockReceiver, filter);

        Button buttonPair = findViewById(R.id.buttonPair);
        buttonPair.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), PairingActivity.class);
            startActivity(intent);
        });

        Button buttonManage = findViewById(R.id.buttonManage);
        buttonManage.setOnClickListener(view -> {
            if (buttonManage.getText().toString().equals("Back")) {
                buttonManage.setText(R.string.manage_devices);
                refreshComputerList();
            } else {
                buttonManage.setText(R.string.back);
                deleteComputerList();
            }
        });


        swipeContainer = findViewById(R.id.refresh);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(this::refreshComputerList);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.defaultColorAccent);

        refreshComputerList();
    }

    @Override
    protected void onResume() {
        if (getIntent().getAction() != null) {
            Log.e("INTENT-ACTION:", getIntent().getAction());
        }
        refreshComputerList();

        super.onResume();
    }

    public void refreshComputerList(){
        TextView infoBar = findViewById(R.id.infoBar);
        ArrayList<View> computerList = new ArrayList<>();
        KeyPairList keys = new KeyPairList(this);
        if (keys.isEmpty()) {
            infoBar.setVisibility(View.VISIBLE);
        } else {
            infoBar.setVisibility(View.GONE);
        }
        for (KeyPair k: keys) {
            if (!commanded && getIntent().hasExtra("command")) {
                computerList.add(new ComputerLayout(this, k, getIntent().getStringExtra("command")));
                commanded = true;
            } else {
                computerList.add(new ComputerLayout(this, k));
            }
        }

        LinearLayout feedLayout = findViewById(R.id.CardHolder);
        feedLayout.removeAllViews();


        for(int i = 0; i < computerList.size(); i++){
            feedLayout.addView(computerList.get(i));
        }
        swipeContainer.setRefreshing(false);
    }

    public void deleteComputerList() {
        TextView infoBar = findViewById(R.id.infoBar);
        ArrayList<View> computerList = new ArrayList<>();
        KeyPairList keys = new KeyPairList(this);
        if (keys.isEmpty()) {
            infoBar.setVisibility(View.VISIBLE);
        } else {
            infoBar.setVisibility(View.GONE);
        }
        for (KeyPair k: keys) {
            computerList.add(new ManageLayout(this, k));
            if (!commanded && getIntent().hasExtra("command")) {
                commanded = true;
            }
        }

        LinearLayout feedLayout = findViewById(R.id.CardHolder);
        feedLayout.removeAllViews();


        for(int i = 0; i < computerList.size(); i++){
            feedLayout.addView(computerList.get(i));
        }
        swipeContainer.setRefreshing(false);
    }

    public static String getRandomNumber(int digCount) {
        StringBuilder sb = new StringBuilder(digCount);
        for(int i=0; i < digCount; i++)
            sb.append((char)('0' + rnd.nextInt(10)));
        return sb.toString();
    }
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final BroadcastReceiver unlockReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
//                Log.d("UnlockReceiver", "Device has been unlocked by the user!");
//                Toast.makeText(context, "Hello!", Toast.LENGTH_SHORT).show();
                KeyPairList keys = new KeyPairList(context);
                for (KeyPair k: keys) {
                    if (k.unlock) {
                        try {
                            executor.submit(new ClientBuilder().setHost(k.ip).setPort(61599).setMessage("{\"command\":\"unlock\",\"key\":\"" + k.key + "\"}").createClient()).get(2, TimeUnit.SECONDS);
                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

}
