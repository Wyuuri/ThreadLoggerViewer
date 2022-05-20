package main.java.com.maria.common;

public class Constants {

	public final static String UI_PATH = "D:\\GitHub\\ThreadLoggerViewer\\src\\main\\java\\com\\maria\\view\\";
	public final static String PATH = "D:\\trace\\";
	public final static String PID = "pid";
	public final static String SPAWN = "spawn";
	public final static String SEND = "send";
	public final static String DELIVER = "deliver";
	public final static String RECEIVE = "receive";
	public final static String SEND_REGEX = "[{](\\s*)send,";
	public final static String DELIVER_REGEX = "[{](\\s*)deliver,";
	public final static String RECEIVE_REGEX = "[{](\\s*)(\\S*)receive(\\s*)(\\S*),";
	public final static String SPAWNED_REGEX = "[{](\\S*)spawn(\\S*),[{](\\D+),";
	public final static String FILENAME_PREFIX = "trace_";
	public final static String FILE_EXTENSION = ".log";
	public final static String HTML_FILENAME = "view.html";
	public final static String PROCESSES_FILENAME = "processes.txt";
}
