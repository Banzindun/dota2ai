package cz.cuni.mff.kocur.graphics;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.console.CommandResponse;
import cz.cuni.mff.kocur.console.ConsoleCommand;
import cz.cuni.mff.kocur.console.ConsoleManager;
import cz.cuni.mff.kocur.console.Controllable;

public class ConsolePanel extends BuildableJPanel implements Action, Controllable {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = 7393123734727267406L;

	/**
	 * Logger registered for this class.
	 */
	private Logger logger = LogManager.getLogger(ConsolePanel.class.getName());

	/**
	 * Text area that contains all the logs.
	 */
	private JTextPane consoleTextPane = new JTextPane();

	/**
	 * System independent line separator.
	 */
	private static String ls = System.getProperty("line.separator");

	/**
	 * StyleDocument of the console.
	 */
	private StyledDocument consoleDoc;

	/**
	 * Area for creating commands.
	 */
	private JTextField commandArea = new JTextField();

	/**
	 * Constraints.
	 */
	private GridBagConstraints gbc;

	/**
	 * Default color of the displayed text.
	 */
	private Color defaultColor = Colors.WHITE;

	private final String[] localCommands = new String[] { "clear" };

	/**
	 * Attribute for failed command. Will be red etc.
	 */
	SimpleAttributeSet failedCommandAttr = new SimpleAttributeSet();

	/**
	 * Attribute for passed command. This should make passed command green etc.
	 */
	SimpleAttributeSet passedCommandAttr = new SimpleAttributeSet();

	/**
	 * Attribute for normal command.
	 */
	SimpleAttributeSet commandAttr = new SimpleAttributeSet();

	/**
	 * Constructor. Sets layout and setups constraints.
	 */
	public ConsolePanel() {
		this.setLayout(new GridBagLayout());

		// Set console pane of consoleManager to this.
		ConsoleManager.setConsolePane(this);

		gbc = setupGbc();
	}

	/**
	 * Setups grid bag constraints.
	 * 
	 * @return constraints that are created specifically for this class
	 */
	private GridBagConstraints setupGbc() {
		return ConstraintsBuilder.build().weightx(1).weighty(1).fill(GridBagConstraints.BOTH).insets(10, 10, 0, 10)
				.get();
	}

	/**
	 * Setups all the necessary fields and class components.
	 */
	@Override
	public void build() {
		buildConsolePane();
		buildCommandArea();

		appendToConsole(ConsoleManager.getHelp());

		// Will set this as built.
		super.build();
	}

	/**
	 * Builds the console text pane and wraps a scroller around it.
	 */
	private void buildConsolePane() {
		consoleTextPane.setEditable(false);
		consoleTextPane.setBorder(BorderFactory.createEmptyBorder());
		consoleDoc = consoleTextPane.getStyledDocument();

		StyleConstants.setForeground(failedCommandAttr, Colors.RED);
		StyleConstants.setForeground(commandAttr, defaultColor);
		StyleConstants.setBold(commandAttr, true);
		StyleConstants.setForeground(passedCommandAttr, Colors.GREEN);

		JScrollPane scroller = new JScrollPane(consoleTextPane);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setBorder(BorderFactory.createEmptyBorder());

		DefaultCaret caret = (DefaultCaret) consoleTextPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		this.add(scroller, gbc);
	}

	/**
	 * Setups area containing commands. {@link #commandArea}
	 */
	private void buildCommandArea() {
		commandArea.addActionListener(this);
		commandArea.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		InputMap inputMap = commandArea.getInputMap(WHEN_FOCUSED);
		ActionMap actionMap = commandArea.getActionMap();

		// the key stroke we want to capture
		KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

		// tell input map that we are handling the enter key
		inputMap.put(enterStroke, enterStroke.toString());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");

		// tell action map just how we want to handle the enter key
		actionMap.put(enterStroke.toString(), this);
		actionMap.put("up", new AbstractAction() {
			/**
			 * Generated serial version id.
			 */
			private static final long serialVersionUID = -981148507583224393L;

			@Override
			public void actionPerformed(ActionEvent e) {
				up();
			}
		});
		actionMap.put("down", new AbstractAction() {
			/**
			 * Generated serial version id.
			 */
			private static final long serialVersionUID = -1505882227198712729L;

			@Override
			public void actionPerformed(ActionEvent e) {
				down();
			}
		});

		JTextField label = new JTextField(">");
		label.setBorder(BorderFactory.createEmptyBorder());
		label.setForeground(Colors.GREEN);
		label.setEditable(false);

		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());

