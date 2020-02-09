package client;

import java.io.*;
import java.net.*;
import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Dimension;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.event.*;

public class Client extends Application  {
  private static int PORT = 6969;
//  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
  private static int WIDTH;
  private static int HEIGHT;
  private static String ADDRESS = "localhost";
  public static final String FIRST_NAME = "Unimote";
  public static final String LAST_NAME = "HackCWRU 2020";
  private static Robot robot;
  private static boolean isActive = true;
  
  @Override
  public void start(Stage primaryStage) throws UnknownHostException, IOException, AWTException {
    robot = new Robot();
    WIDTH = 1920;
    HEIGHT = 1080;
    Command command = new Command(Command.CType.HAND, FIRST_NAME, LAST_NAME, Integer.toString(WIDTH), Integer.toString(HEIGHT));
    Socket client = new Socket(ADDRESS, PORT);
    DataInputStream input = new DataInputStream(client.getInputStream());
    DataOutputStream output = new DataOutputStream(client.getOutputStream());
    
    
    primaryStage.setTitle("Student");
    GridPane gp = new GridPane();
    gp.setAlignment(Pos.CENTER);
    gp.setHgap(10);
    gp.setVgap(10);
    gp.setPadding(new Insets(25, 25, 25, 25));
    
    Text scenetile = new Text("Unimote Client");
    scenetile.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
    gp.add(scenetile, 0, 0, 2, 1);
    
    Label userName = new Label("Server IP");
    gp.add(userName, 0, 1);
    TextField userTextField = new TextField(); //important
    gp.add(userTextField, 1, 1);
    Label pw = new Label("Port");
    gp.add(pw, 0, 2);
    TextField portBox = new TextField(); //important
    gp.add(portBox, 1, 2);
    
    Button btn = new Button("Connect");
    Button disconnectbtn = new Button("Disconnect");
    HBox hbBtn = new HBox(10);
    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
    hbBtn.getChildren().add(btn);
    hbBtn.getChildren().add(disconnectbtn);
    gp.add(hbBtn, 1, 4);
    
//    btn.setOnAction(new EventHandler<ActionEvent>() {
//      @Override
//      public void handle(ActionEvent e) {
//        ADDRESS = userTextField.getText();
//        PORT = Integer.parseInt(portBox.getText());
//        try {
//          Socket client = new Socket(ADDRESS, PORT);
//        } catch (Exception err) {
//          err.printStackTrace();
//        }
//        input = new DataInputStream(client.getInputStream());
//        output = new DataOutputStream(client.getOutputStream());
//        output.write(Command.intToByteArray(command.getBytes().length));
//        output.write(command.getBytes());
//        output.flush();
//      }
//    });
    
    disconnectbtn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        if (client == null || !client.isConnected())
          return;
        Command gbye = new Command("GBYE");
        try {
          output.write(Command.intToByteArray(gbye.getBytes().length));
          output.write(gbye.getBytes());
          output.flush();
        } catch (IOException err) {
          err.printStackTrace();
        }
      }
    });
    
    Scene scene = new Scene(gp, 300, 300);
    primaryStage.setScene(scene);
//    primaryStage.show();
    
//    output.write(Command.intToByteArray(command.getBytes().length));
//    output.write(command.getBytes());
//    output.flush();
    
    BufferedImage image;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] imageBytes, data;
    while (true) {
      if (!isActive) {
        data = new byte[input.readInt()];
        input.readFully(data, 0, data.length);
        command = new Command(new String(data));
        if (command.getType().equals("STRT")) {
          isActive = true;
          System.out.println("Starting remote control");
        }
        else
          continue;
      }
      // Send screen buffer to controller (server)
      image = robot.createScreenCapture(new Rectangle(0, 0, WIDTH, HEIGHT));
      ImageIO.write(image, "jpg", bos);
      imageBytes = bos.toByteArray();
      command = new Command(Command.CType.SCRN, Integer.toString(imageBytes.length));
      byte[] temp = command.getBytes();
      output.write(Command.intToByteArray(temp.length));
      output.write(temp);
      output.write(imageBytes);
      bos.reset(); // to make room for next BufferedImage
      
      data = new byte[input.readInt()];
      input.readFully(data, 0, data.length);
      command = new Command(new String(data));
      String[] arg = command.getArgs();
      switch (command.getType()) {
        case "MOVE": move(Integer.parseInt(arg[0]), Integer.parseInt(arg[1])); break;
        case "CLCK": click(arg[0] == "0" ? false : true, Integer.parseInt(arg[1])); break;
        case "KEYB": keyboard(arg[0] == "0" ? false : true, Integer.parseInt(arg[1])); break;
        case "GBYE": isActive = false; break;
        default: break;
      }
    }
    
  }
  
  public static void move(int x, int y) {
    robot.mouseMove(x, y);
  }
  
  public static void click(boolean isRelease, int mask) {
    // A Mask is defined as InputEvent.BUTTON1_MASK and so on.
    if (isRelease)
      robot.mouseRelease(mask);
    else
      robot.mousePress(mask);
  }
  
  public static void keyboard(boolean isRelease, int keycode) {
    if (isRelease)
      robot.keyPress(keycode);
    else
      robot.keyRelease(keycode); 
  }
  
  public static void main(String[] args) throws Exception {
    launch(args);  
  }
}
