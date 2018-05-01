package cz.cuni.mff.kocur.graphics;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.GraphicResources;

/**
 * This class main purpose is to create a JPanel that looks like a small window
 * inside the application. That means it has simple header with options to hide
 * or remove the "window" from parent.
 * 
 * @author Banzindun
 *
 */
public abstract class WindowedJPanel extends BuildableJPanel {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = 4643003796557101378L;

	/**
	 * Logger for logging stuff.
	 */
	protected Logger logger = LogManager.getLogger(WindowedJPanel.class.getName());

	/**
	 * Constraints for grid bag layout.
	 */
	protected GridBagConstraints gbc = null;

	/**
	 * Header - this will contain title and buttons (hide, close)
	 */
	private JPanel header = new JPanel();

	/**
	 * Title - this will contain the "name" of the window.
	 */
	private JLabel title = new JLabel();

	/**
	 * Body will contain some informations that are stored inside this window.
	 */
	protected JPanel body = new JPanel();

	/**
	 * Label that will contain picture that will let us close the window.
	 */
	private JLabel closeL = new JLabel("");

	/**
	 * Label that will contain picture that will let us hide the body.
	 */
	private JLabel hideL = new JLabel("");

	/**
	 * True if the body is hidden.
	 */
	protected boolean hidden = false;

	/**
	 * Simple constructor, that calls super() and initializes layout.
	 */
	public WindowedJPanel() {
		super();
		this.setLayout(new GridBagLayout());
		body.setLayout(new GridBagLayout());

		gbc = ConstraintsBuilder.build().gridxy(0).weightxy(0).anchor(GridBagConstraints.WEST)
				.fill(GridBagConstraints.NONE).insets(0, 4, 0, 4).get();
	}

	/**
	 * Simple constructor.
	 * 
	 * @param body
	 *            Body that should be used inside this window. {@link #body}
	 * @param title
	 *            Title that should be used. {@link #title}
	 */
	public WindowedJPanel(String title, JPanel body) {
		super();

		this.title.setText(title);
		this.body = body;
		body.setLayout(new GridBagLayout());

		gbc = ConstraintsBuilder.build().gridxy(0).weightxy(0).anchor(GridBagConstraints.WEST)
				.fill(GridBagConstraints.NONE).insets(0, 4, 0, 4).get();
	}

	/**
	 * Builds the sections.
	 */
	@Override
	public void build() {
		buildHeader();

		// Set border to body.
		body.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		// Call the update (will add everything to JPanel).
		update();

		// Call build on super. This will set this object as built.
		super.build();
	}

	/**
	 * Builds the header.
	 */
	private void buildHeader() {
		// Initialize hide label.
		initLabels();

		header.setLayout(new GridBagLayout());
		header.setBackground(this.getBackground().darker());

		header.add(hideL, gbc);
		gbc.gridx++;

		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		header.add(title, gbc);
		gbc.gridx++;

		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		header.add(closeL, gbc);

		gbc.gridx = 0;
		updateHeader();
	}

	/**
	 * Hides the body.
	 */
	public void hideBody() {
		hidden = true;
		GraphicResources.setLabelIcon(hideL, GraphicResources.plusI);
		body.setVisible(false);
	}

	/**
	 * Shows the body.
	 */
	public void showBody() {
		hidden = false;
		GraphicResources.setLabelIcon(hideL, GraphicResources.minusI);
		body.setVisible(true);
	}

	/**
	 * Initializes hide label.
	 */
	private void initLabels() {
		GraphicResources.setLabelIcon(closeL, GraphicResources.crossI);
		GraphicResources.setLabelIcon(hideL, GraphicResources.minusI);

		hideL.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if (!hidden)
					hideBody();
				else
					showBody();
			}
		});
		hideL.setSize(new Dimension(16, 16)); // size of the Icon

		closeL.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				close();
			}
		});
		closeL.setSize(new Dimension(16, 16));
	}

	/**
	 * Class that should close this window. </br>
	 * This should possible alert parent of component to remove the component.
	 */
	protected abstract void close();

	/**
	 * Changes the title of this window.
	 * 
	 * @param title
	 *            New title of this window.
	 */
	public void changeTitle(String title) {
		this.title.setText(title);
	}

	/**
	 * Updates header information.
	 */
	private void updateHeader() {
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		this.add(header, gbc);
	}

	/**
	 * Updates the header and body.
	 */
	protected void update() {
		updateHeader();

		// gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy++;
		this.add(body, gbc);
	}

	/**
	 * 
	 * @return Returns the body of this panel.
	 */
	public JPanel getBody() {
		return body;
	}

	/**
	 * 
	 * @return Returns constraints used in this class.
	 */
	public GridBagConstraints getGbc() {
		return gbc;
	}

	/**
	 * Hides hide label. :D
	 */
	public void hideHideLabel() {
		hideL.setVisible(false);
	}

	/**
	 * Sets show label to visible.
	 */
	public void showHideLabel() {
		hideL.setVisible(true);
	}

	/**
	 * Hides close label.
	 */
	public void hideCloseLabel() {
		closeL.setVisible(false);
	}

	/**
	 * Sets close label to visible.
	 */
	public void showCloseLabel() {
		closeL.setVisible(false);
	}
}
