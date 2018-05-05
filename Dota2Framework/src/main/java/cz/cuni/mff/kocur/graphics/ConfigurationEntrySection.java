package cz.cuni.mff.kocur.graphics;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Colors;
import cz.cuni.mff.kocur.configuration.CItem;

/**
 * Class that represents section of configuration items
 * 
 * @author kocur
 *
 */
public class ConfigurationEntrySection extends JPanel {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -5962432904197806834L;

	/**
	 * Logger registered for this class.
	 */
	private static final Logger logger = LogManager.getLogger(ConfigurationEntrySection.class);

	/**
	 * Constraints we use throughout this object.
	 */
	private GridBagConstraints gbc;

	/**
	 * The item we use to create the section.
	 */
	private CItem item;

	/**
	 * The savable item's representation. (CRadio, CCheck etc.)
	 */
	private CSavable savable;

	/**
	 * Name of the field we are storing.
	 */
	String name;

	/**
	 * Constructor.
	 * 
	 * @param i
	 *            Item.
	 * @param name
	 *            It's name.
	 */
	public ConfigurationEntrySection(CItem i, String name) {
		item = i;
		this.name = name;
	}

	/**
	 * Build the section.
	 */
	public void build() {
		gbc = ConstraintsBuilder.build().gridxy(0).anchor(GridBagConstraints.WEST).fill(GridBagConstraints.NONE)
				.weightxy(0).insets(0, 0, 0, 0).get();

		this.setLayout(new GridBagLayout());
		addTitlePanel();

		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridy++;

		// Create wrapper that will wrap around CITEM representation
		JPanel wrapper = new JPanel(new GridBagLayout());
		wrapper.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		// Create graphics for item and add them to section
		createCItemGraphics(wrapper);
		this.add(wrapper, gbc);
	}

	/**
	 * Creates the information entry graphics.
	 * 
	 * @param wrapper
	 *            Wrapper to which we should insert all the graphics.
	 */
	private void createCItemGraphics(JPanel wrapper) {
		GridBagConstraints _gbc = ConstraintsBuilder.build().gridxy(0).anchor(GridBagConstraints.WEST)
				.fill(GridBagConstraints.NONE).weightxy(0).insets(2, 10, 2, 10).get();

		if (item.getLabel() != null) {
			JLabel label = new JLabel(item.getLabel().toUpperCase());

			label.setForeground(Colors.YELLOW);
			wrapper.add(label, _gbc);
			_gbc.gridy++;
		}

		if (item.getHelp() != null) {
			JTextArea help = new JTextArea(1, 40);
			help.setText(item.getHelp());
			help.setEditable(false);

			help.setBackground(this.getBackground());
			help.setBorder(BorderFactory.createEmptyBorder()); // LAF??

			help.setLineWrap(true);
			help.setWrapStyleWord(true);

			wrapper.add(help, _gbc);
			_gbc.gridy++;
		}

		if (item.getType() != null) {
			// Create instance of argument passed through configuration.
			try {
				Class<?> cls = Class.forName(this.getClass().getPackage().getName() + "." + item.getType());
				Component cfgEntry = (Component) cls.getDeclaredConstructor(CItem.class).newInstance(item);

				savable = (CSavable) cfgEntry;
				wrapper.add(cfgEntry, _gbc);
			} catch (Exception e) {
				logger.error("Unable to create class type from argument: " + item.getType(), e);
			}
		}
	}

	/**
	 * Adds title to this section.
	 */
	private void addTitlePanel() {
		JLabel title = new JLabel(" " + name.toUpperCase() + " ");
		title.setBackground(this.getBackground().darker());
		title.setOpaque(true);
		gbc.fill = GridBagConstraints.BOTH;
		this.add(title, gbc);
	}

	/**
	 * Saves, what this section represents. (the citem)
	 */
	public void save() {
		savable.save();

	}

}
