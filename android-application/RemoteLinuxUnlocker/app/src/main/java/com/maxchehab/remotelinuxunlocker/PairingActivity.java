package com.maxchehab.remotelinuxunlocker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PairingActivity extends AppCompatActivity {

    private EditText ipInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);

        ipInput = findViewById(R.id.ipInput);
        Button pairButton = findViewById(R.id.pairButton);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = (source, start, end, dest, dstart, dend) -> {
            if (end > start) {
                String destTxt = dest.toString();
                String resultingTxt = destTxt.substring(0, dstart) +
                        source.subSequence(start, end) +
                        destTxt.substring(dend);
                if (!resultingTxt.matches ("^\\d{1,3}(\\." +
                        "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                    return "";
                } else {
                    String[] splits = resultingTxt.split("\\.");
                    for (String split : splits) {
                        if (Integer.parseInt(split) > 255) {
                            return "";
                        }
                    }
                }
            }
            return null;
        };
        ipInput.setFilters(filters);

        pairButton.setOnClickListener(view -> pair());
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(view -> finish());
    }
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    /** @noinspection CallToPrintStackTrace*/
    void pair(){
        KeyPair key = new KeyPair(ipInput.getText().toString());
        KeyPairList keys = new KeyPairList(this);

        try {
            String response = executor.submit(new ClientBuilder().setHost(key.ip).setPort(61598).setMessage("{\"command\":\"pair\",\"key\":\"" + key.key + "\"}").createClient()).get(1, TimeUnit.SECONDS);
            Log.d("UI RESPONSE", "response: " + response);
            if(response == null){
                pairFailed();
            }else{
                JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
                key.user = responseObject.get("user").getAsString();
                if (keys.containsKey(key)) {
                    ipInput.setError("IP address is already paired with this user.");
                    return;
                }
                keys.addKey(key);
                keys.commitKeys(this);
                pairSuccess();
            }
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            pairFailed();
            e.printStackTrace();
        }
    }

    void pairFailed(){
        ipInput.setError("IP address is invalid. Make sure your computer is in pairing mode.");
    }

    void pairSuccess(){
        ipInput.setError(null);
        finish();
    }

}
