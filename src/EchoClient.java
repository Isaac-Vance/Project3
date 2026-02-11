import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class EchoClient {
    public static void main(String[] args) throws Exception{
        if (args.length != 2){
            System.out.println("Please specify server IP & port");
            return;
        }

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);

        //try to connect to the server
        try(Socket socket = new Socket(serverIP, serverPort)){
            System.out.println("Connected to server.");
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            String fileName = "test2.jpg";
            // write bytes to the connection
            out.write(fileName.getBytes());
            // send the special signal to let server know
            // we've done sending
            socket.shutdownOutput();

            File myFolder = new File("ClientFiles");
            if(!myFolder.exists()){
                myFolder.mkdirs();
            }
            File myFile = new File(myFolder, fileName);

            byte[] buffer = new byte[1024];
            int bytesRead;

            try(FileOutputStream fos = new FileOutputStream(myFile)) {
                while ((bytesRead = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
        }
    }
}
