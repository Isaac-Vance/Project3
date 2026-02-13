import java.io.*;
import java.net.*;
import java.util.Scanner;

public class CustomClient {
    static Scanner scan = new Scanner(System.in);
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(args[0], 3000);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        while (true) {
            int choice = menu();
            switch (choice) {
                case 1: // List
                    dos.writeUTF("L");
                    System.out.println(dis.readUTF());
                    break;
                case 2: // Delete
                    dos.writeUTF("D");
                    System.out.print("Filename: ");
                    dos.writeUTF(scan.next());
                    System.out.println(dis.readUTF());
                    break;
                case 3: // Rename
                    dos.writeUTF("R");
                    System.out.print("Old name: ");
                    dos.writeUTF(scan.next());
                    System.out.print("New name: ");
                    dos.writeUTF(scan.next());
                    System.out.println(dis.readUTF());
                    break;
                case 4: // Download
                    dos.writeUTF("DOWNLOAD");
                    System.out.print("Filename: ");
                    String downloadName = scan.next();
                    dos.writeUTF(downloadName);
                    String status = dis.readUTF();
                    if (status.equals("NOT_FOUND")) {
                        System.out.println("File not found.");
                    } else {
                        long size = dis.readLong();
                        try (FileOutputStream fos =
                                     new FileOutputStream(downloadName)) {
                            byte[] buffer = new byte[4096];
                            long remaining = size;

                            while (remaining > 0) {
                                int read = dis.read(buffer, 0,
                                        (int)Math.min(buffer.length, remaining));
                                fos.write(buffer, 0, read);
                                remaining -= read;
                            }
                        }
                        System.out.println("Download complete.");
                    }
                    break;
                case 5: // Upload
                    dos.writeUTF("UPLOAD");
                    System.out.print("Filename: ");
                    String uploadName = scan.next();
                    File file = new File(uploadName);
                    if (!file.exists()) {
                        System.out.println("File does not exist.");
                        break;
                    }
                    dos.writeUTF(uploadName);
                    dos.writeLong(file.length());
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int read;
                        while ((read = fis.read(buffer)) != -1) {
                            dos.write(buffer, 0, read);
                        }
                    }
                    System.out.println(dis.readUTF());
                    break;
                case 6:
                    dos.writeUTF("E");
                    socket.close();
                    return;
            }
            dos.flush();
        }
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
}
