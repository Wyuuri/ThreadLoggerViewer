package application;

import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.backend.LogFilesReader;
import main.common.Constants;
import main.frontend.UserInterface;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class App extends Application {
	
	 private static WebEngine webEngine;
	 String tracePath;
	
	 @Override
	 public void start(Stage stage) throws Exception {
		 
	    // Frontend
	    WebView webView = new WebView();
	    webEngine = webView.getEngine();
	    
	    Button button = new Button("Select a directory");
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				tracePath = getTracePath(stage);
				loadHTML(tracePath);
			}
		});

		ToolBar toolBar = new ToolBar();
	    toolBar.getItems().add(button);
	    
	    VBox vBox = new VBox();
	    VBox.setVgrow(webView, Priority.ALWAYS);
	    vBox.getChildren().addAll(toolBar, webView);
	    
	    // Default path for testing
	    tracePath = "D:\\trace\\";
		loadHTML(tracePath);
	    
	    Scene scene = new Scene(vBox,800,800);
	    scene.getStylesheets().add(getClass().getResource("/main/frontend/button.css").toExternalForm());
	    stage.setScene(scene);
	    stage.getIcons().add(new Image(getClass().getResourceAsStream("/main/frontend/icon.jpg")));
	    stage.setTitle("Thread Logger Viewer");
	    stage.show();
	}
	 
	 private static void loadHTML(String tracePath) {
		 String startFile = tracePath + Constants.START_FILE;
		 
		// Backend
		String firstProcess = String.valueOf(LogFilesReader.getLaunchProcess(startFile));
		LogFilesReader.setTracePath(tracePath);
		LogFilesReader.readLineByLine(tracePath + "trace_" + firstProcess + ".log");
		    
		webEngine.loadContent(UserInterface.readHTMLFile_andBeautify(tracePath));
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
