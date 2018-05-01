package cz.cuni.mff.kocur.events;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * This class holds events of the same type. 
 * Lets user query them using the distance etc. 
 * @author kocur
 *
 */
public abstract class EventsHolder<T extends Event> {
	/**
	 * I will be adding to the end and removing from the beginning. Because I add events in order, I will have older events at the beginning.
	 */
	protected ConcurrentLinkedQueue<T> events = new ConcurrentLinkedQueue<>(); 
	
	/**
	 * After how many inserted events we should perform a cleanup. This should be configured inside the frameworkConfiguration. 
	 */
	protected int cleanupPeriod = 10;
	
	/**
	 * How many adds were performed after last cleanup.
	 */
	protected int lastCleanup = 0;
	
	/**
	 * Adds a new event. 
	 * @param e New event.
	 */
	public void add(T e) {
		events.add(e);
		
		++lastCleanup;
		if (lastCleanup > cleanupPeriod)
			cleanup();
	}
	
	/**
	 * Goes through all the events and removes those that are too old (==irrelevant).
	 */
	public void cleanup() {
		Iterator<T> it = events.iterator();
		while(it.hasNext()) {
			Event e = it.next();
			if (e.isIrrelevant()) 
				it.remove();
			else {
				// Events should be stored from the newest to the oldest. We stop if we reach events that are not old. (relevant)
				break;
			}
		}
	}
	
	/**
	 * 
	 * @return Returns all events in list.
	 */
	public List<T> toList() {
		LinkedList<T> ts = new LinkedList<>();
		for (T e : events) {
			ts.add(e);
		}
		return ts;
	}

	/**
	 * 
	 * @param maxAge Maximum age of the events.
	 * @return Returns all younger events.
	 */
	public List<T> getYounger(int maxAge) {
		long t = System.currentTimeMillis();
		return events.stream().filter(e -> t - e.getTime() < maxAge).collect(Collectors.toList());
	}

}
