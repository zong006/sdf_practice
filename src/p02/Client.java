package p02;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    
    public static void main(String[] args) throws UnknownHostException, IOException {

        int port = 3000;
        String host = "localhost";

        Socket socket = new Socket(host, port);
        System.out.println("Connection established...");

        OutputStream os = socket.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        InputStream is = socket.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        ObjectInputStream ois = new ObjectInputStream(bis);

        boolean close = false;

        while (!close){

            
            String lineRead = ois.readUTF();
            String[] items = lineRead.split(" ");
        
            List<String> numbers = new ArrayList<>(Arrays.asList(items[1].split(",")));
            int sum = numbers.stream().mapToInt( p -> Integer.parseInt(p)).sum();
            float avg = (float) sum/ numbers.size();
            
            String[] sendToServer = {items[0], "example tan", "sample@email.com"};
            for (String s : sendToServer){
                oos.writeUTF(s);
                oos.flush();
                System.out.println("sent " + s);
            }
            oos.writeFloat(avg);
            oos.flush();
            System.out.println("sent " + avg);
            
            boolean result = ois.readBoolean();
            if (result) {
                System.out.println("SUCCESS");
            }
            else {
                String errorMsg = ois.readUTF();
                System.out.println("FAILED");
                System.out.println(errorMsg);
            }
            close = true;
            socket.close();
            
        }

    }
}
