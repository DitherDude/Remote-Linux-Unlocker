package com.maxchehab.remotelinuxunlocker;

import static com.maxchehab.remotelinuxunlocker.ComputerListActivity.getRandomNumber;

import java.math.BigInteger;
import java.util.Objects;

public class KeyPair {
    final String ip;
    final String key;
    String user;

    boolean unlock = false;

    KeyPair(String ip) {
        this.ip = ip;
        this.key = new BigInteger(getRandomNumber(64)).toString();
        this.user = "";
        this.unlock = false;
    }

    public boolean containsIp(String ip) {
        return Objects.equals(this.ip, ip);
    }
    public boolean containsUser(String user) {
        return Objects.equals(this.user, user);
    }
}

