package cz.cuni.mff.kocur.base;

import java.util.Map;
import java.util.Map.Entry;

import cz.cuni.mff.kocur.configuration.CItem;

/**
 * This class extends CustomStringBuilder and defines methods for adding indentation for 
 * lines I am adding. You can add a few lines, increase the indent, add few more, decrease the indent.
 * 
 * This helps a lot when creating some text output that should be indented.
 * @author kocur
 *
 */
public class IndentationStringBuilder extends CustomStringBuilder{
	/**
	 * Indentation string of this builder. 
	 */
	private String indent = "";
		
	/**
	 * Constructor.
	 */
	public IndentationStringBuilder() {
		super();
	}
	
	/**
	 * Constructor with indent.
	 * @param indent Initial indent of the builder.
	 */
	public IndentationStringBuilder(String indent) {
		this.indent = indent;
	}
	
	/**
	 * Appends map values to the builder in form key:value.
	 * @param in Map of strings and some type T that can be printed using toString().
	 */
	public <T> void appendMap(Map<String, T> in) {
		for (Entry<String, T> e : in.entrySet()) {
			if (e.getValue() == null)
				appendVariable(e.getKey(), "null");
			else 
				appendVariable(e.getKey(), e.getValue());
		}
	}
	
	/**
	 * Increases indent
	 */
	public void indent() {
		indent = indent + "\t";
	}
	
	/**
	 * Decreases indent.
	 */
	public void deindent() {
		if (indent.length() == 0 || indent.length() == 1) indent = "";
		else indent = indent.substring(0, indent.length()-2);
	}
	
	/**
	 * 
	 * @return Returns the indentation string. 
	 */
	public String getIndent() {
		return indent;
	}
	
	/**
	 * Sets the indentation string.
	 * @param indent The indentation string.
	 */
	public void setIndent(String indent) {
		this.indent = indent;
	}

	/**
	 * 
	 * @param in String that should be quoted.
	 * @return Returns a string surruounded by double quotes
	 */
	private String quote(String in) {
		return "\"" + in + "\"";
	}
	
	/**
	 * Appends a line with indentation.
	 */
	@Override
	public void appendLine(String line) {
		builder.append(indent + line + ls);
	}
	
	/**
	 * Appends a line with indentation.
	 */
	@Override
	public void append(String line) {
		builder.append(indent + line);
	}
	
	/**
	 * Appends variable to builder in form key:value.
	 * If the second argument is {@link cz.cuni.mff.kocur.configuration.CItem},
	 * then it appends it's toString() representation.
	 * @param key Name of the variable.
	 * @param value Value of the variable. toString() will be called on the variable.
	 */
	public <T> void appendVariable(String key, T value) {
		if (value instanceof CItem) {
			appendLine(key + " : ");
			indent();
			String[] lines = value.toString().split(ls);
			for(String l : lines) {
				appendLine(l);
			}			
			
			deindent();
		}
		else 
			appendLine(key + " : " + value.toString());
	}
}
