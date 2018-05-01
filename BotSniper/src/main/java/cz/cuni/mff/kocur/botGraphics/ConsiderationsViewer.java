package cz.cuni.mff.kocur.botGraphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.GraphicResources;
import cz.cuni.mff.kocur.brain.Brain;
import cz.cuni.mff.kocur.decisions.Decision;
import cz.cuni.mff.kocur.graphics.ConstraintsBuilder;

public class ConsiderationsViewer extends JPanel implements ActionListener{
	
	private static final Logger logger = LogManager.getLogger(ConsiderationsViewer.class);
	
	
	private HashMap<Decision, DecisionPanel> decisionPanels = new HashMap<>();
	
	private Brain brain = null;
	
	private GridBagConstraints gbc;
	
	private JButton reloadBrain = new JButton("Reload brain");
	
	private JButton hideAll = new JButton("Hide all");
	 
	private JButton showAll = new JButton("Show all");
	
	public ConsiderationsViewer(Brain brain) {
		this.brain =  brain;
		
		this.setLayout(new GridBagLayout());
		
	}


	public void build() {
		gbc = ConstraintsBuilder.build()
				.gridxy(0)
				.weightxy(1,0)
				.fill(GridBagConstraints.BOTH)
				.anchor(GridBagConstraints.WEST)
				.get();
		
		addButtons();
		
		LinkedList<Decision> decisions = brain.getAllDecisions();
		for (Decision d : decisions) {
			DecisionPanel panel = new DecisionPanel(d);
			panel.build(); 
			panel.updateDecisions();
			
			decisionPanels.put(d, panel);
			
			this.add(panel, gbc);
			gbc.gridy++;
		}
		
		
		// Addd filler
		gbc.gridy++;
		gbc.weighty = 1;
		this.add(new JPanel(), gbc);
	}

	private void addButtons() {
		reloadBrain.setIcon(GraphicResources.restartI);
		reloadBrain.addActionListener(this);
		
		hideAll.addActionListener(this);
		showAll.addActionListener(this);
		
		JPanel wrap = new JPanel(new GridBagLayout());
		GridBagConstraints _gbc = ConstraintsBuilder.build().gridxy(0).weightxy(0).fill(GridBagConstraints.NONE).get();
				
		wrap.add(reloadBrain, _gbc);
		_gbc.gridx++;
		
		// Filler
		_gbc.fill = GridBagConstraints.BOTH;
		_gbc.weightx = 1;
		wrap.add(new JPanel(), _gbc);
		
		_gbc.gridx++;
		_gbc.weightx = 0;
		_gbc.fill = GridBagConstraints.NONE;
		
		wrap.add(hideAll, _gbc);
		_gbc.gridx++;
		
		wrap.add(showAll, _gbc);
		
		this.add(wrap, gbc);
		gbc.gridy++;
	}


	public void update() {
		LinkedList<Decision> decisions = brain.getAllDecisions();
		
		for (Decision d : decisions) {
			DecisionPanel dp = decisionPanels.get(d);
			if (dp == null) {
				logger.error("Unable to find decision in decision panels.");
			} else {
				dp.updateDecisions();
			}
		}		
	}

	public void focused() {
		update();
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == reloadBrain) {
			brain.reload();
			this.removeAll();
			this.build();
		} else if (e.getSource() == hideAll) {
			for (DecisionPanel d : decisionPanels.values()) {
				d.hideBody();
			}
		} else if (e.getSource() == showAll) {
			for (DecisionPanel d : decisionPanels.values()) {
				d.showBody();
			}
		}
		
		
	}
	
	
}
