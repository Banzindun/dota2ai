package cz.cuni.mff.kocur.botGraphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.GraphicResources;
import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.graphics.ConstraintsBuilder;
import cz.cuni.mff.kocur.utility.FunctionFactory;
import cz.cuni.mff.kocur.utility.UtilityFunction;
import cz.cuni.mff.kocur.utility.UtilityFunction.TYPE;

/**
 * Class that represent information panel that contains information about
 * considerations.
 * 
 * @author kocur
 *
 */
public class ConsiderationInfoPanel extends JPanel implements ActionListener, KeyListener {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = 225269190685319916L;

	/**
	 * Logger for this class.
	 */
	private static final Logger logger = LogManager.getLogger(ConsiderationInfoPanel.class);

	/**
	 * JComboBox for selection of type of function of this consideration.
	 */
	protected JComboBox<UtilityFunction.TYPE> type = new JComboBox<UtilityFunction.TYPE>();

	/**
	 * M parameter field.
	 */
	protected JTextField mParam = new JTextField("", 2);

	/**
	 * K parameter field.
	 */
	protected JTextField kParam = new JTextField("", 2);

	/**
	 * C parameter field.
	 */
	protected JTextField cParam = new JTextField("", 2);

	/**
	 * B parameter field.
	 */
	protected JTextField bParam = new JTextField("", 2);

	/**
	 * Function plotter. This is a window that plots the utility functions.
	 */
	private FunctionPlotter plotter = null;

	/**
	 * Button that creates new function plotter.
	 */
	private JButton plotButton = new JButton("Plot");

	/**
	 * Panel that wraps around parameters.
	 */
	protected JPanel paramsWrapper = new JPanel();

	/**
	 * Label with name.
	 */
	private JLabel name = new JLabel();

	/**
	 * Label with normalizedValue.
	 */
	private JLabel normalizedValue = new JLabel();

	/**
	 * Label with score.
	 */
	private JLabel score = new JLabel();

	/**
	 * Consideration.
	 */
	protected Consideration consideration = null;

	/**
	 * Grid base constraints we use.
	 */
	protected GridBagConstraints gbc;

	public ConsiderationInfoPanel() {

	}

	/**
	 * Constructor, that creates panel for given consideration.
	 * 
	 * @param c
	 *            Consideration.
	 */
	public ConsiderationInfoPanel(Consideration c) {
		this.setLayout(new GridBagLayout());

		consideration = c;

		build();
	}

	/**
	 * Builds the info panel.
	 */
	private void build() {
		buildType();

		paramsWrapper = new JPanel();
		buildParameters();

		addPlotButtonToParams();

		gbc = ConstraintsBuilder.build().gridxy(0).weightxy(1, 0).fill(GridBagConstraints.BOTH)
				.anchor(GridBagConstraints.LINE_START).insets(2).get();

		this.add(name, gbc);
		gbc.gridx++;

		JPanel wrap = new JPanel();
		wrap.add(normalizedValue);
		wrap.add(score);

		this.add(wrap, gbc);

		gbc.gridx = 0;
		gbc.gridy++;

		this.add(type, gbc);
		gbc.gridx++;

		this.add(paramsWrapper, gbc);
	}

	/**
	 * Updates the panel.
	 */
	public void update() {
		name.setText(consideration.getName());

		// Take scores only 5 digits of the score.
		normalizedValue.setText("IN: " + sanitizeScore(consideration.getNormalizedInput()));
		score.setText("OUT: " + sanitizeScore(consideration.getScore()));
	}

	/**
	 * Updates the parameters.
	 */
	public void updateParameters() {
		UtilityFunction f = consideration.getUtilityFunction();

		mParam.setText(f.getM() + "");
		kParam.setText(f.getK() + "");
		cParam.setText(f.getC() + "");
		bParam.setText(f.getB() + "");
	}

	/**
	 * Updates the considerations type.
	 */
	public void updateType() {
		type.setSelectedItem(consideration.getUtilityFunction().getType());
	}

