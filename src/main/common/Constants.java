package main.common;

public class Constants {

	public final static String UI_PATH = "D:\\GitHub\\ThreadLoggerViewer\\src\\main\\frontend\\";
	public final static String START_FILE = "trace_result.log";
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
	public final static int STARTING_X_COORDINATE = 30;
	public final static int STARTING_Y_COORDINATE = 50;
	public final static int GAP_X_COORDINATE = 120;
	public final static int GAP_Y_COORDINATE = 30;
	public final static int STARTING_Y_TEXT_COORDINATE = 40;
}
