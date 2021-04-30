package Loginfo;

import java.io.IOException;

public class demo02 {
    public static void main(String[] args) throws IOException {

//        MyLog myLog1 = new MyLog();
        MyLog myLog2 = new MyLog();
//        MyLog myLog3 = new MyLog();

//        new Thread(()->{
//            myLog1.my_printf(1,"???");
////            myLog1.my_printf(1,"...");
////            myLog1.my_printf(1,"【【【");
//            try {
//                myLog1.Send_mess();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//        new Thread(()->{
            myLog2.my_printf(2,"646");
            myLog2.my_printf(2,"6464");
            myLog2.my_printf(2,"6797");
            myLog2.my_printf(2,"997");
            myLog2.my_printf(2,"+45+6");
            myLog2.my_printf(2,"897");
            myLog2.my_printf(2,"897");
            myLog2.my_printf(2,"897");

            try {
                myLog2.Send_mess();
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }).start();
//        new Thread(()->{
//            myLog3.my_printf(3,"[[[[[[///]]]]]]");
//            myLog3.my_printf(3,"55555555555555");
//            try {
//                myLog3.Send_mess();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
    }
}
