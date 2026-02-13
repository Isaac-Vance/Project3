import java.io.*;
import java.net.*;
import java.nio.file.Files;

public class CustomServer {

    static File serverFolder = new File("serverFolder");

    public static void main(String[] args) throws IOException {

        if (!serverFolder.exists()) {
            serverFolder.mkdir();
        }

        try (DatagramSocket socket = new DatagramSocket(3000)) {
            System.out.println("Server running on port 3000...");

            while (true) {

                byte[] buffer = new byte[2048];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: " + message);

                String response = handleCommand(message, socket, packet);

                if (response != null) {
                    byte[] responseData = response.getBytes();
                    DatagramPacket responsePacket =
                            new DatagramPacket(responseData, responseData.length,
                                    packet.getAddress(), packet.getPort());
                    socket.send(responsePacket);
                }
            }
        }
    }

    private static String handleCommand(String message, DatagramSocket socket, DatagramPacket packet) throws IOException {

        String[] parts = message.split(" ");

        switch (parts[0]) {

            case "L":
                return listFiles();

            case "D":
                if (parts.length < 2) return "Error: Missing filename.";
                return deleteFile(parts[1]);

            case "R":
                if (parts.length < 3) return "Error: Missing filenames.";
                return renameFile(parts[1], parts[2]);

            case "O":  // Download
                if (parts.length < 2) return "Error: Missing filename.";
                byte[] fileData = downloadFile(parts[1]);

                DatagramPacket filePacket = new DatagramPacket(
                        fileData,
                        fileData.length,
                        packet.getAddress(),
                        packet.getPort()
                );
                socket.send(filePacket);
                return null;

            case "U":  // Upload
                if (parts.length < 3) return "Error: Missing data.";

                String fileName = parts[1];
                byte[] fileBytes = message.substring(
                        parts[0].length() + parts[1].length() + 2
                ).getBytes();

                return uploadFile(fileName, fileBytes);

            case "E":
                return "Goodbye.";

            default:
                return "Unknown command.";
        }
    }

    public static String listFiles() {
        File[] files = serverFolder.listFiles();
        if (files == null || files.length == 0)
            return "No files found.";

        StringBuilder sb = new StringBuilder();
        for (File f : files) {
            sb.append(f.getName()).append("\n");
        }
        return sb.toString();
    }

    public static String deleteFile(String fileName) {
        File file = new File(serverFolder, fileName);
        if (!file.exists()) return "File not found.";
        if (file.delete()) return "File deleted.";
        return "Could not delete file.";
    }

    public static String renameFile(String oldName, String newName) {
        File oldFile = new File(serverFolder, oldName);
        File newFile = new File(serverFolder, newName);

        if (!oldFile.exists()) return "File not found.";
        if (newFile.exists()) return "New filename already exists.";

        if (oldFile.renameTo(newFile)) return "File renamed.";
        return "Rename failed.";
    }

    public static byte[] downloadFile(String fileName) throws IOException {
        File file = new File(serverFolder, fileName);
        if (!file.exists())
            return "File not found.".getBytes();
        return Files.readAllBytes(file.toPath());
    }

    public static String uploadFile(String fileName, byte[] data) throws IOException {
        File file = new File(serverFolder, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
        return "File uploaded.";
    }
}
