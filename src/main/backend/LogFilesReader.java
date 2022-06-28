package main.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import main.common.Constants;

public class LogFilesReader {
	
	private static String tracePath;
	
	private static final Pattern sendPattern = Pattern.compile(Constants.SEND_REGEX);
	private static final Pattern deliverPattern = Pattern.compile(Constants.DELIVER_REGEX);
	private static final Pattern receivePattern = Pattern.compile(Constants.RECEIVE_REGEX);
	
	private static Map<String,List<String>> sortedEvents = new TreeMap<>();
	private static Map<Integer,String> sendEvents = new HashMap<>();
	private static Map<Integer,String> deliverEvents = new HashMap<>();
	private static Map<Integer,String> receiveEvents = new HashMap<>();
	
	/**
	 * @param tracepath - The absolute path where all log files reside.
	 */
	public static void updateTracePath(String tracepath) {
		tracePath = tracepath;
	}
	
	/**
	 * Cleans the maps of each app execution
	 */
	public static void cleanMaps() {
		LogFilesReader.sortedEvents.clear();
		LogFilesReader.sendEvents.clear();
		LogFilesReader.deliverEvents.clear();
		LogFilesReader.receiveEvents.clear();
	}
	
	/**
	 * Reads all log files recursively starting from the process' log file given. 
	 * Every time a process spawns, this method is called and reads it.
	 * 
	 * Puts all send messages in the sendMsg HashMap
	 * Puts all deliver messages in the deliverMsg HashMap
	 * Puts all receive messages in the receiveMsg HashMap
	 * Puts all messages in the sortedMessages TreeMap
	 * 
	 * @param path - The launch process' log file
	 * @throws IOException 
	 */
	public static void readLineByLine(String path) throws IOException {
		
		String processNumber = LogUtils.getProcNumberFromPath(path);
		List<String> myMessages = new ArrayList<>();
		 
		File file=new File(path); 
		FileReader fr=new FileReader(file);   //reads the file  
		BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream  
		// StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters  
		
		Matcher matcher;
		
		String line;
		int number;
		String filepath;
		while((line=br.readLine())!=null) {
			
			if(LogUtils.isSend(line)) {
		        matcher = sendPattern.matcher(line);
		        matcher.find();
		        
		        number = Integer.valueOf(line.substring(matcher.end(), line.indexOf("}")));
				sendEvents.put(number, processNumber);
				myMessages.add(Constants.SEND +" "+ number);
			}
			else if(LogUtils.isDeliver(line)) {
		        matcher = deliverPattern.matcher(line);
		        matcher.find();
		        
		        number = Integer.valueOf(line.substring(matcher.end(), line.indexOf("}")));
				deliverEvents.put(number, processNumber);
				myMessages.add(Constants.DELIVER +" "+ number);
			}
			else if(LogUtils.isReceive(line)) {
		        matcher = receivePattern.matcher(line);
		        matcher.find();
		        
		        number = Integer.valueOf(line.substring(matcher.end(), line.indexOf("}")));
				receiveEvents.put(number, processNumber);
				myMessages.add(Constants.RECEIVE+" "+ number);
			}
			else if(LogUtils.isSpawn(line)) {
				filepath = tracePath + Constants.FILENAME_PREFIX 
						+ LogUtils.getProcessNumber(line) + Constants.FILE_EXTENSION;
				
				readLineByLine(filepath);
			}
			
			// Optional: Print each log file content
			/* sb.append(line);
			   sb.append("\n"); */
		}  
		fr.close();    //closes the stream and release the resources  
		/* System.out.println("Contents of File: ");  
		   System.out.println(sb.toString());  */  
	
		sortedEvents.put(processNumber, myMessages);
	}
	
	/**
	 * @param path - The log file that contains the launch process number.
	 * @return The launch process number.
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static int getLaunchProcess(String path) throws NumberFormatException, IOException {
		int pidNum = 0;
		
		File file=new File(path); 
		FileReader fr = new FileReader(file);   //reads the file  
		BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream 
			
		String line;  
		while((line=br.readLine())!=null) {
				
			if (line.contains("pid")) {
				Pattern pattern = Pattern.compile("(\\d+)");
			    Matcher matcher = pattern.matcher(line);
			    matcher.find();
			        
			    pidNum = Integer.valueOf(line.substring(matcher.start(), matcher.end()));
			    break;
			}
		}  
		fr.close();
			
		// Optional: Print the launch process
		System.out.println("LAUNCH PROCESS: " + pidNum + "\n");
		
		return pidNum;
	}
	
	/**
	 * @return key: send message number
	 * 		 value: process number (String) that contains this message
	 */
	public static Map<Integer,String> getSendEvents( ) {
		return sendEvents;
	}
	
	/**
	 * @return key: deliver message number
	 * 		 value: process number (String) that contains this message
	 */
	
	public static Map<Integer,String> getDeliverEvents( ) {
		return deliverEvents;
	}
	
	/**
	 * @return key: receive message number
	 * 		 value: process number (String) that contains this message
	 */
	
	public static Map<Integer,String> getReceiveEvents( ) {
		return receiveEvents;
	}
	
	/**
	 * @return key: process number (String) (natural order keys)
	 * 		 value: List of messages of this process
	 */

	public static Map<String, List<String>> getSortedEvents() {
		return sortedEvents;
	}
}