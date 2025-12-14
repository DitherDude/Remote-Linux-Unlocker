package com.maxchehab.remotelinuxunlocker;

import static com.maxchehab.remotelinuxunlocker.ComputerListActivity.getRandomNumber;

import java.math.BigInteger;
import java.util.Objects;

public class KeyPair {
    final String ip;
    final String key;

    KeyPair(String ip) {
        this.ip = ip;
        this.key = new BigInteger(getRandomNumber(64)).toString();
    }

    public boolean containsIp(String ip) {
        return Objects.equals(this.ip, ip);
    }
}

