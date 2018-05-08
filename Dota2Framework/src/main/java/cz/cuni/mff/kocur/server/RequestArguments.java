package cz.cuni.mff.kocur.server;

import javax.servlet.http.HttpServletResponse;

import cz.cuni.mff.kocur.framework.App;
import cz.cuni.mff.kocur.server.RequestHandler.CODE;

/**
 * This class should represent a object, that stores all necessary information
 * about an incoming request. It should store the url, incoming message and code
 * of the request (UPDATE, UPDATEDIRE ..).
 * 
 * @author kocur
 *
 */
public class RequestArguments {
	/**
	 * CODE of this method.
	 */
	private CODE method;

	/**
	 * Is this meant for server?
	 */
	private boolean server = false;

	/**
	 * Is this meant for client?
	 */
	private boolean agent = false;

	/**
	 * Name of the agent that issued this command (if this is meant for agent).
	 */
	private String agentName;

	/**
	 * Message, that came during the request. (This should be the json)
	 */
	private String message = "";

	/**
	 * Constructor.
	 */
	public RequestArguments() {

	}

	/**
	 * Sets this object from uri fields. Method code and resolution (agent/server)
	 * is set from this.
	 * 
	 * @param uriFields
	 *            Fields obtained from url address.
	 * @return Returns a response, that is set to passed if everything went
	 *         according to plan. Else there is a message attached to it with error.
	 */
	public Response set(String[] uriFields) {
		Response res = new Response();

		setResolution(uriFields[2].toLowerCase());

		// We are not server nor agent. Bad.
		if (!server && !agent) {
			return res.set(HttpServletResponse.SC_BAD_REQUEST,
					"Incorrect resolution. Second part of the url should be client/ or server/.");
		}

		// If server method will be on 4th position.
		if (server) {
			method = parseMethod(uriFields[3].toLowerCase());
		}
		// If client agent name is on 4th, method is on 5th. (.../bot_lina/update/)
		else {
			agentName = uriFields[3];
			method = parseMethod(uriFields[4].toLowerCase());
		}

		// Check the method is ok.
		checkMethod(res, method);
		return res; // Initially set as OK.
	}

	/**
	 * Creates a CODE representing a method (UPDATE, UPDATEDIRE) from a string.
	 * 
	 * @param method
	 *            Method in string.
	 * @return Returns code of the method.
	 */
	private CODE parseMethod(String method) {
		// Parse method string to cmd CODE
		CODE cmd;
		try {
			cmd = CODE.valueOf(method.toUpperCase());
		} catch (IllegalArgumentException e) {
			cmd = CODE.UNKNOWN;
		}
		return cmd;
	}

	/**
	 * Checks if the method is the correct one. For example during init there should
	 * be no updates, if update comes this method will return fail response.
	 * 
	 * @param res
	 *            Reference to response that will be modified.
	 * @param method
	 *            CODE of the method.
	 */
	private void checkMethod(Response res, CODE method) {
		if (App.state == App.State.INIT) {
			switch (method) {
			case SETUP:
				break;
			case SELECT:
				break;
			case SELECTED:
				break;
			case GAMESTART:
				break;
			case RESET:
				break;
			case PUSHINTERESTS:
				break;
			default:
				res.set(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
						"Method: " + method.name() + " not allowed during INIT.");
				break;
			}
		}
	}

	/**
	 * Sets the resolution of this request, meaning if the request is for server or
	 * client.
	 * 
	 * @param resolution
	 *            The string that should be saying the resolution.
	 */
	public void setResolution(String resolution) {
		if (resolution.equals("agent"))
			agent = true;
		else if (resolution.equals("server"))
			server = true;
	}

	/**
	 * Returns a stamp of this argument. Stamp is in format BOTID_timestamp or
	 * timestamp for case of big updates.
	 * 
	 * @return Returns a stamp of this argument.
	 */
	public String getStamp() {
		String stamp = "";
		if (forClient()) {
			stamp = agentName + "_" + System.currentTimeMillis();
		} else {
			stamp = "" + System.currentTimeMillis();
		}

		return stamp;
	}

	/**
	 * 
	 * @return Returns name of the agent.
	 */
	public String getAgentName() {
		return agentName;
	}

	/**
	 * 
	 * @param botName
	 *            New name of the agent.
	 */
	public void setAgentName(String botName) {
		this.agentName = botName;
	}

	/**
	 * 
	 * @return Returns true if arguments are meant for server.
	 */
	public boolean forServer() {
		return server;
	}

	/**
	 * 
	 * @return Returns true if arguments are meant for agent.
	 */
	public boolean forClient() {
		return agent;
	}

	/**
	 * 
	 * @param msg
	 *            New message for the arguments.
	 */
	public void setMessage(String msg) {
		message = msg;
	}

	/**
	 * 
	 * @return Returns message from the arguments.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * 
	 * @return Returns code of the method.
	 */
	public CODE getMethod() {
		return method;
	}

}
