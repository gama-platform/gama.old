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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.displays.awt;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.Semaphore;
import javax.imageio.ImageIO;
import javax.swing.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.gui.displays.layers.LayerManager;
import msi.gama.gui.views.SWTNavigationPanel;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.operators.Files;
import com.vividsolutions.jts.geom.Envelope;

@display("java2D")
public final class AWTDisplaySurface extends JPanel implements IDisplaySurface {

	private boolean autosave = false;
	private String snapshotFileName;
	private Point snapshotDimension;
	public static String snapshotFolder = "snapshots";
	protected ILayerManager manager;
	boolean paused;
	private volatile boolean canBeUpdated = true;
	double widthHeightConstraint = 1.0;

	private IGraphics displayGraphics;
	private Color bgColor = Color.black;
	protected double zoomIncrement = 0.1;
	protected double zoomFactor = 1.0 + zoomIncrement;
	protected BufferedImage buffImage;
	protected int bWidth, bHeight;
	protected Point origin = new Point(0, 0);

	protected Point mousePosition;
	Dimension previousPanelSize;
	protected boolean navigationImageEnabled = true;
	protected SWTNavigationPanel navigator;
	private final AffineTransform translation = new AffineTransform();
	private final Semaphore paintingNeeded = new Semaphore(1, true);
	private boolean synchronous = false;
	private final Thread animationThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (true) {
				try {
					paintingNeeded.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				repaint();
			}
		}
	});
	private AWTDisplaySurfaceMenu menuManager;

	/**
	 * Save this surface into an image passed as a parameter
	 * @param scope
	 * @param image
	 */
	public void save(final IScope scope, final RenderedImage image) {
		try {
			Files.newFolder(scope, snapshotFolder);
		} catch (GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + snapshotFolder);
			GAMA.reportError(e1);
			e1.printStackTrace();
			return;
		}
		String snapshotFile =
			scope.getSimulationScope().getModel()
				.getRelativeFilePath(snapshotFolder + "/" + snapshotFileName, false);

		String file = snapshotFile + SimulationClock.getCycle() + ".png";
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new FileOutputStream(file));
			ImageIO.write(image, "png", os);
		} catch (java.io.IOException ex) {
			GamaRuntimeException e = new GamaRuntimeException(ex);
			e.addContext("Unable to create output stream for snapshot image");
			GAMA.reportError(e);
		} finally {
			try {
				if ( os != null ) {
					os.close();
				}
			} catch (Exception ex) {
				GamaRuntimeException e = new GamaRuntimeException(ex);
				e.addContext("Unable to close output stream for snapshot image");
				GAMA.reportError(e);
			}
		}
	}

	@Override
	public void setPaused(final boolean flag) {
		paused = flag;
		updateDisplay();
	}

	@Override
	public ILayerManager getManager() {
		return manager;
	}

	@Override
	public boolean isPaused() {
		return paused;
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
				setOrigin(origin.x + p.x - mousePosition.x, origin.y + p.y - mousePosition.y);
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

	@Override
	public void initialize(final double env_width, final double env_height,
		final IDisplayOutput layerDisplayOutput) {
		outputChanged(env_width, env_height, layerDisplayOutput);
		setOpaque(true);
		setDoubleBuffered(false);
		setCursor(createCursor());
		menuManager = new AWTDisplaySurfaceMenu(this);
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
		if ( manager == null ) {
			manager = new LayerManager(this);
			final List<? extends ISymbol> layers = output.getChildren();
			for ( final ISymbol layer : layers ) {
				manager.addLayer(LayerManager.createLayer((ILayerStatement) layer, env_width,
					env_height, displayGraphics));
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

	/*
	 * @Override
	 * public int[] computeBoundsFrom(final int vwidth, final int vheight) {
	 * // we take the smallest dimension as a guide
	 * int[] dim = new int[2];
	 * dim[0] = vwidth > vheight ? (int) (vheight / widthHeightConstraint) : vwidth;
	 * dim[1] = vwidth <= vheight ? (int) (vwidth * widthHeightConstraint) : vheight;
	 * return dim;
	 * }
	 */

	@Override
	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		int[] dim = new int[2];
		if ( widthHeightConstraint < 1 ) {
			dim[1] = Math.min(vheight, (int) (vwidth * widthHeightConstraint));
			dim[0] = Math.min(vwidth, (int) (dim[1] / widthHeightConstraint));
		} else {
			dim[0] = Math.min(vwidth, (int) (vheight / widthHeightConstraint));
			dim[1] = Math.min(vheight, (int) (dim[0] * widthHeightConstraint));
		}
		return dim;
	}

	public void selectAgents(final int x, final int y) {
		int xc = x - origin.x;
		int yc = y - origin.y;
		final List<ILayer> displays = manager.getLayersIntersecting(xc, yc);
		menuManager.selectAgents(xc, yc, displays);
	}

	@Override
	public void updateDisplay() {
		if ( synchronous && !EventQueue.isDispatchThread() &&
			!GAMA.getFrontmostSimulation().isPaused() ) {
			try {
				EventQueue.invokeAndWait(displayBlock);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			EventQueue.invokeLater(displayBlock);
		}
		if ( ex[0] != null ) {
			GAMA.reportError(ex[0]);
			ex[0] = null;
		}
	}

	@Override
	public void forceUpdateDisplay() {
		boolean old = synchronous;
		setSynchronized(false);
		updateDisplay();
		setSynchronized(old);
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
		manager.drawLayersOn(displayGraphics);
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
		if ( autosave ) {
			snapshot();
		}
		redrawNavigator();
	}

	void redrawNavigator() {
		if ( !navigationImageEnabled ) { return; }
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				if ( navigator == null || navigator.isDisposed() ) { return; }
				navigator.redraw();
				// navigator.update();
			}
		});
	}

	@Override
	public void dispose() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				removeAll();
			}
		});

		if ( manager != null ) {
			manager.dispose();
		}

	}

	@Override
	public BufferedImage getImage() {
		return buffImage;
	}

	@Override
	public boolean resizeImage(final int x, final int y) {
		canBeUpdated(false);
		int[] point = computeBoundsFrom(x, y);
		int imageWidth = Math.max(1, point[0]);
		int imageHeight = Math.max(1, point[1]);
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
			redrawNavigator();
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
		if ( resizeImage(Math.max(1, (int) Math.round(bWidth * factor)),
			Math.max(1, (int) Math.round(bHeight * factor))) ) {
			int imagePX =
				c.x < origin.x ? 0 : c.x >= bWidth + origin.x ? bWidth - 1 : c.x - origin.x;
			int imagePY =
				c.y < origin.y ? 0 : c.y >= bHeight + origin.y ? bHeight - 1 : c.y - origin.y;
			zoomFactor = factor;
			setOrigin(c.x - (int) Math.round(imagePX * zoomFactor),
				c.y - (int) Math.round(imagePY * zoomFactor));
			updateDisplay();
		}
	}

	void scaleOrigin() {
		setOrigin(origin.x * getWidth() / previousPanelSize.width, origin.y * getHeight() /
			previousPanelSize.height);
		paintingNeeded.release();
	}

	void centerImage() {
		setOrigin((getWidth() - bWidth) / 2, (getHeight() - bHeight) / 2);
	}

	@Override
	public void zoomFit() {
		mousePosition = new Point(getWidth() / 2, getHeight() / 2);
		if ( resizeImage(getWidth(), getHeight()) ) {
			centerImage();
			updateDisplay();
		}
	}

	@Override
	public void focusOn(final IShape geometry, final ILayer display) {
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
	}

	@Override
	public void setOrigin(final int x, final int y) {
		this.origin = new Point(x, y);
		translation.setToTranslation(origin.x, origin.y);
		displayGraphics.setClipping(getImageClipBounds());
		redrawNavigator();
	}

	/**
	 * @param checked
	 */
	@Override
	public void setSynchronized(final boolean checked) {
		synchronous = checked;
	}

	@Override
	public void setQualityRendering(final boolean quality) {
		if ( displayGraphics == null ) { return; }
		displayGraphics.setQualityRendering(quality);
		if ( isPaused() ) {
			updateDisplay();
		}
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#setAutoSave(boolean)
	 */
	@Override
	public void setAutoSave(final boolean autosave, final int x, final int y) {
		this.autosave = autosave;
		snapshotDimension = new Point(x, y);
	}

	@Override
	public void setSnapshotFileName(final String file) {
		snapshotFileName = file;
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#snapshot()
	 */
	@Override
	public void snapshot() {
		if ( snapshotDimension.x == -1 && snapshotDimension.y == -1 ) {
			save(GAMA.getDefaultScope(), buffImage);
			return;
		}
		BufferedImage newImage =
			ImageUtils.createCompatibleImage(snapshotDimension.x, snapshotDimension.y);
		IGraphics tempGraphics = new AWTDisplayGraphics(newImage);
		tempGraphics.fill(bgColor, 1);
		manager.drawLayersOn(tempGraphics);
		save(GAMA.getDefaultScope(), newImage);
		newImage.flush();
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#setNavigator(java.lang.Object)
	 */
	@Override
	public void setNavigator(final Object nav) {
		if ( nav instanceof SWTNavigationPanel ) {
			navigator = (SWTNavigationPanel) nav;
		}
	}

	@Override
	public int getImageWidth() {
		return bWidth;
	}

	@Override
	public int getImageHeight() {
		return bHeight;
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#getOriginX()
	 */
	@Override
	public int getOriginX() {
		return origin.x;
	}

	/**
	 * @see msi.gama.common.interfaces.IDisplaySurface#getOriginY()
	 */
	@Override
	public int getOriginY() {
		return origin.y;
	}

	@Override
	public int[] getHighlightColor() {
		if ( displayGraphics == null ) { return null; }
		return displayGraphics.getHighlightColor();
	}

	@Override
	public void setHighlightColor(final int[] rgb) {
		if ( displayGraphics == null ) { return; }
		displayGraphics.setHighlightColor(rgb);
	}

	/**
	 * This method does nothing for JAVA2D display
	 */
	@Override
	public void toggleView() {
		System.out.println("toggle view is only available for Opengl Display");
	}

	/**
	 * This method does nothing for JAVA2D display
	 */
	@Override
	public void togglePicking() {
		System.out.println("toggle picking is only available for Opengl Display");
	}

	/**
	 * This method does nothing for JAVA2D display
	 */
	@Override
	public void toggleArcball() {
		System.out.println("arcball is only available for Opengl Display");
	}

	/**
	 * This method does nothing for JAVA2D display
	 */
	@Override
	public void toggleSelectRectangle() {
		System.out.println("select rectangle tool is only available for Opengl Display");
	}

	/**
	 * This method does nothing for JAVA2D display
	 */
	@Override
	public void addShapeFile() {
		// TODO Auto-generated method stub
	}

	public Point getOrigin() {
		return origin;
	}

	@Override
	public ILayerManager getLayerManager() {
		return this.manager;
	}

	@Override
	public void addMouseEventListener(final MouseListener e) {
		// TODO Auto-generated method stub
		this.addMouseListener(e);
	}

	@Override
	public IGraphics getMyGraphics() {
		return this.displayGraphics;
	}
}
