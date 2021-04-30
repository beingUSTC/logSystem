package Loginfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 * 　<b>自定义LOG工具，丁风华</b>
 * <ul>
 * <b>特点</b>
 * <li>无配置文件，默认为info级别，控制台输出，无需改动即可满足一般开发需要。。</li>
 * <li>直接打log，在java文件中直接使用　<code>Log.info("test");</code>　即可，如果输出到文件，则自动按不同级别生成不同记录文件</li>
 * <li>使用四个级别的log。分别为　<code>Log.debug("");　Log.info("");　Log.warn("");　Log.error("");</code>　对于一般项目已经足够。</li>
 * <li>log输出方式有两种，控制台输出和文件输出。输出格式两者统一。文件输出时的文件路径可定制。默认为class文件夹下,名称格式为　等级_时间.log　如: INFO_2011-10-12.log</li>
 * </ul>
 * <ul>
 * <b>输出到文件使用方法</b><br>
 * 更改构造器方法中的数据. private Log()<br>
 * 1.设置输出级别: setLogLevel("info");<br>
 * 2.设置输出到文件: HANDLER_FILE = true;<br>
 * 3.设置设置文件保存路径: LOG_FILE_PATH = "D:\\log\\";<br>
 * 4.设置每个文件的最大值: FILE_SIZE = 2000000; // *** 每个文件最大2M <br>
 * 5.设置每天最多生成多少个文件数量: FILE_MAX = 10; // *** 每天最多生成10个文件,最多不可超过100<br>
 * 6.设置log文件后缀名: LOG_FILE_LASTNAME = ".log";
 * </ul>
 * <ul>
 * <b>更改记录内容的格式</b><br>
 * 可更改　dealInfoFormat　方法中的　formatMessage　变量值即可。
 * </ul>
 *
 * @author DingFengHua
 */
public class Log {

    public Log() {
        setLogLevel("INFO");

        HANDLER_CONSOLE = true; // *** 控制台输出
        HANDLER_FILE = false; // *** 文件输出

        // *** 如果不想使用文件打LOG，即 HANDLER_FILE = false;，则不用关心以下数据
        // *** 自动创建路径文件夹
        if (HANDLER_FILE) {

            // *** 1. LOG文件夹在class文件夹目录下
            // LOG_FILE_PATH = new File(Log.class.getClassLoader().getResource(".").getFile()).toString() + "/log/";
            // *** 2. 如果是web项目，LOG文件夹在项目根目录下
            LOG_FILE_PATH = new File(Log.class.getClassLoader().getResource("/").getFile()).toString() + "/../../log/";

            FILE_SIZE = 2000000; // *** 每个文件最大2M
            FILE_MAX = 10; // *** 每天最多生成10个文件,最多不可超过100

            LOG_FILE_LASTNAME = ".log"; // *** 文件后缀名
        }
    }

    /*
     * 以下内容不必更改**********************************************************************************************************
     */
    /* ************************************************************************************************************************* */
    private String getLogFileName(String level) {
        String firtFileName = LOG_FILE_PATH + level + "_" + getDateFormat("yyyy-MM-dd", new Date()) + LOG_FILE_LASTNAME;
        return firtFileName;
    }

    /**
     * 从字符串转换为JDKLOG的LEVEL等级
     *
     * @param strLevel　自定义的等级字符串
     * @return JDKLOG的等级
     */
    private Level getLogLevelFromString(String strLevel) {
        Level l = Level.INFO;

        if (LEVEL_DEBUG.equalsIgnoreCase(strLevel)) {
            l = Level.FINE;
        }

        if (LEVEL_WARN.equalsIgnoreCase(strLevel)) {
            l = Level.WARNING;
        }

        if (LEVEL_ERROR.equalsIgnoreCase(strLevel)) {
            l = Level.SEVERE;
        }

        return l;
    }

    /**
     * 低等级记录
     *
     * @param info 记录信息
     */
    public static void debug(String info) {
        INSTANCE.log(LEVEL_DEBUG, "[" + LEVEL_DEBUG + "] " + info, null);
    }

    /**
     * 普通信息记录
     *
     * @param info 记录信息
     */
    public static void info(String info) {
        INSTANCE.log(LEVEL_INFO, "[" + LEVEL_INFO + "] " + info, null);
    }

