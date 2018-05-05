package cz.cuni.mff.kocur.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Wraps the JSON ObjectMapper class. And implements some classes for checking
 * the correctness of sessions from RequestHandler.
 * 
 * @author Banzindun
 *
 */
public class MapperWrapper {
	/**
	 * Mapper from com.fasterxml.jackson.databind.ObjectMapper. Maps objects from
	 * JSON data to a given object, when used as: MAPPER.readValue(JsonParser p,
	 * JavaType/ResolvedType/TypeReference type)
	 */
	private final static ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * Static configuration of Mapper.
	 */
	static {
		MAPPER.configure(Feature.AUTO_CLOSE_SOURCE, false);
		MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	/**
	 * Creates a new class of type supplied as parameter. The class is created by
	 * deserializing JSON data from input stream.
	 * 
	 * @param <T>
	 *            Type of the specified class.
	 * @param is
	 *            Input stream, from which we read JSON data.
	 * @param cl
	 *            Class that should be constructed.
	 * @return Returns a new deserialized class.
	 * @throws JsonParseException
	 *             If parsing went wrong.
	 * @throws JsonMappingException
	 *             If mapping of objects went wrong.
	 * @throws IOException
	 *             If there was a problem reading the stream.
	 */
	public static <T> T readValue(InputStream is, Class<T> cl)
			throws JsonParseException, JsonMappingException, IOException {
		return MAPPER.readValue(is, cl);
	}

	/**
	 * Creates a new class of type supplied as parameter. The class is created by
	 * deserializing JSON data that are stored insite the byte array passed as
	 * input.
	 * 
	 * @param <T>
	 *            Type of the specified class.
	 * @param bts
	 *            Byte array of JSON data.
	 * @param cl
	 *            Class that should be created from the data.
	 * @return Returns a new instance of given class, that was initialized from JSON
	 *         data.
	 * @throws JsonParseException
	 *             If parsing went wrong.
	 * @throws JsonMappingException
	 *             If mapping of objects went wrong.
	 * @throws IOException
	 *             If there was a problem reading the stream.
	 */
	public static <T> T readValue(byte[] bts, Class<T> cl)
			throws JsonParseException, JsonMappingException, IOException {
		return MAPPER.readValue(bts, cl);
	}

	/**
	 * Creates a new class of type supplied as parameter. The class is created by
	 * deserializing JSON data from input string.
	 * 
	 * @param <T>
	 *            Type of the specified class.
	 * @param input
	 *            Input string with JSON data.
	 * @param cl
	 *            Class that should be created.
	 * @return Returns a new instance of given class, that was initialized from JSON
	 *         data.
	 * @throws JsonParseException
	 *             If parsing went wrong.
	 * @throws JsonMappingException
	 *             If mapping of objects went wrong.
	 * @throws IOException
	 *             If there was a problem reading the stream.
	 */
	public static <T> T readValue(String input, Class<T> cl)
			throws JsonParseException, JsonMappingException, IOException {
		return MAPPER.readValue(input, cl);
	}

	/**
	 * Creates a new hash map by deserializing JSON data from input string.
	 * 
	 * @param json
	 *            Json from which the map should be constructed.
	 * @param typeRef
	 *            TypeReference to hash map containing Strings.
	 * @return Returns a new instance hash map created from JSON data.
	 * @throws JsonParseException
	 *             If parsing went wrong.
	 * @throws JsonMappingException
	 *             If mapping of objects went wrong.
	 * @throws IOException
	 *             If there was a problem reading the stream.
	 */
	public static HashMap<String, String> readValue(String json, TypeReference<HashMap<String, String>> typeRef)
			throws JsonParseException, JsonMappingException, IOException {
		return MAPPER.readValue(json, typeRef);
	}

	/**
	 * Updates a fields of given object with data obtained from input JSON.
	 * 
	 * @param o
	 *            Object, that should be update using the JSON string.
	 * @param json
	 *            JSON string.
	 * @return Returns a Object, that was updated using the supplied string.
	 * @throws IOException
	 *             If reading went wrong.
	 */
	public static Object updateObject(Object o, String json) throws IOException {
		return MAPPER.readerForUpdating(o).readValue(json);
	}

	/**
	 * 
	 * @return Returns reference to the ObjectMapper stored in this class.
	 */
	public static ObjectMapper getMapper() {
		return MAPPER;
	}

}
