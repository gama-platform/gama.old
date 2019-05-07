/*******************************************************************************************************
 *
 * ummisco.gama.opengl.view.SWTOpenGLDisplaySurface.java, in plugin ummisco.gama.opengl, is part of the source code of
 * the GAMA modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.view;

import static org.eclipse.swt.internal.DPIUtil.autoScaleUp;
import static ummisco.gama.ui.utils.PlatformHelper.isLinux;
import static ummisco.gama.ui.utils.PlatformHelper.isWindows;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

import com.jogamp.opengl.FPSCounter;
import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.swt.GLCanvas;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.interfaces.ILayerManager;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.agent.AgentIdentifier;
import msi.gama.metamodel.agent.IAgent;
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
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.draw.DrawingAttributes;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.renderer.JOGLRenderer;
import ummisco.gama.ui.menus.AgentsMenu;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.displays.DisplaySurfaceMenu;

/**
 * Class OpenGLSWTDisplaySurface.
 *
 * @author drogoul
 * @since 25 mars 2015
 *
 */
@display ("opengl")
public class SWTOpenGLDisplaySurface implements IDisplaySurface.OpenGL {

	static {
		DEBUG.OFF();
	}

	GLAnimatorControl animator;
	IOpenGLRenderer renderer;
	protected double zoomIncrement = 0.1;
	protected boolean zoomFit = true;
	Set<IEventLayerListener> listeners = new HashSet<>();
	final LayeredDisplayOutput output;
	final LayerManager layerManager;
	protected DisplaySurfaceMenu menuManager;
	protected IExpression temp_focus;
	IScope scope;
	final Composite parent;
	volatile boolean disposed;
	private volatile boolean alreadyUpdating;

	public SWTOpenGLDisplaySurface(final Object... objects) {
		output = (LayeredDisplayOutput) objects[0];
		parent = (Composite) objects[1];
		output.getData().addListener(this);
		output.setSurface(this);
		setDisplayScope(output.getScope().copy("in opengl display"));
		renderer = createRenderer();
		renderer.setDisplaySurface(this);
		animator = createAnimator();

		layerManager = new LayerManager(this, output);
		temp_focus = output.getFacet(IKeyword.FOCUS);

		animator.start();
	}

	protected IOpenGLRenderer createRenderer() {
		final IOpenGLRenderer r = new JOGLRenderer();
		return r;
	}

	private GLAnimatorControl createAnimator() {
		final GLAutoDrawable drawable = createCanvas(parent);
		return drawable.getAnimator();
	}

	public GLCanvas createCanvas(final Composite parent) {
		final GLProfile profile = GLProfile.getDefault();
		final GLCapabilities cap = new GLCapabilities(profile);
		cap.setDepthBits(24);
		cap.setDoubleBuffered(true);
		cap.setHardwareAccelerated(true);
		cap.setSampleBuffers(true);
		cap.setAlphaBits(8);
		cap.setNumSamples(8);
		final GLCanvas canvas = new GLCanvas(parent, SWT.NONE, cap, null) {

			@SuppressWarnings ("restriction")
			@Override
			public Rectangle getClientArea() {
				if (isWindows() || isLinux()) { return autoScaleUp(super.getClientArea()); }
				return super.getClientArea();
			}
		};
		canvas.setAutoSwapBufferMode(true);
		final SWTGLAnimator animator = new SWTGLAnimator(canvas);
		animator.setUpdateFPSFrames(FPSCounter.DEFAULT_FRAMES_PER_INTERVAL, null);
		renderer.setCanvas(canvas);
		final FillLayout gl = new FillLayout();
		canvas.setLayout(gl);
		return canvas;
	}

	@Override
	public void setMenuManager(final Object menuManager) {
		this.menuManager = (DisplaySurfaceMenu) menuManager;
	}

