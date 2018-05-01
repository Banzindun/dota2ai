package cz.cuni.mff.kocur.console;

import cz.cuni.mff.kocur.base.CustomStringBuilder;

/**
 * Class that represents a console command response. The response stores the
 * status of the command, that was performed as well as it's message.
 * 
 * @author kocur
 *
 */
public class CommandResponse extends CustomStringBuilder {
	private boolean passed = true;

	/**
	 * Sets this response as done with given status.
	 * 
	 * @param d
	 *            Status of this response (true = passed, false = failed)
	 */
	public void setDone(boolean d) {
		passed = d;
	}

	/**
	 * Sets this response as failed.
	 * 
	 * @return Returns reference to this.
	 */
	public CommandResponse fail() {
		passed = false;
		return this;
	}

	/**
	 * Sets the message of this response and sets its status as failed.
	 * 
	 * @param msg
	 *            Message to be stored as a response.
	 * @return Returns reference to this.
	 */
	public CommandResponse fail(String msg) {
		clear(msg);
		return fail();
	}

	/**
	 * Sets this object to passed.
	 * 
	 * @return Returns reference to self.
	 */
	public CommandResponse pass() {
		passed = true;
		return this;
	}

	/**
	 * Sets this command response as passed and appends the given message.
	 * 
	 * @param msg
	 *            Message that should be stored inside this response.
	 * @return Returns reference to this object.
	 */
	public CommandResponse pass(String msg) {
		clear(msg);
		return pass();
	}

	/**
	 * 
	 * @return Returns true if command that created this response passed.
	 */
	public boolean passed() {
		return passed;
	}

	/**
	 * 
	 * @return Returns true if command that created this response failed.
	 */
	public boolean failed() {
		return !passed;
	}

	/**
	 * Inserts a string to start of the stored String data.
	 * 
	 * @param s
	 *            String to be inserted to the start.
	 */
	public void insertToStart(String s) {
		builder.insert(0, s);
	}

	/**
	 * Set stored message by rewrite what was stored here before.
	 * 
	 * @param msg
	 *            String to be stored inside this object.
	 */
	public void setMsg(String msg) {
		builder = new StringBuilder(msg);
	}

}
