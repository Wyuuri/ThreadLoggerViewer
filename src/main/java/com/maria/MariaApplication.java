// SWING version's main

/*package main.java.com.maria;
import main.java.com.maria.common.Constants;
import main.java.com.maria.service.LogFilesReader;
//import main.java.com.maria.view.UserInterface;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


public class MariaApplication extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		WebView myWebView = new WebView();
		WebEngine engine = myWebView.getEngine();
		
		Button btn = new Button("Load Google");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				engine.load("https://www.google.com");
			}
		});
		
		Button btn2 = new Button("Execute JavaScript");
		btn2.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				engine.executeScript("window.location = \"https://www.youtube.com/\";");
			}
		});
		
		Button btn3 = new Button("Load Local");
		btn3.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				engine.loadContent("<html><body><h1>Hello</h1><body></html>");
			}
		});
		
		Button btn4 = new Button("Reload");
		btn4.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				engine.reload();
			}
		});
		
		engine.setUserAgent("Mozilla/5.0 (Linux; U; Android 2.2.1; en-us; Nexus One Build/FRG83) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
		
		
		VBox root = new VBox();
		root.getChildren().addAll(myWebView, btn, btn2, btn3, btn4);
		
		Scene scene = new Scene(root, 800, 500);
		stage.setScene(scene);
		
		stage.show();
	}

	
	public static void main(String[] args) {
        /*System.out.println("Starting...");
        
        LogFilesReader log = new LogFilesReader();
        String firstProcess = String.valueOf(log.getLaunchProcess(Constants.PATH + "trace_result.log"));
        log.readLineByLine(Constants.PATH + "trace_" + firstProcess + ".log");
        log.getSpawnedProcess(Constants.PATH + "trace_81.log");
        
        //log.getAllProcessesNumbers(Constants.PATH);
        log.getSendMsg();
        
        UserInterface.createWindow();
        log.readHTMLFile_andBeautify(); /
        
        Application.launch(args);
    }
}
*/
