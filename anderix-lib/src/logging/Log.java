/**
 * Copyright (c) 2003-2006  Anderix
 * All Rights Reserved
 */
package anderix.logging;

import java.io.*;
import java.util.Date;
import anderix.io.*;

public class Log {

	protected String logName = "application";
	protected String logPath = "./logs";

	
	public static String parseStackTrace(Throwable t) {
		StringWriter sout = new StringWriter();
		t.printStackTrace(new PrintWriter(sout));
		return sout.toString();
	}

	
	public Log() {
	
	}

    public Log(String logName) {
    	this.logName = logName;
    }
    
    public Log(String logName, String logPath) {
    	this.logName = logName;
    	this.logPath = logPath;
    }

    public void error(Throwable t) {
		log("ERROR", parseStackTrace(t));
    }

    public void error(String logName, Throwable t) {
		this.logName = logName;
		log("ERROR", parseStackTrace(t));
    }
    
    public void error(String msg) {
		log("ERROR", msg);
    }

    public void error(String logName, String msg) {
		this.logName = logName;
		log("ERROR", msg);
    }

    public void warn(String msg) {
		log("WARNING", msg);
    }

    public void warn(String logName, String msg) {
		this.logName = logName;
		log("WARNING", msg);
    }

    public void info(String msg) {
		log("INFORMATION", msg);
    }

    public void info(String logName, String msg) {
    	this.logName = logName;
		log("INFORMATION", msg);
    }

    protected void log(String logType, String msg) {
        try {
			if ( !( new File(logPath).exists() ) ) {
				new File(logPath).mkdirs();
			}
			BufferedWriter out = new BufferedWriter(
				new FileWriter(logPath + "/" + logName + ".log", true)
			);
			out.write(logType + "\t");
			out.write(new Date().toString() + "\t");
			out.write(msg);
			out.newLine();
			out.close();
        } catch ( IOException ioex ) {
            throw new RuntimeException("Attempt to write exception to log " +
                "failed. Exception details:\nType: " + logType +
                "\nApplication: " + logName +
                "\nMessage: " + msg);
        }
    }

}
