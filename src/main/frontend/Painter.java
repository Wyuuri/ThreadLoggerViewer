package main.frontend;

import java.util.List;
import java.util.Map;

import main.backend.LogFilesReader;
import main.common.Constants;
import main.common.StyleUtils;
import main.frontend.algorithm.Algorithm;

public class Painter {
	
	private final static Map<Integer,String> sendMsg = LogFilesReader.getSendMsg();
	private final static Map<Integer,String> deliverMsg = LogFilesReader.getDeliverMsg();
	private final static Map<Integer,String> receiveMsg = LogFilesReader.getReceiveMsg();

	/**
	 * @param svg_width - The width of the SVG element.
	 * @param svg_height - The height of the SVG element.
	 * 
	 * @return The HTML elements in raw string, a styled SVG header.
	 */
	public static String drawSVGHeader(int svg_width, int svg_height) {
		return "<svg class=\"svg-style\" width=\""+ svg_width +"\" height=\""+ svg_height +"\" >\r\n";
	}
	
	/**
	 * @param pids - All processes numbers
	 * 
	 * @return The HTML elements in raw string, draws each process number side by side.
	 */
	public static String drawProcesses(List<String> pids) {
		String res = "";
		for(String pid: pids) {
			res += "<div class=\"process-style\">" + pid + "</div>\r\n";
		}
		return res;
	}

	/**
	 * @param size - Total number of processes
	 * @param height - Dashed rectangles height
	 * 
	 * @return The HTML elements in raw string, draws {size} dashed lines and rectangles.
	 */
	public static String drawHistoryLines(int size, int height) {
		int x = StyleUtils.STARTING_X_DASHED_LINE;
		int y1 = StyleUtils.STARTING_Y_DASHED_LINE;
		int y2 = StyleUtils.STARTING_Y_DASHED_RECTANGLE; // when process history starts, increase 30 by 30
		int rectX = StyleUtils.STARTING_X_DASHED_RECTANGLE;
		int rx = StyleUtils.DASHED_RECTANGLE_X_CORNER_ROUND, 
			ry = StyleUtils.DASHED_RECTANGLE_Y_CORNER_ROUND;
		
		String res = "";
		for(int i = 0; i < size; i++) {
			res += "<line class=\"dash\" x1=\""+ x +"\" y1=\""+ y1 +"\" x2=\""+ x +"\" y2=\""+ y2 +"\" stroke-dasharray=\"4, 5\"/>\r\n"
				+ "<rect class=\"dash\" x=\""+ rectX +"\" y=\""+ y2 +"\" rx=\"" + rx + "\" ry=\"" + ry + "\" height=\""+ height +"\"/>\r\n";
				
			x += StyleUtils.GAP_X_COORDINATE;
			rectX += StyleUtils.GAP_X_COORDINATE;
		}
		return res;
	}
	
	/**
	 * @param pids - All processes numbers
	 * @param xCoordinates - Processes x coordinate integer value
	 * @param yCoordinates - Processes messages y coordinate integer values
	 * 
	 * @return The HTML elements in raw string, draws arrows from sender to receiver process 
	 * 	and circles on the receiver processes, both with their respective messages above these elements.
	 */
	public static String drawArrows(List<String> pids, Map<String, Integer> xCoordinates, Map<String, List<Map<String, Integer>>> yCoordinates) {
		String res = "";
		List<Map<String, Integer>> coordinates;
		String senderProcess, receiverProcess, deliverMessage;
		int x1, x2, y1, y2, exp, msgNumber;
		
		for(String process : pids) {
			coordinates = yCoordinates.get(process);
			for(int i=0; i < coordinates.size(); i++) {
				for(String msg : coordinates.get(i).keySet()) {
					if(msg.contains(Constants.SEND)) {
						msgNumber = Algorithm.getMsgNumber(msg);
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
						receiverProcess = receiveMsg.get(Algorithm.getMsgNumber(msg));
						x2 = xCoordinates.get(receiverProcess);
						y2 = coordinates.get(i).get(msg);
						res += drawReceivePoint(x2, y2, msg);
					}
				}
			}
		}
		
		return res;
	}

	/**
	 * @param x1, y1 - The x and y coordinate of the sender process
	 * @param x2, y2 - The x and y coordinate of the receiver process
	 * 
	 * @return The HTML elements in raw string, draws an arrow from the sender to the receiver process
	 */
	public static String drawArrow(int x1, int y1, int x2, int y2) {
		return "<line class=\"arrow\" x1=\""+ x1 +"\" y1=\""+ y1 +"\" x2=\""+ x2 +"\" y2=\""+ y2 +"\" marker-end=\"url(#arrowhead)\" />\r\n";
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
			yText = y - 10,
			radius = StyleUtils.RECEIVE_CIRCLE_RADIUS;
		
		res = "<circle style=\"fill:none;stroke:#010101;stroke-width:1.6871;stroke-miterlimit:10;\" cx=\""+ x +"\" cy=\""+ y +"\" r=\""+ radius +"\"></circle>"
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
			xText = (exp < 0) ? x - StyleUtils.TEXT_TO_THE_LEFT : x - 5;
		} else { xText = x + 5; }
		
		res = "<circle style=\"fill:none;stroke:#010101;stroke-width:1.6871;stroke-miterlimit:10;\" cx=\""+ x +"\" cy=\""+ y +"\" r=\"2\"></circle>"
		    + "<text class=\"msg\" x=\""+ xText +"\" y=\""+ yText +"\" text-anchor=\"start\" dy=\"1px\">" + msg + "</text>";
		return res;
	}
}
