import java.io.*;
import java.net.*;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Robot;

public class Client {
    private static final int PORT = 6969;
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static Robot robot = new Robot();
	private boolean isActive = false;

    public static void main(String[] args) throws UnknownHostException, IOException {
        Command command = new Command(Command.CType.HAND, "First Name", "Last Name");
        Socket client = new Socket("localhost", PORT);
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
				command = new Command(input.readLine());
				if (command.getType() == "STRT")
					isActive = true;
				else
					continue;
			}
			// Send screen buffer to controller (server)
			image = robot.createScreenCapture(new Rectangle(0, 0, WIDTH, HEIGHT));
			ImageIO.write(image, "jpg", bos);
			imageBytes = bos.toByteArray();
			command = new Command(Command.CType.SCRN, WIDTH, HEIGHT, imageBytes.length);
			byte[] temp = command.getBytes();
			output.write(Command.intToByteArray(temp.length));
			output.write(temp);
			output.write(imageBytes);
			bos.reset(); // to make room for next BufferedImage

			command = new Command(input.readLine());
			String[] arg = command.getArgs();
			switch (command.getType()) {
				case "MOVE": move(arg[0], arg[1]);
				case "CLCK": // mouse click
				case "KEYB": // keyboard press
				case "GBYE": // disconnect
				default: break;
			}
        }
    }
}
