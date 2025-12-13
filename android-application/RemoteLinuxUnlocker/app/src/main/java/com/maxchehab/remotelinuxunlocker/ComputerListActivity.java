package com.maxchehab.remotelinuxunlocker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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



        SharedPreferences sharedPref = getSharedPreferences("data", MODE_PRIVATE);
        if(!sharedPref.contains("key")){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("key", new BigInteger(getRandomNumber(64)).toString());
            editor.commit();
        }
        if(!sharedPref.contains("ips")){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("ips", "");
            editor.commit();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PairingActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton deleteFab = (FloatingActionButton) findViewById(R.id.deleteFab);
        deleteFab.setOnClickListener(new View.OnClickListener() {
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
        SharedPreferences sharedPref = getSharedPreferences("data", MODE_PRIVATE);
        String key = sharedPref.getString("key",null);
        Log.d("ips",sharedPref.getString("ips",null));
        String ipString = sharedPref.getString("ips",null);
        List<String> ips = new ArrayList<String>();
        if(!ipString.isEmpty()){
            ips = Arrays.asList(sharedPref.getString("ips",null).split(","));
            ips = new ArrayList<String>(ips);
        }
        ArrayList<View> computerList = new ArrayList<View>();
        if (ips.isEmpty()) {
            infoBar.setVisibility(View.VISIBLE);
        } else {
            infoBar.setVisibility(View.GONE);
        }
        for(int i = 0 ; i < ips.size(); i++){
            if(!ips.get(i).isEmpty()){
                Log.d("creating-ip",ips.get(i));

                if(!commanded && getIntent().hasExtra("command")){

                    computerList.add(new ComputerLayout(this,ips.get(i),key,getIntent().getStringExtra("command")));
                    commanded = true;
                }else{
                    computerList.add(new ComputerLayout(this,ips.get(i),key));
                }

            }
        }

        LinearLayout feedLayout = (LinearLayout) findViewById(R.id.delete);
        feedLayout.removeAllViews();


        for(int i = 0; i < computerList.size(); i++){
            feedLayout.addView(computerList.get(i));
        }
        swipeContainer.setRefreshing(false);
    }

    public void deleteComputerList() {
        TextView infoBar = (TextView) findViewById(R.id.infoBar);
        SharedPreferences sharedPref = getSharedPreferences("data", MODE_PRIVATE);
        String key = sharedPref.getString("key",null);
        // Log.d("ips",sharedPref.getString("ips",null));
        String ipString = sharedPref.getString("ips",null);
        List<String> ips = new ArrayList<String>();
        if(!ipString.isEmpty()){
            ips = Arrays.asList(sharedPref.getString("ips",null).split(","));
            ips = new ArrayList<String>(ips);
        }
        ArrayList<View> computerList = new ArrayList<View>();
        if (ips.isEmpty()) {
            infoBar.setVisibility(View.VISIBLE);
        } else {
            infoBar.setVisibility(View.GONE);
        }
        for(int i = 0 ; i < ips.size(); i++){
            if(!ips.get(i).isEmpty()){
                // Log.d("creating-ip",ips.get(i));

                if(!commanded && getIntent().hasExtra("command")){

                    computerList.add(new DeleteLayout(this,ips.get(i),key));
                    commanded = true;
                }else{
                    computerList.add(new DeleteLayout(this,ips.get(i),key));
                }

            }
        }

        LinearLayout feedLayout = (LinearLayout) findViewById(R.id.delete);
        feedLayout.removeAllViews();


        for(int i = 0; i < computerList.size(); i++){
            feedLayout.addView(computerList.get(i));
        }
        swipeContainer.setRefreshing(false);
    }

    public void DeleteMenu(android.view.View view){

    }

    public static String getRandomNumber(int digCount) {
        StringBuilder sb = new StringBuilder(digCount);
        for(int i=0; i < digCount; i++)
            sb.append((char)('0' + rnd.nextInt(10)));
        return sb.toString();
    }

}
