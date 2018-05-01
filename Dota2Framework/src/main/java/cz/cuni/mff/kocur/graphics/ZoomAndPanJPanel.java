package cz.cuni.mff.kocur.graphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.world.GridSystem;

/**
 * Class that represents panel, that can can be painted on and which is zoomable
 * and draggable.
 * 
 * @author kocur
 *
 */
public abstract class ZoomAndPanJPanel extends JPanel
		implements MouseWheelListener, MouseMotionListener, MouseListener {
	/**
	 * Generated serial version id.
	 */
	private static final long serialVersionUID = -6661907776994771854L;

	/**
	 * Logger registered for ZoomAndPanJPanel class.
	 */
	private static final Logger logger = LogManager.getLogger(ZoomAndPanJPanel.class);

	/**
	 * Buffered image, where we paint. So that there is no flickering.
	 */
	protected BufferedImage buffer = null;

	/**
	 * Current zoom level.
	 */
	protected double zoom = 1.0;
	
	/**
	 * Maximum zoom level.
	 */
	protected final double MAX_ZOOM = 16.0;
	
	/**
	 * Minimum zoom level.
	 */
	protected final double MIN_ZOOM = 0.25;
	
	/**
	 * Step by which we change the zoom.
	 */
	protected final double ZOOM_STEP = 0.05;
	
	/**
	 * X translation.
	 */
	protected double translateX = 0;
	
	/**
	 * Y translation.
	 */
	protected double translateY = 0;

	/**
	 * Affine transform.
	 */
	protected AffineTransform at = null;
	
	
	protected Point2D XFormedPoint;
	
	/**
	 * X reference.
	 */
	protected double referenceX;
	
	/**
	 * Y reference.
	 */
	protected double referenceY;
	
	/**
	 * Initial affine transform.
	 */
	protected AffineTransform initialTransform;

	/**
	 * Displayed height.
	 */
	protected int _height;
	
	/**
	 * Displayed width.
	 */
	protected int _width;

	/**
	 * We might want to make the displayed image bigger. How many tiles of this
	 * picture correspond to one tile of original grid?
	 */
	protected int resolution = 1;

	/**
	 * Constructor.
	 * @param g GridSystem that we are displaying. We need to know the resolution.
	 */
	public ZoomAndPanJPanel(GridSystem g) {
		super();
		setup(resolution * g.getWidth(), resolution * g.getHeight());

		addMouseWheelListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	public ZoomAndPanJPanel() {
		_width = 1;
		_height = 1;

		addMouseWheelListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	/**
	 * Setups the buffered image and height and width.
	 * @param width Width of the area we will be displaying.
	 * @param height Height of the area we will be displaying.
	 */
	public void setup(int width, int height) {
		_height = height;
		_width = width;

		buffer = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
		setPreferredSize(new Dimension(_width, _height));
	}

	/**
	 * Method that should redraw the buffer.
	 */
	protected abstract void redrawBuffer();

	/**
	 * Method called after this panel was focused.
	 */
	public void focused() {
		if (this.getGraphics() != null) {
			redrawBuffer();
			repaint();
		}
	};

	
	public void paintComponent(Graphics g) {
		// super.paintComponent(g);

		Graphics2D g2D = (Graphics2D) g;

		// Get the transform
		AffineTransform saveTransform = g2D.getTransform();

		// Clear the rect
		g2D.clearRect(0, 0, getWidth(), getHeight());

		// New transform
		at = new AffineTransform(saveTransform);

		// Translate and scale the at
		at.translate(getWidth() / 2, getHeight() / 2);
		at.scale(zoom, zoom);
		at.translate(-getWidth() / 2, -getHeight() / 2);

		at.translate(translateX, translateY);

		g2D.setTransform(at);

		// Draw the buffer 
		if (buffer != null) {
			g.drawImage(buffer, 0, 0, null);
		}

		g2D.setTransform(saveTransform);
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		double newZoom = zoom;

		if (notches < 0) {
			newZoom += ZOOM_STEP;
			if (newZoom > MAX_ZOOM)
				return;
		} else {
			newZoom -= ZOOM_STEP;

			if (newZoom < MIN_ZOOM)
				return;
		}

		zoom = newZoom;
	}

	public void mouseDragged(MouseEvent e) {
		try {
			XFormedPoint = initialTransform.inverseTransform(e.getPoint(), null);
		} catch (NoninvertibleTransformException e1) {
			e1.printStackTrace();
		}

		// the size of the pan translations
		// are defined by the current mouse location subtracted
		// from the reference location
		double deltaX = XFormedPoint.getX() - referenceX;
		double deltaY = XFormedPoint.getY() - referenceY;

		// make the reference point be the new mouse point.
		referenceX = XFormedPoint.getX();
		referenceY = XFormedPoint.getY();

		translateX += deltaX;
		translateY += deltaY;

		// schedule a repaint.
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		try {
			XFormedPoint = at.inverseTransform(e.getPoint(), null);
		} catch (NoninvertibleTransformException e1) {
			logger.error("NoninvertibleTransformException.");
			e1.printStackTrace();
		}

		referenceX = XFormedPoint.getX();
		referenceY = XFormedPoint.getY();
		initialTransform = at;
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}
}
