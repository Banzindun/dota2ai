package cz.cuni.mff.kocur.configuration;

import cz.cuni.mff.kocur.base.CustomStringBuilder;


/**
 * Main purpose of this class is to store the result of test that can be performed on configuration (BotConfiguration, GlobalConfiguration etc.).
 * @author Banzindun
 *
 */
public class ConfigurationTestResult extends CustomStringBuilder {
	/**
	 * Status of this test. True if the test passed.
	 */
	private boolean passed = false;

	/**
	 * Constructor.
	 * @param passed True if the test passed.
	 * @param msg Message that should be stored inside the result.
	 */
	public ConfigurationTestResult(boolean passed, String msg) {
		super();
		this.passed = passed;
		appendLine(msg);
	}	
	
	/**
	 * Constructor.
	 */
	public ConfigurationTestResult() {
		super();
	}

	/**
	 * @return Returns true if the test result is positive. False otherwise.
	 */
	public boolean passed() {
		return passed;
	}

	/**
	 * Sets the test result to supplied value.
	 * @param passed True if the test passed.
	 */
	public void setPassed(boolean passed) {
		this.passed = passed;
	}
	
}
