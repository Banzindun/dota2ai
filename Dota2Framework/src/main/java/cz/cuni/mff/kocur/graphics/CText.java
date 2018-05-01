package cz.cuni.mff.kocur.Graphics;

import javax.swing.JTextField;

import cz.cuni.mff.kocur.Configuration.CItem;

public class CText extends JTextField implements CSavable{
	
	CItem item;
	
	public CText(CItem item) {
		super(item.getValue());
		this.item = item;
	}

	@Override
	public void save() {
		item.setValue(this.getText());
	}
}