	/**
	 * This function takes double, rounds it to 5 digits and then makes a string out
	 * of it, that is 5 digits long.
	 * 
	 * @param score
	 *            Score to be sanitized
	 * @return Returns string with score, that contains 5 digits.
	 */
	protected String sanitizeScore(double score) {
		double rounded = (double) Math.round(score * 100000d) / 100000d;
		String out = rounded + "";
		if (out.length() < 7) { // 5 digits, point and leading zero
			for (int i = out.length(); i < 7; i++)
				out += "0";
		}

		return out;
	}

	/**
	 * Builds the the type drop menu.
	 */
	protected void buildType() {
		for (UtilityFunction.TYPE t : UtilityFunction.TYPE.values()) {
			type.addItem(t);
		}

		updateType();

		type.addActionListener(this);
	}

	/**
	 * Builds the parameters labels.
	 */
	protected void buildParameters() {
		mParam.setToolTipText("multiplier");
		kParam.setToolTipText("power");
		cParam.setToolTipText("shiftX");
		bParam.setToolTipText("shiftY");

		updateParameters();

		// Add key listeners
		mParam.addKeyListener(this);
		kParam.addKeyListener(this);
		cParam.addKeyListener(this);
		bParam.addKeyListener(this);

		// Wrap around the parameters
		paramsWrapper.add(new JLabel("["));
		paramsWrapper.add(mParam);
		paramsWrapper.add(new JLabel(","));
		paramsWrapper.add(kParam);
		paramsWrapper.add(new JLabel(","));
		paramsWrapper.add(cParam);
		paramsWrapper.add(new JLabel(","));
		paramsWrapper.add(bParam);
		paramsWrapper.add(new JLabel("]"));
	}

	/**
	 * Adds plot button to wrapper.
	 */
	public void addPlotButtonToParams() {
		GraphicResources.setButtonIcon(plotButton, GraphicResources.writeI);
		plotButton.addActionListener(this);

		paramsWrapper.add(plotButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == type) { // We have changed type
			createNewFunction((TYPE) type.getSelectedItem(), mParam.getText(), kParam.getText(), cParam.getText(),
					bParam.getText());
		} else if (e.getSource() == plotButton) {
			if (plotter == null) {
				plotter = new FunctionPlotter(this, consideration);
			}
		}

	}

	/**
	 * Creates new function from supplied string parameters.
	 * 
	 * @param type
	 *            type of the funciton (LINEAR etc. see UtilityFunction.TYPE)
	 * @param sm
	 *            m parameter
	 * @param sk
	 *            k parameter
	 * @param sc
	 *            c parameter
	 * @param sb
	 *            b parameter
	 */
	protected void createNewFunction(UtilityFunction.TYPE type, String sm, String sk, String sc, String sb) {
		try {
			double m = tryParseToDouble(sm);
			double k = tryParseToDouble(sk);
			double c = tryParseToDouble(sc);
			double b = tryParseToDouble(sb);

			logger.info("Creating new utility function " + type.name() + " with parameters: " + m + ", " + k + ", " + c
					+ ", " + b);
			consideration.setUtilityFunction(FunctionFactory.createFunction(type, m, k, c, b));
		} catch (NumberFormatException ex) {
			logger.error("Unable to parse supplied type and parameters. The utility function has not changed!");
			return;
		}
	}

	/**
	 * Tries to parse double from string.
	 * 
	 * @param str
	 *            String to parse.
	 * @return Returns the double.
	 */
	protected double tryParseToDouble(String str) {
		return Double.parseDouble(str);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource() == mParam || e.getSource() == kParam || e.getSource() == cParam || e.getSource() == bParam) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				createNewFunction((TYPE) type.getSelectedItem(), mParam.getText(), kParam.getText(), cParam.getText(),
						bParam.getText());
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	/**
	 * Called if the window with plotter is closing.
	 */
	public void windowClosing() {
		plotter = null;

	}

}
