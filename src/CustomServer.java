import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;

public class CustomServer {
    static File serverFolder = new File ("serverFolder");
    public static void main(String[] args) throws SocketException {

        try (DatagramSocket socket = new DatagramSocket(3000)) {
            byte[] bytes = new byte[1024];
            while(true){
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: " + message);

                String response = "ACK: " + message;
                byte[] responseData = response.getBytes();
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
                socket.send(responsePacket);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static StringBuilder listFiles(){
        File[] fileList = serverFolder.listFiles();
        StringBuilder fileListString = new StringBuilder();
        for(int x = 0; x<fileList.length; x++){
           fileListString.append(fileList[x].getName()) ;
        }
        return fileListString;
    }
    public static String deleteFile(String fileName) {
        try {
            File fileToDelete = new File(serverFolder, fileName);

            // Security check: ensure file is inside serverFolder
            if (!fileToDelete.getCanonicalPath()
                    .startsWith(serverFolder.getCanonicalPath())) {
                return "Error: Invalid file path.";
            }

            if (!fileToDelete.exists()) {
                return "Error: File does not exist.";
            }

            if (!fileToDelete.isFile()) {
                return "Error: Not a valid file.";
            }

            if (fileToDelete.delete()) {
                return "Success: File deleted.";
            } else {
                return "Error: Could not delete file.";
            }

        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
    public static String uploadFile(String fileName, byte[] fileData, int length) {
        try {
            File outputFile = new File(serverFolder, fileName);
            // Security check: prevent path traversal
            if (!outputFile.getCanonicalPath()
                    .startsWith(serverFolder.getCanonicalPath())) {
                return "Error: Invalid file path.";
            }
            // Ensure server folder exists
            if (!serverFolder.exists()) {
                serverFolder.mkdir();
            }
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(fileData, 0, length);
            }
            return "Success: File uploaded.";
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
    public static String renameFile(String oldFileName, String newFileName) {
        try {
            File oldFile = new File(serverFolder, oldFileName);
            File newFile = new File(serverFolder, newFileName);
            // Security check: prevent path traversal
            if (!oldFile.getCanonicalPath().startsWith(serverFolder.getCanonicalPath()) ||
                    !newFile.getCanonicalPath().startsWith(serverFolder.getCanonicalPath())) {
                return "Error: Invalid file path.";
            }
            if (!oldFile.exists()) {
                return "Error: File does not exist.";
            }
            if (!oldFile.isFile()) {
                return "Error: Not a valid file.";
            }
            if (newFile.exists()) {
                return "Error: A file with the new name already exists.";
            }
            boolean renamed = oldFile.renameTo(newFile);
            if (renamed) {
                return "Success: File renamed.";
            } else {
                return "Error: Could not rename file.";
            }
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
    public static byte[] downloadFile(String fileName) {
        try {
            File file = new File(serverFolder, fileName);
            // Security check: prevent path traversal
            if (!file.getCanonicalPath()
                    .startsWith(serverFolder.getCanonicalPath())) {
                return "Error: Invalid file path.".getBytes();
            }

            if (!file.exists()) {
                return "Error: File does not exist.".getBytes();
            }
            if (!file.isFile()) {
                return "Error: Not a valid file.".getBytes();
            }
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            return ("Error: " + e.getMessage()).getBytes();
        }
    }

}