	/**
	 * Method getImage()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getImage()
	 */
	@Override
	public BufferedImage getImage(final int w, final int h) {
		while (!isRendered()) {
			try {
				Thread.sleep(20);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		final GLCanvas glad = renderer.getCanvas();
		if (glad == null || glad.getGL() == null || glad.getGL().getContext() == null) { return null; }
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

		if (alreadyUpdating) { return; }
		try {
			alreadyUpdating = true;

			final boolean oldState = animator.isPaused();
			if (force) {
				animator.resume();
			}
			layerManager.drawLayersOn(renderer);

			// EXPERIMENTAL

			if (temp_focus != null) {
				final IShape geometry = Cast.asGeometry(getScope(), temp_focus.value(getScope()));
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

	@Override
	public double getDisplayWidth() {
		return renderer.getWidth();
	}

	@Override
	public double getDisplayHeight() {
		return renderer.getHeight();
	}

	/**
	 * Method zoomIn()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomIn()
	 */
	@Override
	public void zoomIn() {
		if (renderer.getData().cameraInteractionDisabled()) { return; }
		renderer.getCameraHelper().zoom(true);
	}

	/**
	 * Method zoomOut()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomOut()
	 */
	@Override
	public void zoomOut() {
		if (renderer.getData().cameraInteractionDisabled()) { return; }
		renderer.getCameraHelper().zoom(false);
	}

	/**
	 * Method zoomFit()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomFit()
	 */
	@Override
	public void zoomFit() {
		// if (renderer.getData().cameraInteractionDisabled())
		// return;
		renderer.getCameraHelper().initialize();
		output.getData().resetZRotation();
		output.getData().setZoomLevel(LayeredDisplayData.INITIAL_ZOOM, true, true);
		zoomFit = true;

	}

	/**
	 * Method getManager()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getManager()
	 */
	@Override
	public ILayerManager getManager() {
		return layerManager;
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
		renderer.getCameraHelper().zoomFocus(geometry.getEnvelope().yNegated());
	}

	/**
	 * Method waitForUpdateAndRun()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#waitForUpdateAndRun(java.lang.Runnable)
	 */
	@Override
	public void runAndUpdate(final Runnable r) {
		r.run();
		if (getScope().isPaused()) {
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
		return renderer.getCanvas().getSurfaceWidth();
		// return size.x;
	}

	/**
	 * Method getHeight()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getHeight()
	 */
	@Override
	public int getHeight() {
		return renderer.getCanvas().getSurfaceHeight();
		// return size.y;
	}

	/**
	 * Method outputReloaded()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#outputReloaded()
	 */
	@Override
	public void outputReloaded() {
		setDisplayScope(output.getScope().copy("in opengl display"));
		if (!GamaPreferences.Runtime.ERRORS_IN_DISPLAYS.getValue()) {
			getScope().disableErrorReporting();
		}
		renderer.initScene();
		layerManager.outputChanged();

		// resizeImage(getWidth(), getHeight(), true);
		if (zoomFit) {
			zoomFit();
		}
	}

	/**
	 * Method addMouseListener()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#addMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void addListener(final IEventLayerListener listener) {
		listeners.add(listener);
	}

	/**
	 * Method removeMouseListener()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#removeMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void removeListener(final IEventLayerListener listener) {
		listeners.remove(listener);

	}

	@Override
	public Collection<IEventLayerListener> getLayerListeners() {
		return listeners;
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
		final Point mp = renderer.getCameraHelper().getMousePosition();
		if (mp == null) { return null; }
		final GamaPoint p = renderer.getRealWorldPointFromWindowPoint(mp);
		if (p == null) { return null; }
		return new GamaPoint(p.x, -p.y);
	}

	@Override
	public void getModelCoordinatesInfo(final StringBuilder sb) {
		boolean canObtainInfo = getManager().isProvidingCoordinates();
		if (!canObtainInfo) {
			sb.append("No world coordinates");
			return;
		}
		canObtainInfo = getManager().isProvidingWorldCoordinates();
		if (!canObtainInfo) {
			sb.append("No world coordinates");
			return;
		}
		// By default, returns the coordinates in the world.
		final ILocation point = getModelCoordinates();
		final String x = point == null ? "N/A" : String.format("%8.6f", point.getX());
		final String y = point == null ? "N/A" : String.format("%8.6f", point.getY());
		final Object[] objects = new Object[] { x, y };
		sb.append(String.format("X%15s | Y%15s", objects));
	}

	@Override
	public Envelope getVisibleRegionForLayer(final ILayer currentLayer) {
		if (currentLayer instanceof OverlayLayer) { return getScope().getSimulation().getEnvelope(); }
		Envelope e = currentLayer.getData().getVisibleRegion();
		if (e == null) {
			e = new Envelope();
			final Point origin = new Point(0, 0);
			int xc = -origin.x;
			int yc = -origin.y;
			e.expandToInclude((GamaPoint) currentLayer.getModelCoordinatesFrom(xc, yc, this));
			xc = xc + renderer.getCanvas().getSurfaceWidth();
			yc = yc + renderer.getCanvas().getSurfaceHeight();
			e.expandToInclude((GamaPoint) currentLayer.getModelCoordinatesFrom(xc, yc, this));
			currentLayer.getData().setVisibleRegion(e);
		}
		return e;
	}

	/**
	 * Method getModelCoordinatesFrom()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinatesFrom(int, int, java.awt.Point, java.awt.Point)
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
		return scope.getRoot().getTopology().getNeighborsOf(scope, new GamaPoint(pp.getX(), pp.getY()),
				renderer.getMaxEnvDim() / 100, Different.with());
	}

	/**
	 * Method followAgent()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#followAgent(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void followAgent(final IAgent a) {
		new Thread(
				() -> WorkbenchHelper.asyncRun(() -> renderer.getCameraHelper().zoomFocus(a.getEnvelope().yNegated())))
						.start();

	}

	/**
	 * Method getZoomLevel()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getZoomLevel()
	 */
	@Override
	public double getZoomLevel() {
		if (output.getData().getZoomLevel() == null) {
			output.getData().setZoomLevel(computeInitialZoomLevel(), true, false);
		}
		return output.getData().getZoomLevel();
	}

	protected Double computeInitialZoomLevel() {
		return renderer.getCameraHelper().zoomLevel();
	}

	/**
	 * Method getDisplayScope()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface#getDisplayScope()
	 */
	@Override
	public IScope getScope() {
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

	final Runnable cleanup = () -> WorkbenchHelper.asyncRun(() -> renderer.getPickingHelper().setPicking(false));

	/**
	 * Method selectAgents()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface.OpenGL#selectAgents(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void selectAgent(final DrawingAttributes attributes) {
		IAgent ag = null;
		boolean withHighlight = true;
		if (attributes != null) {
			if (attributes.getSpeciesName() != null) {
				// The picked image is a grid or an image of a grid
				withHighlight = false;
				final GamaPoint pickedPoint = renderer
						.getRealWorldPointFromWindowPoint(renderer.getCameraHelper().getLastMousePressedPosition());
				ag = scope.getRoot().getPopulationFor(attributes.getSpeciesName()).getAgent(scope,
						new GamaPoint(pickedPoint.x, -pickedPoint.y));
			} else {
				final AgentIdentifier id = attributes.getAgentIdentifier();
				if (id != null) {
					ag = id.getAgent(scope);
				}
			}
		}
		if (withHighlight) {
			menuManager.buildMenu(renderer.getCameraHelper().getMousePosition().x,
					renderer.getCameraHelper().getMousePosition().y, ag, cleanup, AgentsMenu.getHighlightActionFor(ag));
		} else {
			menuManager.buildMenu(renderer.getCameraHelper().getMousePosition().x,
					renderer.getCameraHelper().getMousePosition().y, ag, cleanup);
		}
	}

	/**
	 * Method selectSeveralAgents()
	 *
	 * @see msi.gama.common.interfaces.IDisplaySurface.OpenGL#selectSeveralAgents(java.util.Collection, int)
	 */
	@Override
	public void selectionIn(final Envelope3D env) {

		final Envelope3D envInWorld = Envelope3D.withYNegated(env);
		final Collection<IAgent> agents = scope.getTopology().getSpatialIndex().allInEnvelope(scope,
				envInWorld.centre(), envInWorld, new Different(), false);
		final Map<String, Runnable> actions = new LinkedHashMap<>();
		final Map<String, Image> images = new HashMap<>();
		images.put(renderer.getOpenGLHelper().isStickyROI() ? "Hide region" : "Keep region visible",
				GamaIcons.create(IGamaIcons.MENU_FOLLOW).image());
		images.put("Focus on region", GamaIcons.create(IGamaIcons.DISPLAY_TOOLBAR_ZOOMFIT).image());
		actions.put(renderer.getOpenGLHelper().isStickyROI() ? "Hide region" : "Keep region visible",
				() -> renderer.getOpenGLHelper().toogleROI());
		actions.put("Focus on region", () -> renderer.getCameraHelper().zoomFocus(env));
		WorkbenchHelper.run(() -> {
			final Menu menu = menuManager.buildROIMenu(renderer.getCameraHelper().getMousePosition().x,
					renderer.getCameraHelper().getMousePosition().y, agents, actions, images);
			menu.addMenuListener(new MenuListener() {

				@Override
				public void menuHidden(final MenuEvent e) {
					animator.resume();
					// Will be run after the selection
					WorkbenchHelper.asyncRun(() -> renderer.getOpenGLHelper().cancelROI());

				}

				@Override
				public void menuShown(final MenuEvent e) {
					animator.pause();
				}
			});

			menu.setVisible(true);
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
		if (disposed) { return; }
		disposed = true;
		if (layerManager != null) {
			layerManager.dispose();
		}
		if (animator != null && animator.isStarted()) {
			animator.stop();
		}
		this.menuManager = null;
		this.listeners.clear();
		this.renderer = null;
		GAMA.releaseScope(getScope());
		setDisplayScope(null);
	}

	@Override
	public LayeredDisplayData getData() {
		return output.getData();
	}

	/**
	 * Method changed()
	 *
	 * @see msi.gama.outputs.LayeredDisplayData.DisplayDataListener#changed(int, boolean)
	 */
	@Override
	public void changed(final Changes property, final Object value) {
		if (renderer == null) { return; }
		switch (property) {

			case CHANGE_CAMERA:
				renderer.getCameraHelper().setupCamera();
				break;
			case SPLIT_LAYER:
				final double gap = (Double) value;
				if (DEBUG.IS_ON()) {
					DEBUG.OUT("Value received by SWTOpenGLDisplaySurface= " + value);
				}
				double currentElevation = 0;

				for (final ILayer layer : this.getManager().getItems()) {
					layer.getData().addElevation(currentElevation);
					currentElevation += gap;
				}
				renderer.getSceneHelper().layerOffsetChanged();

				break;
			case CAMERA_POS:
				renderer.getCameraHelper().updatePosition();
				break;
			case CAMERA_UP:
				renderer.getCameraHelper().updateOrientation();
				break;
			case CAMERA_TARGET:
				renderer.getCameraHelper().updateTarget();
				break;
			case CAMERA_PRESET:
				renderer.getCameraHelper().applyPreset((String) value);
				break;
			case ZOOM:
				renderer.getCameraHelper().zoom((Double) value);
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
	public void setSize(final int x, final int y) {}

	@Override
	public void layersChanged() {
		renderer.getSceneHelper().layersChanged();

	}

	public void invalidateVisibleRegions() {
		for (final ILayer layer : layerManager.getItems()) {
			layer.getData().setVisibleRegion(null);
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
		if (renderer == null) { return false; }
		final GLAutoDrawable d = renderer.getCanvas();
		if (d == null) { return false; }
		return d.isRealized();
	}

	@Override
	public boolean isRendered() {
		if (renderer == null || renderer.getSceneHelper().getSceneToRender() == null) { return false; }
		return renderer.getSceneHelper().getSceneToRender().rendered();
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public Envelope3D getROIDimensions() {
		return renderer.getOpenGLHelper().getROIEnvelope();
	}

	@Override
	public void dispatchKeyEvent(final char e) {
		for (final IEventLayerListener gl : listeners) {
			gl.keyPressed(String.valueOf(e));
		}
	}

	@Override
	public void dispatchMouseEvent(final int swtMouseEvent) {
		final Point p = renderer.getCameraHelper().getMousePosition();
		final int x = p.x;
		final int y = p.y;
		for (final IEventLayerListener gl : listeners) {
			switch (swtMouseEvent) {
				case SWT.MouseDown:
					gl.mouseDown(x, y, 1);
					break;
				case SWT.MouseUp:
					gl.mouseUp(x, y, 1);
					break;
				case SWT.MouseMove:
					gl.mouseMove(x, y);
					break;
				case SWT.MouseEnter:
					gl.mouseEnter(x, y);
					break;
				case SWT.MouseExit:
					gl.mouseExit(x, y);
					break;
			}
		}
	}

	@Override
	public void setMousePosition(final int x, final int y) {
		// Nothing to do (taken in charge by the camera)

	}

	@Override
	public void selectAgentsAroundMouse() {
		// Nothing to do (taken in charge by the camera)
	}

	@Override
	public void draggedTo(final int x, final int y) {
		// Nothing to do (taken in charge by the camera

	}

}
