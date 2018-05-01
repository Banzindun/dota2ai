package cz.cuni.mff.kocur.server;

import javax.servlet.http.HttpServletResponse;

/**
 * This class is a helper class, that helps us to create a response to request.
 * Main goal of this class is to contain message, object and status. Message and
 * object will be serialized to JSON once we will be sending the response.
 * 
 * @author kocur
 *
 */
public class Response {
	/**
	 * Status of this response. 
	 */
	private int status = HttpServletResponse.SC_OK;

	/**
	 * Message.
	 */
	private String msg = "";

	/**
	 * Object, that should be deserialized. Here will be usually stored server's or agent's command.
	 */
	private Object o = null;

	/**
	 * Constructor.
	 */
	public Response() {

	}

	/**
	 * Constructor that takes a message.
	 * @param msg Message.
	 */
	public Response(String msg) {
		this.msg = msg;
	}

	/**
	 * Constructor that takes a message and a status.
	 * @param msg Message.
	 * @param status Status of this response.
	 */
	public Response(int status, String msg) {
		this.msg = msg;
		this.status = status;
	}

	/**
	 * Constructor that takes the object for deserialization.
	 * @param o Object that should be deserialized at the end.
	 */
	public Response(Object o) {
		this.o = o;
	}

	/**
	 * Sets status and message of this response.
	 * @param status New status code.
	 * @param msg New message.
	 * @return Returns reference to this response (for chaining).
	 */
	public Response set(int status, String msg) {
		this.status = status;
		this.msg = msg;

		return this;
	}

	/**
	 * Sets status.
	 * @param s New status.
	 */
	public void setStatus(int s) {
		status = s;
	}

	/**
	 * Gets status.
	 * @return Returns status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Sets msg.
	 * @param msg New msg.
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * 
	 * @return Returns message.
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * Sets new object.
	 * @param o New object.
	 */
	public void setObject(Object o) {
		this.o = o;
	}

	/**
	 * 
	 * @return Returns the stored object.
	 */
	public Object getObject() {
		return o;

	}

	/**
	 * Returns true, if the status of this response equals to HttpServletResponse.SC_OK.
	 * @return True or false. True if status of this response equals to HttpServletResponse.SC_OK.
	 */
	public boolean isOK() {
		return status == HttpServletResponse.SC_OK;
	}

}
