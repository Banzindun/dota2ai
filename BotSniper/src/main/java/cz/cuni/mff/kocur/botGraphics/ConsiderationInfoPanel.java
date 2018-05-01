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

public class ConsiderationInfoPanel extends JPanel implements ActionListener, KeyListener {
	private static final Logger logger = LogManager.getLogger(ConsiderationInfoPanel.class);

	/**
	 * JComboBox for selection of type of function of this consideration.
	 */
	protected JComboBox<UtilityFunction.TYPE> type = new JComboBox<UtilityFunction.TYPE>();

	protected JTextField mParam = new JTextField("", 2);
	protected JTextField kParam = new JTextField("", 2);
	protected JTextField cParam = new JTextField("", 2);
	protected JTextField bParam = new JTextField("", 2);

	/**
	 * Function plotter. This is a window that plots the utility functions.
	 */
	private FunctionPlotter plotter = null;

	/**
	 * Button that creates new function plotter.
	 */
	private JButton plotButton = new JButton("Plot");

	protected JPanel paramsWrapper = new JPanel();

	private JLabel name = new JLabel();
	private JLabel normalizedValue = new JLabel();
	private JLabel score = new JLabel();

	protected Consideration consideration = null;

	protected GridBagConstraints gbc;

	public ConsiderationInfoPanel() {

	}

	public ConsiderationInfoPanel(Consideration c) {
		this.setLayout(new GridBagLayout());

		consideration = c;

		build();
	}

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

	public void update() {
		name.setText(consideration.getName());

		// Take scores only 5 digits of the score.
		normalizedValue.setText("IN: " + sanitizeScore(consideration.getNormalizedInput()));
		score.setText("OUT: " + sanitizeScore(consideration.getScore()));
	}
	
	public void updateParameters() {
		UtilityFunction f = consideration.getUtilityFunction();

		mParam.setText(f.getM() + "");
		kParam.setText(f.getK() + "");
		cParam.setText(f.getC() + "");
		bParam.setText(f.getB() + "");
	}
	
	public void updateType() {
		type.setSelectedItem(consideration.getUtilityFunction().getType());
	}

	/**
	 * This function takes double, rounds it to 5 digits and then makes a string out
	 * of it, that is 5 digits long.
	 * 
	 * @param score
	 * @return
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

	protected void buildType() {
		for (UtilityFunction.TYPE t : UtilityFunction.TYPE.values()) {
			type.addItem(t);
		}

		updateType();

		type.addActionListener(this);
	}

	protected void buildParameters() {
		mParam.setToolTipText("multiplier");
		kParam.setToolTipText("power");
		cParam.setToolTipText("shiftX");
		bParam.setToolTipText("shiftY");
		
		updateParameters();

		mParam.addKeyListener(this);
		kParam.addKeyListener(this);
		cParam.addKeyListener(this);
		bParam.addKeyListener(this);

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

	public void windowClosing() {
		plotter = null;

	}

}
