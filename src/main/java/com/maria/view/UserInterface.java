package main.java.com.maria.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.com.maria.common.Constants;
import main.java.com.maria.service.LogFilesReader;

public class UserInterface {
	
	private final static String htmlFilepath = Constants.UI_PATH + Constants.HTML_FILENAME;
	private final static Map<Integer,String> sendMsg = LogFilesReader.getSendMsg();
	private final static Map<Integer,String> deliverMsg = LogFilesReader.getDeliverMsg();
	private final static Map<Integer,String> receiveMsg = LogFilesReader.getReceiveMsg();
	private final static Map<String, Integer> xValues =  LogFilesReader.Xvalues();
	
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
					msgLines = drawMsg(pids);
					sb.append(msgLines);
					continue;
				}
				sb.append(line);
			}  
			fr.close();
			res = sb.toString();
			//System.out.println(res);  
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
		int height = 300; // same height that the SVG wrapper
		
		String res = "";
		for(int i = 0; i < size; i++) {
			res += "<line class=\"dash\" x1=\""+ x +"\" y1=\""+ y1 +"\" x2=\""+ x +"\" y2=\""+ y2 +"\" stroke-dasharray=\"4, 5\"/>\r\n"
				+ "<rect class=\"dash\" x=\""+ rectX +"\" y=\""+ y2 +"\" rx=\"5\" ry=\"5\" height=\""+ height +"\"/>\r\n";
				
			x += 120;
			rectX += 120;
		}
		return res;
	}
	
	public static String drawMsg(List<String> pids) {
		String res = "";
		
		int lastMsg = LogFilesReader.lastMessageNumber;
		for(int i = 0; i < lastMsg; i++) {
			
		}
		
		int xText = 35;
		int yText;
		int x1; // = Constants.STARTING_X_COORDINATE;
		int x2 = 120;
		int y1;
		int y2 = 50;
		int y = 50; // += 30
		
		Map<String, List<String>> sortedPoints = LogFilesReader.getSortedPoints();
		for (String process: sortedPoints.keySet()) {
		    List<String> messages = sortedPoints.get(process);
		    
		    yText = 40;
		    y1 = 50;
		    
		    x1 = xValues.get(process);
		    for (String msg: messages) {
		    	// TODO: Trying to make circles correspond to their arrows Y coordinate
		    	switch (msg) {
		    		case "send": 	x2 = xValues.get(sendMsg.get(getMsgNumber(msg))); break;
		    		case "deliver": x2 = xValues.get(deliverMsg.get(getMsgNumber(msg))); break;
		    		case "receive": x2 = xValues.get(receiveMsg.get(getMsgNumber(msg))); break;
		    	}
		    	
		    	res += "<circle style=\"fill:none;stroke:#010101;stroke-width:1.6871;stroke-miterlimit:10;\" cx=\""+ x1 +"\" cy=\""+ y1 +"\" r=\"5\"></circle>"
		    		+ "<text font-size=\"10\" x=\""+ xText +"\" y=\""+ yText +"\" text-anchor=\"start\" stroke=\"red\" stroke-width=\"1px\" dy=\"1px\">" + msg + "</text>";
		    
		    	yText += 30;
		    	y1 += 30; 
		    }
		    System.out.println("Process " + process + " and my messages:\n" + messages + "\n");
		    //x1 += 120; 
		    x2 += 120; xText += 120;
		}
		res += drawArrows(pids);
		return res;
	}
	
	public static String drawArrows(List<String> pids) {
		String arrowRight = "<line class=\"msg\" x1=\"30\" y1=\"50\" x2=\"120\" y2=\"50\" \r\n"
				+ "            marker-end=\"url(#arrowhead)\" />\r\n";
		
		String arrowLeft = "<line class=\"msg\" x1=\"130\" y1=\"150\" x2=\"40\" y2=\"150\" \r\n"
				+ "            marker-end=\"url(#arrowhead)\" />";
		
		String res = "";
		String processNumber;
		int x1, x2, y = 50;
		for(int msgNumber = 0; msgNumber < LogFilesReader.lastMessageNumber; msgNumber++) {
			processNumber = sendMsg.get(msgNumber);
			x1 = xValues.get(processNumber);
			x2 = xValues.get(deliverMsg.get(msgNumber));
			res += "<line class=\"msg\" x1=\""+ x1 +"\" y1=\""+ y +"\" x2=\""+ x2 +"\" y2=\""+ y +"\" marker-end=\"url(#arrowhead)\" />";
			y += 30;
		}
		return res;
	}

	private static Integer getMsgNumber(String message) {
		Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(message);
        matcher.find();
        return Integer.valueOf(message.substring(matcher.start(), matcher.end()));
	}
	
	private static String getMsgString(String message) {
		Pattern pattern = Pattern.compile("(\\S+)");
        Matcher matcher = pattern.matcher(message);
        matcher.find();
        return message.substring(matcher.start(), matcher.end());
	}
}
