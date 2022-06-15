package application;

import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.backend.LogFilesReader;
import main.common.Constants;
import main.frontend.UserInterface;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class MariaApp extends Application {
	
	 String tracePath;
	
	 @Override
	 public void start(Stage stage) throws Exception {
		Parameters params = getParameters(); //args from Main
		String tracePath = params.getNamed().get("tracePath");
	    String startFile = tracePath + Constants.START_FILE;
		
		/*Button button = new Button("Select a directory");
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				tracePath = getTracePath(stage);
			}
		});
		String startFile = tracePath + Constants.START_FILE;*/
	    
		// Backend
		LogFilesReader log = new LogFilesReader();
	    String firstProcess = String.valueOf(log.getLaunchProcess(startFile));
	    LogFilesReader.readLineByLine(tracePath + "trace_" + firstProcess + ".log");
	    
	     
	    // Frontend
	    WebView webView = new WebView();
	    WebEngine webEngine = webView.getEngine();
	    webEngine.loadContent(UserInterface.readHTMLFile_andBeautify());
	     
		/*HBox root = new HBox();
		root.getChildren().addAll(webView, button);*/
	    
	    Scene scene = new Scene(webView,600,800); //TODO: Change height
	    //scene.getStylesheets().add(getClass().getResource("view.css").toExternalForm());
	    stage.setScene(scene);
	    stage.setTitle("Thread Logger Viewer");
	    stage.show();
	}
	 
	 private String getTracePath(Stage stage) {
		 DirectoryChooser directoryChooser = new DirectoryChooser();
		 File selectedDirectory = directoryChooser.showDialog(stage);

		if(selectedDirectory == null){
			System.out.println("No directory selected");
			System.exit(0);
		}
		return selectedDirectory.getAbsolutePath() + "\\";
	 }
}
