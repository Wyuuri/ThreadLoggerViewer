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
			String receivePoints = "";
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
					receivePoints = drawReceivePoints();
					sb.append(receivePoints);
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
	
	public static String drawReceivePoints() {
		String res = "";
		Integer yTimes = 0;
		String receiverProcess;
		int x1, y1, xText, yText;
		for(String process : sortedPoints.keySet()) {
			for(String msg : sortedPoints.get(process)) {
				if(msg.contains("receive")) {
					yText = Constants.STARTING_Y_TEXT_COORDINATE + + yTimes * Constants.GAP_Y_COORDINATE;
					y1 = Constants.STARTING_Y_COORDINATE + yTimes * Constants.GAP_Y_COORDINATE;
					List<Integer> yReceive = new ArrayList<>();
					yReceive.add(y1);
					takenYvalues.put(process, yReceive);
					
					receiverProcess = deliverMsg.get(getMsgNumber(msg));
					x1 = xValues.get(receiverProcess);
					xText = x1 + 5;
					res += "<circle style=\"fill:none;stroke:#010101;stroke-width:1.6871;stroke-miterlimit:10;\" cx=\""+ x1 +"\" cy=\""+ y1 +"\" r=\"5\"></circle>"
						+ "<text font-size=\"10\" x=\""+ xText +"\" y=\""+ yText +"\" text-anchor=\"start\" stroke=\"red\" stroke-width=\"1px\" dy=\"1px\">" + msg + "</text>";

				}
				yTimes++;
			}
		}
		
		return res;
	}
	
	public static String drawArrows(List<String> pids) {
		String arrowRight = "<line class=\"msg\" x1=\"30\" y1=\"50\" x2=\"120\" y2=\"50\" \r\n"
				+ "            marker-end=\"url(#arrowhead)\" />\r\n";
		
		String arrowLeft = "<line class=\"msg\" x1=\"130\" y1=\"150\" x2=\"40\" y2=\"150\" \r\n"
				+ "            marker-end=\"url(#arrowhead)\" />";
		
		String res = "";
		String processNumber;
		String receiverProcess;
		int exp;
		int x1, x2, y1 = 50, y2 = 50;
		int xText, yText = Constants.STARTING_Y_TEXT_COORDINATE;
		for(int msgNumber = 0; msgNumber < LogFilesReader.lastMessageNumber; msgNumber++) {
			processNumber = sendMsg.get(msgNumber);
			receiverProcess = deliverMsg.get(msgNumber);
			exp = processNumber.compareTo(receiverProcess);
			x1 = xValues.get(processNumber);
			if(exp < 0) { x2 = xValues.get(receiverProcess) - 30; }
			else { x2 = xValues.get(receiverProcess) + 10; }
			
			res += "<line class=\"msg\" x1=\""+ x1 +"\" y1=\""+ y1 +"\" x2=\""+ x2 +"\" y2=\""+ y2 +"\" marker-end=\"url(#arrowhead)\" />";
			
			xText = x1 + 5;
			res += drawMsg(x1, y1, xText, yText, "send "+msgNumber);
			
			xText = x2 + 5;
			res += drawMsg(x2, y2, xText, yText, "deliver "+msgNumber);
			
			y1 += 30;
			y2 += 30;
			yText += 30;
			
			if(!takenYvalues.containsKey(processNumber)) {
				List<Integer> y = new ArrayList<>();
				y.add(y1);
				takenYvalues.put(processNumber, y);
			} else {
				takenYvalues.get(processNumber).add(y1);
			}
			
			if(!takenYvalues.containsKey(receiverProcess)) {
				List<Integer> y = new ArrayList<>();
				y.add(y2);
				takenYvalues.put(receiverProcess, y);
			} else {
				takenYvalues.get(receiverProcess).add(y2);
			}
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
