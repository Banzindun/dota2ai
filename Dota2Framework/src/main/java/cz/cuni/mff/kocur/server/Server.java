package cz.cuni.mff.kocur.Server;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;


import fi.iki.elonen.NanoHTTPD;
import cz.cuni.mff.kocur.Bot.Bot;
import cz.cuni.mff.kocur.Bot.Bot.Command;
import cz.cuni.mff.kocur.Bot.BotCommands.LevelUp;
import cz.cuni.mff.kocur.Bot.BotCommands.Select;
import cz.cuni.mff.kocur.Dota2AIOverlay.App;
import cz.cuni.mff.kocur.World.ChatEvent;
import cz.cuni.mff.kocur.World.*;

/**
 * Class that implements NanoHTTPD server. 
 * @author Banzindun
 *
 */
public class Server extends NanoHTTPD {
	/**
	 * Mapper from com.fasterxml.jackson.databind.ObjectMapper.
	 * Maps objects from JSON data to a given object, when used as: 
	 * MAPPER.readValue(JsonParser p, JavaType/ResolvedType/TypeReference type)
	 */
    private final static ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * logger registered in Logger class.	
	 */
	private static final Logger logger = Logger.getLogger(Server.class.getName());
        
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

    static {
        MAPPER.configure( Feature.AUTO_CLOSE_SOURCE, false );
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    
    private final Set<FrameListener> listeners;

    // private final Bot bot;

    public Server( int port ) throws IOException {
        super( port );

        //this.bot = bot;

        listeners = new HashSet<>();
    }

    public void add( FrameListener l ) {
        listeners.add( l );
    }

    /**
     * Ensures that the "accept" header is either set to JSON or empty
     *
     * @param session
     * @return
     */
    private Response assureAcceptJSON( IHTTPSession session ) {
        final Map<String, String> headers = session.getHeaders();
        if (!APPLICATION_JSON.equals( headers.get( ACCEPT_FIELD ) )) {
            return newFixedLengthResponse( Response.Status.NOT_ACCEPTABLE, MIME_PLAINTEXT, "set accept to application/json or remove it" );
        }
        else {
            return null;
        }
    }

    /**
     * Helper method to serialize a POJO((Plain Old Java Objects) into JSON
     *
     * @param o
     * @return
     * @throws JsonProcessingException
     */
    private Response buildJSONResponse( Object o ) throws JsonProcessingException {
        return newFixedLengthResponse( MAPPER.writeValueAsString( o ) );

    }

    private void chat( IHTTPSession session ) throws JsonParseException, JsonMappingException, IOException {
        final ChatEvent e = MAPPER.readValue( session.getInputStream(), ChatEvent.class );
        //bot.onChat( e );
    }

    private Response levelup( IHTTPSession session ) throws JsonProcessingException {
        final Response res = assureAcceptJSON( session );
        if (res != null) {
            return res;
        }
        //final LevelUp l = bot.levelUp();
        //return buildJSONResponse( l );
        return null;
    }

    /**
     * Ensures that the supplied data has the "content-type" set to JSON
     *
     * @param session
     * @return
     */
    private Response requireJSON( IHTTPSession session ) {
        final Map<String, String> headers = session.getHeaders();
        if (!APPLICATION_JSON.equals( headers.get( CONTENT_TYPE ) )) {
            return newFixedLengthResponse( Response.Status.NOT_ACCEPTABLE, MIME_PLAINTEXT, "Set content-type to application/json" );
        }
        else {
            return null;
        }
    }

    private void reset( IHTTPSession session ) {
        //bot.reset();
    }

    private Response select( IHTTPSession session ) throws JsonProcessingException {
        //final Select s = bot.select();
        //return buildJSONResponse( s );
    	return null;
    }

   /**
    * Handles incoming requests. Called from NanoHTTPD 
    */
    @Override
    public Response serve( IHTTPSession session ) {
        // This method does a few sanity checks and then calls the respective
        // method
        // based on the requested URL. These methods then build the response
        if (session.getMethod() != Method.POST) {
            return newFixedLengthResponse( Response.Status.METHOD_NOT_ALLOWED, "text/plain", "Only POST allowed" );
        }

        final String method = session.getUri().substring( session.getUri().lastIndexOf( '/' ) + 1 ).toLowerCase();
        Response res;

        try {
            switch (method) {
                case "chat":
                    res = requireJSON( session );
                    if (res != null) {
                        break;
                    }
                    chat( session );
                    res = newFixedLengthResponse( "" );
                    break;
                case "reset":
                    reset( session );
                    res = newFixedLengthResponse( "" );
                    break;
                case "levelup":
                    res = levelup( session );
                    break;
                case "select":
                    res = select( session );
                    break;
                case "update":
                    res = requireJSON( session );
                    if (res != null) {
                        break;
                    }
                    res = update( session );
                    break;
                default:
                    res = newFixedLengthResponse( Response.Status.NOT_FOUND, MIME_PLAINTEXT, "method not found" );
                    break;

            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            res = newFixedLengthResponse( Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, e.getMessage() );
        }
        return res;
    }

    private Response update( IHTTPSession session ) throws IOException {
    	// Read and print update.
    	InputStream msg = session.getInputStream(); 
    	byte[] bytes = new byte[msg.available()];
    	msg.read(bytes);

    	
    	// MUST DO WorldUpdater
    	/* Loads whole world .. forgets the information about the previous state. */ 
        final World world = MAPPER.readValue( bytes, World.class );
        
        
        
        listeners.stream().forEach( l -> l.update( world ) );
        //final Command c = bot.update( world );
        //return buildJSONResponse( c );
        return null;
    }
}
