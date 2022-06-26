package main.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import main.backend.LogUtils;
import main.common.Constants;
import main.common.StyleUtils;
import main.frontend.algorithm.Algorithm;

public class UserInterface {

	private static int dashed_rectangle_height;
	
	/**
	 * @param tracePath - The absolute path where all log files reside.
	 * @return The HTML raw string to be displayed.
	 * @throws IOException 
	 */
	public static String readHTMLFile_andBeautify(String tracePath) throws IOException, Exception {
		String html = "";
		
		File file = new File(Constants.HTML_FILEPATH);    //creates a new file instance  
		FileReader fr = new FileReader(file);   //reads the file  
		BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
		StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
	
		List<String> pids = LogUtils.getAllProcessesNumbers(tracePath);
		Map<String, Integer> xCoordinates = Algorithm.fillXCoordinates(pids);
		Map<String, List<Map<String, Integer>>> yCoordinates = Algorithm.fillYCoordinates(pids);
		
		dashed_rectangle_height = Algorithm.getMaxY() + StyleUtils.GAP_Y_COORDINATE;
		int svg_width = Algorithm.getMaxX() + StyleUtils.GAP_X_COORDINATE + 100;
		int svg_height = dashed_rectangle_height + 100;
		
		String processes = "", 
			historyLines = "",
			msgLines = "", 
			svgHeader = "";
		
		String line;
		
		while((line=br.readLine())!=null) {
			if(line.trim().equals("toBeChanged")) {
				processes = Painter.drawProcesses(pids);
				sb.append(processes);
				continue;
			} else if(line.trim().equals("toBeChanged2")) {
				svgHeader = Painter.drawSVGHeader(svg_width, svg_height);
				sb.append(svgHeader);
				
				historyLines = Painter.drawHistoryLines(pids.size(), dashed_rectangle_height);
				sb.append(historyLines);
				continue;
			} else if(line.trim().equals("toBeChanged3")) {
				msgLines = Painter.drawArrows(pids, xCoordinates, yCoordinates);
				sb.append(msgLines);
				continue;
			}
			sb.append(line);
		}  
		fr.close();
		html = sb.toString();
		
		// Optional: Print processes and their messages X and Y coordinates
		Algorithm.printXcoordinates();
		System.out.println();
		Algorithm.printYcoordinates();
		
		return html;
	}
}
