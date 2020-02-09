package client;

import java.io.*;
import java.net.*;
import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Robot;
import javax.imageio.ImageIO;

public class Client {
<<<<<<< HEAD
    private static final int PORT = 6969;
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final String ADDRESS = "192.168.43.60"
	private static final Robot robot = new Robot();
	private boolean isActive = false;

    public static void main(String[] args) throws UnknownHostException, IOException {
        Command command = new Command(Command.CType.HAND, "First Name", "Last Name", WIDTH, HEIGHT);
        Socket client = new Socket(ADDRESS, PORT);
        BufferedReader input =
			new BufferedReader(new InputStreamReader(client.getInputStream()));
        DataOutputStream output = new DataOutputStream(client.getOutputStream());
		
		output.write(Command.intToByteArray(command.getBytes().length));
        output.write(command.getBytes());
        
		//Robot robot = new Robot();
		BufferedImage image;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] imageBytes;
		while (true) {
			if (!isActive) {
				// TODO Does this really work?
				command = new Command(input.readLine());
				if (command.getType() == "STRT") {
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
			command = new Command(Command.CType.SCRN, imageBytes.length);
			byte[] temp = command.getBytes();
			output.write(Command.intToByteArray(temp.length));
			output.write(temp);
			output.write(imageBytes);
			bos.reset(); // to make room for next BufferedImage

			command = new Command(input.readLine());
			String[] arg = command.getArgs();
			switch (command.getType()) {
				case "MOVE": move(Integer.parseInt(arg[0]), Integer.parseInt(arg[1]));
				case "CLCK": click(arg[0] == "0" ? false : true, Integer.parseInt(arg[1]));
				case "KEYB": keyboard(arg[0] == "0" ? false : true, Integer.parseInt(arg[1]));
				case "GBYE": isActive = false;
				default: break;
			}
=======
  private static final int PORT = 6969;
  private static final int WIDTH = 800;
  private static final int HEIGHT = 600;
  private static Robot robot;
  private static boolean isActive = false;
  
  public static void main(String[] args) throws UnknownHostException, IOException {
    try {
      robot = new Robot();
    } catch (AWTException e) {
      e.printStackTrace();
    }
    Command command = new Command(Command.CType.HAND, "First Name", "Last Name", Integer.toString(WIDTH), Integer.toString(HEIGHT));
    Socket client = new Socket("localhost", PORT);
    
    BufferedReader input =
      new BufferedReader(new InputStreamReader(client.getInputStream()));
    DataOutputStream output = new DataOutputStream(client.getOutputStream());
    
    //output.write(Command.intToByteArray(command.getBytes().length));
    output.write(command.getBytes());
    
    //Robot robot = new Robot();
    BufferedImage image;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] imageBytes;
    while (true) {
      if (!isActive) {
        // TODO Does this really work?
        command = new Command(input.readLine());
        if (command.getType() == "STRT") {
          isActive = true;
          System.out.println("Starting remote control");
>>>>>>> 49f0d90933ff6b096784ad0128b006f99bf2f09c
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
      
      command = new Command(input.readLine());
      String[] arg = command.getArgs();
      switch (command.getType()) {
        case "MOVE": move(Integer.parseInt(arg[0]), Integer.parseInt(arg[1]));
        case "CLCK": click(arg[0] == "0" ? false : true, Integer.parseInt(arg[1]));
        case "KEYB": keyboard(arg[0] == "0" ? false : true, Integer.parseInt(arg[1]));
        case "GBYE": isActive = false;
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
  
  
}
