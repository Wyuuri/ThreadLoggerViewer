package main.java.com.maria.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.com.maria.common.Constants;

public class LogFilesReader {
	
	List<String> sortedProcessing = new ArrayList<>();
	
	//index 0 --- {send,0}. --- process number String
	Map<Integer,String> sendMsg = new HashMap<>();
	
	//index 0 --- {deliver,0}.
	Map<Integer,String> deliverMsg = new HashMap<>();
	
	//index 0 --- {receive,0}.
	Map<Integer,String> receiveMsg = new HashMap<>();
	
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
	
	public int getSpawnedProcess(String path) {
		int pidNum = 0;
		
		try {  
			File file=new File(path);    //creates a new file instance  
			FileReader fr = new FileReader(file);   //reads the file  
			BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
			StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
			
			String line;  
			while((line=br.readLine())!=null && spawn(line)) {
					Pattern pattern = Pattern.compile("(\\d+)");
			        Matcher matcher = pattern.matcher(line);

			        matcher.find();
			        pidNum = Integer.valueOf(line.substring(matcher.start(), matcher.end()));
					sb.append(pidNum);      //appends line to string buffer  
					sb.append("\n");     //line feed 
					break;
				
			}  
			fr.close();    //closes the stream and release the resources  
			System.out.println("Spawned proccess: ");  
			System.out.println(sb.toString());   //returns a string that textually represents the object  
		}  
		catch(IOException e) {  
			e.printStackTrace();
		}
		
		return pidNum;
	}
	
	public static boolean spawn(String line) {
		return line.contains(Constants.SPAWN);
	}
	
	public static boolean send(String line) {
		return line.contains(Constants.SEND);
	}
	
	public static boolean receive(String line) {
		return line.contains(Constants.RECEIVE);
	}
	
	public static boolean deliver(String line) {
		return line.contains(Constants.DELIVER);
	}
	
	public String getProcessNumber(String path) {
		return path.substring(path.length()-6, path.length()-4);
	}
	
	public void readLineByLine(String path) {
		try {  
			File file=new File(path);    //creates a new file instance  
			FileReader fr=new FileReader(file);   //reads the file  
			BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream  
			StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters  
			
			String processNumber = getProcessNumber(path);
			Pattern pattern;
			Matcher matcher;
			
			String line;
			int number;
			while((line=br.readLine())!=null) {
				
				if(send(line)) {
					// TODO: number --- extract number from {send,number}.
					pattern = Pattern.compile("[{](\\s*)send,");
			        matcher = pattern.matcher(line);
			        matcher.find();
			        
			        number = Integer.valueOf(line.substring(matcher.end(), line.indexOf("}")));
					sendMsg.put(number, processNumber);
				}
				else if(deliver(line)) {
					pattern = Pattern.compile("[{](\\s*)deliver,");
			        matcher = pattern.matcher(line);
			        matcher.find();
			        
			        number = Integer.valueOf(line.substring(matcher.end(), line.indexOf("}")));
					deliverMsg.put(number, processNumber);
				}
				else if(receive(line)) {
					pattern = Pattern.compile("[{](\\s*)(\\S*)receive(\\s*)(\\S*),");
			        matcher = pattern.matcher(line);
			        matcher.find();
			        
			        number = Integer.valueOf(line.substring(matcher.end(), line.indexOf("}")));
					receiveMsg.put(number, processNumber);
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
	}

	public String[] getLogFiles(String path) {
		
		//Creating a File object for directory
	      File directoryPath = new File(path);
	      
		//List of all files and directories
	      String contents[] = directoryPath.list();
	      
	      System.out.println("List of files and directories in the specified directory:");
	      
	      for(int i=0; i<contents.length && contents[i].endsWith(".log"); i++) {
	         System.out.println(contents[i]);
	      }
	      
	      return contents;
	}

	public void getSortedProcessing( ) {
		for(int i=0; i<sortedProcessing.size(); i++) {
			System.out.println(sortedProcessing.get(i));
		}
	}
	
	public void getSendMsg( ) {
		for(int i=0; i<sendMsg.size(); i++) {
			System.out.println(sendMsg.get(i));
		}
	}
	
	public void getDeliverMsg( ) {
		for(int i=0; i<deliverMsg.size(); i++) {
			System.out.println(deliverMsg.get(i));
		}
	}
	
	public void getReceiveMsg( ) {
		for (Integer name: receiveMsg.keySet()) {
		    String key = name.toString();
		    String value = receiveMsg.get(name).toString();
		    System.out.println(key + " " + value);
		}
	}
}