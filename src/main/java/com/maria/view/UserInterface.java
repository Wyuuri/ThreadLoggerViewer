package main.java.com.maria.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import main.java.com.maria.common.Constants;
import main.java.com.maria.service.LogFilesReader;

public class UserInterface {
	
	private final static String htmlFilepath = Constants.UI_PATH + Constants.HTML_FILENAME;
	
	public static String readHTMLFile_andBeautify() {
		String res = "";
		
		try {  
			File file=new File(htmlFilepath);    //creates a new file instance  
			FileReader fr=new FileReader(file);   //reads the file  
			BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream  
			StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters  
		
			List<String> pids = LogFilesReader.getAllProcessesNumbers(Constants.PATH);
			
			String processes = "";
			String historyLines = "";
			String msgLines = "";
			String line;
			while((line=br.readLine())!=null) {
				if(line.trim().equals("toBeChanged")) {
					for(String pid: pids) {
						processes += "<div class=\"process-style\">" + pid + "</div>";
					}
					sb.append(processes);
					continue;
				} else if(line.trim().equals("toBeChanged2")) {
					historyLines = drawHistoryLines(pids.size());
					sb.append(historyLines);
					continue;
				} else if(line.trim().equals("toBeChanged3")) {
					msgLines = drawMsg();
					sb.append(msgLines);
					continue;
				}
				sb.append(line);
			}  
			fr.close();
			res = sb.toString();
			System.out.println(res);  
		}  
		catch(IOException e) {  
			e.printStackTrace();
		}
		
		return res;
	}

	public static String drawHistoryLines(int size) {
		int x = 20;
		int rectX = 10;
		int y1 = 0;
		int y2 = 30; // when process history starts, increase 30 by 30
		int height = 300; // changeable
		
		String res = "";
		for(int i = 0; i < size; i++) {
			res += "<line class=\"dash\" x1=\""+ x +"\" y1=\""+ y1 +"\" x2=\""+ x +"\" y2=\""+ y2 +"\" stroke-dasharray=\"4, 5\"/>\r\n"
				+ "<rect class=\"dash\" x=\""+ rectX +"\" y=\""+ y2 +"\" rx=\"5\" ry=\"5\" height=\""+ height +"\"/>\r\n";
				
			x += 120;
			rectX += 120;
		}
		return res;
	}
	
	public static String drawMsg() {
		String res = "<!-- ARROW TO THE RIGHT -->\r\n"
				+ "        <line class=\"msg\" x1=\"30\" y1=\"50\" x2=\"120\" y2=\"50\" \r\n"
				+ "            marker-end=\"url(#arrowhead)\" />\r\n"
				+ "        \r\n"
				+ "        <!-- SELF ARROW -->\r\n"
				+ "        <line class=\"msg\" x1=\"30\" y1=\"90\" x2=\"60\" y2=\"90\"/>\r\n"
				+ "        <line class=\"msg\" x1=\"60\" y1=\"90\" x2=\"60\" y2=\"110\"/>\r\n"
				+ "        <line class=\"msg\" x1=\"60\" y1=\"110\" x2=\"40\" y2=\"110\" \r\n"
				+ "            marker-end=\"url(#arrowhead)\" />\r\n"
				+ "        \r\n"
				+ "    \r\n"
				+ "        <!-- ARROW TO THE LEFT -->\r\n"
				+ "        <line class=\"msg\" x1=\"130\" y1=\"150\" x2=\"40\" y2=\"150\" \r\n"
				+ "            marker-end=\"url(#arrowhead)\" />";
		
		Map<Integer,String> sendMsg = LogFilesReader.getSendMsg();
		String processNumber = sendMsg.get(0).toString(); // 88
		
		System.out.println(processNumber);
		return res;
	}
}
