package Loginfo;

import java.io.*;
import java.net.*;
import java.util.Properties;

public class logServer
{
    public void getServer() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        BufferedReader reader = null;
        try {
            serverSocket = new ServerSocket(8885);
            while (true) {
                socket = serverSocket.accept();
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("132\n");
                System.out.println(reader.readLine().substring(0,1) + "?????");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static void receive () throws IOException {
        LogMapper logMapper = new LogMapper();
        Properties prop = new Properties();
        File fr = null;
        fr = new File("src\\log.properties");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String size = prop.getProperty("SIZE");
        int Size = Integer.parseInt(size);
        DatagramSocket server = null;
        try {
            server = new DatagramSocket(5555);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while(true)
        {
            byte[] container = new byte[Size];
            DatagramPacket packet = new DatagramPacket(container,0,container.length);
            try {
                server.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] data = packet.getData();
            String receiveData = new String(data,0,data.length);
            System.out.println(receiveData);
            if(receiveData.equals("bye")){
                break;
            }
            logMapper.my_printf(1,receiveData,"INFO");
            logMapper.out_write_log();
        }
        server.close();
    }

    public static void main(String [] args) throws IOException {
        logServer logServer = new logServer();
        logServer.getServer();
    }
}