    /**
     * 警告信息记录
     *
     * @param info 记录信息
     */
    public static void warn(String info) {
        INSTANCE.log(LEVEL_WARN, "[" + LEVEL_WARN + "] " + info, null);
    }

    /**
     * 错误信息记录
     *
     * @param info 记录信息
     */
    public static void error(String info) {
        INSTANCE.log(LEVEL_ERROR, "[" + LEVEL_ERROR + "] " + info, null);
    }

    /**
     * 错误信息记录
     *
     * @param info 异常
     */
    public static void error(Throwable info) {
        INSTANCE.log(LEVEL_ERROR, "", info);
    }

    /**
     * 处理记录信息
     *
     * @param level　信息等级
     * @param info 　信息内容
     * @param exception 　异常信息
     */
    private void log(String level, String info, Throwable exception) {
        checkFileDate(level);
        Logger logger = Logger.getLogger(level);
        if (!logList.contains(level)) {
            logList.add(level);
            dealHander(logger);
            dealInfoFormat(logger);
        }
        logger.setLevel(logOutPutLevel);
        logger.log(getLogLevelFromString(level), info, exception);

    }

    /**
     * 内容格式化，默认内容格式为　时间 文件名(行数):[等级] 内容
     *
     * @param logger JDK Log
     */
    private void dealInfoFormat(Logger logger) {
        logger.setUseParentHandlers(false);

        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();

        final StackTraceElement element = stackTraceElement[stackTraceElement.length - 1];

        for (Handler handler : logger.getHandlers()) {
            handler.setFormatter(new Formatter() {

                @Override
                public String format(LogRecord record) {
                    StringBuilder formatMessage = new StringBuilder();
                    formatMessage.append(getDateFormat("yyyy-MM-dd HH:mm:ss", new Date()));
                    formatMessage.append(" ").append(element.getFileName()).append("(Line:")
                            .append(element.getLineNumber()).append("):").append(" ")
                            .append(record.getMessage()).append("\n");

                    if (record.getThrown() != null) {
                        try {
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            record.getThrown().printStackTrace(pw);
                            pw.close();
                            formatMessage.append(sw.toString());
                        } catch (Exception ex) {
                        }
                    }

                    return formatMessage.toString();
                }
            });
        }

    }

    /**
     * 输出处理，控制台和文件,文件输出的文件名格式为 等级_yyyy-MM-dd.log 如果有多个文件，会在文件名后递增数字
     *
     * @param logger jdk log
     */
    private void dealHander(Logger logger) {
        boolean alreadyHasConsole = false, alreadyHasFile = false;
        Handler[] handlers = logger.getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            if (handlers[i] instanceof ConsoleHandler) {
                alreadyHasConsole = true;
            } else if (handlers[i] instanceof MyFileHandler) {
                alreadyHasFile = true;
            }
        }

        if (HANDLER_CONSOLE && !alreadyHasConsole) {

            ConsoleHandler ch = new ConsoleHandler();
            ch.setLevel(logOutPutLevel);
            logger.addHandler(ch);
        }

