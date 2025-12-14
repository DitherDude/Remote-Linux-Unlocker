package com.maxchehab.remotelinuxunlocker;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class KeyPairList extends ArrayList<KeyPair> {
    public boolean containsIp(String ip) {
        for (KeyPair k: this) {
            if (k.containsIp(ip)) {
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
        ArrayList<String> splits = new ArrayList<String>();
        for (KeyPair k : this) {
            splits.add(k.ip + " " + k.key);
        }
        String collected = splits.stream().collect(Collectors.joining(","));
        SharedPreferences sharedPref = context.getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("keysets", collected);
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
        if (data == null || data.isEmpty()) {
            return;
        }
        String[] splitData = data.split(",");
        for (String split: splitData) {
            String[] reSplit = split.split(" ");
            this.add(new KeyPair(reSplit[0], reSplit[1]));
        }
    }
}
