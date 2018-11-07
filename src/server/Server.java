package server;

import common.ServerConst;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server implements ServerConst {
    private Vector<ClientHandler> clients;

    public Vector<ClientHandler> getClients() {
        return clients;
    }

    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        ServerSocket serverSocket = null;
        Socket socket;
        clients = new Vector<>();
        try {
            serverSocket = new ServerSocket(PORT);
            authService = new BaseAuthService();
            authService.start();
            System.out.println("Server is running, awaiting for connections...");
            while (true) {
                socket = serverSocket.accept();
                clients.add(new ClientHandler(this, socket));
                System.out.println("Client has been connected");
            }
        } catch (IOException e) {
            System.out.println("Initialization error");
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void unicast(ClientHandler client, String message) {
        client.sendMessage(message);
    }

    public void unsubscribeMe(ClientHandler c) {
        clients.remove(c);
    }
}
