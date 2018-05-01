package cz.cuni.mff.kocur.Graphics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cz.cuni.mff.kocur.Configuration.CItem;

public class CRadio extends JPanel implements CSavable{
	
	CItem item;
	
	ButtonGroup bGroup;
	
	public CRadio(CItem item) {
		super();
		
		this.item = item;
		
		bGroup = new ButtonGroup();		
		
		for (String s : item.getOptions()) {
			JRadioButton b = new JRadioButton(s, s.equals(item.getValue()));
			b.setActionCommand(s);
		
			bGroup.add(b);
			this.add(b);
		}	
		
	}

	@Override
	public void save() {
		item.setValue(bGroup.getSelection().getActionCommand());		
	}

	
	
}
