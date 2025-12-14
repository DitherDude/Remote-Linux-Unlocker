package com.maxchehab.remotelinuxunlocker;

public class ClientBuilder {

    private String host;
    private int port;
    private String message;

    public ClientBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public ClientBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public ClientBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public Client createClient() {
        return new Client(host, port, message);
    }
}
