package main.java.com.maria;
import main.java.com.maria.common.Constants;
import main.java.com.maria.service.LogFilesReader;

public class MariaApplication {
	
	public static void main(String[] args) {
        System.out.println("Starting...");
        
        LogFilesReader log = new LogFilesReader();
        String firstProcess = String.valueOf(log.getLaunchProcess(Constants.PATH + "trace_result.log"));
        log.readLineByLine(Constants.PATH + "trace_" + firstProcess + ".log");
        log.getSpawnedProcess(Constants.PATH + "trace_81.log");
    }
}
