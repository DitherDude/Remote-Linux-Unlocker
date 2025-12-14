package com.maxchehab.remotelinuxunlocker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
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
        init(context, ip,key,null);
    }

    public ComputerLayout(Context context, String ip, String key, String command) {
        super(context);
        init(context, ip ,key, command);
    }

    private TextView hostname;
    private Button lockButton;

    private boolean locked = false;

    private void init(Context context, final String ip, final String key, String command) {
        inflate(getContext(), R.layout.computer_layout, this);
        hostname = findViewById(R.id.hostname);
        lockButton = findViewById(R.id.lockButton);
        ExecutorService executor2 = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor2.execute(() -> {
            StatusResult result = status(ip, key);
            handler.post(() -> {
               applyResult(context, result);
            });
        });

        lockButton.setOnClickListener(v -> {
            lockButton.setEnabled(false);
            lockButton.setClickable(false);
            int color = ContextCompat.getColor(context, R.color.warningColorAccent);
            lockButton.setBackgroundTintList(ColorStateList.valueOf(color));
            lockButton.setText(R.string.loading_placeholder);
            invalidate();
            if(locked){
                lock(context, ip,key,"unlock");
            }else{
                lock(context, ip, key,"lock");
            }
        });

        if(command != null){
            lock(context, ip,key,command);
        }
    }
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    /** @noinspection CallToPrintStackTrace*/
    private void lock(Context context, String ip, String key, String action){
        try {
            executor.submit(new ClientBuilder().setHost(ip).setPort(61599).setMessage("{\"command\":\"" + action + "\",\"key\":\"" + key + "\"}").createClient()).get(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        ExecutorService executor2 = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor2.execute(() -> {
            boolean tempLocked = locked;
            while(tempLocked == locked){
                StatusResult result = status(ip, key);
                if (result != null && result.locked != locked) {
                    handler.post(() -> {
                        applyResult(context, result);
                        lockButton.setClickable(true);
                        lockButton.setEnabled(true);
                    });
                }
            }
        });
    }

    private record StatusResult(String hostname, boolean locked){}

    private void applyResult(Context context, StatusResult result) {
        locked = result.locked;
        hostname.setText(result.hostname);
        if(locked){
            int color = ContextCompat.getColor(context, R.color.dangerColorAccent);
            lockButton.setBackgroundTintList(ColorStateList.valueOf(color));
            lockButton.setText(R.string.unlock);
        } else {
            int color = ContextCompat.getColor(context, R.color.successColorAccent);
            lockButton.setBackgroundTintList(ColorStateList.valueOf(color));
            lockButton.setText(R.string.lock);
        }
    }


    /** @noinspection CallToPrintStackTrace*/
    private StatusResult status(String ip, String key){
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
            return new StatusResult(rootObj.get("hostname").getAsString(), rootObj.get("isLocked").getAsBoolean());
        }
        return null;
    }

}
