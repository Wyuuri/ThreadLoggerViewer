package main.java.com.maria.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogFilesReader {
	
	public LogFilesReader( ) {
		
	}
	
	public int getLaunchProccess(String path) {
		int pidNum = 0;
		
		try {  
			File file=new File(path);    //creates a new file instance  
			FileReader fr = new FileReader(file);   //reads the file  
			BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
			StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
			String line;  
			while((line=br.readLine())!=null) {
				if(line.contains("pid")) {
					Pattern pattern = Pattern.compile("[{](\\S+),");
			        Matcher matcher = pattern.matcher(line);

			        matcher.find();
			        pidNum = Integer.valueOf(line.substring(matcher.end(), line.indexOf("}")));
					sb.append(pidNum);      //appends line to string buffer  
					sb.append("\n");     //line feed 
				}  
			}  
			fr.close();    //closes the stream and release the resources  
			System.out.println("Contents of File: ");  
			System.out.println(sb.toString());   //returns a string that textually represents the object  
		}  
		catch(IOException e) {  
			e.printStackTrace();
		}
		return pidNum;
	}
	
	public void readLineByLine(String path) {
		try {  
			File file=new File(path);    //creates a new file instance  
			FileReader fr=new FileReader(file);   //reads the file  
			BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream  
			StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters  
			String line;  
			while((line=br.readLine())!=null) {  
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
}