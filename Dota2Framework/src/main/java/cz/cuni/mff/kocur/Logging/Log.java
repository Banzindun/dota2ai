package cz.cuni.mff.kocur.Logging;

public class Log {
	private static long initialTime = 0;
	
	private int priority; 
	private String data;
	private long time;
	private String location;
	
	public enum Type{
		ERROR, MESSAGE, WARNING, INIT
	}
	private Type type;

	/**
	 * Sets static initialTime (time of the start of application), so logs can measure their creation as (currentTime - initialTime).
	 * @param time long value of time (System.nanoTime()) in nanoseconds.
	 */
	public static void setInitialTime(long time) {
		initialTime = time;
	}	
	
	public Log(Type type, int priority, String location, String data) {
		this.type = type; 
		this.priority = priority;
		this.location = location;
		this.data = data;
		setTime(System.nanoTime());
	}
	
	public Log(Type type, int priority, long time, String location, String data) {
		this.type = type;
		this.priority = priority;
		setTime(time);
		this.location = location;
		this.data = data;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time - initialTime;	
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Outputs string representation of Log. For logging purposes. 	
	 */
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append(type + "\t");
		out.append(priority + "\t");
		out.append(time + "\t");
		out.append("location" + "\t");
		out.append(data + "\t\n");
		return out.toString();		
	}
}
