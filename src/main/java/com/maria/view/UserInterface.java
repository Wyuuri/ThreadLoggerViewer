package main.java.com.maria.view;

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
import main.java.com.maria.service.LogFilesReader;

public class UserInterface {
	
	private final static String htmlFilepath = Constants.UI_PATH + Constants.HTML_FILENAME;
	private final static Map<Integer,String> sendMsg = LogFilesReader.getSendMsg();
	private final static Map<Integer,String> deliverMsg = LogFilesReader.getDeliverMsg();
	private final static Map<Integer,String> receiveMsg = LogFilesReader.getReceiveMsg();
	private final static Map<String, Integer> xValues =  LogFilesReader.Xvalues();
	private final static Map<String,List<String>> sortedPoints = LogFilesReader.getSortedPoints();
	private static Map<String, List<Integer>> takenYvalues = new HashMap<>();
	private static Map<String, String> receivePreviousMsg = new HashMap<>();
	private static Map<String, String> receiveNextMsg = new HashMap<>();
	
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
					msgLines = drawArrows(pids);
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
		String height = "100%"; // same height that the SVG wrapper
		
		String res = "";
		for(int i = 0; i < size; i++) {
			res += "<line class=\"dash\" x1=\""+ x +"\" y1=\""+ y1 +"\" x2=\""+ x +"\" y2=\""+ y2 +"\" stroke-dasharray=\"4, 5\"/>\r\n"
				+ "<rect class=\"dash\" x=\""+ rectX +"\" y=\""+ y2 +"\" rx=\"5\" ry=\"5\" height=\""+ height +"\"/>\r\n";
				
			x += 120;
			rectX += 120;
		}
		return res;
	}
	
	private static Map<String, String> fillMsgList() {
		boolean isNext = false;
		String previousMsg = "";
		String receiveMsg = "";
		for(String process : sortedPoints.keySet()) {
			for(String msg : sortedPoints.get(process)) {
				if(isNext) {
					receiveNextMsg.put(msg, receiveMsg);
					isNext = false;
				}
				if(msg.contains("receive")) {
					receiveMsg = msg;
					receivePreviousMsg.put(previousMsg, msg);
					isNext = true;
				}
				previousMsg = msg;
			}
		}
		printMsgList();
		return receivePreviousMsg;
	}
	
	public static void printMsgList( ) {
		for (String prevMsg: receivePreviousMsg.keySet()) {
		    String msg = receivePreviousMsg.get(prevMsg).toString();
		    System.out.println(prevMsg + " " + msg);
		}
	}
	
	public static String drawReceivePoint(int x1, int y1, int xText, int  yText, String msg) {
		return "<circle style=\"fill:none;stroke:#010101;stroke-width:1.6871;stroke-miterlimit:10;\" cx=\""+ x1 +"\" cy=\""+ y1 +"\" r=\"5\"></circle>"
			+ "<text font-size=\"10\" x=\""+ xText +"\" y=\""+ yText +"\" text-anchor=\"start\" stroke=\"red\" stroke-width=\"1px\" dy=\"1px\">" + msg + "</text>";
	}
	
	public static String drawArrows(List<String> pids) {
		fillMsgList();
		
		String res = "";
		String processNumber;
		String receiverProcess;
		String message, receiveMessage;
		boolean isNext = false;
		int exp;
		int x1, x2, y1 = Constants.STARTING_Y_COORDINATE, y2 = Constants.STARTING_Y_COORDINATE;
		int xText, yText = Constants.STARTING_Y_TEXT_COORDINATE;
		int auxY1, auxY2, auxYText;
		for(int msgNumber = 0; msgNumber < LogFilesReader.lastMessageNumber; msgNumber++) {
			processNumber = sendMsg.get(msgNumber);
			receiverProcess = deliverMsg.get(msgNumber);
			exp = processNumber.compareTo(receiverProcess);
			x1 = xValues.get(processNumber);
			if(exp < 0) { x2 = xValues.get(receiverProcess) - 30; }
			else { x2 = xValues.get(receiverProcess) + 10; }
			
			res += "<line class=\"msg\" x1=\""+ x1 +"\" y1=\""+ y1 +"\" x2=\""+ x2 +"\" y2=\""+ y2 +"\" marker-end=\"url(#arrowhead)\" />";
			
			xText = x1 + 5;
			message = "send "+msgNumber;
			res += drawMsg(x1, y1, xText, yText, message);
			if(receivePreviousMsg.containsKey(message)) {
				auxY1 = y1 + 30;
				auxYText = yText + 30;
				receiveMessage = receivePreviousMsg.get(message);
				res += drawReceivePoint(x1, auxY1, xText, auxYText, receiveMessage);
				//TODO
				res += drawReceivePoint(x1, auxY1+30, xText, auxYText+30, receivePreviousMsg.get(receiveMessage));
				isNext = true;
			}
			if(isNext) {
				/*y1 += 30;
				yText += 30;*/
				isNext = false;
			}
			
			xText = x2 + 5;
			message = "deliver "+msgNumber;
			res += drawMsg(x2, y2, xText, yText, message);
			if(receivePreviousMsg.containsKey(message)) {
				auxY2 = y2 + 30;
				auxYText = yText + 30;
				receiveMessage = receivePreviousMsg.get(message);
				res += drawReceivePoint(x2, auxY2, xText, auxYText, receiveMessage);
				//TODO
				res += drawReceivePoint(x1, auxY2+30, xText, auxYText+30, receivePreviousMsg.get(receiveMessage));
				isNext = true;
			}
			if(isNext) {
				/*y2 += 30;
				yText += 30;*/
				isNext = false;
			}
			
			y1 += 30;
			y2 += 30;
			yText += 30;
		}
		return res;
	}

	public static String drawMsg(int x1, int y1, int xText, int yText, String msg) {
		String res = "";
		
		res = "<circle style=\"fill:none;stroke:#010101;stroke-width:1.6871;stroke-miterlimit:10;\" cx=\""+ x1 +"\" cy=\""+ y1 +"\" r=\"2\"></circle>"
		    + "<text font-size=\"10\" x=\""+ xText +"\" y=\""+ yText +"\" text-anchor=\"start\" stroke=\"red\" stroke-width=\"1px\" dy=\"1px\">" + msg + "</text>";

		return res;
	}
	
	private static Integer getMsgNumber(String message) {
		Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(message);
        matcher.find();
        String msgNumber = message.substring(matcher.start(), matcher.end());
        return Integer.valueOf(msgNumber);
	}
}
