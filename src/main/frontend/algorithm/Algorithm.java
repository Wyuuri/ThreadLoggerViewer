package main.frontend.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.backend.LogFilesReader;
import main.common.Constants;
import main.common.StyleUtils;

public class Algorithm {

	private static Map<String, Integer> xCoordinates = new TreeMap<>();
	private static Map<String, List<Map<String, Integer>>> yCoordinates = new TreeMap<>();
	
	private final static Map<String,List<String>> sortedMessages = LogFilesReader.getSortedMessages();
	
	private static int maxX, maxY = -1;
	
	/**
	 * @return The largest x value in xCoordinates.
	 */
	public static int getMaxX() {
		return maxX;
	}
	
	/**
	 * @return The largest y value in yCoordinates.
	 */
	public static int getMaxY() {
		return maxY;
	}
	
	/**
	 * @param message - A string that contains a number.
	 * @return The first number found in the given message.
	 */
	public static Integer getMsgNumber(String message) {
		Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(message);
        matcher.find();
        String msgNumber = message.substring(matcher.start(), matcher.end());
        return Integer.valueOf(msgNumber);
	}
	
	/**
	 * The variable maxX is calculated in this method.
	 * 
	 * @param pids - All processes numbers
	 * @return xCoordinates
	 */
	public static Map<String, Integer> fillXCoordinates(List<String> pids) {
		int x = StyleUtils.STARTING_X_COORDINATE;
		for(String pid : pids) {
			xCoordinates.put(pid, x);
			if(maxX < x) { maxX = x; }
			x += StyleUtils.GAP_X_COORDINATE;
		}
		return xCoordinates;
	}
	
	/**
	 * The variable maxY is calculated in this method.
	 * 
	 * This method also calls another private method to print yCoordinates content.
	 * 
	 * @param pids - All processes numbers
	 * @return yCoordinates
	 */
	public static Map<String, List<Map<String, Integer>>> fillYCoordinates(List<String> pids) {
		Map<String, Integer> lastY = Initializer.initialize_lastYList(pids);
		Map<String, Boolean> waitingProcess = Initializer.initialize_WaitingProcessList(pids);
		Map<String, Integer> processMsgPointer = Initializer.initialize_MsgPointerList(pids);
		Initializer.initialize_YcoordinatesList(pids, yCoordinates);
		
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
	 * Print yCoordinates content.
	 */
	private static void printYcoordinates() {
		for (String process: yCoordinates.keySet()) {
		    String messagesY = yCoordinates.get(process).toString();
		    System.out.println(process + " " + messagesY);
		}
	}
}
