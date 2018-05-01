package cz.cuni.mff.kocur.Graphics;

import java.util.List;
import java.util.LinkedList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import cz.cuni.mff.kocur.Configuration.CItem;

/*
 * 	private JPanel createJCheck(CItem i) {
		JPanel boxes = new JPanel();
		

		return boxes;		
	}
 * 
 */
public class CCheck extends JPanel implements CSavable{
	
	private CItem item;
	
	LinkedList<JCheckBox> boxes = new LinkedList<JCheckBox>();
	
	
	public CCheck(CItem item) {
		super();
		
		this.item = item;
		
		for (String s : item.getOptions()) {
			boxes.add(new JCheckBox(s, s.equals(item.getValue())));
			boxes.getLast().setActionCommand(s);
			this.add(boxes.getLast());			
		}		
	}

	@Override
	public void save() {
		LinkedList<String> selected = new LinkedList<String>();
		for (JCheckBox b : boxes) {
			if (b.isSelected()) {
				selected.add(b.getActionCommand());				
			}
		}
		
		// TODO should consider how will be JCheckBox used and set the output value accordingly
		item.setValue(selected.toString());
	}
	

}
