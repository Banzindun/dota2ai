package cz.cuni.mff.kocur.Logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.kocur.Logging.Log.Type;

public class Logger {
	private static Map<String, Logger> loggers = new HashMap<String, Logger>(); 
	
	/**
	 * Returns logger instance, that logs class with {@link className}
	 * @param className Name of the class the logger is created for.
	 * @return Reference to instance of logger with className.
	 */
	public static Logger getLogger(String className) {
		if (!loggers.containsKey(className)) loggers.put(className, new Logger(className)); 
		return loggers.get(className);
	}
	
	public static boolean exists(String className) {
		return loggers.containsKey(className);
	}
	
	/**
	 * Creates new logger above supplied className.
	 * @param className
	 */
	public Logger(String className) {
		this.className = className;
	}
		
	private List<Log> logs = new ArrayList<Log>(); 
	private String className;


	/**
	 * Creates log with supplied information. Location is equal to className.
	 * @param type Type of message (ERROR, MSG etc.)
	 * @param priority
	 * @param data Message.
	 */
	public void log(Type type, int priority, String data) {
		logs.add(new Log(type, priority, className, data));
	}
	
	/**
	 * Creates log with supplied information.
	 * @param type Type of message (ERROR, MSG etc.)
	 * @param priority
	 * @param location Additional information towards location.
	 * @param data Message.
	 */
	public void log(Type type, int priority, String location, String data) {
		logs.add(new Log(type, priority, className + "->" + location, data));
	}
	
	/**
	 * Creates log with supplied information.
	 * @param type Type of message.
	 * @param priority 
	 * @param time
	 * @param location Aka some additional information about location.
	 * @param data
	 */
	public void Log(Type type, int priority, long time, String location, String data) {
		logs.add(new Log(type, priority, time, className + "->" + location, data));
	}
	
	/**
	 * Returns last log in string format.
	 * @return
	 */
	public String last() {
		return logs.get(logs.size()-1).toString();	
	}
	
}
