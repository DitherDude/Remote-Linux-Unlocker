package com.maxchehab.remotelinuxunlocker;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Max on 5/10/2017.
 */

public class ComputerLayout extends CardView{


    public ComputerLayout(Context context) {
        super(context);
    }

    public ComputerLayout(Context context, String ip, String key) {
        super(context);
        init(ip,key,null);
    }

    public ComputerLayout(Context context, String ip, String key, String command) {
        super(context);
        init(ip,key, command);
    }

    private TextView hostname;
    private Button lockButton;

    private boolean locked = false;

    private void init(final String ip, final String key, String command) {

        inflate(getContext(), R.layout.computer_layout, this);
        hostname = findViewById(R.id.hostname);
        lockButton = findViewById(R.id.lockButton);

        status(ip, key);

        lockButton.setOnClickListener(v -> {
            lockButton.setEnabled(false);
            lockButton.setClickable(false);
            if(locked){
                lock(ip,key,"unlock");
            }else{
                lock(ip, key,"lock");
            }
        });

        if(command != null){
            lock(ip,key,command);
        }
    }
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    /** @noinspection CallToPrintStackTrace*/
    private void lock(String ip, String key, String action){
        try {
            executor.submit(new ClientBuilder().setHost(ip).setPort(61599).setMessage("{\"command\":\"" + action + "\",\"key\":\"" + key + "\"}").createClient()).get(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        boolean tempLocked = locked;
        while(tempLocked == locked){
            status(ip,key);
        }
        lockButton.setClickable(true);
        lockButton.setEnabled(true);
    }


    /** @noinspection CallToPrintStackTrace*/
    private void status(String ip, String key){
        String echoResponse = null;
        try {
            echoResponse = executor.submit(new ClientBuilder().setHost(ip).setPort(61599).setMessage("{\"command\":\"status\",\"key\":\"" + key + "\"}").createClient()).get(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        Log.d("status-response","Response: " + echoResponse);

        if(echoResponse == null){
            this.setVisibility(View.GONE);
        }else{
            this.setVisibility(View.VISIBLE);
            JsonObject rootObj = JsonParser.parseString(echoResponse).getAsJsonObject();
            Log.d("hostname",rootObj.get("hostname").getAsString());
            hostname.setText(rootObj.get("hostname").getAsString());
            locked = rootObj.get("isLocked").getAsBoolean();

            if(locked){
                lockButton.setText(R.string.unlock);
            }else{
                lockButton.setText(R.string.lock);
            }
        }
    }

}
