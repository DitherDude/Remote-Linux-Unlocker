package com.maxchehab.remotelinuxunlocker;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

public class KeyPairList extends ArrayList<KeyPair> {
    public boolean containsKey(KeyPair key) {
        for (KeyPair k: this) {
            if (k.containsIp(key.ip) && k.containsUser(key.user)) {
                return true;
            }
        }
        return false;
    }
    public void addKey(KeyPair key) {
        this.add(key);
    }
    public boolean removeKey(String ip) {
        for (KeyPair k : this) {
            if (Objects.equals(k.ip, ip)) {
                this.remove(k);
                return true;
            }
        }
        return false;
    }

    public void commitKeys(Context context) {
        String serialized = new Gson().toJson(this);
        SharedPreferences sharedPref = context.getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("keysets", serialized);
        editor.apply();
    }
    public KeyPairList(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("data", MODE_PRIVATE);
        if(!sharedPref.contains("keysets")){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("keysets", "");
            editor.apply();
        }
        String data = sharedPref.getString("keysets",null);
        try {
            KeyPairList list = new Gson().fromJson(data, KeyPairList.class);
            if (list == null || list.isEmpty()) {
                return;
            }
            this.addAll(list);
        } catch (Exception e) {
            //noinspection ThrowablePrintedToSystemOut
            System.out.println(e);
            KeyPairList empty = new KeyPairList();
            empty.commitKeys(context);
        }
    }

    public KeyPairList(){}
}
