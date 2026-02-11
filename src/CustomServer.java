import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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
        return listFiles();
    }
    public static void deleteFile(String fileName){

    }
    public static void uploadFile(){

    }
    public static void renameFile(){

    }
    public static void downloadFile(){

    }
}
