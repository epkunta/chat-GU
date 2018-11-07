package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatWindow extends JFrame {
    private JTextField messageField;
    private JTextArea chatHistory;
    private JTextField login;
    private JPasswordField password;
    private JPanel top;
    private JPanel bottom;

    private ClientConnection clientConnection;

    public ChatWindow() {
        clientConnection = new ClientConnection();
        clientConnection.init(this);

        setTitle("Chat");
        setSize(400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        chatHistory = new JTextArea();
        chatHistory.setLineWrap(true);
        chatHistory.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(chatHistory);

        bottom = new JPanel();
        bottom.setLayout(new BorderLayout());
        bottom.setPreferredSize(new Dimension(300, 50));

        JButton send = new JButton("Send");
        messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(200, 50));

        login = new JTextField();
        password = new JPasswordField();
        JButton auth = new JButton("Login");
        top = new JPanel(new GridLayout(1, 3));
        top.add(login);
        top.add(password);
        top.add(auth);

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        auth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                auth();
            }
        });

        bottom.add(send, BorderLayout.EAST);
        bottom.add(messageField, BorderLayout.CENTER);

        add(jScrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        switchWindows();
        setVisible(true);
    }

    public void sendMessage() {
        String message = this.messageField.getText();
        this.messageField.setText("");
        clientConnection.sendMessage(message);
    }

    public void auth() {
        clientConnection.auth(login.getText(), new String(password.getPassword()));
        login.setText("");
        password.setText("");
    }

    public void showMessage(String message) {
        chatHistory.append(message + "\n");
        chatHistory.setCaretPosition(chatHistory.getDocument().getLength());
    }

    public void switchWindows() {
        top.setVisible(!clientConnection.isAuthorized());
        bottom.setVisible(clientConnection.isAuthorized());
    }
}
