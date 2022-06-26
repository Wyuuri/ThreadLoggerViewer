package main.common;

import java.io.File;

public class Constants {

	public final static String START_FILE = "trace_result.log";
	public final static String FILENAME_PREFIX = "trace_";
	public final static String FILE_EXTENSION = ".log";
	
	public final static String HTML_FILEPATH = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "view.html";
	public final static String CSS_FILEPATH = "file:" + System.getProperty("user.dir").replace("\\", "/") + "/resources/button.css"; 
	public final static String LOGO_PATH = "file:" + System.getProperty("user.dir").replace("\\", "/") + "/resources/icon.jpg";

	public final static String PID = "pid";
	public final static String SPAWN = "spawn";
	public final static String SEND = "send";
	public final static String DELIVER = "deliver";
	public final static String DELIVERY = "delivery";
	public final static String RECEIVE = "receive";
	public final static String SEND_REGEX = "[{](\\s*)(\\S*)send(\\S*)(\\s*),(\\s*)";
	public final static String DELIVER_REGEX = "[{](\\s*)(\\S*)deliver(\\S*)(\\s*),(\\s*)";
	public final static String RECEIVE_REGEX = "[{](\\s*)(\\S*)receive(\\S*)(\\s*),(\\s*)";
	public final static String SPAWNED_REGEX = "[{](\\s*)(\\S*)spawn(\\S*)(\\s*),(\\s*)[{](\\s*)(\\D+)(\\s*),(\\s*)";
}
