/*********************************************************************************************
 *
 *
 * 'AbstractAWTDisplaySurface.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.displays.awt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import com.vividsolutions.jts.geom.Envelope;
import msi.gama.common.GamaPreferences;
import msi.gama.common.GamaPreferences.IPreferenceChangeListener;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.interfaces.ILayerManager;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.LayeredDisplayData.Changes;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.display.AWTDisplayGraphics;
import msi.gama.outputs.display.LayerManager;
import msi.gama.outputs.layers.IEventLayerListener;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.operators.fastmaths.FastMath;

@display("java2D")
public class Java2DDisplaySurface extends JPanel implements IDisplaySurface {

	static {
		GamaPreferences.DISPLAY_NO_ACCELERATION.addChangeListener(new IPreferenceChangeListener<Boolean>() {

			@Override
			public boolean beforeValueChange(final Boolean newValue) {
				return true;
			}

			// Corresponds to JVM options : -Dsun.java2d.noddraw=true -Dsun.awt.noerasebackground=true -Dsun.java2d.d3d=false -Dsun.java2d.opengl=false -Dsun.java2d.pmoffscreen=false
			@Override
			public void afterValueChange(final Boolean newValue) {
				System.setProperty("sun.java2d.noddraw", newValue ? "true" : "false");
				System.setProperty("sun.awt.noerasebackground", "true"); // Always true
				System.setProperty("sun.java2d.d3d", newValue ? "false" : "true");
				System.setProperty("sun.java2d.opengl", newValue ? "false" : "true");
				System.setProperty("sun.java2d.pmoffscreen", newValue ? "false" : "true");
			}
		});
		// Forces the listener to run at least once
		GamaPreferences.DISPLAY_NO_ACCELERATION.set(GamaPreferences.DISPLAY_NO_ACCELERATION.getValue());
	}

	final LayeredDisplayOutput output;
	protected final Rectangle viewPort = new Rectangle();
	protected final AffineTransform translation = new AffineTransform();
	protected final ILayerManager manager;
	protected IGraphics iGraphics;

	protected DisplaySurfaceMenu menuManager;
	protected IExpression temp_focus;

	protected Dimension previousPanelSize;
	protected double zoomIncrement = 0.1;
	protected boolean zoomFit = true;
	protected boolean disposed;

	private IScope scope;
	final DisplayMouseListener listener;
	int frames;
	private volatile boolean realized = false;
	private volatile boolean rendered = false;
	Map<IEventLayerListener, MouseAdapter> listeners = new HashMap();

	// private boolean alreadyZooming = false;

	public Java2DDisplaySurface(final Object ... args) {
		output = (LayeredDisplayOutput) args[0];
		output.setSurface(this);
		setDisplayScope(output.getScope().copy("in Java2DDisplaySurface"));
		// data = output.getData();
		output.getData().addListener(this);
		temp_focus = output.getFacet(IKeyword.FOCUS);
		// setOpaque(true);
		setDoubleBuffered(true);
		setIgnoreRepaint(true);

		//

		setLayout(new BorderLayout());
		setBackground(output.getData().getBackgroundColor());
		setName(output.getName());
		manager = new LayerManager(this, output);
		listener = new DisplayMouseListener(this);
		addMouseListener(listener);
		addMouseMotionListener(listener);
		addMouseWheelListener(listener);
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				if ( zoomFit ) {
					zoomFit();
				} else {
					if ( isFullImageInPanel() ) {
						centerImage();
					} else if ( isImageEdgeInPanel() ) {
						scaleOrigin();
					}
					updateDisplay(true);
				}
				final double newZoom = FastMath.min(getWidth() / getDisplayWidth(), getHeight() / getDisplayHeight());
				newZoomLevel(1 / newZoom);
				previousPanelSize = getSize();
			}
		});

	}

	@Override
	public int getFPS() {
		final int result = frames;
		frames = 0;
		return result;
	}

	@Override
	public void outputReloaded() {
		// We first copy the scope
		setDisplayScope(output.getScope().copy("in Java2DDisplaySurface"));
		// We disable error reporting
		getDisplayScope().disableErrorReporting();

		manager.outputChanged();

		resizeImage(getWidth(), getHeight(), true);
		if ( zoomFit ) {
			zoomFit();
		}
		updateDisplay(true);
	}

	@Override
	public IScope getDisplayScope() {
		return scope;
	}

	// FIXME Ugly code. The hack must be better written
	@Override
	public void setSWTMenuManager(final Object manager) {
		menuManager = (DisplaySurfaceMenu) manager;
	}

	@Override
	public ILayerManager getManager() {
		return manager;
	}

	Point getOrigin() {
		return viewPort.getLocation();
	}

	@Override
	public void setFont(final Font f) {
		// super.setFont(null);
	}

	@Override
	public BufferedImage getImage(final int w, final int h) {
		final int previousWidth = getWidth();
		final int previousHeight = getHeight();
		final int width = w == -1 ? previousWidth : w;
		final int height = h == -1 ? previousHeight : h;
		final BufferedImage newImage = ImageUtils.createCompatibleImage(width, height);
		final Graphics g = newImage.getGraphics();

		while (!rendered) {
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			EventQueue.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					resizeImage(width, height, false);
					paintComponent(g);
					resizeImage(previousWidth, previousHeight, false);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
		return newImage;
	}

	protected void scaleOrigin() {
		final Point origin = getOrigin();
		setOrigin(origin.x * getWidth() / previousPanelSize.width, origin.y * getHeight() / previousPanelSize.height);
		updateDisplay(true);
	}

	protected void centerImage() {
		setOrigin((int) FastMath.round((getWidth() - getDisplayWidth()) / 2),
			(int) FastMath.round((getHeight() - getDisplayHeight()) / 2));
	}

	protected int getOriginX() {
		return getOrigin().x;
	}

	protected int getOriginY() {
		return getOrigin().y;
	}

	void setOrigin(final int x, final int y) {
		viewPort.x = x;
		viewPort.y = y;
		translation.setToTranslation(x, y);
	}

	@Override
	public void updateDisplay(final boolean force) {
		if ( disposed ) { return; }
		rendered = false;
		if ( temp_focus != null ) {
			final IShape geometry = Cast.asGeometry(getDisplayScope(), temp_focus.value(getDisplayScope()), false);
			temp_focus = null;
			focusOn(geometry);
		}
		repaint();
	}

	@Override
	public void focusOn(final IShape geometry) {
		final Rectangle2D r = this.getManager().focusOn(geometry, this);
		if ( r == null ) { return; }
		final double xScale = getWidth() / r.getWidth();
		final double yScale = getHeight() / r.getHeight();
		double zoomFactor = FastMath.min(xScale, yScale);
		final Point center = new Point((int) FastMath.round(r.getCenterX()), (int) FastMath.round(r.getCenterY()));

		zoomFactor = applyZoom(zoomFactor);
		center.setLocation(center.x * zoomFactor, center.y * zoomFactor);
		centerOnDisplayCoordinates(center);

		updateDisplay(true);
	}

	@Override
	public void zoomIn() {
		// if ( alreadyZooming ) { return; }
		// alreadyZooming = true;
		final Point origin = getOrigin();
		listener.setMousePosition(new Point(getWidth() / 2, getHeight() / 2));
		final double zoomFactor = applyZoom(1.0 + zoomIncrement);
		final double newx = FastMath.round(zoomFactor * (getWidth() / 2 - origin.x));
		final double newy = FastMath.round(zoomFactor * (getHeight() / 2 - origin.y));
		centerOnDisplayCoordinates(new Point((int) newx, (int) newy));
		updateDisplay(true);
		// alreadyZooming = false;
	}

	@Override
	public void zoomOut() {
		// if ( alreadyZooming ) { return; }
		// alreadyZooming = true;
		final Point origin = getOrigin();
		listener.setMousePosition(new Point(getWidth() / 2, getHeight() / 2));
		final double zoomFactor = applyZoom(1.0 - zoomIncrement);
		final double newx = FastMath.round(zoomFactor * (getWidth() / 2 - origin.x));
		final double newy = FastMath.round(zoomFactor * (getHeight() / 2 - origin.y));
		centerOnDisplayCoordinates(new Point((int) newx, (int) newy));
		updateDisplay(true);
		// alreadyZooming = false;
	}

	// Used when the image is resized.
	public boolean isImageEdgeInPanel() {
		if ( previousPanelSize == null ) { return false; }
		final Point origin = getOrigin();
		return origin.x > 0 && origin.x < previousPanelSize.width ||
			origin.y > 0 && origin.y < previousPanelSize.height;
	}

	// Tests whether the image is displayed in its entirety in the panel.
	public boolean isFullImageInPanel() {
		final Point origin = getOrigin();
		return origin.x >= 0 && origin.x + getDisplayWidth() < getWidth() && origin.y >= 0 &&
			origin.y + getDisplayHeight() < getHeight();
	}

	@Override
	public boolean resizeImage(final int x, final int y, final boolean force) {
		if ( !force && x == viewPort.width && y == viewPort.height ) { return true; }
		if ( x < 10 || y < 10 ) { return false; }
		if ( getWidth() <= 0 && getHeight() <= 0 ) { return false; }
		// java.lang.System.out.println("Resize display : " + x + " " + y);
		final int[] point = computeBoundsFrom(x, y);
		final int imageWidth = CmnFastMath.max(1, point[0]);
		final int imageHeight = CmnFastMath.max(1, point[1]);
		setDisplayHeight(imageHeight);
		setDisplayWidth(imageWidth);
		iGraphics = new AWTDisplayGraphics(this, (Graphics2D) this.getGraphics());
		return true;

	}

	@Override
	public void paintComponent(final Graphics g) {
		realized = true;
		if ( iGraphics == null ) { return; }
		super.paintComponent(g);
		final Graphics2D g2d =
			(Graphics2D) g.create(getOrigin().x, getOrigin().y, (int) getDisplayWidth(), (int) getDisplayHeight());
		getIGraphics().setGraphics2D(g2d);
		getIGraphics().setUntranslatedGraphics2D((Graphics2D) g);
		manager.drawLayersOn(iGraphics);
		g2d.dispose();
		frames++;
		rendered = true;
	}

	AWTDisplayGraphics getIGraphics() {
		return (AWTDisplayGraphics) iGraphics;
	}

	@Override
	public ILocation getModelCoordinates() {
		final Point origin = getOrigin();
		final Point mouse = listener.getMousePosition();
		if ( mouse == null ) { return null; }
		final int xc = mouse.x - origin.x;
		final int yc = mouse.y - origin.y;
		final List<ILayer> layers = manager.getLayersIntersecting(xc, yc);
		for ( final ILayer layer : layers ) {
			if ( layer.isProvidingWorldCoordinates() ) { return layer.getModelCoordinatesFrom(xc, yc, this); }
		}
		return null;
	}

	@Override
	public String getModelCoordinatesInfo() {
		final Point origin = getOrigin();
		final Point mouse = listener.getMousePosition();
		if ( mouse == null ) { return null; }
		final int xc = mouse.x - origin.x;
		final int yc = mouse.y - origin.y;
		final List<ILayer> layers = manager.getLayersIntersecting(xc, yc);
		for ( final ILayer layer : layers ) {
			if ( layer.isProvidingCoordinates() ) { return layer.getModelCoordinatesInfo(xc, yc, this); }
		}
		return "No world coordinates";
	}

	@Override
	public double getEnvWidth() {
		return output.getData().getEnvWidth();
	}

	@Override
	public double getEnvHeight() {
		return output.getData().getEnvHeight();
	}

	@Override
	public double getDisplayWidth() {
		return viewPort.width;
	}

	protected void setDisplayWidth(final int displayWidth) {
		viewPort.width = displayWidth;
	}

	@Override
	public LayeredDisplayData getData() {
		return output.getData();
	}

	@Override
	public double getDisplayHeight() {
		return viewPort.height;
	}

	protected void setDisplayHeight(final int displayHeight) {
		viewPort.height = displayHeight;
	}

	@Override
	public LayeredDisplayOutput getOutput() {
		return output;
	}

	public void newZoomLevel(final double newZoomLevel) {
		getData().setZoomLevel(newZoomLevel);
	}

	@Override
	public double getZoomLevel() {
		if ( getData().getZoomLevel() == null ) {
			getData().setZoomLevel(1.0);
		}
		return getData().getZoomLevel();
	}

	@Override
	public void zoomFit() {
		listener.setMousePosition(new Point(getWidth() / 2, getHeight() / 2));
		if ( resizeImage(getWidth(), getHeight(), false) ) {
			newZoomLevel(1d);
			zoomFit = true;
			centerImage();
			updateDisplay(true);
		}
	}

	private int[] computeBoundsFrom(final int vwidth, final int vheight) {
		if ( !manager.stayProportional() ) { return new int[] { vwidth, vheight }; }
		final int[] dim = new int[2];
		final double widthHeightConstraint = getEnvHeight() / getEnvWidth();
		if ( widthHeightConstraint < 1 ) {
			dim[1] = CmnFastMath.min(vheight, (int) FastMath.round(vwidth * widthHeightConstraint));
			dim[0] = CmnFastMath.min(vwidth, (int) FastMath.round(dim[1] / widthHeightConstraint));
		} else {
			dim[0] = CmnFastMath.min(vwidth, (int) FastMath.round(vheight / widthHeightConstraint));
			dim[1] = CmnFastMath.min(vheight, (int) FastMath.round(dim[0] * widthHeightConstraint));
		}
		return dim;
	}

	@Override
	public GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final Point sizeInPixels,
		final Point positionInPixels) {
		final double xScale = sizeInPixels.x / getEnvWidth();
		final double yScale = sizeInPixels.y / getEnvHeight();
		final int xInDisplay = xOnScreen - positionInPixels.x;
		final int yInDisplay = yOnScreen - positionInPixels.y;
		final double xInModel = xInDisplay / xScale;
		final double yInModel = yInDisplay / yScale;
		return new GamaPoint(xInModel, yInModel);
	}

	@Override
	public Envelope getVisibleRegionForLayer(final ILayer currentLayer) {
		if ( currentLayer instanceof OverlayLayer ) { return getDisplayScope().getSimulationScope().getEnvelope(); }
		final Envelope e = new Envelope();
		final Point origin = getOrigin();
		int xc = -origin.x;
		int yc = -origin.y;
		e.expandToInclude((GamaPoint) currentLayer.getModelCoordinatesFrom(xc, yc, this));
		xc = xc + getIGraphics().getViewWidth();
		yc = yc + getIGraphics().getViewHeight();
		e.expandToInclude((GamaPoint) currentLayer.getModelCoordinatesFrom(xc, yc, this));
		return e;
	}

	@Override
	public Collection<IAgent> selectAgent(final int x, final int y) {
		final int xc = x - getOriginX();
		final int yc = y - getOriginY();
		final List<IAgent> result = new ArrayList();
		final List<ILayer> layers = getManager().getLayersIntersecting(xc, yc);
		for ( final ILayer layer : layers ) {
			final Set<IAgent> agents = layer.collectAgentsAt(xc, yc, this);
			if ( !agents.isEmpty() ) {
				result.addAll(agents);
			}
		}
		return result;
	}

	protected void setDisplayScope(final IScope scope) {
		if ( this.scope != null ) {
			GAMA.releaseScope(this.scope);
		}
		this.scope = scope;
	}

	@Override
	public void runAndUpdate(final Runnable r) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				r.run();
				if ( output.isPaused() || GAMA.isPaused() ) {
					updateDisplay(true);
				}
			}
		}).start();
	}

	@Override
	public void dispose() {
		getData().removeListener(this);
		if ( disposed ) { return; }
		disposed = true;
		if ( manager != null ) {
			manager.dispose();
		}

		GAMA.releaseScope(getDisplayScope());
		setDisplayScope(null);
	}

	@Override
	public void addListener(final IEventLayerListener ell) {
		if ( listeners.containsKey(ell) ) { return; }

		final MouseAdapter l = new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				ell.mouseClicked(e.getX(), e.getY(), e.getButton());
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				ell.mouseDown(e.getX(), e.getY(), e.getButton());
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				ell.mouseUp(e.getX(), e.getY(), e.getButton());
			}

			@Override
			public void mouseMoved(final MouseEvent e) {
				if ( e.getButton() > 0 ) { return; }
				ell.mouseMove(e.getX(), e.getY());
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
				if ( e.getButton() > 0 ) { return; }
				ell.mouseEnter(e.getX(), e.getY());
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				if ( e.getButton() > 0 ) { return; }
				ell.mouseExit(e.getX(), e.getY());
			}

		};
		listeners.put(ell, l);
		addMouseListener(l);
		addMouseMotionListener(l);
	}

	@Override
	public void removeListener(final IEventLayerListener ell) {
		final MouseAdapter l = listeners.get(ell);
		if ( l == null ) { return; }
		listeners.remove(ell);
		super.removeMouseListener(l);
		super.removeMouseMotionListener(l);
	}

	@Override
	public Collection<IEventLayerListener> getLayerListeners() {
		return listeners.keySet();
	}

	/**
	 * Method followAgent()
	 * @see msi.gama.common.interfaces.IDisplaySurface#followAgent(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void followAgent(final IAgent a) {}

	@Override
	public void setBounds(final int arg0, final int arg1, final int arg2, final int arg3) {
		// scope.getGui().debug("Set bounds called with " + arg2 + " " + arg3);
		if ( arg2 == 0 && arg3 == 0 ) { return; }
		super.setBounds(arg0, arg1, arg2, arg3);
	}

	@Override
	public void setBounds(final Rectangle r) {
		// scope.getGui().debug("Set bounds called with " + r);
		if ( r.width < 1 && r.height < 1 ) { return; }
		super.setBounds(r);
	}

	double applyZoom(final double factor) {
		double real_factor = FastMath.min(factor, 10 / getZoomLevel());
		real_factor = FastMath.max(MIN_ZOOM_FACTOR, real_factor);
		real_factor = FastMath.min(MAX_ZOOM_FACTOR, real_factor);
		final boolean success = resizeImage(CmnFastMath.max(1, (int) FastMath.round(getDisplayWidth() * real_factor)),
			Math.max(1, (int) FastMath.round(getDisplayHeight() * real_factor)), false);

		if ( success ) {
			zoomFit = false;
			final double widthHeightConstraint = getEnvHeight() / getEnvWidth();

			if ( widthHeightConstraint < 1 ) {
				newZoomLevel(getDisplayWidth() / getWidth());
			} else {
				newZoomLevel(getDisplayHeight() / getHeight());
			}
		}
		return real_factor;
	}

	private void centerOnViewCoordinates(final Point p) {
		final Point origin = getOrigin();
		final int translationX = p.x - FastMath.round(getWidth() / (float) 2);
		final int translationY = p.y - FastMath.round(getHeight() / (float) 2);
		setOrigin(origin.x - translationX, origin.y - translationY);

	}

	void centerOnDisplayCoordinates(final Point p) {
		final Point origin = getOrigin();
		centerOnViewCoordinates(new Point(p.x + origin.x, p.y + origin.y));
	}

	void selectAgents(final int mousex, final int mousey) {
		final Point origin = getOrigin();
		final int xc = mousex - origin.x;
		final int yc = mousey - origin.y;
		final List<ILayer> layers = manager.getLayersIntersecting(xc, yc);
		if ( layers.isEmpty() ) { return; }
		final ILocation modelCoordinates = layers.get(0).getModelCoordinatesFrom(xc, yc, this);
		scope.getGui().run(new Runnable() {

			@Override
			public void run() {
				menuManager.buildMenu(mousex, mousey, xc, yc, modelCoordinates, layers);
			}
		});
	}

	@Override
	public void layersChanged() {}

	/**
	 * Method changed()
	 * @see msi.gama.outputs.LayeredDisplayData.DisplayDataListener#changed(int, boolean)
	 */
	@Override
	public void changed(final Changes property, final boolean value) {

		switch (property) {
			case BACKGROUND:
				setBackground(getData().getBackgroundColor());
				break;
			default:;
		}

	};

	/**
	 * Method getZoomIncrement()
	 * @see msi.gama.gui.displays.awt.IJava2DDisplaySurface#getZoomIncrement()
	 */
	double getZoomIncrement() {
		return zoomIncrement;
	}

	@Override
	public boolean isRealized() {
		return realized;
	}

	@Override
	public boolean isRendered() {
		return rendered;
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	// Code to use a BufferStrategy instead. Problem is it needs a Canvas, which is difficult to obtain. One possibility could be to directly use SWT_AWT to obtain a Frame and build a Canvas on top of
	// it, bypassing all the problems raised by the Swing components.
	// public BufferStrategy getBufferStrategy() {
	// if ( bs == null ) {
	// canvas.createBufferStrategy(3);
	// bs = canvas.getBufferStrategy();
	// }
	// return bs;
	// }
	//
	// void draw() {
	// if ( iGraphics == null ) { return; }
	// if ( output.getData().isAutosave() ) {
	// snapshot();
	// }
	//
	// // Method which prepares the screen for drawing
	// bs = getBufferStrategy(); // Gets the buffer strategy our canvas is currently using
	//
	// Graphics g = bs.getDrawGraphics(); // Get the graphics from our buffer strategy (which is connected to our canvas)
	//
	// Graphics2D g2d =
	// (Graphics2D) g.create(getOrigin().x, getOrigin().y, (int) getDisplayWidth(), (int) getDisplayHeight());
	//
	// ((AWTDisplayGraphics) iGraphics).setGraphics2D(g2d);
	// manager.drawLayersOn(iGraphics);
	// g2d.dispose();
	//
	// g.dispose(); // Dispose of our graphics object because it is no longer needed, and unnecessarily taking up memory
	// bs.show(); // Show the buffer strategy, flip it if necessary (make back buffer the visible buffer and vice versa)
	// frames++;
	//
	// }

}