package application;

import javafx.application.Application;
import main.common.Constants;

public class Main {
	public static void main(String[] args) {
		Application.launch(MariaApp.class, 
		"--tracePath=" + Constants.PATH);
	}
}