		gbc.insets = new Insets(0, 10, 10, 0);
		gbc.weighty = 0;
		gbc.weightx = 0;
		p.add(label, gbc);

		gbc.gridy++;
		gbc.weightx = 1;

		p.add(commandArea, gbc);

		gbc.insets = new Insets(0, 0, 18, 10);
		gbc.gridx = 0;
		gbc.gridy++;
		this.add(p, gbc);

	}

	/**
	 * Moves one command up in the commands history.
	 */
	private void up() {
		ConsoleManager.getHistory().up();
		ConsoleCommand c = ConsoleManager.getHistory().getCommand();

		commandArea.setText(c.toString());
	}

	/**
	 * Moves one command down in the commands history.
	 */
	private void down() {
		ConsoleManager.getHistory().down();
		ConsoleCommand c = ConsoleManager.getHistory().getCommand();

		commandArea.setText(c.toString());
	}

	/**
	 * Resizes the console. This is called after the console size overcame one of
	 * its limits.
	 */
	private void resizeConsole() {
		try {
			String newText = consoleDoc.getText(consoleDoc.getLength() - 8000, 4000);
			consoleTextPane.setText(newText);
		} catch (BadLocationException e) {
			logger.error("Location calculated badly on console resize.", e);
		}
	}

	/**
	 * Appens response of command to the command section.
	 * 
	 * @param res
	 *            Response, which message we want to append.
	 */
	private void appendCommandResponse(CommandResponse res) {
		// Resize if necessary
		if (consoleDoc.getLength() > 12000)
			resizeConsole();

		try {
			if (res.passed())
				consoleDoc.insertString(consoleDoc.getLength(), res.toString() + ls, passedCommandAttr);
			else
				consoleDoc.insertString(consoleDoc.getLength(), res.toString() + ls, failedCommandAttr);
		} catch (BadLocationException e) {
			logger.error("Could not insert command result to console pane.", e);
		}
	}

	/**
	 * Appends a message to console.
	 * 
	 * @param msg
	 *            Message to append.
	 */
	private void appendToConsole(String msg) {
		if (consoleDoc.getLength() > 12000)
			resizeConsole();

		try {
			consoleDoc.insertString(consoleDoc.getLength(), msg, commandAttr);
		} catch (BadLocationException e) {
			logger.error("Could not insert string to console pane.", e);
		}
	}

	/**
	 * Called when this panel has been focused. (Tab was switched .. )
	 */
	public void focused() {
		// Request focus for commandArea
		commandArea.requestFocusInWindow();
	}

	/**
	 * Checks if passed string is inside the local commands. (these are commands,
	 * that are handled by console panel itself, without passing them)
	 * 
	 * @param txt Command name that we are looking for.
	 * @return Returns true if the command is inside the local commands.
	 */
	public boolean inLocalCommands(String txt) {
		boolean r = false;
		for (String s : localCommands)
			if (s.equals(txt))
				r = true;

		return r;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String text = commandArea.getText();
		commandArea.setText("");

		appendToConsole("> " + text + ls);

		ConsoleCommand cmd = new ConsoleCommand(text);

		// Check if the command is one of the local ones
		if (inLocalCommands(text.trim())) {
			appendCommandResponse(command(cmd));

		} else {
			// Else we pass the commands to ConsoleManager to handle them
			CommandResponse response = ConsoleManager.command(cmd);
			appendCommandResponse(response);
		}
	}

	@Override
	public CommandResponse command(ConsoleCommand cmd) {
		CommandResponse response = new CommandResponse();

		String firstField = cmd.getField();
		switch (firstField) {
		case "clear":
			clearConsole();
			response.appendLine("Console cleared.");
			response.pass();
			break;
		default:
			response.appendLine("Unknown command.");
			response.fail();
			break;
		}

		return response;
	}

	/**
	 * Clears the console.
	 * @throws BadLocationException 
	 */
	private void clearConsole() {
		consoleTextPane.setText("");
		appendToConsole(">clear" + System.getProperty("line.separator"));
	};

	@Override
	public String getHelp() {
		StringBuilder help = new StringBuilder();

		help.append("\tclear\tclears the console" + ls);

		return help.toString();
	}

	@Override
	public String getControllableName() {
		return "ConsolePanel";
	}

	@Override
	public Object getValue(String key) {
		return null;
	}

	@Override
	public void putValue(String key, Object value) {

	}

}
