package com.maxchehab.remotelinuxunlocker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Settings settings = new Settings(this);
        Switch switchUnlockHook = findViewById(R.id.switchUnlockHook);
        if (settings.unlockHook) {
            switchUnlockHook.setChecked(true);
        }

        Button buttonCommit = findViewById(R.id.buttonCommit);
        buttonCommit.setOnClickListener(view -> {
            setVisible(false);
            settings.unlockHook = switchUnlockHook.isChecked();
            settings.commitSettings(this);
            Intent mainActivity = new Intent(this, ComputerListActivity.class);
            int pendingId = 0;
            PendingIntent pendingIntent = PendingIntent.getActivity(this, pendingId, mainActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            manager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
            android.os.Process.killProcess(android.os.Process.myPid());
        });
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(view -> finish());
    }
}
