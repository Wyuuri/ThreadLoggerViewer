package main.java.com.maria;
import main.java.com.maria.service.LogFilesReader;

public class MariaApplication {
	
	final static String PATH = "D:\\trace\\";
	
	public static void main(String[] args) {
        System.out.println("Starting...");
        
        LogFilesReader log = new LogFilesReader();
        log.getLaunchProccess(PATH + "trace_result.log");
        log.readLineByLine(PATH + "trace_81.log");
    }
}
