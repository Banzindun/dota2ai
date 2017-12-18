package cz.cuni.mff.kocur.Configuration;

import java.util.ArrayList;
import java.util.List;


// TO DO ADD COMMENTS

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
	 * Selection, that user has.
	 */
	private ArrayList<String> options;
	
	public String getType()	{
		return type;		
	}
	
	public void setType(String type) {
		this.type = type;		
	}
	
	public String getValue() {
		return value;		
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public List<String> getOptions(){
		return options;
	}
	
	public void setOptions(ArrayList<String> selection) {
		this.options = selection;
	}
	
	@Override
	public String toString() {
		// TO DO - should print JSON representation
		
		StringBuilder builder = new StringBuilder();
		builder.append("\t\t" + quote("type") + " : " + quote(type) + ",\n");
		builder.append("\t\t" + quote("value") + ": " + quote(value) + ",\n");
		builder.append("\t\t" + quote("selection:") + " : [");
		
		for (int i = 0; i < options.size(); i++) {
			if (i == options.size() - 1) builder.append(quote(options.get(i)) + "]\n");
			else builder.append(quote(options.get(i)) + ", ");
			
		}
				 
		builder.append("\t}\n");
		return builder.toString();
	}


	private String quote(String s){
		return "\"" + s + "\"";
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
		
	
}
