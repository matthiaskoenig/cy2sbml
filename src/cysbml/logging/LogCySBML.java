package cysbml.logging;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import cysbml.CySBMLPlugin;
import cytoscape.plugin.PluginManager;





public class LogCySBML {
	
	//// Print to System.out & System.err //////
	enum PrintLevels {
		CONFIG, INFO, WARNING, ERROR;
	}
	

	public static void config(String text){
		printLevel(System.out, PrintLevels.CONFIG, text);
	}
	
	public static void info(String text){
		printLevel(System.out, PrintLevels.INFO, text);
	}
	
	public static void warning(String text){
		printLevel(System.err, PrintLevels.WARNING, text);
	}
	
	public static void error(String text){
		printLevel(System.err, PrintLevels.ERROR, text);
	}
	
	private static void printLevel(PrintStream stream, PrintLevels level, String text){
		stream.println(String.format("%s[%s]-> %s", CySBMLPlugin.NAME, level.toString(), text));
	}
	
	/* Function to log StackTraces */
	public static String getStackTrace(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}	
	
	
	
	/*
	// Start logging
	LOGGER = LoggerTools.getCySBMLLogger();
	if (DEBUG){
		SimpleFormatter fmt = new SimpleFormatter();
		StreamHandler sh = new StreamHandler(System.out, fmt);
		LOGGER.addHandler(sh);
	}
	LOGGER.config(String.format("%s-%s : Initialization started", NAME, VERSION));
	*/
	
	public static Logger getCySBMLLogger() throws SecurityException, IOException{
		Logger logger = Logger.getLogger(CySBMLPlugin.class.getName());
		String fname = getCySBMLLoggingFilename();
		FileHandler logFileHandler = new FileHandler(fname);
		logger.setLevel(Level.CONFIG);
		logger.addHandler(logFileHandler);
		LogCySBML.info("Logging file -> " + fname);
		return logger;
	}
	
	public static String getCySBMLLoggingFilename(){
		String fname = PluginManager.getPluginManager().getPluginManageDirectory() + 
				 File.separator + String.format("%s-%s.log", CySBMLPlugin.NAME, CySBMLPlugin.VERSION);
		return fname;
	}
	

	
}
