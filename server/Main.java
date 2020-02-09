package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.management.ImmutableDescriptor;
import java.awt.Robot;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main extends Application {
    private ServerSocket serverSocket;

    private ArrayList<Student> students = new ArrayList<Student>();

    private Rectangle remoteScreen;

    private int currentIndex = -1;

    private Thread mouseDetectThread;

    private Button home;
    private Button connect;
    private Button search;
    private Button trash;
    private Button help;
    private Button settings;
    private Button exit;
    private BorderPane pane;
    private VBox studentPane;

    public void process(Command command, Student student) throws IOException {
        switch (command.getType()) {
            case "HAND":
                String firstName = command.getArgAt(0);
                String lastName = command.getArgAt(1);
                int width = Integer.parseInt(command.getArgAt(2));
                int height = Integer.parseInt(command.getArgAt(3));
                student.updateName(firstName, lastName);
                student.updateDimension(width, height);
                // Some code to add GUI
                HBox studentBox = (HBox)studentPane.getChildren().get(student.getIndex());
                Label studentName = (Label) studentBox.getChildren().get(1);
                studentName.setText(student.getName());
                break;
            case "SCRN":
                int numBuffer = Integer.parseInt(command.getArgAt(0));
                byte[] byteImg = new byte[numBuffer];
                student.getReceiver().readFully(byteImg, 0, numBuffer);
                // Some code to convert bytes to image
                Image img = new Image(new ByteArrayInputStream(byteImg));
                // Some code to set image as the background of GUI
                remoteScreen.setFill(new ImagePattern(img, 0, 0, 1300, 731.25, false));
                break;
            case "GBYE":
                remoteScreen.setFill(Color.rgb(66, 68, 73));
                break;
        }
    }

    public void addNewStudent(Socket client) throws InterruptedException {
        students.add(new Student(client));
        Student student = students.get(students.size() - 1);
        student.updateIndex(students.size() - 1);
        student.addThread(new Thread(() -> {
            try {
                while (true) {
                    if (student.getReceiver().available() < 4)
                        continue;
                    int numBuffer = student.getReceiver().readInt();
                    byte[] data = new byte[numBuffer];
                    student.getReceiver().readFully(data, 0, numBuffer);
                    System.out.println(new String(data));
                    Command command = new Command(new String(data));
                    process(command, student);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        /** GUI */
        HBox studentBox = new HBox();
        studentBox.setPadding(new Insets(10, 0, 10, 10));
        studentBox.setBackground(new Background(new BackgroundFill(Color.rgb(54, 56, 61), CornerRadii.EMPTY, Insets.EMPTY)));
        Rectangle studentAva = new Rectangle(50, 50, new ImagePattern(new Image("/image/studentAva.png")));
        Label studentName = new Label(student.getName());
        studentName.setPadding(new Insets(10, 0, 10, 5));
        studentName.setTextFill(Color.WHITE);
        studentBox.getChildren().add(studentAva);
        studentBox.getChildren().add(studentName);
        System.out.println("ajajfsdkfdf");
        studentBox.setOnMouseClicked(e -> {
            if (currentIndex != -1)
                students.get(currentIndex).sendGoodBye();
            for(int i = 0; i < students.size(); i++) {
                Label label = (Label)studentBox.getChildren().get(1);
                if (students.get(i).getName().equals(label.getText())) {
                    currentIndex = i;
                    break;
                }
            }
            students.get(currentIndex).sendStart();
        });
        Platform.runLater(() -> {
            studentPane.getChildren().add(studentBox);
        });
    }

    public int normXCord(double originalX, int studentIndex) {
        return (int)(originalX * students.get(studentIndex).getWidth() / 1300);
    }

    public int normYCord(double originalY, int studentIndex) {
        return (int)(originalY * students.get(studentIndex).getHeight() / 731.25);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Teacher");
        remoteScreen = new Rectangle(1300, 731.25, Color.rgb(66, 68, 73));
        remoteScreen.setOnMouseMoved((e) -> {
            //System.out.println(e.getX() + "    " + e.getY());
            if (currentIndex != -1)
                students.get(currentIndex).sendMouseInfo(normXCord(e.getX(), currentIndex), normYCord(e.getY(), currentIndex));
        });
        remoteScreen.setOnMousePressed((e) -> {
            if (currentIndex != -1)
                students.get(currentIndex).sendMousePressed(1);
        });
        remoteScreen.setOnMouseReleased((e) -> {
            if (currentIndex != -1)
                students.get(currentIndex).sendMousePressed(0);
        });
        serverSocket = new ServerSocket(6969);
        Thread waitingClients = new Thread(() -> {
            try {
                while (true) {
                    Socket newClient = serverSocket.accept();
                    addNewStudent(newClient);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        waitingClients.start();

        /**
         * GUI
         */
        pane = new BorderPane();
        pane.setPrefSize(1600,731.25);

        String cssButton = "-fx-background-color: rgb(66, 68, 73);\n";

        int sizeButton = 60;

        home = new Button();
        home.setStyle(cssButton);
        home.setMinSize(sizeButton, sizeButton);
        connect = new Button();
        connect.setStyle(cssButton);
        connect.setMinSize(sizeButton, sizeButton);
        search = new Button();
        search.setStyle(cssButton);
        search.setMinSize(sizeButton, sizeButton);
        trash = new Button();
        trash.setStyle(cssButton);
        trash.setMinSize(sizeButton, sizeButton);
        help = new Button("Help");
        settings = new Button("Settings");
        exit = new Button("Exit");

        InputStream input1 = getClass().getResourceAsStream("/image/home.png");
        Image image1 = new Image(input1, 30, 30, true, true);
        ImageView imageView1 = new ImageView(image1);
        home.setGraphic(imageView1);

        InputStream input2 = getClass().getResourceAsStream("/image/connect.png");
        Image image2 = new Image(input2, 30, 30, true, true);
        ImageView imageView2 = new ImageView(image2);
        connect.setGraphic(imageView2);

        InputStream input3 = getClass().getResourceAsStream("/image/connect2.png");
        Image image3 = new Image(input3, 30, 30, true, true);
        ImageView imageView3 = new ImageView(image3);
        search.setGraphic(imageView3);

        InputStream input4 = getClass().getResourceAsStream("/image/trash.png");
        Image image4 = new Image(input4, 30, 30, true, true);
        ImageView imageView4 = new ImageView(image4);
        trash.setGraphic(imageView4);

        String cssLayout = "-fx-border-color: black;\n" +
                "-fx-border-insets: 0;\n" +
                "-fx-border-width: 0;\n";

        VBox vbButtons = new VBox();
        vbButtons.setPrefSize(70,731.25);
        vbButtons.setSpacing(30);
        vbButtons.setPadding(new Insets(20, 10, 10, 20));
        vbButtons.setBackground(new Background(new BackgroundFill(Color.rgb(29, 33, 35), CornerRadii.EMPTY, Insets.EMPTY)));
        vbButtons.getChildren().addAll(home, search, connect, trash);

        vbButtons.setStyle(cssLayout);

        studentPane = new VBox();
        studentPane.setPrefSize(230,631.25);
        studentPane.setBackground(new Background(new BackgroundFill(Color.rgb(54, 56, 61), CornerRadii.EMPTY, Insets.EMPTY)));
        studentPane.setStyle(cssLayout);

        VBox teacher = new VBox();
        teacher.setBackground(new Background(new BackgroundFill(Color.rgb(39, 42, 47), CornerRadii.EMPTY, Insets.EMPTY)));
        teacher.setPrefSize(200,100);
        teacher.setStyle(cssLayout);

        BorderPane border1 = new BorderPane();
        BorderPane border2 = new BorderPane();
        BorderPane border3 = new BorderPane();

        pane.setLeft(border1);
        pane.setCenter(border2);
        pane.setRight(border3);

        border1.setPrefSize(100,731.25);
        border1.setTop(vbButtons);

        border2.setPrefSize(200,731.25);
        border2.setTop(studentPane);
        border2.setBottom(teacher);

        border3.setPrefSize(1300,731.25);
        border3.setCenter(remoteScreen);
        Scene scene = new Scene(pane, 1600, 731.25);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
