package cz.cuni.mff.kocur.Graphics;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GUtils {
	static GridBagConstraints getGBC() {
    	GridBagConstraints gbc = new GridBagConstraints();
    	gbc.fill = GridBagConstraints.HORIZONTAL;
    	gbc.anchor = GridBagConstraints.NORTH;
    	
    	gbc.gridx = 0;
    	gbc.gridy = 0;
    	gbc.weightx = 0;
    	gbc.weighty = 0;
    	gbc.ipady = 0;
    	gbc.insets = new Insets(0,0,0,0);
    				
		return gbc;
	}
	
	
}
