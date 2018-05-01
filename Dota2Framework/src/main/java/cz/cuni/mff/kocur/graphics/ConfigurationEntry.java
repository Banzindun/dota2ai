package cz.cuni.mff.kocur.graphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.GraphicResources;
import cz.cuni.mff.kocur.configuration.CItem;
import cz.cuni.mff.kocur.configuration.Configuration;

/**
 * Extension of JPanel, that is used to display and later edit configuration of
 * framework and agents.
 * 
 * @author Banzindun
 *
 */
public class ConfigurationEntry extends BuildableJPanel implements ActionListener {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -1066253612521585511L;

	/**
	 * ConfigurationTab logger instance.
	 */
	private static Logger logger = LogManager.getLogger(ConfigurationEntry.class.getName());

	/**
	 * Reference to configuration.
	 */
	private Configuration configuration;

	/**
	 * Reload button.
	 */
	private JButton reload = new JButton("Reload");

	/**
	 * Save button.
	 */
	private JButton save = new JButton("Save");

	/**
	 * Write button.
	 */
	private JButton write = new JButton("Write");

	/**
	 * Grid bag constraints that we use to position our graphics.
	 */
	private GridBagConstraints gbc;

	/**
	 * Entries of this configuration.
	 */
	private LinkedList<ConfigurationEntrySection> sections = new LinkedList<>();

	/**
	 * Constructor, that takes a configuration, for which we are creating the entry.
	 * 
	 * @param c
	 *            Configuration.
	 */
	public ConfigurationEntry(Configuration c) {
		super();

		this.configuration = c;

		this.setLayout(new GridBagLayout());
		initButtons();
	}

	@Override
	public void build() {
		// Create grid bag constraints
		gbc = ConstraintsBuilder.build().fill(GridBagConstraints.BOTH).anchor(GridBagConstraints.NORTHWEST).weightxy(1)
				.gridxy(0).insets(2, 5, 2, 5).get();

		JPanel cfgSection = createConfiguration();

		JScrollPane scroller = new JScrollPane(cfgSection);

		scroller.setBorder(BorderFactory.createEmptyBorder());
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.getViewport().setViewPosition(new Point(0, 0));

		this.add(scroller, gbc);
		scroller.setVisible(true);
		scroller.getVerticalScrollBar().setValue(0);

		gbc.gridy++;

		addButtons();
		gbc.gridy++;

		// Call super - will set the boolean that this was built
		super.build();
	}

	/**
	 * Initializes the buttons, adds listeners and icons to them.
	 */
	private void initButtons() {
		reload.addActionListener(this);
		save.addActionListener(this);
		write.addActionListener(this);

		GraphicResources.setButtonIcon(save, GraphicResources.saveI);
		GraphicResources.setButtonIcon(reload, GraphicResources.restartI);
		GraphicResources.setButtonIcon(write, GraphicResources.writeI);

		save.setText("Save");
		reload.setText("Reload");
		write.setText("Write");
	}

	/**
	 * Adds buttons to this panel.
	 */
	private void addButtons() {
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0;
		gbc.weighty = 0;

		JPanel wrap = new JPanel();
		wrap.add(reload);
		wrap.add(save);
		wrap.add(write);
		this.add(wrap, gbc);
	}

	/**
	 * Creates configuration section.
	 * 
	 * @return Returns new JPanel that represents a new cfg section.
	 */
	private JPanel createConfiguration() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints _gbc = ConstraintsBuilder.build().fill(GridBagConstraints.BOTH)
				.anchor(GridBagConstraints.NORTHWEST).weightxy(1, 0).gridxy(0).insets(10, 10, 0, 10).get();

		Map<String, CItem> items = configuration.getConfiguration();
		for (Entry<String, CItem> i : items.entrySet()) {
			ConfigurationEntrySection section = new ConfigurationEntrySection(i.getValue(), i.getKey());
			section.build();

			sections.add(section);
			panel.add(section, _gbc);
			_gbc.gridy++;
		}

		// Add filler
		_gbc.weighty = 1;
		panel.add(new JLabel(""), _gbc);

		return panel;
	}

	/**
	 * 
	 * @param bc
	 *            Configuration.
	 * @return Returns true, if the configuration stored in this entry is equal to
	 *         the passed one.
	 */
	public boolean cfgEqual(Configuration bc) {
		return configuration.equals(bc);
	}

	/**
	 * Reloads this entry (removes all and builds it again).
	 */
	private void reload() {
		this.removeAll();

		build();

		this.revalidate();
		this.repaint();
	}

	/**
	 * Writes the configuration to file.
	 */
	private void write() {
		logger.info("Saving the stored bot configuration.");
		configuration.save();
	}

	/**
	 * Saves the configuration.
	 */
	private void save() {
		for (ConfigurationEntrySection c : sections)
			c.save();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == reload) {
			logger.info("Reloading the configuration.");
			reload();
			configuration.onChange();
		} else if (e.getSource() == write) {
			logger.info("Writing configuration to file specified at: " + configuration.getPath());
			write();
			configuration.onChange();
		} else if (e.getSource() == save) {
			logger.info("Updating the configuration.");
			save();
			configuration.onChange();
		}

	}

}
