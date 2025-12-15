package com.maxchehab.remotelinuxunlocker;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class Settings {
    boolean unlockHook = false;
    public Settings() {}

    /** @noinspection CallToPrintStackTrace*/
    public Settings(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("data", MODE_PRIVATE);
        if (!sharedPref.contains("settings")) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("settings", "");
            editor.apply();
        }
        String data = sharedPref.getString("settings", null);
        try {
            Settings settings = new Gson().fromJson(data, Settings.class);
            if (settings == null) {
                return;
            }
            this.unlockHook = settings.unlockHook;
        } catch (Exception e) {
            e.printStackTrace();
            Settings settings = new Settings();
            settings.commitSettings(context);
        }
    }
    @SuppressLint("ApplySharedPref")
    public void commitSettings(Context context) {
        String serialized = new Gson().toJson(this);
        SharedPreferences sharedPref = context.getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("settings", serialized);
        editor.commit();
    }
}
