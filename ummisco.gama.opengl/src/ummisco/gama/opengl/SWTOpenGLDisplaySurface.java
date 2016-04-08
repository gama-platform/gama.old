/**
 * Created by drogoul, 25 mars 2015
 *
 */
package ummisco.gama.opengl;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;

import com.jogamp.opengl.FPSCounter;
import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.interfaces.ILayerManager;
import msi.gama.common.util.ImageUtils;
import msi.gama.gui.displays.awt.DisplaySurfaceMenu;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.gui.views.actions.DisplayedAgentsMenu;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.LayeredDisplayData.Changes;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.display.LayerManager;
import msi.gama.outputs.layers.IEventLayerListener;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

/**
 * Class OpenGLSWTDisplaySurface.
 *
 * @author drogoul
 * @since 25 mars 2015
 *
 */
@msi.gama.precompiler.GamlAnnotations.display("opengl")
public class SWTOpenGLDisplaySurface implements IDisplaySurface.OpenGL {

	final GLAnimatorControl animator;
	final JOGLRenderer renderer;
	protected double zoomIncrement = 0.1;
	protected boolean zoomFit = true;
	Map<IEventLayerListener, GamaEventListener> eventListeners = new HashMap();
	final LayeredDisplayOutput output;
	final LayerManager manager;
	protected DisplaySurfaceMenu menuManager;
	protected IExpression temp_focus;
	IScope scope;
	final Composite parent;
	volatile boolean disposed;
	private volatile boolean alreadyUpdating;

	// NEVER USED
	public SWTOpenGLDisplaySurface(final Object... objects) {
		parent = null;
		manager = null;
		output = null;
		animator = null;
		renderer = null;
	}

	/**
	 * @param parent
	 * @param style
	 */
	public SWTOpenGLDisplaySurface(final Composite parent, final LayeredDisplayOutput output) {
		this.output = output;
		this.parent = parent;
		parent.setLayout(new GridLayout(1, false));
		output.getData().addListener(this);
		output.setSurface(this);
		setDisplayScope(output.getScope().copy("in OpenGLDisplaySuface"));
		renderer = createRenderer();
		animator = createAnimator();
		animator.setUpdateFPSFrames(FPSCounter.DEFAULT_FRAMES_PER_INTERVAL, null);
		manager = new LayerManager(this, output);
		temp_focus = output.getFacet(IKeyword.FOCUS);

		animator.start();
	}

