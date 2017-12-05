package cz.cuni.mff.kocur.Configuration;

import java.util.List;

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
	private List<String> selection;
	
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
	
	public List<String> getSelection(){
		return selection;
	}
	
	public void setSelection(List<String> selection) {
		this.selection = selection;
	}
	
	@Override
	public String toString() {
		// TO DO - should print JSON representation
		
		StringBuilder builder = new StringBuilder();
		builder.append("\t\t" + quote("type") + " : " + quote(type) + ",\n");
		builder.append("\t\t" + quote("value") + ": " + quote(value) + ",\n");
		builder.append("\t\t" + quote("selection:") + " : [");
		
		for (int i = 0; i < selection.size(); i++) {
			if (i == selection.size() - 1) builder.append(quote(selection.get(i)) + "]\n");
			else builder.append(quote(selection.get(i)) + ", ");
			
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
