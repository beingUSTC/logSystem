package net_test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UpdReceiveDemo01 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(8888);
        //准备数据 控制台读取
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String data = reader.readLine();
        byte[] datas = data.getBytes();

//        socket.send();
    }
}
