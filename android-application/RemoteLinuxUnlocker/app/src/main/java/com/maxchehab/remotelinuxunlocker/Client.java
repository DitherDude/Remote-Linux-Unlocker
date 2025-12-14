package com.maxchehab.remotelinuxunlocker;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

public class Client implements Callable<String> {
    final String host;
    final int port;
    final String message;

    Client(String host, int port,String message) {
        this.host = host;
        this.port = port;
        this.message = message;
    }

    @Override
    public String call() {
        Socket socket = null;
        try {
            InetAddress address = InetAddress.getByName(host);
            socket = new Socket(address, port);
            Log.d("async-client","connected to: " + host + ":" + port);
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);

            bw.write(message);
            bw.flush();

            Log.d("async-client","sent message: " + message);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();
            Log.d("async-client","received message: " + response);
            return response;
        } catch (IOException exception) {
            Log.d("async-client", "the server is offline?");
        }finally {
            Log.d("async-client","success");
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}