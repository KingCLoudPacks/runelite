package com.loudpacks.net.socket;

import com.loudpacks.net.Request;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketClientHandler extends Thread {

    private final Socket socket;
    private final int id;

    private LinkedBlockingQueue<Request> requestQueue = new LinkedBlockingQueue<Request>();

    protected SocketClientHandler(Socket socket, int id) {
        this.socket = socket;
        this.id = id;
        log.debug(String.format("New connection from %s:%d", socket.getInetAddress().toString(), socket.getPort()));
        start();
    }

    protected void disconnect() throws IOException {
        socket.close();
    }

    protected boolean isConnected() {
        return socket.isConnected();
    }

    protected void queueRequest(Request request) {
        try {
            requestQueue.put(request);
        } catch (InterruptedException e) {
            log.debug(String.format("SocketClientHandler Error - %s", e.getMessage()));
        }
    }

    @Override
    public void run() {
        try {

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            while (socket.isConnected()) {

                Request request = requestQueue.take();
                out.writeUTF(request.getJsonData());
                out.flush();

            }

            in.close();
            out.close();


        } catch (IOException | InterruptedException e) {
            log.debug(String.format("SocketClientHandler Error - %s", e.getMessage()));
        }
    }

}
