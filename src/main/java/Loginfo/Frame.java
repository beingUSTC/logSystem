package Loginfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

public class Frame {
    private static int i = 0;
    public static void main(String[] args) {
        JFrame a = new JFrame("Log");
        a.setLocation(100, 100);
        a.setSize(600, 300);
        a.setLayout(null);

        JButton b = new JButton("开启一个新的客户端");
        b.setLocation(100, 100);
        b.setSize(400, 100);
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    JDialog c = new JDialog(a);
                    c.setTitle("这是一个模态窗口");
                    c.setSize(450, 200);
                    c.setLocation(300, 300);
                    c.setLayout(null);
                    MyLog myLog1 = new MyLog();
                    Integer j = (int) (1 + Math.random() * 10);
                    JButton write = new JButton("写入日志");
                    JButton read = new JButton("读出日志到服务器");
                    write.setBounds(50, 50, 280, 30);
                    read.setBounds(50, 80, 280, 30);

                    write.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (c.isResizable()) {
                                Integer i = (int) (1 + Math.random() * 10);
                                myLog1.my_printf(j, i.toString());
                                myLog1.show();
                            }
                        }
                    });
                    read.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (c.isResizable()) {
                                try {
                                    myLog1.Send_mess();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    });
                    c.add(write);
                    c.add(read);
                    c.setVisible(true);
                }).start();
            }
        });
        a.add(b);
        a.setVisible(true);
    }
}
