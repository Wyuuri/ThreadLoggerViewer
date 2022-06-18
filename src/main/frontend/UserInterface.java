package main.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.backend.LogFilesReader;
import main.common.Constants;
import main.common.StyleUtils;

public class UserInterface {
	
	private final static Map<Integer,String> sendMsg = LogFilesReader.getSendMsg();
	private final static Map<Integer,String> deliverMsg = LogFilesReader.getDeliverMsg();
	private final static Map<Integer,String> receiveMsg = LogFilesReader.getReceiveMsg();
	private final static Map<String,List<String>> sortedMessages = LogFilesReader.getSortedMessages();
	private static Map<String, Integer> lastY = new HashMap<>();
	private static Map<String, Boolean> waitingProcess = new HashMap<>();
	private static Map<String, Integer> processMsgPointer = new HashMap<>();
	// process number String --- X coordinate
	private static Map<String, Integer> xCoordinates = new TreeMap<>();
	private static Map<String, List<Map<String, Integer>>> yCoordinates = new TreeMap<>();
	private static int maxX, maxY = -1, dashed_rectangle_height;
	
	public static String readHTMLFile_andBeautify(String tracePath) {
		String res = "";
		
		try {  
			URL url = UserInterface.class.getResource(Constants.HTML_FILENAME);
			File file = new File(url.getPath());    //creates a new file instance  
			FileReader fr = new FileReader(file);   //reads the file  
			BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
			StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
		
			List<String> pids = LogFilesReader.getAllProcessesNumbers(tracePath);
			fillXCoordinates(pids); // maxX is calculated in this method
			fillYCoordinates(pids); // maxY is calculated in this method
			
			dashed_rectangle_height = getMaxY() + StyleUtils.GAP_Y_COORDINATE;
			int svg_width = getMaxX() + StyleUtils.GAP_X_COORDINATE + 100;
			int svg_height = dashed_rectangle_height + 100;
			
			String processes = "";
			String historyLines = "";
			String msgLines = "";
			String svgHeader = "";
			String line;
			while((line=br.readLine())!=null) {
				if(line.trim().equals("toBeChanged")) {
					for(String pid: pids) {
						processes += "<div class=\"process-style\">" + pid + "</div>";
					}
					sb.append(processes);
					continue;
				} else if(line.trim().equals("toBeChanged2")) {
					svgHeader = "<svg width=\""+ svg_width +"\" height=\""+ svg_height +"\" style=\"margin-left:30px;\">";
					sb.append(svgHeader);
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
		int x = StyleUtils.STARTING_X_DASHED_LINE;
		int y1 = StyleUtils.STARTING_Y_DASHED_LINE;
		int y2 = StyleUtils.STARTING_Y_DASHED_RECTANGLE; // when process history starts, increase 30 by 30
		int rectX = StyleUtils.STARTING_X_DASHED_RECTANGLE;
		int rx = StyleUtils.DASHED_RECTANGLE_X_CORNER_ROUND, 
			ry = StyleUtils.DASHED_RECTANGLE_Y_CORNER_ROUND;
		int height = dashed_rectangle_height;
		
		String res = "";
		for(int i = 0; i < size; i++) {
			res += "<line class=\"dash\" x1=\""+ x +"\" y1=\""+ y1 +"\" x2=\""+ x +"\" y2=\""+ y2 +"\" stroke-dasharray=\"4, 5\"/>\r\n"
				+ "<rect class=\"dash\" x=\""+ rectX +"\" y=\""+ y2 +"\" rx=\"" + rx + "\" ry=\"" + ry + "\" height=\""+ height +"\"/>\r\n";
				
			x += StyleUtils.GAP_X_COORDINATE;
			rectX += StyleUtils.GAP_X_COORDINATE;
		}
		return res;
	}

	private static Map<String, Integer> initialize_MsgPointerList() {
		for(String process : sortedMessages.keySet()) {
			processMsgPointer.put(process, 0);
		}
		return processMsgPointer;
	}
	
	private static Map<String, Boolean> initialize_WaitingProcessList() {
		for(String process : sortedMessages.keySet()) {
			waitingProcess.put(process, false);
		}
		return waitingProcess;
	}
	
	private static Map<String, Integer> initialize_lastYList() {
		for(String process : sortedMessages.keySet()) {
			lastY.put(process, StyleUtils.STARTING_Y_COORDINATE);
		}
		return lastY;
	}
	
	private static Map<String, List<Map<String, Integer>>> initialize_YcoordinatesList() {
		for(String process : sortedMessages.keySet()) {
			yCoordinates.put(process, new ArrayList<Map<String, Integer>>());
		}
		return yCoordinates;
	}
	
	public static void printYcoordinates() {
		for (String prevMsg: yCoordinates.keySet()) {
		    String msg = yCoordinates.get(prevMsg).toString();
		    System.out.println(prevMsg + " " + msg);
		}
	}
	
	public static String drawArrows(List<String> pids) {
		String res = "";
		List<Map<String, Integer>> coordinates;
		String senderProcess, receiverProcess, deliverMessage;
		int x1, x2, y1, y2, exp, msgNumber;
		
		for(String process : pids) {
			coordinates = yCoordinates.get(process);
			for(int i=0; i < coordinates.size(); i++) {
				for(String msg : coordinates.get(i).keySet()) {
					if(msg.contains(Constants.SEND)) {
						msgNumber = getMsgNumber(msg);
						deliverMessage = Constants.DELIVER+" "+msgNumber;
						
						senderProcess = sendMsg.get(msgNumber);
						receiverProcess = deliverMsg.get(msgNumber);
						
						x1 = xCoordinates.get(senderProcess);
						y1 = coordinates.get(i).get(msg);
						
						exp = process.compareTo(receiverProcess);
						if(exp < 0) { x2 = xCoordinates.get(receiverProcess) - 30; }
						else { x2 = xCoordinates.get(receiverProcess) + 10; }
						y2 = y1;
						
						res += drawMsg(x1,y1, msg, exp);
						res += drawMsg(x2,y2, deliverMessage, exp);
						res += drawArrow(x1,y1,x2,y2);
					}
					else if(msg.contains(Constants.RECEIVE)) {
						receiverProcess = receiveMsg.get(getMsgNumber(msg));
						x2 = xCoordinates.get(receiverProcess);
						y2 = coordinates.get(i).get(msg);
						res += drawReceivePoint(x2, y2, msg);
					}
				}
			}
		}
		
		return res;
	}

	public static Map<String, Integer> fillXCoordinates(List<String> pids) {
		int x = StyleUtils.STARTING_X_COORDINATE;
		for(String pid : pids) {
			xCoordinates.put(pid, x);
			if(maxX < x) { maxX = x; }
			x += StyleUtils.GAP_X_COORDINATE;
		}
		return xCoordinates;
	}
	
	public static Map<String, List<Map<String, Integer>>> fillYCoordinates(List<String> pids) {
		initialize_lastYList();
		initialize_WaitingProcessList();
		initialize_MsgPointerList();
		initialize_YcoordinatesList();
		
		List<String> messages, messages2;
		int msgPointer, msgPointer2;
		String message, message2;
		String senderProcess;
		int y, cont, lastSendY = StyleUtils.STARTING_Y_COORDINATE;
		
		while(true) {
			for(String process : pids) {
				messages = sortedMessages.get(process);
				msgPointer = processMsgPointer.get(process);
				
				// Evitar un IndexOutOfBounds
				if(msgPointer >= messages.size()) continue;
				
				message = messages.get(msgPointer);
				
				// Si se sabe que es un deliver esperando un send, no hace falta ejecutar más instrucciones
	            if(waitingProcess.get(process)) continue; //siguiente proceso
	            
	            // En caso de que se detecte un deliver, se espera a encontrar el send respectivo
	            if(message.contains(Constants.DELIVER) && !waitingProcess.get(process)) {
	            	waitingProcess.put(process, true);
	                continue; // siguiente proceso
	            }
	            // En caso de que se detecte un send, hay que revisar todos los deliver en espera
	            // y comprobar si sus codigos son iguales
	            else if(message.contains("send")) {
	            	senderProcess = process; // Esto es solo para que se entienda mejor
	                
	            	// Para cada posible proceso que contenga un deliver:
	                for(String deliverProcess : pids) {
	                	messages2 = sortedMessages.get(deliverProcess);
	                	msgPointer2 = processMsgPointer.get(deliverProcess);
	                   // Evitar un IndexOutOfBounds
	                   if(msgPointer2 >= messages2.size()) continue;

	                   message2 = messages2.get(msgPointer2);

	                   // Si el proceso que contiene send es diferente al que posiblemente contiene un deliver,
	                   // y resulta que si contiene un deliver (tiene waiting_process a True) y sus codigos son el mismo
	                   // entonces se debe calcular sus coordenadas Y y añadir ambos a y_axis_process
	                   if (senderProcess != deliverProcess
	                      && waitingProcess.get(deliverProcess)
	                      && getMsgNumber(message2) == getMsgNumber(message)) {
	                            
	                      // Calculo de coordenada Y
	                      y = lastSendY 
	                          + StyleUtils.GAP_Y_COORDINATE;

	                      Map<String, Integer> pos = new HashMap<>();
	                      pos.put(message, y);
	                      
	                      yCoordinates.get(senderProcess).add(pos); 
	                      lastY.put(senderProcess, y);
	                      
	                      Map<String, Integer> pos2 = new HashMap<>();
	                      pos2.put(message2, y);

	                      yCoordinates.get(deliverProcess).add(pos2);
	                      lastY.put(deliverProcess, y);
	                      
	                      lastSendY = y;
	                      if(maxY < y) { maxY = y; }

	                       // Puesto que el deliver y el send ya se han tenido en cuenta, se continua con
	                       // los siuientes eventos sus respectivos procesos 
	                       waitingProcess.put(deliverProcess, false);
	                       processMsgPointer.put(senderProcess, processMsgPointer.get(senderProcess)+1);
	                       processMsgPointer.put(deliverProcess, processMsgPointer.get(deliverProcess)+1);
	                       break;
	                     }

	                }
	            }
	            // Para el caso del receive solo se debe calcular la coordenada Y y añadirlo a y_axis_process
	            else if(message.contains("receive")) {
	                    y = lastY.get(process) + StyleUtils.GAP_Y_COORDINATE;
	                    
	                    Map<String, Integer> pos = new HashMap<>();
	                    pos.put(message, y);
	                    
	                    lastSendY = y;
	                    
	                    yCoordinates.get(process).add(pos);
	                    lastY.put(process, y);
	                    processMsgPointer.put(process, processMsgPointer.get(process)+1);
	            }
			}
	            
	        // Por ultimo para detener el algoritmo se comprueba que se haya calculado la coordenada Y
	        // de todos los eventos
	        cont = 0;
	        for(String proceso : pids) {
	          if(processMsgPointer.get(proceso) != sortedMessages.get(proceso).size()) break;
	          cont++;
	        }
	        if(cont == pids.size()) {
	        	printYcoordinates(); 
	        	return yCoordinates;
	        }
			
		}
	}
	
	/**
	 * @param x1, y1 - The x and y coordinate of the sender process
	 * @param x2, y2 - The x and y coordinate of the receiver process
	 * 
	 * @return The HTML elements in raw string, draws an arrow from the sender to the receiver process
	 */
	public static String drawArrow(int x1, int y1, int x2, int y2) {
		return "<line class=\"arrow\" x1=\""+ x1 +"\" y1=\""+ y1 +"\" x2=\""+ x2 +"\" y2=\""+ y2 +"\" marker-end=\"url(#arrowhead)\" />";
	}
	
	/**
	 * @param x - The x coordinate of a process
	 * @param y - The y coordinate of a message of this process
	 * @param msg - The "receive" message to be written
	 * 
	 * @return The HTML elements in raw string, draws the message above a circle
	 */
	public static String drawReceivePoint(int x, int y, String msg) {
		String res = "";
		int xText = x + 5, 
			yText = y - 10; 
		res = "<circle style=\"fill:none;stroke:#010101;stroke-width:1.6871;stroke-miterlimit:10;\" cx=\""+ x +"\" cy=\""+ y +"\" r=\"5\"></circle>"
			+ "<text class=\"msg\" x=\""+ xText +"\" y=\""+ yText +"\" text-anchor=\"start\" dy=\"1px\">" + msg + "</text>";
		return res;
	}
	
	/**
	 * This method takes the font-size into account, to re-calculate the x, 
	 * so the elements don't overlap with any dashed rectangle.
	 * 
	 * @param x - The x coordinate of a process
	 * @param y - The y coordinate of a message of this process
	 * @param msg - The "send" or "deliver" message to be written
	 * 
	 * @return The HTML elements in raw string, draws the message above a circle
	 */
	public static String drawMsg(int x, int y, String msg, int exp) {
		String res = "";
		int xText, 
			yText = y - 10; 
		
		if (msg.contains(Constants.DELIVER)) {
			xText = (exp < 0) ? x - 50 : x - 5;
		} else { xText = x + 5; }
		
		res = "<circle style=\"fill:none;stroke:#010101;stroke-width:1.6871;stroke-miterlimit:10;\" cx=\""+ x +"\" cy=\""+ y +"\" r=\"2\"></circle>"
		    + "<text class=\"msg\" x=\""+ xText +"\" y=\""+ yText +"\" text-anchor=\"start\" dy=\"1px\">" + msg + "</text>";
		return res;
	}
	
	private static Integer getMsgNumber(String message) {
		Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(message);
        matcher.find();
        String msgNumber = message.substring(matcher.start(), matcher.end());
        return Integer.valueOf(msgNumber);
	}
	
	private static int getMaxX() {
		return maxX;
	}
	
	private static int getMaxY() {
		return maxY;
	}
}
