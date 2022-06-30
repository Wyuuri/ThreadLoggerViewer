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
	
	private final static Map<String,List<String>> sortedEvents = LogFilesReader.getSortedEvents();
	
	private static int maxX, maxY;
	
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
	public static Integer getEventNumber(String message) {
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
		xCoordinates.clear();
		maxX = -1;
		
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
	 * @param pids - All processes numbers
	 * @return yCoordinates
	 * @throws Exception 
	 */
	public static Map<String, List<Map<String, Integer>>> fillYCoordinates(List<String> pids) throws Exception {
		Map<String, Integer> lastY = Initializer.initialize_lastYList(pids);
		Map<String, Boolean> waitingProcess = Initializer.initialize_WaitingProcessList(pids);
		Map<String, Integer> processEventPointer = Initializer.initialize_EventPointerList(pids);
		Initializer.initialize_YcoordinatesList(pids, yCoordinates);
		maxY = -1;
		
		List<String> events, events2;
		int eventPointer, eventPointer2;
		String event, event2;
		String senderProcess;
		int y, cont, lastSendY = StyleUtils.STARTING_Y_COORDINATE;
		int loop_count = 0;
		
		while(true) {
			for(String process : pids) {
				// In the worst case, iterations will be made equal to the number of processes squared 
				// without finding the right "deliver" of any "send" event
				if (++loop_count > pids.size()*pids.size()) { throw new Exception("Too many iterations, possible deadlock?"); }
				
				events = sortedEvents.get(process);
				eventPointer = processEventPointer.get(process);
				
				// Avoid IndexOutOfBounds
				if(eventPointer >= events.size()) continue;
				
				event = events.get(eventPointer);
				
				// If it is known that it is a "deliver" waiting for a "send", there is no need to execute more instructions
	            if(waitingProcess.get(process)) continue; // next process
	            
	            //In case a "deliver" is detected, it will wait to find the respective "send"
	            if(event.contains(Constants.DELIVER) && !waitingProcess.get(process)) {
	            	waitingProcess.put(process, true);
	                continue; // next process
	            }
	            // In case a "send" is detected, it has to check all the "deliver" to see if their codes are the same
	            else if(event.contains(Constants.SEND)) {
	            	senderProcess = process; // Just to make it more clear
	                
	            	// For each process that may contain a "deliver":
	                for(String deliverProcess : pids) {
	                	events2 = sortedEvents.get(deliverProcess);
	                	
	                	// If the processes are the same it can mean that it is making a "send" on itself.
	                    if (senderProcess.equals(deliverProcess)) {
	                    	// Bypassing "send" event
	                    	eventPointer2 = processEventPointer.get(senderProcess) + 1;
	                    	// Avoid IndexOutOfBounds
	                    	if(eventPointer2 >= events2.size()) continue;
	                    	event2 = events2.get(eventPointer2);
	                    	
	                    	// It is making a "send" on itself only if eventNumber are the same
		                    if (getEventNumber(event2).intValue() == getEventNumber(event).intValue()) {
		                    	loop_count = 0; 
		                	    // Y coordinate calculation (send)
		                	    y = lastSendY 
		                	    		+ StyleUtils.GAP_Y_COORDINATE;
								
		                	    Map<String, Integer> pos = new HashMap<>();
		                	    pos.put(event, y);
								  
		                	    yCoordinates.get(senderProcess).add(pos); 
		                	    lastY.put(senderProcess, y);
								  
		                	    // Y coordinate recalculation (deliver)
		                	    y += StyleUtils.GAP_Y_COORDINATE;
								  
		                	    Map<String, Integer> pos2 = new HashMap<>();
		                	    pos2.put(event2, y);
								
		                	    yCoordinates.get(deliverProcess).add(pos2);
		                	    lastY.put(deliverProcess, y);
								  
		                	    lastSendY = y;
		                	    if(maxY < y) { maxY = y; }
		                	    // Since the "deliver" and the "send" have already been taken into account, 
		                	    // the following events of their respective processes are continued.
		                	    waitingProcess.put(deliverProcess, false);
		                	    processEventPointer.put(senderProcess, processEventPointer.get(senderProcess)+1);
		                	    processEventPointer.put(deliverProcess, processEventPointer.get(deliverProcess)+1);
		                	    break;
		                   }
	                	   
	                   }
	                   // Not the same process
	                   else {

		                	eventPointer2 = processEventPointer.get(deliverProcess);
		                   // Avoid IndexOutOfBounds
		                   if(eventPointer2 >= events2.size()) continue;

		                   event2 = events2.get(eventPointer2);
	                   
		                   // If the process that contains "send" is different from the one that possibly contains a "deliver", 
		                   // and it turns out that if it contains a "deliver" (it has waitingProcess to True) and its codes are the same,
		                   // then it must calculate its Y coordinates and add both to yCoordinates
		                   if (!senderProcess.equals(deliverProcess)
		                      && waitingProcess.get(deliverProcess)
		                      && getEventNumber(event2) == getEventNumber(event)) {

		                	  loop_count = 0; 
		                	  // Y coordinate calculation
		                      y = lastSendY 
		                          + StyleUtils.GAP_Y_COORDINATE;
	
		                      Map<String, Integer> pos = new HashMap<>();
		                      pos.put(event, y);
		                      
		                      yCoordinates.get(senderProcess).add(pos); 
		                      lastY.put(senderProcess, y);
		                      
		                      Map<String, Integer> pos2 = new HashMap<>();
		                      pos2.put(event2, y);
	
		                      yCoordinates.get(deliverProcess).add(pos2);
		                      lastY.put(deliverProcess, y);
		                      
		                      lastSendY = y;
		                      if(maxY < y) { maxY = y; }
	
		                      // Since the "deliver" and the "send" have already been taken into account, 
		                	  // the following events of their respective processes are continued.
		                      waitingProcess.put(deliverProcess, false);
		                      processEventPointer.put(senderProcess, processEventPointer.get(senderProcess)+1);
		                      processEventPointer.put(deliverProcess, processEventPointer.get(deliverProcess)+1);
		                      break;
		                   }
	                   }
	                }
	            }
	            // In case of "receive" it just have to calculate the Y coordinate and add it to yCoordinates
	            else if(event.contains("receive")) {
	            	loop_count = 0;
             	   		
	            	y = lastY.get(process) + StyleUtils.GAP_Y_COORDINATE;
	                    
	            	Map<String, Integer> pos = new HashMap<>();
	            	pos.put(event, y);
	                    
	            	lastSendY = y;
	                    
                    yCoordinates.get(process).add(pos);
                    lastY.put(process, y);
                    processEventPointer.put(process, processEventPointer.get(process)+1);
	            }
			}
	            
	        // At last, to stop the algorithm, verify that the y-coordinates
			// of all events have been calculated
	        cont = 0;
	        for(String proceso : pids) {
	          if(processEventPointer.get(proceso) != sortedEvents.get(proceso).size()) break;
	          cont++;
	        }
	        if(cont == pids.size()) {
	        	return yCoordinates;
	        }
			
		}
	}

	/**
	 * Print xCoordinates content.
	 */
	public static void printXcoordinates() {
		System.out.println("X COORDINATES:");
		for (String process: xCoordinates.keySet()) {
		    String messagesY = xCoordinates.get(process).toString();
		    System.out.println("Process " + process + ": " + messagesY);
		}
	}
	
	/**
	 * Print yCoordinates content.
	 */
	public static void printYcoordinates() {
		System.out.println("Y COORDINATES:");
		for (String process: yCoordinates.keySet()) {
		    String messagesY = yCoordinates.get(process).toString();
		    System.out.println("Process " + process + ": " + messagesY);
		}
	}
}
