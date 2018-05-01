package cz.cuni.mff.kocur.graphics;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.dota2AIFramework.App;

/**
 * Class that represents a main window of this application.
 * 
 * @author kocur
 *
 */
public class MainWindow extends Window {
	/**
	 * Get logger registered for this class.
	 */
	private static final Logger logger = LogManager.getLogger(Window.class.getName());

	/**
	 * JTabbedPane for storing tabs
	 */
	private Tabs tabs = new Tabs();

	/**
	 * Side buttons.
	 */
	private SideButtons buttons = new SideButtons();

	/**
	 * Window constructor. Does nothing now. WHY??
	 */
	public MainWindow(App app, String title) {
		super(app, title);
		initializeFrame();
	}

	/**
	 * Builds the window.
	 */
	public void build() {
		tabs.build();
		buttons.build();

		frame.setLayout(new GridBagLayout());
		GridBagConstraints gbc = ConstraintsBuilder.build().gridxy(0).fill(GridBagConstraints.BOTH)
				.anchor(GridBagConstraints.NORTH).weightxy(1).insets(2, 0, 0, 0).get();

		frame.add(tabs, gbc);

		JLayeredPane layeredPane = frame.getLayeredPane();
		layeredPane.add(buttons, JLayeredPane.PALETTE_LAYER);

		double x = frame.getPreferredSize().getWidth();
		buttons.setBounds((int) (x - 30), 30, 20, 115);
	}

	/**
	 * Initializes the frame of this window.
	 */
	protected void initializeFrame() {
		// Decorate window
		JFrame.setDefaultLookAndFeelDecorated(false);

		// Set sizes
		frame.setMinimumSize(new Dimension(400, 300));
		frame.setPreferredSize(new Dimension(1200, 800));
		frame.setResizable(false);

		// Add close operation.
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {

			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				logger.info("Closing the main window.");
				app.stopServer();
				app.finish();
			}
		});
	}

	/**
	 * 
	 * @return Returns tabs displayed in the window.
	 */
	public Tabs getTabs() {
		return tabs;
	}

	/**
	 * 
	 * @param tabs
	 *            Tabs that should be dispalyed in this window.
	 */
	public void setTabs(Tabs tabs) {
		this.tabs = tabs;
	}
}