	/**
	 * Method getImage()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getImage()
	 */
	@Override
	public BufferedImage getImage(final int w, final int h) {
		while (renderer.getCurrentScene() == null || !renderer.getCurrentScene().rendered()) {
			try {
				Thread.sleep(20);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		final GLAutoDrawable glad = renderer.getDrawable();
		if (glad == null || glad.getGL() == null || glad.getGL().getContext() == null) {
			return null;
		}
		final boolean current = glad.getGL().getContext().isCurrent();
		if (!current) {
			glad.getGL().getContext().makeCurrent();
		}
		final AWTGLReadBufferUtil glReadBufferUtil = new AWTGLReadBufferUtil(glad.getGLProfile(), false);
		final BufferedImage image = glReadBufferUtil.readPixelsToBufferedImage(glad.getGL(), true);
		if (!current) {
			glad.getGL().getContext().release();
		}
		return ImageUtils.resize(image, w, h);
	}

	/**
	 * Method updateDisplay()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#updateDisplay(boolean)
	 */
	@Override
	public void updateDisplay(final boolean force) {

		if (alreadyUpdating) {
			return;
		}
		try {
			alreadyUpdating = true;

			final boolean oldState = animator.isPaused();
			if (force) {
				animator.resume();
			}
			manager.drawLayersOn(renderer);

			// EXPERIMENTAL

			if (temp_focus != null) {
				final IShape geometry = Cast.asGeometry(getDisplayScope(), temp_focus.value(getDisplayScope()));
				if (geometry != null) {
					temp_focus = null;
					focusOn(geometry);
				}
			}
			if (force) {
				if (oldState) {
					animator.pause();
				}
			}
		} finally {
			alreadyUpdating = false;
		}
	}

	/**
	 * Method resizeImage()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#resizeImage(int, int,
	 *      boolean)
	 */
	@Override
	public boolean resizeImage(final int x, final int y, final boolean force) {
		return true;
	}

	@Override
	public double getDisplayWidth() {
		return renderer.getCanvas().getSurfaceWidth() * getZoomLevel();
	}

	@Override
	public double getDisplayHeight() {
		return renderer.getCanvas().getSurfaceHeight() * getZoomLevel();
	}

	/**
	 * Method zoomIn()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomIn()
	 */
	@Override
	public void zoomIn() {
		renderer.camera.zoom(true);
	}

	/**
	 * Method zoomOut()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomOut()
	 */
	@Override
	public void zoomOut() {
		renderer.camera.zoom(false);
	}

	/**
	 * Method zoomFit()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomFit()
	 */
	@Override
	public void zoomFit() {
		renderer.currentZRotation = 0;
		renderer.camera.reset();
		output.getData().setZoomLevel(1d);
		zoomFit = true;

	}

	/**
	 * Method getManager()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getManager()
	 */
	@Override
	public ILayerManager getManager() {
		return manager;
	}

	/**
	 * Method focusOn()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#focusOn(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public void focusOn(final IShape geometry) {
		// FIXME: Need to compute the depth of the shape to adjust ZPos value.
		// FIXME: Problem when the geometry is a point how to determine the
		// maxExtent of the shape?
		// FIXME: Problem when an agent is placed on a layer with a z_value how
		// to get this z_layer value to offset it?
		renderer.camera.zoomFocus(geometry);
	}

	/**
	 * Method waitForUpdateAndRun()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#waitForUpdateAndRun(java.lang.Runnable)
	 */
	@Override
	public void runAndUpdate(final Runnable r) {
		r.run();
		if (GAMA.isPaused()) {
			updateDisplay(true);
		}
		if (animator.isPaused()) {
			animator.resume();
			animator.pause();
		}
	}

	/**
	 * Method getWidth()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getWidth()
	 */
	@Override
	public int getWidth() {
		return renderer.getDrawable().getSurfaceWidth();
		// return size.x;
	}

	/**
	 * Method getHeight()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getHeight()
	 */
	@Override
	public int getHeight() {
		return renderer.getDrawable().getSurfaceHeight();
		// return size.y;
	}

	/**
	 * Method outputReloaded()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#outputReloaded()
	 */
	@Override
	public void outputReloaded() {
		setDisplayScope(output.getScope().copy("in OpenGLDisplaySurface"));
		getDisplayScope().disableErrorReporting();
		renderer.initScene();
		manager.outputChanged();

		// resizeImage(getWidth(), getHeight(), true);
		if (zoomFit) {
			zoomFit();
		}
	}

	private class GamaEventListener extends MouseAdapter
			implements MouseTrackListener, MouseMoveListener, FocusListener, KeyListener {

		final IEventLayerListener listener;
		int down_x, down_y;

		GamaEventListener(final IEventLayerListener listener) {
			this.listener = listener;
		}

		@Override
		public void mouseMove(final MouseEvent e) {
			if (e.button > 0) {
				return;
			}
			listener.mouseMove(e.x, e.y);
		}

		@Override
		public void mouseExit(final MouseEvent e) {
			if (e.button > 0) {
				return;
			}
			listener.mouseExit(e.x, e.y);
		}

		@Override
		public void mouseEnter(final MouseEvent e) {
			if (e.button > 0) {
				return;
			}
			listener.mouseEnter(e.x, e.y);
		}

		@Override
		public void mouseHover(final MouseEvent e) {
			if (e.button > 0) {
				return;
			}
			listener.mouseMove(e.x, e.y);
		}

		@Override
		public void mouseDown(final MouseEvent e) {
			down_x = e.x;
			down_y = e.y;
			listener.mouseDown(e.x, e.y, e.button);
		}

		@Override
		public void mouseUp(final MouseEvent e) {
			if (e.x == down_x && e.y == down_y) {
				listener.mouseClicked(e.x, e.y, e.button);
			} else {
				listener.mouseUp(e.x, e.y, e.button);
			}
		}

		@Override
		public void focusGained(final FocusEvent e) {
			listener.mouseEnter(0, 0);
		}

		@Override
		public void focusLost(final FocusEvent e) {
			listener.mouseExit(0, 0);
		}

		@Override
		public void keyPressed(final KeyEvent e) {
			listener.keyPressed(String.valueOf(e.character));
		}

		@Override
		public void keyReleased(final KeyEvent e) {
		}

	}

	/**
	 * Method addMouseListener()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#addMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void addListener(final IEventLayerListener listener) {
		if (eventListeners.containsKey(listener)) {
			return;
		}
		final GamaEventListener l = new GamaEventListener(listener);
		eventListeners.put(listener, l);
		renderer.canvas.addMouseListener(l);
		renderer.canvas.addMouseMoveListener(l);
		renderer.canvas.addFocusListener(l);
		renderer.canvas.addKeyListener(l);

	}

	/**
	 * Method removeMouseListener()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#removeMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void removeListener(final IEventLayerListener listener) {
		final GamaEventListener l = eventListeners.get(listener);
		if (l == null) {
			return;
		}
		eventListeners.remove(listener);
		GAMA.getGui().run(new Runnable() {

			@Override
			public void run() {
				if (renderer.canvas != null && !renderer.canvas.isDisposed()) {
					renderer.canvas.removeMouseListener(l);
					renderer.canvas.removeMouseMoveListener(l);
					renderer.canvas.removeFocusListener(l);
					renderer.canvas.removeKeyListener(l);
				}
			}
		});

	}

	@Override
	public Collection<IEventLayerListener> getLayerListeners() {
		return eventListeners.keySet();
	}

	/**
	 * Method getEnvWidth()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getEnvWidth()
	 */
	@Override
	public double getEnvWidth() {
		return output.getData().getEnvWidth();
	}

	/**
	 * Method getEnvHeight()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getEnvHeight()
	 */
	@Override
	public double getEnvHeight() {
		return output.getData().getEnvHeight();
	}

	/**
	 * Method getModelCoordinates()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinates()
	 */
	@Override
	public ILocation getModelCoordinates() {
		final Point mp = renderer.camera.getMousePosition();
		if (mp == null) {
			return null;
		}
		final GamaPoint p = renderer.getRealWorldPointFromWindowPoint(mp);
		if (p == null) {
			return null;
		}
		return new GamaPoint(p.x, -p.y);
	}

	@Override
	public String getModelCoordinatesInfo() {
		boolean canObtainInfo = getManager().isProvidingCoordinates();
		if (!canObtainInfo) {
			return "No world coordinates";
		}
		canObtainInfo = getManager().isProvidingWorldCoordinates();
		if (!canObtainInfo) {
			return "No world coordinates";
		}
		// By default, returns the coordinates in the world.
		final ILocation point = getModelCoordinates();
		final String x = point == null ? "N/A" : String.format("%8.2f", point.getX());
		final String y = point == null ? "N/A" : String.format("%8.2f", point.getY());
		final Object[] objects = new Object[] { x, y };
		return String.format("X%10s | Y%10s", objects);
	}

	@Override
	public Envelope getVisibleRegionForLayer(final ILayer currentLayer) {
		if (currentLayer instanceof OverlayLayer) {
			return getDisplayScope().getSimulationScope().getEnvelope();
		}
		Envelope e = currentLayer.getVisibleRegion();
		if (e == null) {
			e = new Envelope();
			final Point origin = new Point(0, 0);
			int xc = -origin.x;
			int yc = -origin.y;
			e.expandToInclude((GamaPoint) currentLayer.getModelCoordinatesFrom(xc, yc, this));
			xc = xc + renderer.getDrawable().getSurfaceWidth();
			yc = yc + renderer.getDrawable().getSurfaceHeight();
			e.expandToInclude((GamaPoint) currentLayer.getModelCoordinatesFrom(xc, yc, this));
			currentLayer.setVisibleRegion(e);
		}
		return e;
	}

	/**
	 * Method getModelCoordinatesFrom()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinatesFrom(int,
	 *      int, java.awt.Point, java.awt.Point)
	 */
	@Override
	public ILocation getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final Point sizeInPixels,
			final Point positionInPixels) {
		final Point mp = new Point(xOnScreen, yOnScreen);
		final GamaPoint p = renderer.getRealWorldPointFromWindowPoint(mp);
		return new GamaPoint(p.x, -p.y);
	}

	/**
	 * Method selectAgent()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#selectAgent(int, int)
	 */
	@Override
	public Collection<IAgent> selectAgent(final int x, final int y) {
		final ILocation pp = getModelCoordinatesFrom(x, y, null, null);
		return scope.getRoot().getPopulation().getTopology().getNeighboursOf(scope, new GamaPoint(pp.getX(), pp.getY()),
				renderer.getMaxEnvDim() / 100, Different.with());
	}

	/**
	 * Method followAgent()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#followAgent(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void followAgent(final IAgent a) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				scope.getGui().asyncRun(new Runnable() {

					@Override
					public void run() {
						renderer.camera.zoomFocus(a);
					}
				});
			}
		}).start();

	}

	/**
	 * Method getZoomLevel()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getZoomLevel()
	 */
	@Override
	public double getZoomLevel() {
		if (output.getData().getZoomLevel() == null) {
			output.getData().setZoomLevel(computeInitialZoomLevel());
		}
		return output.getData().getZoomLevel();
	}

	protected Double computeInitialZoomLevel() {
		if (renderer.camera == null) {
			return 1.0;
		}
		return renderer.camera.zoomLevel();
	}

	/**
	 * Method getDisplayScope()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getDisplayScope()
	 */
	@Override
	public IScope getDisplayScope() {
		return scope;
	}

	/**
	 * Method getOutput()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getOutput()
	 */
	@Override
	public LayeredDisplayOutput getOutput() {
		return output;
	}

	/**
	 * Method getCameraPosition()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface.OpenGL#getCameraPosition()
	 */
	@Override
	public ILocation getCameraPosition() {
		if (renderer.camera == null) {
			return new GamaPoint(0, 0, 0);
		}
		return renderer.camera.getPosition();
	}

	/**
	 * Method setPaused()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface.OpenGL#setPaused(boolean)
	 */
	@Override
	public void setPaused(final boolean paused) {
		if (paused) {
			animator.pause();
		} else {
			animator.resume();
		}
	}

	/**
	 * Method selectAgents()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface.OpenGL#selectAgents(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void selectAgent(final IAgent agent) {
		menuManager.buildMenu(renderer.camera.getMousePosition().x, renderer.camera.getMousePosition().y, agent);
	}

	org.eclipse.swt.widgets.Menu menu;

	/**
	 * Method selectSeveralAgents()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface.OpenGL#selectSeveralAgents(java.util.Collection,
	 *      int)
	 */
	@Override
	public void selectionIn(final Envelope3D env) {

		final Envelope3D envInWorld = Envelope3D.withYNegated(env);
		final Collection<IAgent> agents = scope.getTopology().getSpatialIndex().allInEnvelope(scope,
				envInWorld.centre(), envInWorld, new Different(), true);

		scope.getGui().asyncRun(new Runnable() {

			@Override
			public void run() {
				if (menu != null && !menu.isDisposed()) {
					menu.dispose();
				}
				final Control swtControl = renderer.getCanvas();
				final DisplayedAgentsMenu menuBuilder = new DisplayedAgentsMenu();
				menu = menuBuilder.getMenu(SWTOpenGLDisplaySurface.this, swtControl, true, true, agents,
						getModelCoordinates(), true);
				menu.setData(IKeyword.USER_LOCATION, getModelCoordinates());
				menu.setLocation(swtControl.toDisplay(renderer.camera.getMousePosition().x,
						renderer.camera.getMousePosition().y));
				final MenuItem mu = new MenuItem(menu, SWT.PUSH, 0);
				mu.setText("Focus on region...");
				mu.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						renderer.camera.zoomRoi(env);
					}

					@Override
					public void widgetDefaultSelected(final SelectionEvent e) {
						widgetSelected(e);

					}
				});
				mu.setImage(IGamaIcons.DISPLAY_TOOLBAR_ZOOMFIT.image());
				new MenuItem(menu, SWT.SEPARATOR, 1);
				menu.addMenuListener(new MenuListener() {

					@Override
					public void menuHidden(final MenuEvent e) {

						renderer.cancelROI();
						animator.resume();
					}

					@Override
					public void menuShown(final MenuEvent e) {
						animator.pause();
					}
				});
				menu.setVisible(true);
			}
		});

	}

	protected void setDisplayScope(final IScope scope) {
		if (this.scope != null) {
			GAMA.releaseScope(this.scope);
		}
		this.scope = scope;
	}

	@Override
	public void dispose() {
		if (disposed) {
			return;
		}
		disposed = true;
		if (manager != null) {
			manager.dispose();
		}
		if (animator != null && animator.isStarted()) {
			animator.stop();
		}
		if (this.menu != null && !menu.isDisposed()) {
			menu.dispose();
			this.menu = null;
		}

		this.menuManager = null;
		this.eventListeners.clear();

		GAMA.releaseScope(getDisplayScope());
		setDisplayScope(null);
	}

	@Override
	public LayeredDisplayData getData() {
		return output.getData();
	}

	/**
	 * Method changed()
	 * 
	 * @see msi.gama.outputs.LayeredDisplayData.DisplayDataListener#changed(int,
	 *      boolean)
	 */
	@Override
	public void changed(final Changes property, final boolean value) {
		switch (property) {
		case CHANGE_CAMERA:
			renderer.switchCamera();
			break;
		case SPLIT_LAYER:
			final int nbLayers = this.getManager().getItems().size();
			int i = 0;
			final Iterator<ILayer> it = this.getManager().getItems().iterator();
			while (it.hasNext()) {
				final ILayer curLayer = it.next();
				if (value) {// Split layer
					curLayer.setElevation((double) i / nbLayers);
				} else {// put all the layer at zero
					curLayer.setElevation(0.0);
				}
				i++;
			}

			updateDisplay(true);
			break;
		case THREED_VIEW:
			// FIXME What is this ???
			break;
		case CAMERA_POS:
			renderer.updateCameraPosition();
			break;
		default:
			break;

		}

	}

	/**
	 * Method setSize()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#setSize(int, int)
	 */
	@Override
	public void setSize(final int x, final int y) {
		// size = new Point(x, y);
	}

	/**
	 * @return
	 */
	public Composite getParent() {
		return parent;
	}

	/**
	 * Method setSWTMenuManager()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#setSWTMenuManager(java.lang.Object)
	 */
	@Override
	public void setSWTMenuManager(final Object displaySurfaceMenu) {
		menuManager = (DisplaySurfaceMenu) displaySurfaceMenu;
	}

	private JOGLRenderer createRenderer() {
		return new JOGLRenderer(this);
	}

	private GLAnimatorControl createAnimator() {
		final GLAutoDrawable drawable = renderer.createDrawable(parent);
		return drawable.getAnimator();
	}

	@Override
	public void layersChanged() {
		renderer.sceneBuffer.layersChanged();

	}

	public void invalidateVisibleRegions() {
		for (final ILayer layer : manager.getItems()) {
			layer.setVisibleRegion(null);
		}
	}

	/**
	 * Method getFPS()
	 * 
	 * @see msi.gama.common.interfaces.IDisplaySurface#getFPS()
	 */
	@Override
	public int getFPS() {
		return (int) this.animator.getTotalFPS();
	}

	@Override
	public boolean isRealized() {
		if (renderer == null) {
			return false;
		}
		final GLAutoDrawable d = renderer.getDrawable();
		if (d == null) {
			return false;
		}
		return d.isRealized();
	}

	@Override
	public boolean isRendered() {
		return renderer.sceneBuffer.getSceneToRender().rendered();
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public Envelope3D getROIDimensions() {
		return renderer.getROIEnvelope();
	}

}
