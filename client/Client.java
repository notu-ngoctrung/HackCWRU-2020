package client;

import java.io.*;
import java.net.*;
import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Robot;
import javax.imageio.ImageIO;

public class Client {
    private static final int PORT = 6969;
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final String ADDRESS = "192.168.43.60"
	private static Robot robot;
	private static boolean isActive = false;

    public static void main(String[] args) throws UnknownHostException, IOException, AWTException {
		robot = new Robot();
        Command command = new Command(Command.CType.HAND, "First Name", "Last Name", WIDTH, HEIGHT);
        Socket client = new Socket(ADDRESS, PORT);
        //BufferedReader input =
			//new BufferedReader(new InputStreamReader(client.getInputStream()));
		DataInputStream input = new DataInputStream(client.getInputStream());
        DataOutputStream output = new DataOutputStream(client.getOutputStream());
		
		output.write(Command.intToByteArray(command.getBytes().length));
        output.write(command.getBytes());
		output.flush();
        
		BufferedImage image;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] imageBytes, data;
		while (true) {
			if (!isActive) {
				data = new byte[input.readInt()];
				input.readFully(data, 0, data.length);
				command = new Command(new String(data))
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

			data = new byte[input.readInt()];
			input.readFully(data, 0, data.length);
			command = new Command(new String(data));
			String[] arg = command.getArgs();
			switch (command.getType()) {
				case "MOVE": move(Integer.parseInt(arg[0]), Integer.parseInt(arg[1]));
				case "CLCK": click(arg[0] == "0" ? false : true, Integer.parseInt(arg[1]));
				case "KEYB": keyboard(arg[0] == "0" ? false : true, Integer.parseInt(arg[1]));
				case "GBYE": isActive = false;
				default: break;
			}
        }
      // Send screen buffer to controller (server)
      image = robot.createScreenCapture(new Rectangle(0, 0, WIDTH, HEIGHT));
      ImageIO.write(image, "jpg", bos);
      imageBytes = bos.toByteArray();
      command = new Command(Command.CType.SCRN, Integer.toString(imageBytes.length));
      byte[] temp = command.getBytes();
      output.write(Command.intToByteArray(temp.length));
      output.write(temp);
	  output.flush();
      output.write(imageBytes);
      bos.reset(); // to make room for next BufferedImage
      
      command = new Command(input.readLine());
      String[] arg = command.getArgs();
      switch (command.getType()) {
        case "MOVE": move(Integer.parseInt(arg[0]), Integer.parseInt(arg[1]));
        case "CLCK": click(arg[0] == "0" ? true : false, Integer.parseInt(arg[1]));
        case "KEYB": keyboard(arg[0] == "0" ? true : false, Integer.parseInt(arg[1]));
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
