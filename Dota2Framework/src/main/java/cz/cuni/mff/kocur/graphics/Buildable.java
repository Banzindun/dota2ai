package cz.cuni.mff.kocur.graphics;

import java.awt.Component;

import cz.cuni.mff.kocur.exceptions.ComponentNotBuiltException;


/**
 * I need graphical objects to be able to build themselves. For example during construction I need the ConfigurationPanel to be created, but I want it to build after I have some configurations to display.</br> 
 * So this interface should ensure there is a build() method to call, as well as a way how to get reference to the component.
 * @author kocur
 *
 */
public interface Buildable {
	
	/**
	 * Builds the component.
	 */
	public void build();
	
	/**
	 * 
	 * @return Returns the reference to component that is beeing displayed. (So the cast should be done inside graphic classes)
	 * @throws ComponentNotBuiltException Thrown if this component haven't been built yet before. 
	 */
	public Component getComponent() throws ComponentNotBuiltException;
	
	/**
	 * Returns reference to this.
	 * @return Buildable object.
	 * @throws ComponentNotBuiltException If this component haven't been built prior to getting it. 
	 */
	public Buildable get() throws ComponentNotBuiltException;
}
