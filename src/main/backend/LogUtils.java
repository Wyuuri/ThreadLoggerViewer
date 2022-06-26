package main.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.common.Constants;

public class LogUtils {
	
	/**
	 * @param tracePath - The absolute path where all log files reside.
	 */
	public static List<String> getAllProcessesNumbers(String tracePath) {
		
		//Creating a File object for directory
	    File directoryPath = new File(tracePath);
	      
		//List of all files and directories
	    String contents[] = directoryPath.list();
	      
	    //List of processes numbers
	    List<String> pids = new ArrayList<>();
	    
	    for(int i=0; i<contents.length && contents[i].endsWith(".log") && contents[i].matches(".*[0-9].*"); i++) {
	    	pids.add(getProcessNumber(contents[i]));
	    }
	      
	    return pids;
	}
	
	/**
	 * @param filename - Any log filename
	 * @return Number of the process associated to the log file
	 */
	public static String getProcessNumber(String filename) {
		Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(filename);
        matcher.find();
        // Last number matched from path (NOT WORKING)
        return matcher.group(matcher.groupCount());
	}
	
	/**
	 * @param path - Any path that contains a log filename
	 * @return Number of the process associated to the log file
	 */
	public static String getProcNumberFromPath(String path) {
		File file = new File(path);
		String logFile = file.getName();
		return getProcessNumber(logFile);
	}

	/**
	 * @param line - A sentence from a log file
	 * @return true if the line contains a "spawn" string, 
	 * 		otherwise false
	 */
	public static boolean isSpawn(String line) {
		return line.contains(Constants.SPAWN);
	}
	
	/**
	 * @param line - A sentence from a log file
	 * @return true if the line contains a "send" string, 
	 * 		otherwise false
	 */
	public static boolean isSend(String line) {
		return line.contains(Constants.SEND);
	}
	
	/**
	 * @param line - A sentence from a log file
	 * @return true if the line contains a "receive" string, 
	 * 		otherwise false
	 */
	public static boolean isReceive(String line) {
		return line.contains(Constants.RECEIVE);
	}
	
	/**
	 * @param line - A sentence from a log file
	 * @return true if the line contains a "deliver" string, 
	 * 		otherwise false
	 */
	public static boolean isDeliver(String line) {
		return line.contains(Constants.DELIVER);
	}
}
