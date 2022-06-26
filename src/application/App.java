package application;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.backend.LogFilesReader;
import main.common.Constants;
import main.frontend.UserInterface;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
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
				if(tracePath != null) { loadHTML(tracePath); }
			}
		});

		ToolBar toolBar = new ToolBar();
	    toolBar.getItems().add(button);
	    
	    VBox vBox = new VBox();
	    VBox.setVgrow(webView, Priority.ALWAYS);
	    vBox.getChildren().addAll(toolBar, webView);
	    
	    Scene scene = new Scene(vBox);
	    scene.getStylesheets().add(Constants.CSS_FILEPATH);
	    stage.setScene(scene);
	    stage.getIcons().add(new Image(Constants.LOGO_PATH));
	    stage.setTitle("Thread Logger Viewer");
	    stage.show();
	}
	 
	 /**
	  * Executes the back-end side and then the front-end side.
	  * Loads the HTML to the WebEngine.
	  * 
	  * @param tracepath - The absolute path where all log files reside.
	  */
	 private static void loadHTML(String tracePath) {
		 String startFile = tracePath + Constants.START_FILE;
		 
		// Backend
		String firstProcess;
		try {
			firstProcess = String.valueOf(LogFilesReader.getLaunchProcess(startFile));
			LogFilesReader.updateTracePath(tracePath);
			LogFilesReader.cleanMaps();
			LogFilesReader.readLineByLine(tracePath + "trace_" + firstProcess + ".log");
			
			try {
				webEngine.loadContent(UserInterface.readHTMLFile_andBeautify(tracePath));
			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Error");
				alert.setContentText("Content could not be loaded.");
				alert.showAndWait();
			} catch (Exception e) { // Possible deadlock in the yCoordinates algorithm
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Error");
				alert.setContentText(e.getMessage());
				alert.showAndWait();
			}
		} catch (NumberFormatException | IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Error");
			alert.setContentText("The selected directory does not contain all required log files.");
			alert.showAndWait();
		}
		
	 }
	 
	 /**
	  * Shows a dialog for the user to choose a file directory.
	  * 
	  * @param stage - The primary stage
	  * @return The absolute path where all log files reside.
	  */
	 private String getTracePath(Stage stage) {
		 DirectoryChooser directoryChooser = new DirectoryChooser();
		 File selectedDirectory = directoryChooser.showDialog(stage);
		
		 if(selectedDirectory == null){
			System.out.println("No directory selected");
			return null;
		 }
		 return selectedDirectory.getAbsolutePath() + File.separator;
	 }
}
