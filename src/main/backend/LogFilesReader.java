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
	
	public static String tracePath;
	
	// process number String --- list of messages (natural order keys)
	private static Map<String,List<String>> sortedMessages = new TreeMap<>();
	
	// send msg number --- process number String
	private static Map<Integer,String> sendMsg = new HashMap<>();
	
	// deliver msg number --- process number String
	private static Map<Integer,String> deliverMsg = new HashMap<>();
	
	// receive msg number --- process number String
	private static Map<Integer,String> receiveMsg = new HashMap<>();
	
	// process number String --- X coordinate
	private static Map<String, Integer> xValues = new TreeMap<>();
	
	public LogFilesReader( ) { }
	
	public int getLaunchProcess(String path) {
		int pidNum = 0;
		
		try {  
			File file=new File(path);    //creates a new file instance  
			FileReader fr = new FileReader(file);   //reads the file  
			BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
			StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
			
			String line;  
			while((line=br.readLine())!=null) {
				
				if (line.contains("pid")) {
					Pattern pattern = Pattern.compile("(\\d+)");
			        Matcher matcher = pattern.matcher(line);

			        matcher.find();
			        pidNum = Integer.valueOf(line.substring(matcher.start(), matcher.end()));
					sb.append(pidNum);      //appends line to string buffer  
					sb.append("\n");     //line feed 
					break;
				}
				  
			}  
			fr.close();    //closes the stream and release the resources  
			System.out.println("Launching process: ");  
			System.out.println(sb.toString());   //returns a string that textually represents the object  
		}  
		catch(IOException e) {  
			e.printStackTrace();
		}
		return pidNum;
	}
	
	public static String getProcessNumber(String path) {
		Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(path);
        matcher.find();
        
        // Last number matched from path
        return matcher.group(matcher.groupCount());
        
		//return path.substring(path.length()-6, path.length()-4);
	}
	
	public static boolean isSpawn(String line) {
		return line.contains(Constants.SPAWN);
	}
	
	public static boolean isSend(String line) {
		return line.contains(Constants.SEND);
	}
	
	public static boolean isReceive(String line) {
		return line.contains(Constants.RECEIVE);
	}
	
	public static boolean isDeliver(String line) {
		return line.contains(Constants.DELIVER);
	}
	
	public static void setTracePath(String tracepath) {
		tracePath = tracepath;
	}
	
	public static void readLineByLine(String path) {
		String processNumber = getProcessNumber(path);
		List<String> myMessages = new ArrayList<>();
		
		try {  
			File file=new File(path);    //creates a new file instance  
			FileReader fr=new FileReader(file);   //reads the file  
			BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream  
			StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters  
			
			Pattern pattern;
			Matcher matcher;
			
			String line;
			int number;
			String filepath;
			while((line=br.readLine())!=null) {
				
				if(isSend(line)) {
					pattern = Pattern.compile(Constants.SEND_REGEX);
			        matcher = pattern.matcher(line);
			        matcher.find();
			        
			        number = Integer.valueOf(line.substring(matcher.end(), line.indexOf("}")));
					sendMsg.put(number, processNumber);
					myMessages.add("send "+ number);
				}
				else if(isDeliver(line)) {
					pattern = Pattern.compile(Constants.DELIVER_REGEX);
			        matcher = pattern.matcher(line);
			        matcher.find();
			        
			        number = Integer.valueOf(line.substring(matcher.end(), line.indexOf("}")));
					deliverMsg.put(number, processNumber);
					myMessages.add("deliver "+ number);
				}
				else if(isReceive(line)) {
					pattern = Pattern.compile(Constants.RECEIVE_REGEX);
			        matcher = pattern.matcher(line);
			        matcher.find();
			        
			        number = Integer.valueOf(line.substring(matcher.end(), line.indexOf("}")));
					receiveMsg.put(number, processNumber);
					myMessages.add("receive "+ number);
				}
				else if(isSpawn(line)) {
					filepath = tracePath + Constants.FILENAME_PREFIX 
							+ getProcessNumber(line) + Constants.FILE_EXTENSION;
					
					readLineByLine(filepath);
				}
				
				sb.append(line);      //appends line to string buffer  
				sb.append("\n");     //line feed   
			}  
			fr.close();    //closes the stream and release the resources  
			System.out.println("Contents of File: ");  
			System.out.println(sb.toString());   //returns a string that textually represents the object  
		}  
		catch(IOException e) {  
			e.printStackTrace();
		}
		
		sortedMessages.put(processNumber, myMessages);
	}

	public static List<String> getAllProcessesNumbers(String path) {
		
		//Creating a File object for directory
	      File directoryPath = new File(path);
	      
		//List of all files and directories
	      String contents[] = directoryPath.list();
	      
	    //List of processes numbers
	      List<String> pids = new ArrayList<>();
	      
	    //System.out.println("List of files and directories in the specified directory:");
	      System.out.println("List of process numbers:");
	    
	    for(int i=0; i<contents.length && contents[i].endsWith(".log") && contents[i].matches(".*[0-9].*"); i++) {
	       System.out.println(contents[i]);
	       
	       System.out.println(getProcessNumber(contents[i]));
	       
	       pids.add(getProcessNumber(contents[i]));
	    }
	      
	      return pids;
	}
	
	public static Map<String, Integer> Xvalues() {
		List<String> pids = getAllProcessesNumbers(tracePath);
		int x = Constants.STARTING_X_COORDINATE;
		for(String pid : pids) {
			xValues.put(pid, x);
			x += Constants.GAP_X_COORDINATE;
		}
		return xValues;
	}
	
	
	public static int numberOfProcesses() {
		List<String> processes = getAllProcessesNumbers(tracePath);
		return processes.size();
	}
	
	public static Map<Integer,String> getSendMsg( ) {
		for (Integer name: sendMsg.keySet()) {
		    String key = name.toString();
		    String value = sendMsg.get(name).toString();
		    System.out.println(key + " " + value);
		}
		return sendMsg;
	}
	
	public static Map<Integer,String> getDeliverMsg( ) {
		for (Integer name: deliverMsg.keySet()) {
		    String key = name.toString();
		    String value = deliverMsg.get(name).toString();
		    System.out.println(key + " " + value);
		}
		return deliverMsg;
	}
	
	public static Map<Integer,String> getReceiveMsg( ) {
		for (Integer name: receiveMsg.keySet()) {
		    String key = name.toString();
		    String value = receiveMsg.get(name).toString();
		    System.out.println("" + key + " " + value);
		}
		return receiveMsg;
	}

	public static Map<String, List<String>> getSortedMessages() {
		return sortedMessages;
	}
}