package com.example.ameer.comp4985_assignmen3;


import android.content.Context;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
    InetAddress IPAddress;
    String hostName;
    int port;
    Socket clientSocket;
    boolean isConnected;
    public TCPClient (String hostName, int port) throws IOException {
        this.hostName = hostName;
        this.port = port;
        isConnected = true;
    }

    public boolean connectToServer(final Context context) throws IOException, InterruptedException {

        Runnable runnable = new Runnable () {
            public void run() {
                try {
                    //Get by name checks whether an IP or a hostName were provided
                    IPAddress = InetAddress.getByName(hostName);
                    clientSocket = new Socket(IPAddress, port);
                } catch (UnknownHostException e) {
                    isConnected = false;
                    e.printStackTrace();
                } catch (IOException e) {
                    isConnected = false;
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread (runnable);
        thread.start();
        thread.join();
        return isConnected;
    }

    public void disconnectFromServer() throws IOException {
        IPAddress = null;
        hostName = null;
        port = 0;
        clientSocket.close();
    }


    public void sendToServer(final String clientStr) throws IOException {
        Runnable runnable = new Runnable () {
            public void run() {
                if (clientSocket != null){
                    OutputStream outToServer = null;
                    try {
                        outToServer = clientSocket.getOutputStream();
                        DataOutputStream out = new DataOutputStream (outToServer);
                        out.write(clientStr.getBytes());
                        //out.writeUTF(clientStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread thread = new Thread (runnable);
        thread.start();
    }
}