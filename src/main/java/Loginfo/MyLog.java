package Loginfo;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyLog {
//    static private Date date = new Date();//liu
//    static private int[] count_id = new int[32];//liu
//    final static private int DEF = 60000;//liu
    static ReentrantLock lock = new ReentrantLock();
    private static num num = new num();
    private int MAX_EXIST = 5;
//    static private String[] logs = new String[1024];//liu
//    static private String[] logs_time = new String[1024];//liu
    private static MyLog INSTANCE = new MyLog();
//    private static int next_w;//记录下一个应该加上去的info位置
//    private static int next_r;//读指针,发送到服务器
    private static int MAX_SIZE = 5;
    private volatile static HashMap<Integer,Integer> map;//用于存储缓冲区中同种log个数
    public static final CopyOnWriteArrayList<Info> arr = new CopyOnWriteArrayList<>();
    public MyLog(){
        map = new HashMap<>(MAX_SIZE);
    }

    static class num{
        private int r;
        private int w;
        public void setR(int r){
            lock.lock();
            try {
                this.r = r;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
        public void setW(int w){
            lock.lock();
            try {
                this.w = w;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
        public int getR(){
            return r;
        }
        public int getW(){
            return w;
        }
        public num(){
            this.r = 0;
            this.w = 0;
        }
    }

    class Info{
        public String string;
        public int number;//忽略set，get方法
        public Info(String s,int number){
            this.string = s;
            this.number = number;
        }
    }
    public void show(){
        for (int i = 0; i < arr.size(); i++) {
            System.out.print(arr.get(i).string.substring(26)+" ");
        }
        System.out.println();
    }

    public int my_printf(int mod_id, String info_str)  {
//        System.out.println(Thread.currentThread().getName());
        //实现环形缓冲区
        lock.lock();
        try {
            if(map.containsKey(mod_id)){
                if(map.get(mod_id)>MAX_EXIST){
                    //每个客户端不能超过是个日志在缓冲区中
                    return 0;//返回0代表不能分配缓冲区
                }
                else {
                    int num = map.get(mod_id);
                    map.put(mod_id,num+1);
                }
            }else map.put(mod_id,1);
            Date nowDate = new Date();
            SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
            String s = "log-" + formatter.format(nowDate);
            String ans = s + " " + mod_id + " [" + "INFO" + "]" + ": " + info_str;
            Info info = new Info(ans,mod_id);

            if(arr.size()==0){
                num.setW(1);
                arr.add(0,info);
            }
            else if(arr.size()==MAX_SIZE&&num.getW()==MAX_SIZE){
                num.setW(1);
                arr.set(0,info);
//                for (int i = arr.size()-1; i > 1; i--) {
//                    arr.set(i)
//                }
            }
            else if(arr.size()==MAX_SIZE){
                num.setW(num.getW()+1);
                arr.set(num.getW()-1,info);
            }
            else {
                num.setW(num.getW()+1);
                arr.add(num.getW()-1,info);
            }

            log(info_str,mod_id);
            return 1;//返回1表示日志生成成功
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return -1;
    }

    private void log(String info,int mod_id){
        lock.lock();
        try {
            Logger logger = Logger.getLogger("INFO");
            logger.log(Level.INFO, info);
            if (map.get(mod_id)==1){//输出后减去缓冲区
                map.remove(mod_id);
            }else{
                int num = map.get(mod_id);
                map.put(mod_id,num-1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }

    public void Send_mess() throws IOException {
        lock.lock();
//        try {
//            Socket socket = new Socket("localhost",8887);
//            PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);
//            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
//        }catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            lock.unlock();
//        }
        try {
            Socket socket = new Socket("localhost",8885);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            try{
                if(num.getR()==num.getW()&&arr.size()==MAX_SIZE){
                    return;
                }
                if(num.getR()<num.getW()&&arr.size()<MAX_SIZE){
                    //没有满
                    for (int i = num.getR(); i < num.getW(); i++) {
                        Info info = arr.get(i);
                        String mess = info.string;
                       bufferedOutputStream.write(mess.getBytes());
                       bufferedOutputStream.flush();
                    }
                    num.setR(num.getW());
                }else if(num.getR()<=num.getW()&&arr.size()==MAX_SIZE){
                    for (int i = 0; i < arr.size(); i++) {
                        Info info = arr.get(i);
                        String mess = info.string;
                        bufferedOutputStream.write(mess.getBytes());
                        bufferedOutputStream.flush();
                    }
                    num.setR(num.getW());
                }

                else if(num.getR()>num.getW()&&arr.size()==MAX_SIZE){
                    for (int i = num.getR(); i < arr.size(); i++) {
                        Info info = arr.get(i);
                        String mess = info.string;
                        bufferedOutputStream.write(mess.getBytes());
                        bufferedOutputStream.flush();
                    }
                    for (int i = 0; i < num.getW(); i++) {
                        Info info = arr.get(i);
                        String mess = info.string;
                        bufferedOutputStream.write(mess.getBytes());
                        bufferedOutputStream.flush();
                        num.setR(num.getW());
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                socket.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
        lock.unlock();
    }

    }
}
