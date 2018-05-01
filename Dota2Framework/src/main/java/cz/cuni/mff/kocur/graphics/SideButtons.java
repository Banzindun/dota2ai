package cz.cuni.mff.kocur.graphics;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cz.cuni.mff.kocur.base.GraphicResources;
import cz.cuni.mff.kocur.dota2AIFramework.App;
import cz.cuni.mff.kocur.events.FrameworkEventListener;
import cz.cuni.mff.kocur.events.ListenersManager;

/**
 * CLass that represents side buttons. These are buttons that are ment for
 * controlling the application.
 * 
 * @author kocur
 *
 */
public class SideButtons extends JPanel implements MouseListener, FrameworkEventListener {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -7579102462479894186L;

	/**
	 * Logger registerd for SideButtons class.
	 */
	// private Logger logger = LogManager.getLogger(SideButtons.class.getName());

	/**
	 * Reference to application for buttons functionality.
	 */
	private App app = App.getInstance();

	/**
	 * Constraints.
	 */
	private GridBagConstraints gbc;

	/**
	 * Indicator that indicates the state of the framework (Running, Paused,
	 * Configuration ..)
	 */
	private JLabel indicator = new JLabel();

	/**
	 * Start button.
	 */
	private JLabel start = new JLabel("Start");

	/**
	 * Stop button.
	 */
	private JLabel stop = new JLabel("Stop");

	/**
	 * Pause button.
	 */
	private JLabel pause = new JLabel("Pause");

	/**
	 * Restart button.
	 */
	private JLabel restart = new JLabel("Restart");

	/**
	 * Step button. (if we want to do max one update per hero and stop)
	 */
	private JLabel step = new JLabel("Step");

	/**
	 * Array for easier going buttons accessibility.
	 */
	private JLabel[] labels;

	/**
	 * Constructor.
	 */
	public SideButtons() {
		super(new GridBagLayout());

		ListenersManager.addFrameworkListener("statechanged", this);

		gbc = ConstraintsBuilder.build().gridxy(0).weightxy(0).fill(GridBagConstraints.NONE)
				.anchor(GridBagConstraints.CENTER).insets(2).get();

		labels = new JLabel[] { start, stop, pause, step, restart };
		
		this.setOpaque(false);
	}

	/**
	 * Builds the graphic part.
	 */
	public void build() {
		buildLabels();
		addListeners();
		addLabels();
	}

	/**
	 * Builds the buttons and indicator - sets their caption and icons.
	 */
	private void buildLabels() {
		// Initially the state of the framework should be blue (configuration)
		indicator.setIcon(GraphicResources.getIndicator("blue"));
		indicator.setSize(new Dimension(16, 16));
		indicator.setToolTipText("Configuration");

		// Set labels
		GraphicResources.setLabelIcon(start, GraphicResources.startI);
		GraphicResources.setLabelIcon(stop, GraphicResources.stopI);
		GraphicResources.setLabelIcon(pause, GraphicResources.pauseI);
		GraphicResources.setLabelIcon(restart, GraphicResources.restartI);
		GraphicResources.setLabelIcon(step, GraphicResources.stepI);
	}

	/**
	 * Adds mouse listeners for every label (button).
	 */
	private void addListeners() {
		for (JLabel l : labels) {
			l.addMouseListener(this);
		}
	}

	/**
	 * Adds all the labels to this JPanel.
	 */
	private void addLabels() {
		this.add(indicator, gbc);
		gbc.gridy++;

		for (JLabel l : labels) {
			this.add(l, gbc);
			gbc.gridy++;
		}
	}

	/**
	 * Updates the buttons. This disables buttons that should be disabled and
	 * changes the indicator. </br>
	 * This function should be called on Application state change.
	 */
	private void updateLabels() {
		indicator.setToolTipText(App.state.name());

		switch (App.state) {
		case CONFIGURATION:
			indicator.setIcon(GraphicResources.getIndicator("blue"));

			start.setEnabled(true);
			stop.setEnabled(false);
			pause.setEnabled(false);
			restart.setEnabled(false);
			step.setEnabled(false);
			break;
		case INIT:
			indicator.setIcon(GraphicResources.getIndicator("yellow"));

			start.setEnabled(false);
			stop.setEnabled(true);
			pause.setEnabled(false);
			restart.setEnabled(true);
			step.setEnabled(false);
			break;
		case RUNNING:
			indicator.setIcon(GraphicResources.getIndicator("green"));
			GraphicResources.setLabelIcon(pause, GraphicResources.pauseI);

			start.setEnabled(false);
			stop.setEnabled(true);
			pause.setEnabled(true);
			restart.setEnabled(true);
			step.setEnabled(false);
			break;
		case PAUSED:
			indicator.setIcon(GraphicResources.getIndicator("purple"));

			step.setEnabled(true);
			GraphicResources.setLabelIcon(pause, GraphicResources.resumeI);
			break;
		default:
			start.setEnabled(true);
			stop.setEnabled(false);
			pause.setEnabled(false);
			restart.setEnabled(false);
			step.setEnabled(false);
			break;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == start) {
			if (start.isEnabled())
				app.debug();
		} else if (e.getSource() == stop) {
			if (stop.isEnabled())
				app.stop();
		} else if (e.getSource() == pause) {
			if (pause.isEnabled()) {
				if (App.state == App.State.PAUSED)
					app.unpause();
				else
					app.pause();
			}
		} else if (e.getSource() == restart) {
			if (restart.isEnabled())
				app.reset();
		} else if (e.getSource() == step) {
			if (step.isEnabled())
				app.step();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void triggered() {
		updateLabels();
	}

	@Override
	public void triggered(Object... os) {
		updateLabels();

	}
}
