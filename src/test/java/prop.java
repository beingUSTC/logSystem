import java.io.*;
import java.util.Properties;

public class prop{
    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        File fr = null;
        fr = new File("src\\log.properties");
        InputStream inputStream = new FileInputStream(fr);
        prop.load(inputStream);
        String size = prop.getProperty("SIZE");
        System.out.println(size);
    }
}
