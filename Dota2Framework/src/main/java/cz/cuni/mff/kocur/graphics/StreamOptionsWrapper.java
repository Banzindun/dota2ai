package cz.cuni.mff.kocur.graphics;

import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cz.cuni.mff.kocur.streaming.BaseDropViewer;

/**
 * Class that wraps around the streams inside the streams panel.
 * @author kocur
 *
 */
public class StreamOptionsWrapper extends JPanel{

	/**
	 * Generated serial version id. 
	 */
	private static final long serialVersionUID = 7349323091451151147L;
	
	/**
	 * Panel with options.
	 */
	protected JPanel optionsPanel = new JPanel();
		
	/**
	 * Constructor, that wraps around the viewer.
	 * @param viewer Streams viewer.
	 */
	public StreamOptionsWrapper(BaseDropViewer viewer) {
		super();
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
        Rectangle rect = viewer.getBounds();
        Rectangle visibleRect = viewer.getVisibleRect();
        double tx = (rect.getWidth() - visibleRect.getWidth())/2;
        double ty = (rect.getHeight() - visibleRect.getHeight())/2;
        visibleRect.setBounds((int)tx, (int)ty, visibleRect.width, visibleRect.height);
        viewer.scrollRectToVisible(visibleRect);
        
		viewer.setWrapper(this);
		
		optionsPanel = viewer.getOptionsPanel();
		
		if (optionsPanel != null) 
			this.add(optionsPanel);
		
		this.add(viewer);
	}
}
