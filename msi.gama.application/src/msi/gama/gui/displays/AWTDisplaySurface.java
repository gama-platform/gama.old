/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.displays;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.Semaphore;
import javax.swing.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.layers.IDisplayLayer;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import com.vividsolutions.jts.geom.Envelope;

public final class AWTDisplaySurface extends JPanel implements IDisplaySurface {

	boolean paused;
	IDisplayManager manager;
	private volatile boolean canBeUpdated = true;
	double widthHeightConstraint = 1.0;
	private PopupMenu agentsMenu = new PopupMenu();
	private IGraphics displayGraphics;
	private Color bgColor = Color.black;
	protected double zoomIncrement = 0.1;
	protected double zoomFactor = 1.0 + zoomIncrement;
	protected BufferedImage buffImage;
	protected int bWidth, bHeight;
	Point origin = new Point(0, 0);
	protected Point mousePosition;
	Dimension previousPanelSize;
	protected boolean navigationImageEnabled = true;
	protected AuxiliaryDisplaySurface navigator;
	private final AffineTransform translation = new AffineTransform();
	private final Semaphore paintingNeeded = new Semaphore(1, true);
	private final Thread animationThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (true) {
				try {
					// GUI.debug("Painting permits remaining before acquiring: "
					// +
					// paintingNeeded.availablePermits());
					paintingNeeded.acquire();
					// GUI.debug("Painting permits remaining after acquiring: "
					// +
					// paintingNeeded.availablePermits());

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				repaint();
				Toolkit.getDefaultToolkit().sync();
			}
		}
	});

	@Override
	public void setPaused(final boolean flag) {
		paused = flag;
		updateDisplay();
	}

	@Override
	public boolean isPaused() {
		return paused;
	}

	@Override
	public IDisplayManager getManager() {
		return manager;
	}

	public AuxiliaryDisplaySurface getNavigator() {
		if ( navigator == null ) {
			navigator = new AuxiliaryDisplaySurface(this);
		}
		return navigator;
	}

	protected Cursor createCursor() {
		Image im =
			new BufferedImage((int) SELECTION_SIZE + 4, (int) SELECTION_SIZE + 4,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) im.getGraphics();
		g.setColor(Color.black);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(new BasicStroke(3.0f));
		g.draw(new Rectangle2D.Double(2, 2, SELECTION_SIZE, SELECTION_SIZE));
		g.dispose();
		Cursor c =
			getToolkit().createCustomCursor(im,
				new Point((int) (SELECTION_SIZE / 2), (int) SELECTION_SIZE / 2), "CIRCLE");
		return c;
	}

	private class DisplayMouseListener extends MouseAdapter {

		boolean dragging;

		@Override
		public void mouseDragged(final MouseEvent e) {
			if ( SwingUtilities.isLeftMouseButton(e) ) {
				dragging = true;
				canBeUpdated(false);
				Point p = e.getPoint();
				if ( mousePosition == null ) {
					mousePosition = new Point(getWidth() / 2, getHeight() / 2);
				}
				setOrigin(new Point(origin.x + p.x - mousePosition.x, origin.y + p.y -
					mousePosition.y));
				mousePosition = p;
				repaint();
			}
		}

		@Override
		public void mouseMoved(final MouseEvent e) {
			// we need the mouse position so that after zooming
			// that position of the image is maintained
			mousePosition = e.getPoint();
		}

		@Override
		public void mouseWheelMoved(final MouseWheelEvent e) {
			boolean zoomIn = e.getWheelRotation() < 0;
			mousePosition = e.getPoint();
			setZoom(zoomIn ? 1.0 + zoomIncrement : 1.0 - zoomIncrement, mousePosition);
			updateDisplay();
		}

		@Override
		public void mouseClicked(final MouseEvent evt) {
			if ( evt.getClickCount() == 2 ) {
				zoomFit();
			} else if ( evt.isControlDown() || evt.isMetaDown() || evt.isPopupTrigger() ) {
				selectAgents(evt.getX(), evt.getY());
			}
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			if ( dragging ) {
				updateDisplay();
				dragging = false;
			}
			canBeUpdated(true);
		}

	}

	public AWTDisplaySurface(final double env_width, final double env_height,
		final IDisplayOutput layerDisplayOutput) {

		outputChanged(env_width, env_height, layerDisplayOutput);
		setOpaque(true);
		setDoubleBuffered(false);
		setCursor(createCursor());
		agentsMenu = new PopupMenu();
		add(agentsMenu);
		DisplayMouseListener d = new DisplayMouseListener();
		addMouseListener(d);
		addMouseMotionListener(d);
		addMouseWheelListener(d);
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				if ( buffImage == null ) {
					zoomFit();
				} else {
					if ( isFullImageInPanel() ) {
						centerImage();
					} else if ( isImageEdgeInPanel() ) {
						scaleOrigin();
					} else {
						displayGraphics.setClipping(getImageClipBounds());
					}
				}
				updateDisplay();
				previousPanelSize = getSize();
			}
		});
		animationThread.start();

	}

	// Used when the image is resized.
	boolean isImageEdgeInPanel() {
		if ( previousPanelSize == null ) { return false; }

		return origin.x > 0 && origin.x < previousPanelSize.width || origin.y > 0 &&
			origin.y < previousPanelSize.height;
	}

	// Tests whether the image is displayed in its entirety in the panel.
	boolean isFullImageInPanel() {
		return origin.x >= 0 && origin.x + bWidth < getWidth() && origin.y >= 0 &&
			origin.y + bHeight < getHeight();
	}

	@Override
	public void outputChanged(final double env_width, final double env_height,
		final IDisplayOutput output) {
		bgColor = output.getBackgroundColor();
		this.setBackground(bgColor);
		widthHeightConstraint = env_height / env_width;
		// if ( manager != null ) {
		// manager.dispose();
		// }
		if ( manager == null ) {
			manager = new DisplayManager(this);
			final List<? extends ISymbol> layers = output.getChildren();
			for ( final ISymbol layer : layers ) {
				IDisplay d =
					manager.addDisplay(DisplayManager.createDisplay((IDisplayLayer) layer,
						env_width, env_height, displayGraphics));
				d.initMenuItems(this);
			}

		} else {
			manager.updateEnvDimensions(env_width, env_height);
		}
		paintingNeeded.release();
	}

	@Override
	public void setBackgroundColor(final Color c) {
		bgColor = c;
	}

	@Override
	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		// we take the smallest dimension as a guide
		int[] dim = new int[2];
		dim[0] = vwidth > vheight ? (int) (vheight / widthHeightConstraint) : vwidth;
		dim[1] = vwidth <= vheight ? (int) (vwidth * widthHeightConstraint) : vheight;
		return dim;
	}

	private void selectAgents(final int x, final int y) {
		agentsMenu.removeAll();
		agentsMenu.setLabel("Layers");
		int xc = x - origin.x;
		int yc = y - origin.y;
		final List<IDisplay> displays = manager.getDisplays(xc, yc);
		for ( IDisplay display : displays ) {
			java.awt.Menu m = new java.awt.Menu(display.getName());
			display.putMenuItemsIn(m, xc, yc);
			agentsMenu.add(m);
		}
		agentsMenu.show(this, x, y);
	}

	@Override
	public void updateDisplay() {
		EventQueue.invokeLater(displayBlock);
		if ( ex[0] != null ) {
			GAMA.reportError(ex[0]);
			ex[0] = null;
		}
	}

	private final GamaRuntimeException[] ex = new GamaRuntimeException[] { null };
	private final Runnable displayBlock = new Runnable() {

		@Override
		public void run() {
			if ( !canBeUpdated() ) { return; }
			canBeUpdated(false);
			drawDisplaysWithoutRepainting();
			paintingNeeded.release();
			canBeUpdated(true);
			Toolkit.getDefaultToolkit().sync();
		}

	};

	public void drawDisplaysWithoutRepainting() {
		if ( displayGraphics == null ) { return; }
		ex[0] = null;
		displayGraphics.fill(bgColor, 1);
		// try {
		manager.drawDisplaysOn(displayGraphics);
		// } catch (GamaRuntimeException e) {
		// ex[0] = e;
		// } catch (Exception e) {
		// e.printStackTrace();
		// ex[0] = new GamaRuntimeException(e);
		// ex[0].addContext("in drawing the layers");
		// }
	}

	protected final Rectangle getImageClipBounds() {
		int panelX1 = -origin.x;
		int panelY1 = -origin.y;
		int panelX2 = getWidth() - 1 + panelX1;
		int panelY2 = getHeight() - 1 + panelY1;
		if ( panelX1 >= bWidth || panelX2 < 0 || panelY1 >= bHeight || panelY2 < 0 ) { return null; }
		int x1 = panelX1 < 0 ? 0 : panelX1;
		int y1 = panelY1 < 0 ? 0 : panelY1;
		int x2 = panelX2 >= bWidth ? bWidth - 1 : panelX2;
		int y2 = panelY2 >= bHeight ? bHeight - 1 : panelY2;
		return new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		((Graphics2D) g).drawRenderedImage(buffImage, translation);
	}

	@Override
	public void dispose() {
		// GUI.debug("Releasing all the components of AWTDisplaySurface");
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				removeAll();
			}
		});

		if ( manager != null ) {
			manager.dispose();
			// manager = null;
		}

	}

	@Override
	public BufferedImage getImage() {
		Rectangle clip = displayGraphics.getClipping();
		// updateDisplay();
		paused = false;
		drawDisplaysWithoutRepainting();
		paused = true;
		displayGraphics.setClipping(clip);
		return buffImage;
	}

	@Override
	public boolean resizeImage(final int x, final int y) {
		canBeUpdated(false);
		int[] point = computeBoundsFrom(x, y);
		int imageWidth = point[0];
		int imageHeight = point[1];
		if ( imageWidth <= MAX_SIZE && imageHeight <= MAX_SIZE ) {
			BufferedImage newImage = ImageUtils.createCompatibleImage(imageWidth, imageHeight);
			bWidth = newImage.getWidth();
			bHeight = newImage.getHeight();
			if ( buffImage != null ) {
				newImage.getGraphics().drawImage(buffImage, 0, 0, bWidth, bHeight, null);
				buffImage.flush();
			}
			buffImage = newImage;
			if ( displayGraphics == null ) {
				displayGraphics = new AWTDisplayGraphics(buffImage);
			} else {
				displayGraphics.setDisplayDimensions(bWidth, bHeight);
				displayGraphics.setGraphics((Graphics2D) newImage.getGraphics());
			}
			displayGraphics.setClipping(getImageClipBounds());
			if ( navigationImageEnabled ) {
				navigator.repaint();
			}
			canBeUpdated(true);
			return true;
		}
		canBeUpdated(true);
		return false;

	}

	@Override
	public void fireSelectionChanged(final Object entity) {
		GAMA.getExperiment().getOutputManager().selectionChanged(entity);
	}

	@Override
	public void zoomIn() {
		mousePosition = new Point(origin.x + bWidth / 2, origin.y + bHeight / 2);
		setZoom(1.0 + zoomIncrement, mousePosition);

	}

	@Override
	public void zoomOut() {
		mousePosition = new Point(origin.x + bWidth / 2, origin.y + bHeight / 2);;
		setZoom(1.0 - zoomIncrement, mousePosition);

	}

	public void setZoom(final double factor, final Point c) {
		if ( resizeImage((int) Math.round(bWidth * factor), (int) Math.round(bHeight * factor)) ) {
			int imagePX =
				c.x < origin.x ? 0 : c.x >= bWidth + origin.x ? bWidth - 1 : c.x - origin.x;
			int imagePY =
				c.y < origin.y ? 0 : c.y >= bHeight + origin.y ? bHeight - 1 : c.y - origin.y;
			zoomFactor = factor;
			setOrigin(new Point(c.x - (int) Math.round(imagePX * zoomFactor), c.y -
				(int) Math.round(imagePY * zoomFactor)));
			// paintingNeeded.release();
			updateDisplay();
		}
	}

	void scaleOrigin() {
		setOrigin(new Point(origin.x * getWidth() / previousPanelSize.width, origin.y *
			getHeight() / previousPanelSize.height));
		paintingNeeded.release();
		// repaint();
	}

	void centerImage() {
		setOrigin(new Point((getWidth() - bWidth) / 2, (getHeight() - bHeight) / 2));
	}

	@Override
	public void zoomFit() {
		mousePosition = new Point(getWidth() / 2, getHeight() / 2);
		if ( resizeImage(getWidth(), getHeight()) ) {
			centerImage();
			// paintingNeeded.release();
			updateDisplay();
		}
	}

	@Override
	public void focusOn(final IShape geometry, final IDisplay display) {
		Envelope env = geometry.getEnvelope();
		double minX = env.getMinX();
		double minY = env.getMinY();
		double maxX = env.getMaxX();
		double maxY = env.getMaxY();

		int leftX = display.getPosition().x + (int) (display.getXScale() * minX + 0.5);
		int leftY = display.getPosition().y + (int) (display.getYScale() * minY + 0.5);
		int rightX = display.getPosition().x + (int) (display.getXScale() * maxX + 0.5);
		int rightY = display.getPosition().y + (int) (display.getYScale() * maxY + 0.5);
		Rectangle envelop =
			new Rectangle(leftX + origin.x, leftY + origin.y, rightX - leftX, rightY - leftY);
		// / PFfff... Quel bordel !
		double xScale = (double) getWidth() / (rightX - leftX);
		double yScale = (double) getHeight() / (rightY - leftY);
		double zoomFactor = Math.min(xScale, yScale);
		if ( bWidth * zoomFactor > MAX_SIZE ) {
			zoomFactor = (double) MAX_SIZE / bWidth;
		}
		setZoom(zoomFactor, new Point((int) envelop.getCenterX(), (int) envelop.getCenterY()));
	}

	@Override
	public void canBeUpdated(final boolean canBeUpdated) {
		this.canBeUpdated = canBeUpdated;
	}

	@Override
	public boolean canBeUpdated() {
		return canBeUpdated && displayGraphics != null && displayGraphics.isReady();
	}

	public void setNavigationImageEnabled(final boolean enabled) {
		navigationImageEnabled = enabled;
		getNavigator().toggle(enabled);
	}

	protected void setOrigin(final Point origin) {
		this.origin = origin;
		translation.setToTranslation(origin.x, origin.y);
		displayGraphics.setClipping(getImageClipBounds());
		if ( navigationImageEnabled ) {
			navigator.repaint();
		}
	}

	/**
	 * 
	 */

}
