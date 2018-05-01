package cz.cuni.mff.kocur.logging;

import java.io.Serializable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;


//@Plugin(name="QueueAppender", category="Core", elementType="appender", printObject=true)
public class QueueAppender extends AbstractAppender{
	
	/**
	 * Creates instance of QueueAppender and registers it for bot's class.
	 * @param className Name of class for which we are creating the appender.
	 */
	public static void registerAppender(String className) {
	    final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
	    final AbstractConfiguration config = (AbstractConfiguration) ctx.getConfiguration();

		PatternLayout layout = PatternLayout.newBuilder()
				.withConfiguration(config)
				.withPattern("%M %d{HH:mm:ss.SSS} [%t] %-5p %c - %m%n")
				.build();
	    
	    // Create and add the appender
	    QueueAppender appender = new QueueAppender("QA_" + className, null, layout);
	    appender.start();
	    config.addAppender(appender);
	    

	    // Create and add the logger
	    AppenderRef[] refs = new AppenderRef[]{AppenderRef.createAppenderRef("QA_" + className, null, null)};
	    LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.ALL, className, "true", refs, null, config, null);
	    
	    loggerConfig.addAppender(appender, null, null);
	    config.addLogger(className, loggerConfig);
	    ctx.updateLoggers();
	}
	
	private LoggingDistributor distributor; 
	
	public QueueAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
		super(name, filter, layout);	
		
		// Get a distributor - can be null!  
		distributor = LoggingDistributor.register(name);		
	}

	
	/**
	 * Override of append method. Stores log in logQueue.
	 */
	@Override
	public void append(LogEvent arg0) {
		// byte[] data = getLayout().toByteArray(arg0);		
		// System.out.println(new String(data).trim());
		if (distributor != null) send(arg0);		
	}
	
	private void send(LogEvent log) {
		distributor.receiveLog(log);		
	}	
}
