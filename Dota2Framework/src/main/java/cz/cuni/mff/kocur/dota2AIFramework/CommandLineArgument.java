package cz.cuni.mff.kocur.dota2AIFramework;

/**
 * Class that represents command line argument (abc=def). 
 * Command line argument stores it's name and value, if there is such a thing supplied.
 * @author kocur
 *
 */
public class CommandLineArgument {
	
	/**
	 * Name of this argument.
	 */
	private String fieldName = "";
	
	/**
	 * Value of this argument. 
	 */
	private String value = null;
	
	/**
	 * Constructor.
	 */
	public CommandLineArgument() {
		
	}
	
	/**
	 * Constructor that takes fieldName and value.
	 * @param fieldName Name of the argument. 
	 * @param value Value of the argument (if there is one)
	 */
	public CommandLineArgument(String fieldName, String value) {
		this.fieldName = fieldName;
		this.value = value;
	}
		
	/**
	 * 
	 * @return Returns this argument's name.
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Sets this arguments name.
	 * @param fieldName Arguments name.
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * 
	 * @return Returnsthis argument's value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets this argument's value.
	 * @param value Value.
	 */
	public void setValue(String value) {
		this.value = value;
	}


	
}
