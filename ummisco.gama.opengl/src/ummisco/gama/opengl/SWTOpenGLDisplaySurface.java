/**
 * Created by drogoul, 25 mars 2015
 *
 */
package ummisco.gama.opengl;

import java.awt.Point;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.vividsolutions.jts.geom.Envelope;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.FileUtils;
import msi.gama.gui.displays.awt.DisplaySurfaceMenu;
import msi.gama.gui.views.actions.DisplayedAgentsMenu;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.outputs.*;
import msi.gama.outputs.LayeredDisplayData.Changes;
import msi.gama.outputs.display.LayerManager;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.*;

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
	// protected volatile boolean canBeUpdated = true;
	protected double zoomIncrement = 0.1;
	// protected Double zoomLevel = null;
	protected boolean zoomFit = true;
	// private IZoomListener zoomListener;
	Map<IEventLayerListener, OwnMouseListener> mouseListeners = new HashMap();
	final LayeredDisplayOutput output;
	final LayerManager manager;
	protected DisplaySurfaceMenu menuManager;
	protected IExpression temp_focus;
	IScope scope;
	final Composite parent;
	boolean disposed;

	// NEVER USED
	public SWTOpenGLDisplaySurface(final Object ... objects) {
		// super((Composite) objects[0], SWT.None);
		// data = null;
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
		setDisplayScope(output.getScope().copy());
		renderer = createRenderer();
		animator = createAnimator();
		animator.setUpdateFPSFrames(FPSCounter.DEFAULT_FRAMES_PER_INTERVAL, null);
		manager = new LayerManager(this, output);
		temp_focus = output.getFacet(IKeyword.FOCUS);

		animator.start();
	}

	/**
	 * Method getImage()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getImage()
	 */
	@Override
	public BufferedImage getImage() {
		GLAutoDrawable glad = renderer.getDrawable();
		if ( glad == null || glad.getGL() == null || glad.getGL().getContext() == null ) { return null; }
		boolean current = glad.getGL().getContext().isCurrent();
		if ( !current ) {
			glad.getGL().getContext().makeCurrent();
		}
		AWTGLReadBufferUtil glReadBufferUtil = new AWTGLReadBufferUtil(glad.getGLProfile(), false);
		BufferedImage image = glReadBufferUtil.readPixelsToBufferedImage(glad.getGL(), true);
		if ( !current ) {
			glad.getGL().getContext().release();
		}
		return image;
	}

	/**
	 * Method updateDisplay()
	 * @see msi.gama.common.interfaces.IDisplaySurface#updateDisplay(boolean)
	 */
	@Override
	public void updateDisplay(final boolean force) {

		// acquireLock();
		boolean oldState = animator.isPaused();
		if ( force ) {
			animator.resume();
		}
		// canBeUpdated(false);
		// try {
		manager.drawLayersOn(renderer);

		if ( output.getData().isAutosave() ) {
			while (renderer.getCurrentScene() == null || !renderer.getCurrentScene().rendered()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			snapshot();
		}
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// } finally {
		// releaseLock();
		// }

		// EXPERIMENTAL

		if ( temp_focus != null ) {
			IShape geometry = Cast.asGeometry(getDisplayScope(), temp_focus.value(getDisplayScope()));
			if ( geometry != null ) {
				temp_focus = null;
				focusOn(geometry);
			}
		}
		if ( force ) {
			if ( oldState ) {
				animator.pause();
			}
		}

	}

	/**
	 * Method resizeImage()
	 * @see msi.gama.common.interfaces.IDisplaySurface#resizeImage(int, int, boolean)
	 */
	@Override
	public boolean resizeImage(final int x, final int y, final boolean force) {
		// if ( !force && x == viewPort.width && y == viewPort.height ) { return true; }
		// // if ( getWidth() <= 0 && getHeight() <= 0 ) { return false; }
		// canBeUpdated(false);
		// final int[] point = new int[2];
		// if ( !manager.stayProportional() ) {
		// point[0] = x;
		// point[1] = y;
		// } else {
		// double widthHeightConstraint = getEnvHeight() / getEnvWidth();
		// if ( widthHeightConstraint < 1 ) {
		// point[1] = FastMath.min(y, (int) FastMath.round(x * widthHeightConstraint));
		// point[0] = FastMath.min(x, (int) FastMath.round(point[1] / widthHeightConstraint));
		// } else {
		// point[0] = FastMath.min(x, (int) FastMath.round(y / widthHeightConstraint));
		// point[1] = FastMath.min(y, (int) FastMath.round(point[0] * widthHeightConstraint));
		// }
		// }
		// viewPort.height = FastMath.max(1, point[1]);;
		// viewPort.width = FastMath.max(1, point[0]);;
		// canBeUpdated(true);
		// setSize(x, y);
		return true;
	}

	@Override
	public double getDisplayWidth() {
		return renderer.getCanvas().getSurfaceWidth() * getZoomLevel();
		// return viewPort.width;
	}

	@Override
	public double getDisplayHeight() {
		return renderer.getCanvas().getSurfaceHeight() * getZoomLevel();
		// return viewPort.height;
	}

	/**
	 * Method zoomIn()
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomIn()
	 */
	@Override
	public void zoomIn() {
		renderer.camera.zoom(true);
	}

	/**
	 * Method zoomOut()
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomOut()
	 */
	@Override
	public void zoomOut() {
		renderer.camera.zoom(false);
	}

	/**
	 * Method zoomFit()
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomFit()
	 */
	@Override
	public void zoomFit() {
		// resizeImage(getWidth(), getHeight(), false);
		renderer.frame = 0;
		renderer.camera.zeroVelocity();
		renderer.camera.resetCamera(getEnvWidth(), getEnvHeight(), output.getData().isOutput3D());
		output.getData().setZoomLevel(1d);
		// newZoomLevel(1d);
		zoomFit = true;

	}

	/**
	 * Method getManager()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getManager()
	 */
	@Override
	public ILayerManager getManager() {
		return manager;
	}

	/**
	 * Method focusOn()
	 * @see msi.gama.common.interfaces.IDisplaySurface#focusOn(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public void focusOn(final IShape geometry) {
		// FIXME: Need to compute the depth of the shape to adjust ZPos value.
		// FIXME: Problem when the geometry is a point how to determine the maxExtent of the shape?
		// FIXME: Problem when an agent is placed on a layer with a z_value how to get this z_layer value to offset it?
		ILocation p = geometry.getLocation();
		renderer.camera.zoomFocus(p.getX(), p.getY(), p.getZ(), geometry.getEnvelope().maxExtent());
	}

	/**
	 * Method waitForUpdateAndRun()
	 * @see msi.gama.common.interfaces.IDisplaySurface#waitForUpdateAndRun(java.lang.Runnable)
	 */
	@Override
	public void runAndUpdate(final Runnable r) {
		r.run();
		if ( GAMA.isPaused() ) {
			updateDisplay(true);
		}
		if ( animator.isPaused() ) {
			animator.resume();
			animator.pause();
		}
	}

	public final void save(final IScope scope, final RenderedImage image) {
		// Intentionnaly passing GAMA.getRuntimeScope() to errors in order to prevent the exceptions from being masked.
		if ( image == null ) { return; }
		try {
			Files.newFolder(scope, SNAPSHOT_FOLDER_NAME);
		} catch (GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + SNAPSHOT_FOLDER_NAME);
			GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
			e1.printStackTrace();
			return;
		}
		String snapshotFile = FileUtils.constructAbsoluteFilePath(scope,
			SNAPSHOT_FOLDER_NAME + "/" + GAMA.getModel().getName() + "_display_" + output.getName(), false);

		String file = snapshotFile + "_size_" + image.getWidth() + "x" + image.getHeight() + "_cycle_" +
			scope.getClock().getCycle() + "_time_" + java.lang.System.currentTimeMillis() + ".png";
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new FileOutputStream(file));
			ImageIO.write(image, "png", os);
		} catch (java.io.IOException ex) {
			GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
			e.addContext("Unable to create output stream for snapshot image");
			GAMA.reportError(GAMA.getRuntimeScope(), e, false);
		} finally {
			try {
				if ( os != null ) {
					os.close();
				}
			} catch (Exception ex) {
				GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
				e.addContext("Unable to close output stream for snapshot image");
				GAMA.reportError(GAMA.getRuntimeScope(), e, false);
			}
		}
	}

	/**
	 * Method snapshot()
	 * @see msi.gama.common.interfaces.IDisplaySurface#snapshot()
	 */
	@Override
	public void snapshot() {
		save(getDisplayScope(), getImage());
	}

	/**
	 * Method getWidth()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getWidth()
	 */
	@Override
	public int getWidth() {
		return renderer.getDrawable().getSurfaceWidth();
		// return size.x;
	}

	/**
	 * Method getHeight()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getHeight()
	 */
	@Override
	public int getHeight() {
		return renderer.getDrawable().getSurfaceHeight();
		// return size.y;
	}

	/**
	 * Method outputReloaded()
	 * @see msi.gama.common.interfaces.IDisplaySurface#outputReloaded()
	 */
	@Override
	public void outputReloaded() {
		setDisplayScope(output.getScope().copy());
		getDisplayScope().disableErrorReporting();
		renderer.initFor(this);
		manager.outputChanged();

		// resizeImage(getWidth(), getHeight(), true);
		if ( zoomFit ) {
			zoomFit();
		}
	}

	private class OwnMouseListener extends MouseAdapter implements MouseTrackListener, MouseMoveListener, FocusListener {

		final IEventLayerListener listener;
		int down_x, down_y;

		OwnMouseListener(final IEventLayerListener listener) {
			this.listener = listener;
		}

		@Override
		public void mouseMove(final MouseEvent e) {
			if ( e.button > 0 ) { return; }
			listener.mouseMove(e.x, e.y);
		}

		@Override
		public void mouseExit(final MouseEvent e) {
			if ( e.button > 0 ) { return; }
			listener.mouseExit(e.x, e.y);
		}

		@Override
		public void mouseEnter(final MouseEvent e) {
			if ( e.button > 0 ) { return; }
			listener.mouseEnter(e.x, e.y);
		}

		@Override
		public void mouseHover(final MouseEvent e) {
			if ( e.button > 0 ) { return; }
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
			if ( e.x == down_x && e.y == down_y ) {
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

	}

	/**
	 * Method addMouseListener()
	 * @see msi.gama.common.interfaces.IDisplaySurface#addMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void addListener(final IEventLayerListener listener) {
		if ( mouseListeners.containsKey(listener) ) { return; }
		OwnMouseListener l = new OwnMouseListener(listener);
		mouseListeners.put(listener, l);
		renderer.canvas.addMouseListener(l);
		renderer.canvas.addMouseMoveListener(l);
		renderer.canvas.addFocusListener(l);

	}

	/**
	 * Method removeMouseListener()
	 * @see msi.gama.common.interfaces.IDisplaySurface#removeMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void removeListener(final IEventLayerListener listener) {
		final OwnMouseListener l = mouseListeners.get(listener);
		if ( l == null ) { return; }
		mouseListeners.remove(listener);
		GAMA.getGui().run(new Runnable() {

			@Override
			public void run() {
				if ( renderer.canvas != null && !renderer.canvas.isDisposed() ) {
					renderer.canvas.removeMouseListener(l);
					renderer.canvas.removeMouseMoveListener(l);
					renderer.canvas.removeFocusListener(l);
				}
			}
		});

	}

	@Override
	public Collection<IEventLayerListener> getLayerListeners() {
		return mouseListeners.keySet();
	}

	/**
	 * Method getEnvWidth()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getEnvWidth()
	 */
	@Override
	public double getEnvWidth() {
		return output.getData().getEnvWidth();
	}

	/**
	 * Method getEnvHeight()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getEnvHeight()
	 */
	@Override
	public double getEnvHeight() {
		return output.getData().getEnvHeight();
	}

	// /**
	// * Method setZoomListener()
	// * @see msi.gama.common.interfaces.IDisplaySurface#setZoomListener(msi.gama.common.interfaces.IDisplaySurface.IZoomListener)
	// */
	// @Override
	// public void setZoomListener(final IZoomListener listener) {
	// zoomListener = listener;
	// }

	/**
	 * Method getModelCoordinates()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinates()
	 */
	@Override
	public ILocation getModelCoordinates() {
		Point mp = renderer.camera.getMousePosition();
		if ( mp == null ) { return null; }
		GamaPoint p = renderer.getRealWorldPointFromWindowPoint(mp);
		if ( p == null ) { return null; }
		return new GamaPoint(p.x, -p.y);
	}

	@Override
	public Envelope getVisibleRegionForLayer(final ILayer currentLayer) {
		if ( currentLayer instanceof OverlayLayer ) { return getDisplayScope().getSimulationScope().getEnvelope(); }
		Envelope e = currentLayer.getVisibleRegion();
		if ( e == null ) {
			e = new Envelope();
			Point origin = new Point(0, 0);
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
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinatesFrom(int, int, java.awt.Point, java.awt.Point)
	 */
	@Override
	public ILocation getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final Point sizeInPixels,
		final Point positionInPixels) {
		Point mp = new Point(xOnScreen, yOnScreen);
		GamaPoint p = renderer.getRealWorldPointFromWindowPoint(mp);
		return new GamaPoint(p.x, -p.y);
	}

	/**
	 * Method selectAgent()
	 * @see msi.gama.common.interfaces.IDisplaySurface#selectAgent(int, int)
	 */
	@Override
	public Collection<IAgent> selectAgent(final int x, final int y) {
		final ILocation pp = getModelCoordinatesFrom(x, y, null, null);
		return GAMA.run(new InScope<Collection<IAgent>>() {

			@Override
			public Collection<IAgent> run(final IScope scope) {
				return scope.getRoot().getPopulation().getTopology().getNeighboursOf(scope,
					new GamaPoint(pp.getX(), pp.getY()), renderer.getMaxEnvDim() / 100, Different.with());
			}
		});

	}

	/**
	 * Method followAgent()
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
						ILocation l = a.getGeometry().getLocation();
						Envelope env = a.getGeometry().getEnvelope();
						renderer.camera.zoomFocus(l.getX(), l.getY(), l.getZ(), env.maxExtent());
					}
				});
			}
		}).start();

	}

	/**
	 * Method getZoomLevel()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getZoomLevel()
	 */
	@Override
	public double getZoomLevel() {
		if ( output.getData().getZoomLevel() == null ) {
			output.getData().setZoomLevel(computeInitialZoomLevel());
		}
		return output.getData().getZoomLevel();
	}

	protected Double computeInitialZoomLevel() {
		if ( renderer.camera == null ) { return 1.0; }
		return renderer.camera.zoomLevel();
	}

	/**
	 * Method getDisplayScope()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getDisplayScope()
	 */
	@Override
	public IScope getDisplayScope() {
		return scope;
	}

	/**
	 * Method getOutput()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getOutput()
	 */
	@Override
	public LayeredDisplayOutput getOutput() {
		return output;
	}

	/**
	 * Method getCameraPosition()
	 * @see msi.gama.common.interfaces.IDisplaySurface.OpenGL#getCameraPosition()
	 */
	@Override
	public ILocation getCameraPosition() {
		if ( renderer.camera == null ) { return new GamaPoint(0, 0, 0); }
		return renderer.camera.getPosition();
	}

	/**
	 * Method setPaused()
	 * @see msi.gama.common.interfaces.IDisplaySurface.OpenGL#setPaused(boolean)
	 */
	@Override
	public void setPaused(final boolean paused) {
		if ( paused ) {
			animator.pause();
		} else {
			animator.resume();
		}
	}

	/**
	 * Method selectAgents()
	 * @see msi.gama.common.interfaces.IDisplaySurface.OpenGL#selectAgents(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void selectAgent(final IAgent agent) {
		menuManager.buildMenu(renderer.camera.getMousePosition().x, renderer.camera.getMousePosition().y, agent);
	}

	org.eclipse.swt.widgets.Menu menu;

	/**
	 * Method selectSeveralAgents()
	 * @see msi.gama.common.interfaces.IDisplaySurface.OpenGL#selectSeveralAgents(java.util.Collection, int)
	 */
	@Override
	public void selectSeveralAgents(final Collection<IAgent> agents) {

		// animator.pause();
		scope.getGui().asyncRun(new Runnable() {

			@Override
			public void run() {
				if ( menu != null && !menu.isDisposed() ) {
					menu.dispose();
				}
				Control swtControl = renderer.getCanvas();
				DisplayedAgentsMenu menuBuilder = new DisplayedAgentsMenu();
				menu = menuBuilder.getMenu(SWTOpenGLDisplaySurface.this, swtControl, true, true, agents,
					getModelCoordinates(), true);
				menu.setData(IKeyword.USER_LOCATION, getModelCoordinates());
				menu.setLocation(
					swtControl.toDisplay(renderer.camera.getMousePosition().x, renderer.camera.getMousePosition().y));
				menu.addMenuListener(new MenuListener() {

					@Override
					public void menuHidden(final MenuEvent e) {
						animator.resume();
						// animator.resume();
					}

					@Override
					public void menuShown(final MenuEvent e) {
						animator.pause();
					}
				});
				menu.setVisible(true);

				// AD 3/10/13: Fix for Issue 669 on Linux GTK setup. See :
				// http://www.eclipse.org/forums/index.php/t/208284/
				// retryVisible(menu, MAX_RETRIES);
			}
		});

	}

	protected void setDisplayScope(final IScope scope) {
		if ( this.scope != null ) {
			GAMA.releaseScope(this.scope);
		}
		this.scope = scope;
	}

	@Override
	public void dispose() {
		if ( disposed ) { return; }
		disposed = true;
		if ( manager != null ) {
			manager.dispose();
		}
		if ( animator != null && animator.isStarted() ) {
			animator.stop();
		}
		GAMA.releaseScope(getDisplayScope());
		setDisplayScope(null);
	}

	@Override
	public LayeredDisplayData getData() {
		return output.getData();
	}

	/**
	 * Method changed()
	 * @see msi.gama.outputs.LayeredDisplayData.DisplayDataListener#changed(int, boolean)
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
					if ( value ) {// Split layer
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
		GLAutoDrawable drawable = renderer.createDrawable(parent);
		return drawable.getAnimator();
	}

	@Override
	public void layersChanged() {
		renderer.sceneBuffer.layersChanged();

	}

	public void invalidateVisibleRegions() {
		for ( ILayer layer : manager.getItems() ) {
			layer.setVisibleRegion(null);
		}
	}

	/**
	 * Method getFPS()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getFPS()
	 */
	@Override
	public int getFPS() {
		return (int) this.animator.getTotalFPS();
	}

	@Override
	public boolean isRealized() {
		return this.animator != null;
	}

	@Override
	public boolean isRendered() {
		return renderer.sceneBuffer.getSceneToRender().rendered();
	}

}
