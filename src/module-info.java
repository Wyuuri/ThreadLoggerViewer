module ThreadLoggerViewer {
	requires javafx.controls;
	requires javafx.web;
	requires javafx.graphics;
	requires javafx.base;
	
	opens application to javafx.graphics, javafx.fxml;
}
