package com.maxchehab.remotelinuxunlocker;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DeleteLayout extends CardView {

    public DeleteLayout(Context context, String ip, String key) {
        super(context);
        init(context, ip, key);
    }

    private TextView hostname2;
    private TextView hostip;
    private Button deleteComputer;
    private void init(Context context, final String ip, final String key) {
        inflate(getContext(), R.layout.delete_layout, this);
        hostname2 = (TextView) findViewById(R.id.hostname2);
        hostip = (TextView) findViewById(R.id.hostip);
        deleteComputer = (Button) findViewById(R.id.deleteComputer);
        deleteComputer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteIp(context, ip);
                setVisibility(View.GONE);
            }
        });
        hostip.setText(ip);
        hostname2.setText("?????");
        status(ip, key);
    }

    void deleteIp(Context context, String ip) {
        KeyPairList keys = new KeyPairList(context);
        keys.removeKey(ip);
        keys.commitKeys(context);
    }

    private void status(String ip, String key) {
        String echoResponse = null;
        try {
            echoResponse = new Client(ip,61599,"{\"command\":\"status\",\"key\":\"" +  key + "\"}").execute().get(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        Log.d("status-response","Response: " + echoResponse);

        if(echoResponse != null) {
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(echoResponse);
            JsonObject rootobj = root.getAsJsonObject();
            Log.d("hostname",rootobj.get("hostname").getAsString());
            hostname2.setText(rootobj.get("hostname").getAsString());
        }
    }

}
