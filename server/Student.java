package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Student {
    private Socket socket;

    private Thread thread;

    private String firstName, lastName;

    private DataInputStream receiver;

    private DataOutputStream sender;

    public Student(Socket socket) {
        this.socket = socket;
        try {
            receiver = new DataInputStream(socket.getInputStream());
            sender = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addThread(Thread thread) {
        this.thread = thread;
        this.thread.run();
    }

    public void updateName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public DataInputStream getReceiver() {
        return receiver;
    }

    public DataOutputStream getSender() {
        return sender;
    }
}
