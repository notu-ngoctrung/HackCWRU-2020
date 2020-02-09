import java.io.*;
import java.net.*;
import java.awt.Robot;

public class Client {
    private static final int PORT = 6969;

    public static void main(String[] args) throws UnknownHostException, IOException {
        Command command = new Command(Command.CType.HAND, "First Name", "Last Name");
        Socket client = new Socket("localhost", PORT);
        BufferedReader input =
			new BufferedReader(new InputStreamReader(client.getInputStream()));
        DataOutputStream output = new DataOutputStream(client.getOutputStream());
        output.write(command.getBytes());
        System.out.println(input.readLine());
        
		Command command;
		Robot robot = new Robot();
		while (true) {
			// Send screen buffer to controller (server)
			
			command = new Command(input.readLine());
			switch (command.getType()) {
				case "MOVE": // code to move mouse here
				case "CLCK": // mouse click
				case "KEYB": // keyboard press
				case "GBYE": // disconnect
				default: break;
			}
        }
    }
}
