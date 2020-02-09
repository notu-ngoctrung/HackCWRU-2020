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

    private int width, height, index;

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
        this.thread.start();
    }

    public void updateName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void updateDimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void updateIndex(int index) {
        this.index = index;
    }

    public void toSend(byte[] dataToSend) {
        try {
            sender.writeInt(dataToSend.length);
            sender.write(dataToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMouseInfo(int posX, int posY) {
        String strToSend = "MOVE " + Integer.toString(posX) + " " + Integer.toString(posY);
        toSend(strToSend.getBytes());
    }

    public void sendMousePressed(int value) {
        String strToSend = "CLCK " + Integer.toString(value);
        toSend(strToSend.getBytes());
    }

    public void sendGoodBye() {
        String strToSend = "GBYE";
        toSend(strToSend.getBytes());
    }

    public void sendStart() {
        String strToSend = "STRT";
        toSend(strToSend.getBytes());
    }

    public DataInputStream getReceiver() {
        return receiver;
    }

    public DataOutputStream getSender() {
        return sender;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public int getIndex() {
        return index;
    }
}
