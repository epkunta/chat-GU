package client;

import common.ServerConst;
import common.Server_API;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection implements ServerConst, Server_API {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean isAuthorized = false;

    public boolean isAuthorized() {
        return isAuthorized;
    }

    private void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public ClientConnection() {
    }

    public void init(ChatWindow view) {
        try {
            this.socket = new Socket(SERVER_URL, PORT);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readUTF();
                        if (message.startsWith(AUTH_SUCCESSFUL)) {
                            setAuthorized(true);
                            view.switchWindows();
                            break;
                        }
                        view.showMessage(message);
                    }
                    while (true) {
                        String message = in.readUTF();
                        String[] elements = message.split(" ");
                        if (message.startsWith(SYSTEM_SYMBOL)) {
                            if (elements[0].equals(CLOSE_CONNECTION))
                                setAuthorized(false);
                            view.showMessage(message.substring(CLOSE_CONNECTION.length() + 1));
                            view.switchWindows();
                        } else {
                            view.showMessage(message);
                        }
                    }
                } catch (IOException e) {
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void auth(String login, String password) {
        try {
            out.writeUTF(AUTH + " " + login + " " + password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            out.writeUTF(CLOSE_CONNECTION);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
