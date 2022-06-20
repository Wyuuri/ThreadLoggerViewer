package main.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.common.Constants;

public class LogFilesReader {
	
	private static String tracePath;
	
	private static Map<String,List<String>> sortedMessages = new TreeMap<>();
	private static Map<Integer,String> sendMsg = new HashMap<>();
	private static Map<Integer,String> deliverMsg = new HashMap<>();
	private static Map<Integer,String> receiveMsg = new HashMap<>();
	
	/**
	 * @param tracepath - The absolute path where all log files reside.
	 */
	public static void updateTracePath(String tracepath) {
		tracePath = tracepath;
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
	 */
	public static void readLineByLine(String path) {
		String processNumber = LogUtils.getProcessNumber(path);
		List<String> myMessages = new ArrayList<>();
		
		try {  
			File file=new File(path); 
			FileReader fr=new FileReader(file);   //reads the file  
			BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream  
			// StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters  
			
			Pattern pattern;
			Matcher matcher;
			
			String line;
			int number;
			String filepath;
			while((line=br.readLine())!=null) {
				
				if(LogUtils.isSend(line)) {
					pattern = Pattern.compile(Constants.SEND_REGEX);
			        matcher = pattern.matcher(line);
			        matcher.find();
			        
			        number = Integer.valueOf(line.substring(matcher.end(), line.indexOf("}")));
					sendMsg.put(number, processNumber);
					myMessages.add("send "+ number);
				}
				else if(LogUtils.isDeliver(line)) {
					pattern = Pattern.compile(Constants.DELIVER_REGEX);
			        matcher = pattern.matcher(line);
			        matcher.find();
			        
			        number = Integer.valueOf(line.substring(matcher.end(), line.indexOf("}")));
					deliverMsg.put(number, processNumber);
					myMessages.add("deliver "+ number);
				}
				else if(LogUtils.isReceive(line)) {
					pattern = Pattern.compile(Constants.RECEIVE_REGEX);
			        matcher = pattern.matcher(line);
			        matcher.find();
			        
			        number = Integer.valueOf(line.substring(matcher.end(), line.indexOf("}")));
					receiveMsg.put(number, processNumber);
					myMessages.add("receive "+ number);
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
		}  
		catch(IOException e) {  
			e.printStackTrace();
		}
		
		sortedMessages.put(processNumber, myMessages);
	}
	
	/**
	 * @param path - The log file that contains the launch process number.
	 * @return The launch process number.
	 */
	public static int getLaunchProcess(String path) {
		int pidNum = 0;
		
		try {  
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
		}  
		catch(IOException e) {  
			e.printStackTrace();
		}
		return pidNum;
	}
	
	/**
	 * @return key: send message number
	 * 		 value: process number (String) that contains this message
	 */
	public static Map<Integer,String> getSendMsg( ) {
		return sendMsg;
	}
	
	/**
	 * @return key: deliver message number
	 * 		 value: process number (String) that contains this message
	 */
	
	public static Map<Integer,String> getDeliverMsg( ) {
		return deliverMsg;
	}
	
	/**
	 * @return key: receive message number
	 * 		 value: process number (String) that contains this message
	 */
	
	public static Map<Integer,String> getReceiveMsg( ) {
		return receiveMsg;
	}
	
	/**
	 * @return key: process number (String) (natural order keys)
	 * 		 value: List of messages of this process
	 */

	public static Map<String, List<String>> getSortedMessages() {
		return sortedMessages;
	}
}