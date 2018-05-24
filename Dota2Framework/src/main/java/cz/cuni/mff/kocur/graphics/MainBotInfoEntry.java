package cz.cuni.mff.kocur.graphics;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.agent.ControllerWrapper;
import cz.cuni.mff.kocur.base.GraphicResources;
import cz.cuni.mff.kocur.events.Event;
import cz.cuni.mff.kocur.events.FrameworkEventListener;
import cz.cuni.mff.kocur.events.ListenersManager;
import cz.cuni.mff.kocur.world.Hero;
import cz.cuni.mff.kocur.world.Inventory;
import cz.cuni.mff.kocur.world.Item;

/**
 * Class that is used to display some basic information about the agent's status in the game.
 * @author kocur
 *
 */
public class MainBotInfoEntry extends WindowedJPanel implements FrameworkEventListener{
	/**
	 * Logger registered for this class. 
	 */
	protected static final Logger logger = LogManager.getLogger(MainBotInfoEntry.class);

	/**
	 * Generated serial version id. 
	 */
	private static final long serialVersionUID = -753884404082827809L;
	
	/**
	 * Wrapper that wraps around the controller.
	 */
	private ControllerWrapper wrapper = null;
	
	/**
	 * Reference to parent. (MainRunningInfo)
	 */
	protected MainRunningInfo parent = null;
	
	/**
	 * Health label.
	 */
	protected JLabel health = new JLabel();
	
	/**
	 * Mana label.
	 */
	protected JLabel mana = new JLabel();
	
	/**
	 * Gold label.
	 */
	protected JLabel gold = new JLabel();
	
	/**
	 * Kills/deaths ratio label.
	 */
	protected JLabel kd = new JLabel();
	
	/**
	 * Agent's inventory slots.
	 */
	protected ArrayList<JLabel> inventorySlots = new ArrayList<JLabel>();
	
	/**
	 * Section that will contain the inventory. We will want reference for times, when we might want to do something with it. (hide it)
	 */
	protected WindowedJPanel inventorySection;
	
	/**
	 * JPanel that will wrap around health, mana, gold etc.
	 */
	protected JPanel indicatorsSection;
	
	/**
	 * Constructor.
	 * @param parent Parent object, that is used if this entry is closed.
	 * @param wrap Wrapper.
	 */
	public MainBotInfoEntry(MainRunningInfo parent, ControllerWrapper wrap) {
		this.parent = parent;
		wrapper = wrap;
				
		// Change title of window to this id.
		changeTitle(wrap.getName());
		
		// Add listener for big updates.
		ListenersManager.addFrameworkListener("team_update", this);
	}
	
	/**
	 * Builds the entry.
	 */
	public void build() {
		inventorySection = new WindowedJPanel() {			
			private static final long serialVersionUID = 4582329217503210257L;

			@Override
			protected void close() {
				inventorySection.setVisible(false);
			}};
		inventorySection.build();
		inventorySection.hideCloseLabel();
		
		// Build body.
		buildBody();

		super.build();
	}
	
	/**
	 * Builds the body.
	 */
	protected void buildBody() {
		//gbc.gridy++;
		gbc.fill = GridBagConstraints.BOTH;
		
		createIndicatorsSection();
		body.add(indicatorsSection, gbc);
		gbc.gridy++;
		
		createInventorySection();	
		body.add(inventorySection, gbc);
	}
	
	/**
	 * Creates section with agent's indicators.
	 */
	private void createIndicatorsSection() {
		indicatorsSection = new JPanel(new GridLayout(2,2));
		
		indicatorsSection.add(wrapAround(health, "HP:"));
		indicatorsSection.add(wrapAround(mana, "MP:"));
		indicatorsSection.add(wrapAround(gold, "GOLD:"));
		indicatorsSection.add(wrapAround(kd, "K/D:"));
	}
	
	/**
	 * Creates section with agent's inventory.
	 */
	private void createInventorySection() {
		// Initialize the slots
		for (int i = 0; i <= Inventory.stashSlotMax; i++) {
			inventorySlots.add(new JLabel());
		}
		
		GridBagConstraints _gbc = ConstraintsBuilder.build()
				.gridxy(0)
				.weightxy(0)
				.fill(GridBagConstraints.BOTH)
				.insets(0)
				.get();
	
		for (JLabel slot : inventorySlots) {
			inventorySection.getBody().add(slot, _gbc);
			_gbc.gridy++;
			
			
			if (_gbc.gridy == 3) {
				_gbc.gridy = 0;
				_gbc.gridx++;
			}
		}
		
		inventorySection.changeTitle("INVENTORY");
		
		// Add something more?
		
	}

	/**
	 * Creates a wrap around supplied panel and title.
	 * @param panel Panel contaning something.
	 * @param title Title of the panel.
	 * @return Returns JPanel that contains JLabel with title and supplied panel next to each other.
	 */
	protected JPanel wrapAround(JLabel panel, String title) {
		JPanel wrap = new JPanel();
		
		wrap.add(new JLabel(title));
		wrap.add(panel);
		
		return wrap;
	}

	@Override
	public void triggered(Event e) {
		// Get the hero from controller
		Hero h = wrapper.getController().getHero();
		
		// Check if hero is not null, that can happen at the start of the application before he is initialized
		if (h == null)
			return;
		
		// Set hero's basic values 
		health.setText(h.getHealth() + "");
		mana.setText(h.getMana() + "");
		gold.setText(h.getGold() + "");
		kd.setText(h.getKills() + "/" + h.getDeaths());
		
		ArrayList<Item> inv = h.getInventory().getAllItems();
		for (int i = 0; i < inventorySlots.size(); i++) {
			Item item = inv.get(i);
			
			String itemName;
			if (item == null)
				itemName = "";
			else 
				itemName = item.getName();
			
			ImageIcon icon;
			if (itemName.length() == 0) { // EMPTY SLOT
				icon = GraphicResources.getItemIcon("empty");
			} else 
				icon = GraphicResources.getItemIcon(itemName);
			
			if (icon == null)
				inventorySlots.get(i).setText(itemName);
			else 
				inventorySlots.get(i).setIcon(icon);
		}
		
	}

	@Override
	public void triggered(Event e, Object... os) {
		triggered(e);
		
	}

	/**
	 * On close we will alert the parent.
	 */
	protected void close() {
		parent.removeEntry(this);
	}	
	
}
