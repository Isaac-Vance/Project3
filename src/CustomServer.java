import java.io.*;
import java.net.*;
import java.nio.file.Files;

public class CustomServer {
    static File serverFolder = new File("serverFolder");
    public static void main(String[] args) throws IOException {
        if (!serverFolder.exists())
            serverFolder.mkdir();
        ServerSocket serverSocket = new ServerSocket(3000);
        System.out.println("TCP File Server running on port 3000...");
        while (true) {
            Socket client = serverSocket.accept();
            new Thread(() -> handleClient(client)).start();
        }
    }
    private static void handleClient(Socket socket) {
        try (
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        ) {
            while (true) {
                String command = dis.readUTF();
                switch (command) {
                    case "L":
                        File[] files = serverFolder.listFiles();
                        if (files == null || files.length == 0) {
                            dos.writeUTF("No files found.");
                        } else {
                            StringBuilder sb = new StringBuilder();
                            for (File f : files)
                                sb.append(f.getName()).append("\n");
                            dos.writeUTF(sb.toString());
                        }
                        break;
                    case "D":
                        String deleteName = dis.readUTF();
                        File deleteFile = new File(serverFolder, deleteName);
                        if (deleteFile.exists() && deleteFile.delete())
                            dos.writeUTF("File deleted.");
                        else
                            dos.writeUTF("Delete failed.");
                        break;
                    case "R":
                        String oldName = dis.readUTF();
                        String newName = dis.readUTF();
                        File oldFile = new File(serverFolder, oldName);
                        File newFile = new File(serverFolder, newName);
                        if (!oldFile.exists())
                            dos.writeUTF("File not found.");
                        else if (newFile.exists())
                            dos.writeUTF("New file already exists.");
                        else if (oldFile.renameTo(newFile))
                            dos.writeUTF("File renamed.");
                        else
                            dos.writeUTF("Rename failed.");
                        break;
                    case "UPLOAD":
                        String uploadName = dis.readUTF();
                        long uploadSize = dis.readLong();
                        File uploadFile = new File(serverFolder, uploadName);
                        try (FileOutputStream fos = new FileOutputStream(uploadFile)) {
                            byte[] buffer = new byte[4096];
                            long remaining = uploadSize;
                            while (remaining > 0) {
                                int read = dis.read(buffer, 0,
                                        (int)Math.min(buffer.length, remaining));
                                fos.write(buffer, 0, read);
                                remaining -= read;
                            }
                        }
                        dos.writeUTF("Upload successful.");
                        break;
                    case "DOWNLOAD":
                        String downloadName = dis.readUTF();
                        File downloadFile = new File(serverFolder, downloadName);
                        if (!downloadFile.exists()) {
                            dos.writeUTF("NOT_FOUND");
                        } else {
                            dos.writeUTF("OK");
                            dos.writeLong(downloadFile.length());

                            try (FileInputStream fis =
                                         new FileInputStream(downloadFile)) {
                                byte[] buffer = new byte[4096];
                                int read;
                                while ((read = fis.read(buffer)) != -1) {
                                    dos.write(buffer, 0, read);
                                }
                            }
                        }
                        break;

                    case "E":
                        socket.close();
                        return;
                }
                dos.flush();
            }
        } catch (Exception e) {
            System.out.println("Client disconnected.");
        }
    }
}
