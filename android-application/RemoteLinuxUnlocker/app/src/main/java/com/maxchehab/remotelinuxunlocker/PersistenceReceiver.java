package com.maxchehab.remotelinuxunlocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PersistenceReceiver extends BroadcastReceiver
{
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    @Override
    public void onReceive(Context context, Intent intent){
        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            KeyPairList keys = new KeyPairList(context);
            for (KeyPair k: keys) {
                if (k.unlock) {
                    try {
                        executor.submit(new ClientBuilder().setHost(k.ip).setPort(61599).setMessage("{\"command\":\"unlock\",\"key\":\"" + k.key + "\"}").createClient()).get(2, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
