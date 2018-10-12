package com.loudpacks.net.socket;

import com.loudpacks.net.Request;
import java.io.IOException;
import java.net.ServerSocket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server extends Thread {

    private final int port;
    private SocketClientHandler client;
    private ServerSocket listener;
    private int id = 0;
    private boolean running = false;

    public Server(int port) {
        this.port = port;
    }

    public boolean isRunning() {
        return running;
    }

    public void stopServer() {
        try {
            listener.close();
            if (client != null)
                client.disconnect();
        } catch (IOException e) {
            log.debug(String.format("Server Error - %s", e.getMessage()));
        }
    }

    public void sendRequestToClient(Request request) {
        if (client != null && client.isConnected()) {
            client.queueRequest(request);
        }
    }

    @Override
    public void run() {
        running = true;

        try {

            listener = new ServerSocket(port);

            while (running) {
                client = new SocketClientHandler(listener.accept(), id++);
            }

        } catch (IOException e) {

            log.debug(String.format("Server Error - %s", e.getMessage()));

        }

    }

}
