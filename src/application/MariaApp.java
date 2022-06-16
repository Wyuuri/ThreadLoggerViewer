package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class MariaApp extends Application {
	
	 private final static String htmlFilepath = Constants.UI_PATH + Constants.HTML_FILENAME;
	 private static WebEngine webEngine;
	 String tracePath;
	
	 @Override
	 public void start(Stage stage) throws Exception {
		Button button = new Button("Select a directory");
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				tracePath = getTracePath(stage);
				loadHTML(tracePath);
			}
		});
		
	    // Frontend
	    WebView webView = new WebView();
	    webEngine = webView.getEngine();
	    
	    HBox hBox = new HBox();
		hBox.getChildren().addAll(button);
	    
	    VBox vBox = new VBox();
	    VBox.setVgrow(webView, Priority.ALWAYS);
	    vBox.getChildren().addAll(hBox, webView);
	    
	    Scene scene = new Scene(vBox,600,800); //TODO: Change height
	    //scene.getStylesheets().add(getClass().getResource("view.css").toExternalForm());
	    stage.setScene(scene);
	    stage.setTitle("Thread Logger Viewer");
	    stage.show();
	}
	 
	 private static void loadHTML(String tracePath) {
		 String startFile = tracePath + Constants.START_FILE;
		 
		// Backend
		LogFilesReader log = new LogFilesReader();
		String firstProcess = String.valueOf(log.getLaunchProcess(startFile));
		LogFilesReader.readLineByLine(tracePath + "trace_" + firstProcess + ".log");
		    
		webEngine.loadContent(UserInterface.readHTMLFile_andBeautify());
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
