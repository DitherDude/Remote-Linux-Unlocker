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

public class ManageLayout extends CardView {

    public ManageLayout(Context context) {
        super(context);
    }

    public ManageLayout(Context context, String ip, String key) {
        super(context);
        init(context, ip, key);
    }

    private TextView hostname2;

    private void init(Context context, final String ip, final String key) {
        inflate(getContext(), R.layout.manage_layout, this);
        hostname2 = findViewById(R.id.hostname2);
        TextView hostip = findViewById(R.id.hostip);
        Button deleteComputer = findViewById(R.id.deleteComputer);
        deleteComputer.setOnClickListener(view -> {
            deleteIp(context, ip);
            setVisibility(View.GONE);
        });
        hostip.setText(ip);
        hostname2.setText("?????");
        status(ip, key);
    }

    void deleteIp(Context context, String ip) {
        KeyPairList keys = new KeyPairList(context);
        if (!keys.removeKey(ip)) {
            System.out.println("Key not found.");
        }
        keys.commitKeys(context);
    }
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    /** @noinspection CallToPrintStackTrace*/
    private void status(String ip, String key) {
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

        if(echoResponse != null) {
            JsonObject rootObj = JsonParser.parseString(echoResponse).getAsJsonObject();
            Log.d("hostname", rootObj.get("hostname").getAsString());
            hostname2.setText(rootObj.get("hostname").getAsString());
        }
    }

}
