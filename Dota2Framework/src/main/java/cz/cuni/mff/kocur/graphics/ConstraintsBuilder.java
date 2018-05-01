package cz.cuni.mff.kocur.graphics;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * This class helps us with building the GridBagConstraints. It makes creating
 * gbc a bit easier and easier to read.
 * 
 * @author kocur
 *
 */
public class ConstraintsBuilder {

	/**
	 * Constraints we are building.
	 */
	GridBagConstraints constraints;

	/**
	 * 
	 * @return Returns new instance of this builder.
	 */
	public static ConstraintsBuilder build() {
		ConstraintsBuilder builder = new ConstraintsBuilder();
		return builder;
	}

	/**
	 * Constructor, that creates new constraints.
	 */
	public ConstraintsBuilder() {
		constraints = new GridBagConstraints();
	}

	/**
	 * 
	 * @return Returns the built constraints.
	 */
	public GridBagConstraints get() {
		return constraints;
	}

	/**
	 * Sets a gridx to builded constraints.
	 * 
	 * @param x
	 *            Value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder gridx(int x) {
		constraints.gridx = x;
		return this;
	}

	/**
	 * Sets a gridy to builded constraints.
	 * 
	 * @param y
	 *            Value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder gridy(int y) {
		constraints.gridy = y;
		return this;
	}

	/**
	 * Sets x to gridx and gridy of the constraints.
	 * 
	 * @param x
	 *            Value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder gridxy(int x) {
		constraints.gridx = x;
		constraints.gridy = x;
		return this;
	}

	/**
	 * Sets x and y to gridx and gridy of the constraints.
	 * 
	 * @param x
	 *            Value.
	 * @param y
	 *            Value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder gridxy(int x, int y) {
		constraints.gridx = x;
		constraints.gridy = y;
		return this;
	}

	/**
	 * Sets weight x of the constraints.
	 * 
	 * @param x
	 *            Value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder weightx(double x) {
		constraints.weightx = x;
		return this;
	}

	/**
	 * Sets weighty y of the constraints.
	 * 
	 * @param y
	 *            Value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder weighty(double y) {
		constraints.weighty = y;
		return this;
	}

	/**
	 * Sets weightx and weighty to the passed x.
	 * 
	 * @param x
	 *            Value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder weightxy(double x) {
		constraints.weighty = x;
		constraints.weightx = x;
		return this;
	}

	/**
	 * Sets x and y to weightx and weighty.
	 * @param x Value.
	 * @param y Value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder weightxy(double x, double y) {
		constraints.weighty = y;
		constraints.weightx = x;
		return this;
	}

	/**
	 * Sets ipady to this constraints. 
	 * @param y Value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder ipady(int y) {
		constraints.ipady = y;
		return this;
	}

	/**
	 * Sets ipadx to the constraints.
	 * @param x Value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder ipadx(int x) {
		constraints.ipadx = x;
		return this;
	}

	/**
	 * Sets ipadxy from the supplied x to the constraints. 
	 * @param x Value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder ipadxy(int x) {
		constraints.ipadx = x;
		constraints.ipady = x;
		return this;
	}

	/**
	 * Sets x and y to ipadx and ipady.
	 * @param x Value.
	 * @param y Value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder ipadxy(int x, int y) {
		constraints.ipadx = x;
		constraints.ipady = y;
		return this;
	}

	/**
	 * Sets insets of the builded constraints.
	 * @param w Inset from the top.
	 * @param x Inset from the left.
	 * @param y Inset from the bottom.
	 * @param z Inset from the right.
	 * @return Returns this.
	 */
	public ConstraintsBuilder insets(int w, int x, int y, int z) {
		constraints.insets = new Insets(w, x, y, z);
		return this;
	}

	/**
	 * Sets all insets to the passed value.
	 * @param x Value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder insets(int x) {
		constraints.insets = new Insets(x, x, x, x);
		return this;
	}

	/**
	 * Sets the anchor of the constraints.
	 * @param f Value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder anchor(int f) {
		constraints.anchor = f;
		return this;
	}

	/**
	 * Sets fill of the constraints.
	 * @param f The fill value.
	 * @return Returns this.
	 */
	public ConstraintsBuilder fill(int f) {
		constraints.fill = f;
		return this;
	}

}
