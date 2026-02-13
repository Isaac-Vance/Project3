import java.io.*;
import java.net.*;
import java.util.Scanner;
public class CustomClient {
    static Scanner scan = new Scanner(System.in);
    public static void main(String[] args) throws Exception {
        String host = args[0];
        int port = 3000;
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName(host);
        while (true) {
            int choice = menu();
            String message = buildCommand(choice);
            byte[] data = message.getBytes();
            DatagramPacket packet =
                    new DatagramPacket(data, data.length, serverAddress, port);
            socket.send(packet);
            if (choice == 6) break;
            byte[] buffer = new byte[4096];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(responsePacket);
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            System.out.println("Server says:\n" + response);
        }
        socket.close();
    }

    public static int menu() {
        System.out.println("""
                1. List files
                2. Delete file
                3. Rename file
                4. Download file
                5. Upload file
                6. Exit
                """);
        return scan.nextInt();
    }
    public static String buildCommand(int choice) throws IOException {
        switch (choice) {
            case 1:
                return "L";
            case 2:
                System.out.print("Enter filename: ");
                return "D " + scan.next();
            case 3:
                System.out.print("Old name: ");
                String oldName = scan.next();
                System.out.print("New name: ");
                String newName = scan.next();
                return "R " + oldName + " " + newName;
            case 4:
                System.out.print("Filename to download: ");
                return "O " + scan.next();
            case 5:
                System.out.print("Filename to upload: ");
                String fileName = scan.next();
                System.out.print("Enter text content: ");
                scan.nextLine(); // clear buffer
                String content = scan.nextLine();
                return "U " + fileName + " " + content;
            case 6:
                return "E";
            default:
                return "";
        }
    }
}
