package com.maxchehab.remotelinuxunlocker;

import static android.content.Context.MODE_PRIVATE;

import static com.maxchehab.remotelinuxunlocker.ComputerListActivity.getRandomNumber;

import android.content.Context;
import android.content.SharedPreferences;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

public class KeyPair {
    String ip;
    String key;

//    KeyPair(String ip, String key) {
//        this.ip = ip;
//        this.key = key;
//    }
//    public KeyPair createKey(String ip) {
//        String key = new BigInteger(getRandomNumber(64)).toString();
//        return new KeyPair(ip, key);
//    }
    KeyPair(String ip) {
        this.ip = ip;
        this.key = new BigInteger(getRandomNumber(64)).toString();
    }

    KeyPair(String ip, String key) {
        this.ip = ip;
        this.key = key;
    }
    public boolean containsIp(String ip) {
        return Objects.equals(this.ip, ip);
    }
}

