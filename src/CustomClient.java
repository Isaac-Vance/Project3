import java.io.IOException;
import java.net.*;
import java.util.Scanner;
public class CustomClient {
    public static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        String host = args[0];
        int port = 3000;
        boolean quit = false;

//      Send test packet and set up address
        System.out.println("Testing Connection...");
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(2000);
        String testMessage = "test";
        byte[] testMessageData = testMessage.getBytes();
        InetAddress serverAddress = InetAddress.getByName(host);
        DatagramPacket testPacket = new DatagramPacket(testMessageData, testMessageData.length, serverAddress, port);
        socket.send(testPacket);

        byte[] testBuffer = new byte[1024];
        DatagramPacket testResponsePacket = new DatagramPacket(testBuffer, testBuffer.length);
        socket.receive(testResponsePacket);
        String testResponse = new String(testResponsePacket.getData(), 0, testResponsePacket.getLength());
        System.out.println("Server replied: " + testResponse);

//      Main UI loop
        while (!quit) {
            String message = choiceConvert(menu());
            if (message.equals("E")) quit = true;
            byte[] messageData = message.getBytes();
            DatagramPacket packet = new DatagramPacket(messageData, messageData.length, serverAddress, port);
            socket.send(packet);

            byte[] buffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(responsePacket);
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            System.out.println("Server replied: " + response);
        }
        socket.close();
    }

    public static int menu() {
        System.out.println("Welcome to Isaac's file service! Please choose what you would like to do.\n1. List all files.\n2. Delete a file from the server\n3. Rename an existing file on the server.\n4. Download a file from the server\n5. Upload your file to the server.\n6. Exit.\nEnter choice here. (1, 2, etc.): ");
        return scan.nextInt();
    }

    public static String choiceConvert(int choice) {
        return switch (choice) {
            case 1 -> "L";
            case 2 -> "D";
            case 3 -> "R";
            case 4 -> "O";
            case 5 -> "U";
            case 6 -> "E";
            default -> "";
        };
    }
}