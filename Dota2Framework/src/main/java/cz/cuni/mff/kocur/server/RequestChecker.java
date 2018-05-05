package cz.cuni.mff.kocur.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Class that checks, that the incoming request is correct. Meaning it has the
 * correct type of information inside, it is a POST etc.
 * 
 * @author kocur
 *
 */
public class RequestChecker {
	/**
	 * Logger for RequestChecker class.
	 */
	private final static Logger logger = LogManager.getLogger(RequestChecker.class);

	/**
	 * Serves for checking if received data are in a correct form.
	 */
	private final static String ACCEPT_FIELD = "accept";

	/**
	 * Serves for checking if received data are in a correct form.
	 */
	private final static String APPLICATION_JSON = "application/json";

	/**
	 * Serves for checking if received data are in a correct form.
	 */
	private final static String CONTENT_TYPE = "content-type";

	/**
	 * Sets headers of the response.
	 * 
	 * @param response
	 *            Response, which headers should be set. (application/json etc.)
	 */
	public static void setHeaders(HttpServletResponse response) {
		response.setContentType(APPLICATION_JSON);
	}

	/**
	 * Checks that this request is ok. If not, the response argument will be set to
	 * failed.
	 * 
	 * @param request
	 *            Request that we are checking.
	 * @param res
	 *            Response. We will write errors to it and set it as failed if the
	 *            check failed.
	 */
	public static void checkRequest(HttpServletRequest request, Response res) {
		// Check that this is a post
		requirePost(request, res);
		if (res.getStatus() != HttpServletResponse.SC_OK)
			return;

		// Check that that the issuer is accepting json
		assureAcceptJSON(request, res);
		if (res.getStatus() != HttpServletResponse.SC_OK)
			return;

		// Check that the data has json in it
		requireJSON(request, res);
		if (res.getStatus() != HttpServletResponse.SC_OK)
			return;
	}

	/**
	 * Checks that the request is a POST request.
	 * 
	 * @param request
	 *            Request.
	 * @param res
	 *            Response, where we will write errors.
	 */
	private static void requirePost(HttpServletRequest request, Response res) {
		if ("POST".equalsIgnoreCase(request.getMethod()))
			return;

		logger.warn("Received request that is not POST.");

		res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		res.setMsg("The request was not POST.");
	}

	/**
	 * Returns array of parameters obtained from the URI. These are the parts of url
	 * address. Aditionaly, if the URI parameters are in incorrect form or the
	 * address is wrong, the response is set as failed.
	 * 
	 * @param request
	 *            HttpServletRequest request.
	 * @param res
	 *            Response on request.
	 * @return Returns array of URI fields, that might be useful.
	 */
	public static String[] getURIparams(HttpServletRequest request, Response res) {
		if (res.isOK() == false)
			return null;

		// Split the fields.
		String[] uriFields = request.getRequestURI().split("/");

		// First field will be "", second one should be Dota2AI
		if (!uriFields[1].equals("Dota2AI")) {
			logger.error("Unknown service:" + uriFields[1] + ". I know just Dota2AI.");

			res.setMsg("Unknown service. I know just Dota2AI.");
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return uriFields;
		}

		// Now we check if we are working with server or client.
		// Server is simpler.
		// Client should contain name of the agent that is sending the request.
		// We set the size accordingly.
		int size = 4;
		if (uriFields[2].equals("server"))
			size = 4;
		else if (uriFields[2].equals("client"))
			size = 5;
		else {
			logger.error("Unknown resolution. I know only client and server.");

			res.setMsg("Unknown resolution. I know only client and server.");
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return uriFields;
		}

		if (uriFields.length != size) {
			logger.error("Input url is too short.");

			res.setMsg("Input url is too short.");
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		return uriFields;
	}

	/**
	 * Ensures that the "accept" header is either set to JSON or empty. If the
	 * header is incorrect, we set the response as failed.
	 * 
	 * @param request
	 *            Request.
	 * @param res
	 *            Response.
	 */
	private static void assureAcceptJSON(HttpServletRequest request, Response res) {
		if (APPLICATION_JSON.equals(request.getHeader(ACCEPT_FIELD)))
			return;

		logger.error("Obtained data do not have accept set to application json!");

		res.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
		res.setMsg("Set accept header to application/json.");
	}

	/**
	 * Creates the response, that should be sent to client. The response is created
	 * by serializing the object stored inside res argument to json string. If no
	 * object is present, we serialize message contained inside the res argument.
	 * 
	 * @param sResponse
	 *            Servlet response, where we will store the result.
	 * @param res
	 *            Response object, from which we will take the object for
	 *            serialization.
	 * @return Returns the string, that was written to response.
	 */
	public static String craftServletResponse(HttpServletResponse sResponse, Response res) {
		// Set headers and status to response
		setHeaders(sResponse);
		sResponse.setStatus(res.getStatus());

		String toWrite = "";

		if (res.getStatus() == HttpServletResponse.SC_OK) {
			// Get the object to serialize
			Object o = res.getObject();
			if (o == null)
				return "";

			// Try to serialize it.
			try {
				toWrite = MapperWrapper.getMapper().writeValueAsString(o);
			} catch (JsonProcessingException e) {
				logger.error("Unable to serialize object to string.", e, o);
				toWrite = "Unable to serialize object to string";
				sResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				sResponse.setContentType("text/html");
			}
		} else {
			toWrite = res.getMsg();
			sResponse.setContentType("text/html");
		}

		// Write it inside the response.
		try {
			sResponse.getWriter().println(toWrite);
		} catch (IOException e) {
			logger.error("Unable to write to response.", e);
		}

		return toWrite;
	}

	/**
	 * Ensures that the supplied data has the "content-type" set to JSON. If not the
	 * response is set to failed.
	 * 
	 * @param request
	 *            Request.
	 * @param res
	 *            Response object, where we will stored the result of this check.
	 */
	private static void requireJSON(HttpServletRequest request, Response res) {
		if (APPLICATION_JSON.equals(request.getHeader(CONTENT_TYPE)))
			return;
		logger.error("Obtained data do not seem to be in JSON format!");

		res.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);

		res.setMsg("Set accept header to application/json.");
	}

}
