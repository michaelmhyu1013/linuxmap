package com.example.ameer.comp4985_assignmen3;


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

    public TCPClient (String hostName, int port) throws IOException {
        this.hostName = hostName;
        this.port = port;
        connectToServer();
    }

    public void connectToServer() throws IOException {
        Runnable runnable = new Runnable () {
            public void run() {
                try {
                    //Get by name checks whether an IP or a hostName were provided
                    IPAddress = InetAddress.getByName(hostName);
                    clientSocket = new Socket(IPAddress, port);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread (runnable);
        thread.start();

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