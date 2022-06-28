package main.frontend.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.common.StyleUtils;

/**
 * Calculate yCoordinates using these Maps.
 */
public class Initializer {
	
	private static Map<String, Integer> lastY = new HashMap<>();
	private static Map<String, Boolean> waitingProcess = new HashMap<>();
	private static Map<String, Integer> processEventPointer = new HashMap<>();
	
	/**
	 * Set initial values as 0.
	 * 
	 * @param pids - All processes numbers
	 * @return A HashMap that tells the event pointed out by each process.
	 */
	public static Map<String, Integer> initialize_EventPointerList(List<String> pids) {
		processEventPointer.clear();
		for(String process : pids) {
			processEventPointer.put(process, 0);
		}
		return processEventPointer;
	}
	
	/**
	 * Set initial values as false.
	 * 
	 * @param pids - All processes numbers
	 * @return A HashMap that tells if each process is waiting for a "send" or not.
	 */
	public static Map<String, Boolean> initialize_WaitingProcessList(List<String> pids) {
		waitingProcess.clear();
		for(String process : pids) {
			waitingProcess.put(process, false);
		}
		return waitingProcess;
	}
	
	/**
	 * Set initial values as StyleUtils.STARTING_Y_COORDINATE.
	 * 
	 * @param pids - All processes numbers
	 * @return A HashMap that contains the last y coordinate of each process.
	 */
	public static Map<String, Integer> initialize_lastYList(List<String> pids) {
		lastY.clear();
		for(String process : pids) {
			lastY.put(process, StyleUtils.STARTING_Y_COORDINATE);
		}
		return lastY;
	}
	
	/**
	 * For each process, add an empty ArrayList<Map<String, Integer>>().
	 * 
	 * @param pids - All processes numbers
	 * @param yCoordinates - Processes events y coordinate integer values
	 */
	public static void initialize_YcoordinatesList(List<String> pids, Map<String, List<Map<String, Integer>>> yCoordinates) {
		yCoordinates.clear();
		for(String process : pids) {
			yCoordinates.put(process, new ArrayList<Map<String, Integer>>());
		}
	}
}
