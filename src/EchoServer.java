import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    public static void main(String args[]) throws Exception{
        //server port
        int port = 3000;

        try(ServerSocket mySocket = new ServerSocket(3000)){
            while(true) {
                System.out.println("Server is waiting on port" + port);
                //establish the connection first
                Socket clientSocket = mySocket.accept();
                System.out.println("Client connected: "
                        + clientSocket.getInetAddress());
                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream();

                byte[] buffer = new byte[1024];
                int bytesRead;
                StringBuilder fileName = new StringBuilder();

                while ((bytesRead = in.read(buffer)) != -1) {
                    String message = new String(buffer, 0, bytesRead);
                    fileName.append(message);
                }
                System.out.println("Client requested file " + fileName.toString());

                File myFolder = new File("ServerFiles");
                File fileToSend = new File(myFolder, fileName.toString());
                if (fileToSend.exists() && !fileToSend.isDirectory()) {
                    try (FileInputStream fis = new FileInputStream(fileToSend)) {
                        byte[] fileBuffer = new byte[1024];
                        while ((bytesRead = fis.read(fileBuffer)) != -1) {
                            out.write(fileBuffer, 0, bytesRead);
                        } //while
                    } //try
                } else if (!fileToSend.exists()) {
                    System.out.println("Error: File not found on server.");
                } else {
                    System.out.println("Error: Client requested a directory.");
                }
                clientSocket.close();
            }//while
        }//try
    }//main
    public static void deleteFile(){

    }
}
