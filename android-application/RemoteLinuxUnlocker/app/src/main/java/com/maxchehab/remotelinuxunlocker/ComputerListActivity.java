package com.maxchehab.remotelinuxunlocker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ComputerListActivity extends AppCompatActivity {

    private static Random rnd = new Random();
    private SwipeRefreshLayout swipeContainer;
    boolean commanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer_list);

        Button buttonPair = (Button) findViewById(R.id.buttonPair);
        buttonPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PairingActivity.class);
                startActivity(intent);
            }
        });

        Button buttonManage = (Button) findViewById(R.id.buttonManage);
        buttonManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteComputerList();
            }
        });


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.refresh);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshComputerList();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorAccent);

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
        TextView infoBar = (TextView) findViewById(R.id.infoBar);
        ArrayList<View> computerList = new ArrayList<View>();
        KeyPairList keys = new KeyPairList(this);
        if (keys.isEmpty()) {
            infoBar.setVisibility(View.VISIBLE);
        } else {
            infoBar.setVisibility(View.GONE);
        }
        for (KeyPair k: keys) {
            if (!commanded && getIntent().hasExtra("command")) {
                computerList.add(new ComputerLayout(this, k.ip, k.key, getIntent().getStringExtra("command")));
                commanded = true;
            } else {
                computerList.add(new ComputerLayout(this, k.ip, k.key));
            }
        }

        LinearLayout feedLayout = (LinearLayout) findViewById(R.id.CardHolder);
        feedLayout.removeAllViews();


        for(int i = 0; i < computerList.size(); i++){
            feedLayout.addView(computerList.get(i));
        }
        swipeContainer.setRefreshing(false);
    }

    public void deleteComputerList() {
        TextView infoBar = (TextView) findViewById(R.id.infoBar);
        ArrayList<View> computerList = new ArrayList<View>();
        KeyPairList keys = new KeyPairList(this);
        if (keys.isEmpty()) {
            infoBar.setVisibility(View.VISIBLE);
        } else {
            infoBar.setVisibility(View.GONE);
        }
        for (KeyPair k: keys) {
            computerList.add(new DeleteLayout(this, k.ip, k.key));
            if (!commanded && getIntent().hasExtra("command")) {
                commanded = true;
            }
        }

        LinearLayout feedLayout = (LinearLayout) findViewById(R.id.CardHolder);
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

}
