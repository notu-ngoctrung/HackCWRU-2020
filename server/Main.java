package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.awt.Robot;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main extends Application {
    private ServerSocket serverSocket;

    private ArrayList<Student> students;

    public void process(Command command, Student student) throws IOException {
        switch (command.getType()) {
            case "HAND":
                String firstName = command.getArgAt(0);
                String lastName = command.getArgAt(1);
                student.updateName(firstName, lastName);
                // Some code to add GUI
                break;
            case "SCRN":
                int width = Integer.parseInt(command.getArgAt(0));
                int height = Integer.parseInt(command.getArgAt(1));
                int numBuffer = Integer.parseInt(command.getArgAt(2));
                byte[] byteImg = new byte[numBuffer];
                student.getReceiver().readFully(byteImg, 0, numBuffer);
                // Some code to convert bytes to image
                // Some code to set image as the background of GUI
                break;
            case "GBYE":
                break;
        }
    }

    public void addNewStudent(Socket client) {
        students.add(new Student(client));
        Student student = students.get(students.size() - 1);
        student.addThread(new Thread(() -> {
            try {
                while (true) {
                    if (student.getReceiver().available() < 4)
                        continue;
                    int numBuffer = student.getReceiver().readInt();
                    byte[] data = new byte[numBuffer];
                    student.getReceiver().readFully(data, 0, numBuffer);
                    Command command = new Command(new String(data));
                    process(command, student);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Teacher");
        primaryStage.setScene(new Scene(root, 1600, 731.25));
        serverSocket = new ServerSocket(6969);
        primaryStage.show();
        while (true) {
            Socket newClient = serverSocket.accept();
            addNewStudent(newClient);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


}
