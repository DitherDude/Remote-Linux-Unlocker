package com.maxchehab.remotelinuxunlocker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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

public class ManageLayout extends CardView {

    public ManageLayout(Context context) {
        super(context);
    }

    public ManageLayout(Context context, KeyPair key) {
        super(context);
        init(context, key);
    }
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private TextView deetsBar;

    private void init(Context context, KeyPair key) {
        ExecutorService executor2 = Executors.newSingleThreadExecutor();
        inflate(getContext(), R.layout.manage_layout, this);
        deetsBar = findViewById(R.id.deetsBar);
        Button deleteComputer = findViewById(R.id.deleteComputer);
        deleteComputer.setOnClickListener(view -> {
            deleteIp(context, key.ip);
            setVisibility(View.GONE);
        });
        deetsBar.setText(String.format("%s@%s", key.user, key.ip));
        executor2.execute(() -> {
            String hostname = status(key);
            handler.post(() -> {
                if (hostname != null) {
                    deetsBar.setText(String.format("%s@%s (%s)", key.user, key.ip, hostname));
                }
            });
        });
    }

    void deleteIp(Context context, String ip) {
        KeyPairList keys = new KeyPairList(context);
        if (!keys.removeKey(ip)) {
            System.out.println("Key not found.");
        }
        keys.commitKeys(context);
    }
    /** @noinspection CallToPrintStackTrace*/
    private String status(KeyPair key) {
        String echoResponse = null;
        try {
            echoResponse = executor.submit(new ClientBuilder().setHost(key.ip).setPort(61599).setMessage("{\"command\":\"status\",\"key\":\"" + key.key + "\"}").createClient()).get(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        Log.d("status-response","Response: " + echoResponse);

        if(echoResponse != null) {
            JsonObject rootObj = JsonParser.parseString(echoResponse).getAsJsonObject();
            Log.d("hostname", rootObj.get("hostname").getAsString());
            return rootObj.get("hostname").getAsString();
        }
        return null;
    }

}
