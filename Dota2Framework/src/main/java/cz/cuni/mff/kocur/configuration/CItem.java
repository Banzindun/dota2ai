package cz.cuni.mff.kocur.configuration;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.kocur.base.IndentationStringBuilder;

/**
 * Represents item that is stored under "configuration" in JSON configuration file and that contains following values: type, value, label, help.
 * These values are then used to display the configuration of this value inside configuration tab inside framework. 
 * @author Banzindun
 *
 */
public class CItem {
	/**
	 * Stores type argument. Can be JRadioButton, ..
	 */
	private String type;
	
	/**
	 * Initial value of configuration.
	 */
	private String value;
	
	/**
	 * Label informing user about the field.
	 */
	private String label;
	
	/**
	 * Help of this field.
	 */
	private String help;
		
	/**
	 * Selection, that user has.
	 */
	private ArrayList<String> options;
	
	/**
	 * 
	 * @return Returns type of this field. 
	 */
	public String getType()	{
		return type;		
	}
	
	/**
	 * Sets a type of this item.
	 * @param type New type of this item.
	 */
	public void setType(String type) {
		this.type = type;		
	}
	
	/**
	 * 
	 * @return Returns stored value.
	 */
	public String getValue() {
		return value;		
	}
	
	/**
	 * Sets a new value.
	 * @param value New value.
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * 
	 * @return Returns list of options.
	 */
	public List<String> getOptions(){
		return options;
	}
	
	/**
	 * Sets a new list of options.
	 * @param options New list of options.
	 */
	public void setOptions(ArrayList<String> options) {
		this.options = options;
	}

	@Override
	public String toString() {
		IndentationStringBuilder builder = new IndentationStringBuilder();
		builder.appendVariable("value", value);
		if (options != null) {
			builder.append("\"options\"" + " : [");
		
			for (String o : options) {
				builder.append(o + ", ");
			}
			builder.appendLine("]");
			
		}
		return builder.toString();
	}

	/**
	 * 
	 * @return Returns the label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets a new label.
	 * @param label New label.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * 
	 * @return Returns the help.
	 */
	public String getHelp() {
		return help;
	}

	/**
	 * Sets a new help String. 
	 * @param help New help. 
	 */
	public void setHelp(String help) {
		this.help = help;
	}
		
	
}
