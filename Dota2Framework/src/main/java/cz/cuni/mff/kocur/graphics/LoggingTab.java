package cz.cuni.mff.kocur.graphics;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.logging.LoggingDisplay;

/**
 * Class that allows the users to display the logs.
 * 
 * @author kocur
 *
 */
public class LoggingTab extends BuildableJPanel implements LoggingDisplay {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = 6912572262546514181L;

	/**
	 * LoggingTab logger instance.
	 */
	private Logger logger = LogManager.getLogger(LoggingTab.class.getName());

	/**
	 * Pane that stores the configuration for the logger.
	 */
	private LoggingTabConfiguration configPane;

	/**
	 * Pane with logs.
	 */
	private JPanel logsPanel = new JPanel();

	/**
	 * Name of this logger. (For which logger we are writing the logs)
	 */
	private String name;

	/**
	 * Text area containing the logs.
	 */
	private JTextPane logText = new JTextPane();

	/**
	 * Class that represents a log, that can have a color. 
	 * @author kocur
	 *
	 */
	private class ColoredLog {
		ColoredLog(String log, Color c) {
			this.log = log;
			this.c = c;
		}

		String log;
		Color c;
	}

	/**
	 * Array of logs, that are colored.
	 */
	private ArrayList<ColoredLog> logs = new ArrayList<>();

	/**
	 * Document for displaying the logs.
	 */
	StyledDocument logsDoc;

	/**
	 * Style of logging.
	 */
	Style logStyle;

	/**
	 * Default logging color.
	 */
	Color defaultColor = Colors.WHITE;

	/**
	 * Count of logs displayed in logTextArea.
	 */
	private int logCount = 0;

	/**
	 * Maximum number of logs that can be displayed..
	 */
	private int MAXLOGS = 400;

	/**
	 * Minimum number that can be displayed after resize.
	 */
	private int MINLOGS = 100;

	// If logging to file
	PrintWriter writer = null;

	///////////////////////////////////

	/**
	 * Logging tab constructor.
	 * 
	 * @param name
	 *            Name for which the appender is registered.
	 */
	public LoggingTab(String name) {
		super();

		// Set name
		this.name = name;

		configPane = new LoggingTabConfiguration(this);
	}

	@Override
	public void build() {
		this.setLayout(new GridBagLayout());

		// Setup gbc
		GridBagConstraints gbc = ConstraintsBuilder.build().fill(GridBagConstraints.BOTH)
				.anchor(GridBagConstraints.NORTH).gridy(0).gridx(0).weightx(1).weighty(1).get();

		// Initialize logPane
		initLogPane();

		this.add(logsPanel, gbc);

		gbc.weightx = 0;
		gbc.gridx++;

		configPane.setup();
		this.add(configPane, gbc);

		logsDoc = logText.getStyledDocument();
		logStyle = logText.addStyle("Log Style", null);

		super.build();
	}

	/**
	 * Returns name of the registered logger.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Initializes logPane. Initializes text area where the logs are stored and its
	 * scroller and constraints.
	 */
	private void initLogPane() {
		logsPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = ConstraintsBuilder.build().fill(GridBagConstraints.BOTH)
				.anchor(GridBagConstraints.NORTH).gridy(0).gridx(0).weightx(1).weighty(1).get();

		logText.setEditable(false);

		DefaultCaret caret = (DefaultCaret) logText.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		JScrollPane scroller = new JScrollPane(logText);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		logsPanel.add(scroller, gbc);
	}

