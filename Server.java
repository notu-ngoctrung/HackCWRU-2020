import java.io.*;
import java.net.*;

class Server {
    private static final int PORT = 6969;
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);

        while (true) {
            String command;
            Socket connectionSocket = server.accept();
            BufferedReader input =
                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream output = new DataOutputStream(connectionSocket.getOutputStream());
            command = input.readLine();
            System.out.println("Received: " + command);
            output.writeBytes("ECHO: " + command);
        }
    }
}