package cz.cuni.mff.kocur.streaming;

import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Sink {
	/**
	 * Logger registered for sink.
	 */
	private static Logger logger = LogManager.getLogger(Sink.class.getName());
		
	/**
	 * Map of sources and informations that flowed from them. 
	 */
	private static TreeMap<Integer, Source> sources = new TreeMap<>(); 

	/**
	 * First available index that I can assign to newly added source.
	 */
	private static Integer availableIndex = new Integer(0);

	/**
	 * Catchers that catch the are alerted upon information drop.
	 */
	private static ArrayList<DropCatcher> catchers = new ArrayList<>();
	
	/**
	 * True if there was a change in catchers (added or removed one). </br>
	 * If there is someone who works with this information he should flip this back.
	 */
	private static boolean catchersChanged = false;
	
	/**
	 * Drains the water coming from source with supplied id. </br>
	 * This should be called from source's flow method. So that the information doesn't leak. 
	 * @param id Id of the source that is flowing the information.
	 * @param d Drop of information.
	 */
	public static void drain(InformationDrop d) {
		for (DropCatcher c : catchers) {
			c.catchDrop(d);
		}
	}
		
	/**
	 * Adds source to sources and sets its id. 
	 * @param s Source to be added.
	 */
	public static void addSource(Source s) {
		assert !sources.containsKey(availableIndex) : "Wrong index.";
		
		logger.info("Adding source: " + s.getSourceName());
		
		sources.put(availableIndex,s);
		s.setSourceId(availableIndex);
		
		availableIndex++;
	}
	
	/**
	 * Removes source from stored sources if possible.
	 * @param s Source to be removed.
	 */
	public static void removeSource(Source s) {
		if (sources.remove(s.getSourceId()) == null) {
			logger.warn("Source with id: " + s.getSourceId() + "wasn't stored inside sources.");
		}
	}
	
	/**
	 * Adds catcher to catchers.
	 * @param c New catcher to add.
	 */
	public static void addCatcher(DropCatcher c) {
		logger.debug("Adding catcher.");
		catchers.add(c);
		catchersChanged = true;
	}
	
	/**
	 * Removes catcher from catchers.
	 * @param c Catcher to be removed.
	 */
	public static void removeCatcher(DropCatcher c) {
		if (catchers.remove(c)) {
			catchersChanged = true;
		} else {
			logger.warn("Drop catcher could not be removed.");
		}		
	}
	
	/**
	 * @return Returns list of drop catchers.
	 */
	public static ArrayList<DropCatcher> getCatchers() {
		return catchers;
	}
		
	/**
	 * 
	 * @return Returns array of sources that are stored inside the sink.
	 */
	public static Source[] getSources() {
		Source[] out = new Source[sources.values().size()];
		
		int i = 0;
		for (Source s : sources.values()) {
			out[i] = s;
			i++;
		}
		
		return out;
	}

	public static Integer[] getIds() {
		return sources.keySet().toArray(new Integer[sources.size()]);
		
		
	}

	public static boolean hasCatchersChanged() {
		return catchersChanged;
	}

	public static void setCatchersChanged(boolean catchersChanged) {
		Sink.catchersChanged = catchersChanged;
	}
	
	
	
	
	
}