	/**
	 * Receives the log. Gets the configuration, serializes the log and prints it.
	 */
	@Override
	public void receiveLog(LogEvent log) {
		// Get Level of log and Level selected in configuration
		Level logLevel = log.getLevel();
		Level level = configPane.getFilterLevel();

		// If level not set then just log. Else check for options and levels from
		// configuration.
		if (level != null) {
			if (configPane.getLogEqualLevel() && configPane.getLogMoreSpecificLevel()) {
				if (logLevel != level && logLevel.isLessSpecificThan(level))
					return;
			} else if (configPane.getLogEqualLevel()) {
				if (logLevel != level)
					return;
			} else if (configPane.getLogMoreSpecificLevel()) {
				if (logLevel.isLessSpecificThan(level))
					return;
			}
			// If nothing selected log everything
		}

		// Now I am working with log that has appropriate level.
		// Add it (if possible)
		addLog(log);

		// Resize if necessary (we dont want to have more than MAXLOGS logs stored)
		if (logCount > MAXLOGS)
			resize();
	}

	/**
	 * Adds a log to the tab.
	 * @param log LogEvent.
	 */
	private void addLog(LogEvent log) {
		// Configure layout with pattern from configPane
		PatternLayout layout = configPane.getPatternLayout();

		// Serialize the log (apply the layout)
		String slog = layout.toSerializable(log);

		// If we are logging, then store log in list and in text area
		if (configPane.getLogsOn()) {
			// Add it to logs and to text area
			if (addColoredLog(log.getLevel(), slog))
				logCount++; // Increment log count
		}

		// If writing to file then write
		if (writer != null)
			writer.print(slog);
	}

	/**
	 * Adds a log to this tab and colors it according to its level.
	 * @param lvl Level of the log. 
	 * @param log The log.
	 * @return Returns true if the log was added.
	 */
	private boolean addColoredLog(Level lvl, String log) {
		// Set the color:
		Color c;

		if (lvl == Level.FATAL)
			c = Colors.RED;
		else if (lvl == Level.ERROR)
			c = Colors.ORANGE;
		else if (lvl == Level.WARN)
			c = Colors.YELLOW;
		else
			c = defaultColor;

		StyleConstants.setForeground(logStyle, c);

		// Add the log: 
		try {
			ColoredLog cl = new ColoredLog(log, c);
			logs.add(cl);
			logsDoc.insertString(logsDoc.getLength(), cl.log, logStyle);

		} catch (BadLocationException e) {
			logger.warn("Unable to insert string to doc!");
			return false;
		}

		StyleConstants.setForeground(logStyle, defaultColor);
		return true;
	}

	/**
	 * Tries to add a colored log.
	 * @param cl Colored log.
	 * @return Return true if the log was added.
	 */
	private boolean addColoredLog(ColoredLog cl) {
		StyleConstants.setForeground(logStyle, cl.c);

		try {
			logsDoc.insertString(logsDoc.getLength(), cl.log, logStyle);
		} catch (BadLocationException e) {
			logger.warn("Unable to insert string to doc!");
			return false;
		}

		StyleConstants.setForeground(logStyle, defaultColor);
		return true;
	}

	/**
	 * Resizes the field with logs if there is too many of them. Should keep the
	 * colors etc.
	 */
	private void resize() {
		logText.setText("");

		ArrayList<ColoredLog> newLogs = new ArrayList<>();

		logCount = MINLOGS;

		for (int i = MAXLOGS - MINLOGS; i < logs.size(); i++) {
			addColoredLog(logs.get(i));
			newLogs.add(logs.get(i));
		}

		logs = newLogs;
	}

	/**
	 * Opens PrintWriter and starts writing to file supplied location.
	 * 
	 * @param fileName
	 *            Where should the PrintWriter point.
	 */
	public void openFile(String fileName) {
		if (writer == null) {
			try {
				logger.info("Openning file: " + fileName + " for writing");
				writer = new PrintWriter(fileName, "UTF-8");
			} catch (UnsupportedEncodingException | FileNotFoundException e1) {
				logger.warn("Couldn't create PrintWriter for the supplied file name with UTF-8 encoding", fileName, e1);
			}
		}
	}

	/**
	 * Closes the writer and finishes the saving of logs.
	 */
	public void closeFile() {
		writer.close();
		writer = null;
	}

}
