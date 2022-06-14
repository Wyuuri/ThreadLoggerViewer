package main.java.com.maria.view;

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

import main.java.com.maria.common.Constants;
import main.java.com.maria.service.LogFilesReader;

public class UserInterface {
	
	private final static String htmlFilepath = Constants.UI_PATH + Constants.HTML_FILENAME;
	private final static Map<Integer,String> sendMsg = LogFilesReader.getSendMsg();
	private final static Map<Integer,String> deliverMsg = LogFilesReader.getDeliverMsg();
	private final static Map<Integer,String> receiveMsg = LogFilesReader.getReceiveMsg();
	private final static Map<String, Integer> xValues =  LogFilesReader.Xvalues();
	private final static Map<String,List<String>> sortedPoints = LogFilesReader.getSortedPoints();
	private static Map<String, String> receivePreviousMsg = new HashMap<>();
	private static Map<String, String> receiveNextMsg = new HashMap<>();
	private static Map<String, Integer> currentY = new HashMap<>();
	private static Map<String, Integer> lastY = new HashMap<>();
	private static Map<String, Boolean> waitingProcess = new HashMap<>();
	private static Map<String, Integer> processMsgPointer = new HashMap<>();
	private static Map<String, List<Map<String, Integer>>> yCoordinates = new TreeMap<>();
	
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
				
			x += Constants.GAP_X_COORDINATE;
			rectX += Constants.GAP_X_COORDINATE;
		}
		return res;
	}

	private static Map<String, Integer> initialize_MsgPointerList() {
		for(String process : sortedPoints.keySet()) {
			processMsgPointer.put(process, 0);
		}
		return processMsgPointer;
	}
	
	private static Map<String, Boolean> initialize_WaitingProcessList() {
		for(String process : sortedPoints.keySet()) {
			waitingProcess.put(process, false);
		}
		return waitingProcess;
	}
	
	private static Map<String, Integer> initialize_lastYList() {
		for(String process : sortedPoints.keySet()) {
			lastY.put(process, Constants.STARTING_Y_COORDINATE);
		}
		return lastY;
	}
	
	private static Map<String, List<Map<String, Integer>>> initialize_YcoordinatesList() {
		for(String process : sortedPoints.keySet()) {
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
		fillYCoordinates(pids);
		String res = "";
		List<Map<String, Integer>> coordinates, coordinates2;
		String senderProcess, receiverProcess, deliverMessage;
		int x1, x2, y1, y2 = 0, exp, msgNumber, cont;
		
		for(String process : pids) {
			coordinates = yCoordinates.get(process);
			for(int i=0; i < coordinates.size(); i++) {
				for(String msg : coordinates.get(i).keySet()) {
					if(msg.contains(Constants.SEND)) {
						msgNumber = getMsgNumber(msg);
						deliverMessage = Constants.DELIVER+" "+msgNumber;
						
						senderProcess = sendMsg.get(msgNumber);
						receiverProcess = deliverMsg.get(msgNumber);
						
						x1 = xValues.get(senderProcess);
						y1 = coordinates.get(i).get(msg);
						
						exp = process.compareTo(receiverProcess);
						if(exp < 0) { x2 = xValues.get(receiverProcess) - 30; }
						else { x2 = xValues.get(receiverProcess) + 10; }
						
						coordinates2 = yCoordinates.get(receiverProcess);
						cont = 0;
						for(int j = 0; j < coordinates2.size(); j++) {
							if(!coordinates2.get(j).containsKey(deliverMessage)) {
								cont++;
							} else break;
						}
						y2 = coordinates2.get(cont).get(deliverMessage);
						
						res += drawMsg(x1,y1, msg);
						res += drawMsg(x2,y2, deliverMessage);
						res += drawArrow(x1,y1,x2,y2);
					}
					else if(msg.contains(Constants.RECEIVE)) {
						receiverProcess = receiveMsg.get(getMsgNumber(msg));
						x2 = xValues.get(receiverProcess);
						y2 = coordinates.get(i).get(msg);
						res += drawReceivePoint(x2, y2, msg);
					}
				}
			}
		}
		
		return res;
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
		int y, cont;
		
		while(true) {
			for(String process : pids) {
				messages = sortedPoints.get(process);
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
	                	messages2 = sortedPoints.get(deliverProcess);
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
	                      y = Math.max(lastY.get(senderProcess), lastY.get(deliverProcess)) 
	                          + Constants.GAP_Y_COORDINATE;

	                      Map<String, Integer> pos = new HashMap<>();
	                      pos.put(message, y);
	                      
	                      yCoordinates.get(senderProcess).add(pos); 
	                      lastY.put(senderProcess, y);
	                      
	                      Map<String, Integer> pos2 = new HashMap<>();
	                      pos2.put(message2, y);

	                      yCoordinates.get(deliverProcess).add(pos2);
	                      lastY.put(deliverProcess, y);

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
	                    y = lastY.get(process) + Constants.GAP_Y_COORDINATE;
	                    
	                    Map<String, Integer> pos = new HashMap<>();
	                    pos.put(message, y);
	                    
	                    yCoordinates.get(process).add(pos);
	                    lastY.put(process, y);
	                    processMsgPointer.put(process, processMsgPointer.get(process)+1);
	            }
			}
	            
	        // Por ultimo para detener el algoritmo se comprueba que se haya calculado la coordenada Y
	        // de todos los eventos
	        cont = 0;
	        for(String proceso : pids) {
	          if(processMsgPointer.get(proceso) != sortedPoints.get(proceso).size()) break;
	          cont++;
	        }
	        if(cont == pids.size()) {
	        	printYcoordinates(); return yCoordinates;
	        }
			
		}
	}
	
	public static String drawArrow(int x1, int y1, int x2, int y2) {
		return "<line class=\"msg\" x1=\""+ x1 +"\" y1=\""+ y1 +"\" x2=\""+ x2 +"\" y2=\""+ y2 +"\" marker-end=\"url(#arrowhead)\" />";
	}
	
	public static String drawReceivePoint(int x, int y, String msg) {
		String res = "";
		int xText = x + 5, 
			yText = y - 10; 
		res = "<circle style=\"fill:none;stroke:#010101;stroke-width:1.6871;stroke-miterlimit:10;\" cx=\""+ x +"\" cy=\""+ y +"\" r=\"5\"></circle>"
			+ "<text font-size=\"10\" x=\""+ xText +"\" y=\""+ yText +"\" text-anchor=\"start\" stroke=\"red\" stroke-width=\"1px\" dy=\"1px\">" + msg + "</text>";
		return res;
	}
	
	public static String drawMsg(int x, int y, String msg) {
		String res = "";
		int xText = x + 5, 
			yText = y - 10; 
		res = "<circle style=\"fill:none;stroke:#010101;stroke-width:1.6871;stroke-miterlimit:10;\" cx=\""+ x +"\" cy=\""+ y +"\" r=\"2\"></circle>"
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
