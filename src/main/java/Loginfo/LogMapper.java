package Loginfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogMapper {

    static private Date date = new Date();
    static private String[] logs = new String[1024];
    static private String[] logs_time = new String[1024];
    static private int[] count_id = new int[32];
    static private int head = 0;
    static private int tail = 0;
    final static private int DEF = 60000;
    final static private int LOG_SIZE = 1024;
    static private String FILEURL = "src\\";

    int my_printf(int mod_id, String fmt, String status) throws IOException {
        Date nowDate = new Date();
        if (!can_write_log(mod_id, nowDate)) {
            return 0;
        }
        SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        logs_time[tail] = "log-" + formatter.format(nowDate);
        logs[tail] = logs_time[tail] + " " + mod_id + " [" + status + "]" + ": " + fmt;
        tail ++;
        tail %= LOG_SIZE;
        if ((tail + 1) % LOG_SIZE == head)out_write_log();
        return 1;
    }

    private boolean can_write_log(int mod_id, Date date)
    {
        if (date.getTime() - LogMapper.date.getTime() >= DEF) {
            LogMapper.date = date;
            count_id = new int[32];
        }
        if (count_id[mod_id] < 10) {
            count_id[mod_id]++;
            return true;
        }
        return false;
    }

    public void out_write_log() throws IOException {
        if (head == tail) {
            return;
        }

//        if ((tail - head + LOG_SIZE) % LOG_SIZE < 20) {
//            return;
//        }
        String url = FILEURL + "\\" + logs_time[head] + ".log";
        try {
            File file = new File(url);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            int count = 0;
            do {
                bufferedWriter.write(logs[head++] + "\n");
                bufferedWriter.flush();
                count ++;
            } while (head != tail && count < 20);
            bufferedWriter.close();
            fileWriter.close();
        }
        catch (IOException e) {
            System.out.println("文件读写有误！ " + e + "\n");
        }
        catch (Exception e) {
            System.out.println("Message: " + e + "\n");
        }
    }

}
