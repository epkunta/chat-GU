package server;

import common.Server_API;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Server_API {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {
                //Authorization
                while (true) {
                    String message = in.readUTF();
                    if (message.startsWith(AUTH)) {
                        String[] elements = message.split(" ");
                        String nick = server.getAuthService().getNickByLoginPass(elements[1], elements[2]);
                        if (nick != null) {
                            sendMessage(AUTH_SUCCESSFUL + " " + nick);
                            this.nick = nick;
                            server.broadcast(this.nick + " has entered the chat room");
                            break;
                        } else sendMessage("Wrong login/password!");
                    } else sendMessage("You should authorize first!");
                }
                while (true) {
                    String message = in.readUTF();
                    if (message.startsWith(SYSTEM_SYMBOL)) {
                        String[] elements = message.split(" ");
                        for (String parts : elements) {
                            System.out.println(parts);
                        }
                        if (message.equalsIgnoreCase(CLOSE_CONNECTION)) {
                            server.broadcast(CLOSE_CONNECTION + " " + nick + " has been disconnected");
                            break;
                        } else if (message.startsWith(UNICAST_TO)) {
                            for (ClientHandler client : server.getClients()) {
                                if (elements[1].equals(client.nick))
                                    System.out.println("Private message to " + client.nick + ": " + message.substring(elements[0].length() + elements[1].length() + 2));
                                server.unicast(client, nick + ": " + message.substring(elements[0].length() + 1));
                            }
                        } else sendMessage("Command doesn't exist!");
                    } else {
                        System.out.println(nick + ": " + message);
                        server.broadcast(nick + ": " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        sendMessage(CLOSE_CONNECTION + " You have been disconnected!");
        server.unsubscribeMe(this);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
