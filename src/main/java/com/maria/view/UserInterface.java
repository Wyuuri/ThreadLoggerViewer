// VIEW ALTERNATIVE: SWING not working in JavaFX

/*package main.java.com.maria.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import main.java.com.maria.common.Constants;
import main.java.com.maria.service.LogFilesReader;


public class UserInterface {
	
	JFrame frame = null;
	
	public static void createWindow() {    
	      JFrame frame = new JFrame("Thread Logger Viewer");
	      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      createUI(frame);
	      frame.setSize(560, 450);      
	      frame.setLocationRelativeTo(null);  
	      frame.setVisible(true);
	}
	
	private static void createUI(final JFrame frame){  
		LayoutManager layout = new FlowLayout(); 
	    JPanel panel = new JPanel();
	    panel.setLayout(layout);       

	    JEditorPane pane = new JEditorPane();
	    pane.setEditable(false);   
	    //URL url= UserInterface.class.getResource(Constants.HTML_FILENAME);
	    
	    // CSS
	    HTMLEditorKit kit = new HTMLEditorKit();
	    pane.setEditorKit(kit);

	    URL urlCSS = UserInterface.class.getResource("view.css");
	    StyleSheet styleSheet = new StyleSheet();
	    styleSheet.importStyleSheet(urlCSS);
	    kit.setStyleSheet(styleSheet);
	    
	    // HTML
	    pane.setText(LogFilesReader.readHTMLFile_andBeautify());
	    //try {  
	    //	pane.setPage(url);
	    //} catch (IOException e) { 
	    //	pane.setContentType("text/html");
	    //	pane.setText("<html>Page not found.</html>");
	   // }

	    JScrollPane jScrollPane = new JScrollPane(pane);
	    jScrollPane.setPreferredSize(new Dimension(540,400));      

	    panel.add(jScrollPane);
	    frame.getContentPane().add(panel, BorderLayout.CENTER);    
	   } 
}
*/