        if (HANDLER_FILE && !alreadyHasFile) {

            String file = getLogFileName(logger.getName());

            File f = new File(LOG_FILE_PATH);
            if (!f.isDirectory()) {
                f.mkdirs();
                f.mkdir();
            }
            try {
                MyFileHandler fh = new MyFileHandler(file, FILE_SIZE, FILE_MAX, true);
                fh.setLevel(logOutPutLevel);
                logger.addHandler(fh);
            } catch (IOException ex) {
                Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * 格式化时间到字符串
     *
     * @param pattern　格式
     * @param date　时间
     * @return 格式化的字符串
     */
    private String getDateFormat(String pattern, Date date) {
        format.applyPattern(pattern);
        return format.format(date);
    }

    /**
     * 设置输出等级
     *
     * @param level 自定义等级,可使用 debug,info,warn,error 忽略大小写
     */
    public final void setLogLevel(String level) {
        logOutPutLevel = getLogLevelFromString(level);
    }

    private void checkFileDate(String level) {
        File f = new File(getLogFileName(level));
        if (!f.exists()) {
            for (Handler handler : Logger.getLogger(level).getHandlers()) {
                handler.close();
            }
            logList.remove(level);
        }
    }

    private static String LOG_FILE_PATH;
    private Level logOutPutLevel;
    private int FILE_SIZE;
    private int FILE_MAX;
    private boolean HANDLER_FILE = false;
    private boolean HANDLER_CONSOLE = false;
    private static ArrayList<String> logList = new ArrayList<String>();
    private String LOG_FILE_LASTNAME;
    private static String LEVEL_DEBUG = "DEBUG";
    private static String LEVEL_INFO = "INFO";
    private static String LEVEL_WARN = "WARN";
    private static String LEVEL_ERROR = "ERROR";
    private static Log INSTANCE = new Log();
    SimpleDateFormat format = new SimpleDateFormat();
}

/**
 * 自定义文件输出管理类，参考JDK FileHandler实现
 *
 * @author DingFengHua
 */
class MyFileHandler extends StreamHandler {

    private MeteredStream meter;
    private boolean append;
    private int limit; // zero => no limit.
    private int count;
    private String pattern;
    private String lockFileName;
    private FileOutputStream lockStream;
    private File files[];
    private static final int MAX_LOCKS = 100;
    private static java.util.HashMap<String, String> locks = new java.util.HashMap<String, String>();

    private class MeteredStream extends OutputStream {

        OutputStream out;
        int written;

        MeteredStream(OutputStream out, int written) {
            this.out = out;
            this.written = written;
        }

        public void write(int b) throws IOException {
            out.write(b);
            written++;
        }

        @Override
        public void write(byte buff[]) throws IOException {
            out.write(buff);
            written += buff.length;
        }

        @Override
        public void write(byte buff[], int off, int len) throws IOException {
            out.write(buff, off, len);
            written += len;
        }

        @Override
        public void flush() throws IOException {
            out.flush();
        }

        @Override
        public void close() throws IOException {
            out.close();
        }
    }

    private void open(File fname, boolean append) throws IOException {
        int len = 0;
        if (append) {
            len = (int) fname.length();
        }
        FileOutputStream fout = new FileOutputStream(fname.toString(), append);
        BufferedOutputStream bout = new BufferedOutputStream(fout);
        meter = new MeteredStream(bout, len);
        setOutputStream(meter);
    }

    public MyFileHandler() throws IOException, SecurityException {
        openFiles();
    }

    public MyFileHandler(String pattern) throws IOException, SecurityException {
        if (pattern.length() < 1) {
            throw new IllegalArgumentException();
        }
        this.pattern = pattern;
        this.limit = 0;
        this.count = 1;
        openFiles();
    }

    public MyFileHandler(String pattern, boolean append) throws IOException, SecurityException {
        if (pattern.length() < 1) {
            throw new IllegalArgumentException();
        }
        this.pattern = pattern;
        this.limit = 0;
        this.count = 1;
        this.append = append;
        openFiles();
    }

    public MyFileHandler(String pattern, int limit, int count) throws IOException, SecurityException {
        if (limit < 0 || count < 1 || pattern.length() < 1) {
            throw new IllegalArgumentException();
        }
        this.pattern = pattern;
        this.limit = limit;
        this.count = count;
        openFiles();
    }

    public MyFileHandler(String pattern, int limit, int count, boolean append) throws IOException, SecurityException {
        if (limit < 0 || count < 1 || pattern.length() < 1) {
            throw new IllegalArgumentException();
        }
        this.pattern = pattern;
        this.limit = limit;
        this.count = count;
        this.append = append;
        openFiles();
    }

    private void openFiles() throws IOException {
        LogManager manager = LogManager.getLogManager();
        manager.checkAccess();
        if (count < 1) {
            throw new IllegalArgumentException("file count = " + count);
        }
        if (limit < 0) {
            limit = 0;
        }

        InitializationErrorManager em = new InitializationErrorManager();
        setErrorManager(em);

        int unique = -1;
        for (;;) {
            unique++;
            if (unique > MAX_LOCKS) {
                throw new IOException("Couldn't get lock for " + pattern);
            }
            lockFileName = generate(pattern, 0, unique).toString() + ".lck";
            synchronized (locks) {
                if (locks.get(lockFileName) != null) {
                    continue;
                }
                FileChannel fc;
                try {
                    lockStream = new FileOutputStream(lockFileName);
                    fc = lockStream.getChannel();
                } catch (IOException ix) {
                    continue;
                }
                try {
                    FileLock fl = fc.tryLock();
                    if (fl == null) {
                        continue;
                    }
                } catch (IOException ix) {
                }
                locks.put(lockFileName, lockFileName);
                break;
            }
        }

        files = new File[count];
        for (int i = 0; i < count; i++) {
            files[i] = generate(pattern, i, unique);
        }
        if (append) {
            open(files[0], true);
        } else {
            rotate();
        }
        Exception ex = em.lastException;
        if (ex != null) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else if (ex instanceof SecurityException) {
                throw (SecurityException) ex;
            } else {
                throw new IOException("Exception: " + ex);
            }
        }
        setErrorManager(new ErrorManager());
    }

    private File generate(String pattern, int generation, int unique) throws IOException {
        File file = null;
        String word = "";
        int ix = 0;
        boolean sawg = false;
        boolean sawu = false;
        while (ix < pattern.length()) {
            char ch = pattern.charAt(ix);
            ix++;
            char ch2 = 0;
            if (ix < pattern.length()) {
                ch2 = Character.toLowerCase(pattern.charAt(ix));
            }
            if (ch == '/') {
                if (file == null) {
                    file = new File(word);
                } else {
                    file = new File(file, word);
                }
                word = "";
                continue;
            } else if (ch == '%') {
                if (ch2 == 't') {
                    String tmpDir = System.getProperty("java.io.tmpdir");
                    if (tmpDir == null) {
                        tmpDir = System.getProperty("user.home");
                    }
                    file = new File(tmpDir);
                    ix++;
                    word = "";
                    continue;
                } else if (ch2 == 'h') {
                    file = new File(System.getProperty("user.home"));
                    if (isSetUID()) {
                        throw new IOException("can't use %h in set UID program");
                    }
                    ix++;
                    word = "";
                    continue;
                } else if (ch2 == 'g') {
                    word = word + generation;
                    sawg = true;
                    ix++;
                    continue;
                } else if (ch2 == 'u') {
                    word = word + unique;
                    sawu = true;
                    ix++;
                    continue;
                } else if (ch2 == '%') {
                    word = word + "%";
                    ix++;
                    continue;
                }
            }
            word = word + ch;
        }
        if (count > 1 && generation > 0 && !sawg) {
            word = word + "." + generation;
        }
        if (unique > 0 && !sawu) {
            word = word + "." + unique;
        }
        if (word.length() > 0) {
            if (file == null) {
                file = new File(word);
            } else {
                file = new File(file, word);
            }
        }
        return file;
    }

    private synchronized void rotate() {
        Level oldLevel = getLevel();
        setLevel(Level.OFF);

        super.close();
        for (int i = count - 2; i >= 0; i--) {
            File f1 = files[i];
            File f2 = files[i + 1];
            if (f1.exists()) {
                if (f2.exists()) {
                    f2.delete();
                }
                f1.renameTo(f2);
            }
        }
        try {
            open(files[0], false);
        } catch (IOException ix) {
            reportError(null, ix, ErrorManager.OPEN_FAILURE);

        }
        setLevel(oldLevel);
    }

    @Override
    public synchronized void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        super.publish(record);
        flush();
        if (limit > 0 && meter.written >= limit) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {

                public Object run() {
                    rotate();
                    return null;
                }
            });
        }
    }

    @Override
    public synchronized void close() throws SecurityException {
        super.close();
        if (lockFileName == null) {
            return;
        }
        try {
            lockStream.close();
        } catch (Exception ex) {
        }
        synchronized (locks) {
            locks.remove(lockFileName);
        }
        new File(lockFileName).delete();
        lockFileName = null;
        lockStream = null;
    }

    private static class InitializationErrorManager extends ErrorManager {

        Exception lastException;

        @Override
        public void error(String msg, Exception ex, int code) {
            lastException = ex;
        }
    }

    private static native boolean isSetUID();

}