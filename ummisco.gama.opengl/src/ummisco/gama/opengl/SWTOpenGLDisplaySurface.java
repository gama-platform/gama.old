/**
 * Created by drogoul, 25 mars 2015
 * 
 */
package ummisco.gama.opengl;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.gui.displays.awt.DisplaySurfaceMenu;
import msi.gama.gui.views.actions.DisplayedAgentsMenu;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.outputs.*;
import msi.gama.outputs.LayeredDisplayData.DisplayDataListener;
import msi.gama.outputs.display.LayerManager;
import msi.gama.outputs.layers.ILayerMouseListener;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Class OpenGLSWTDisplaySurface.
 * 
 * @author drogoul
 * @since 25 mars 2015
 * 
 */
@msi.gama.precompiler.GamlAnnotations.display("opengl")
public class SWTOpenGLDisplaySurface implements IDisplaySurface.OpenGL, DisplayDataListener, GLEventListener {

	static int FRAME_PER_SECOND = 30; // TODO Make it a preference
	final SWTGLAnimator animator;
	final JOGLRenderer renderer;
	protected final AffineTransform translation = new AffineTransform();
	protected volatile boolean canBeUpdated = true;
	protected double zoomIncrement = 0.1;
	protected Double zoomLevel = null;
	protected boolean zoomFit = true;
	private IZoomListener zoomListener;
	protected final Rectangle viewPort = new Rectangle();
	Map<ILayerMouseListener, MouseListener> listeners = new HashMap();
	final LayeredDisplayOutput output;
	final LayerManager manager;
	protected DisplaySurfaceMenu menuManager;
	protected IExpression temp_focus;
	IScope scope;
	// boolean alreadyZooming = false;
	final Composite parent;
	Point size;

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
		manager = new LayerManager(this, output);
		temp_focus = output.getFacet(IKeyword.FOCUS);
		renderer = createRenderer();
		animator = createAnimator();
		animator.start();
	}

	/**
	 * Method getImage()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getImage()
	 */
	@Override
	public BufferedImage getImage() {
		GLAutoDrawable glad = renderer.getDrawable();
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
		if ( !canBeUpdated() ) { return; }
		boolean oldState = getOutput().isPaused();
		if ( force ) {
			getOutput().setPaused(false);
		}
		canBeUpdated(false);
		try {
			if ( output.getData().isAutosave() ) {
				snapshot();
			}
			manager.drawLayersOn(renderer);
		} finally {
			canBeUpdated(true);
		}

		// EXPERIMENTAL

		if ( temp_focus != null ) {
			IShape geometry = Cast.asGeometry(getDisplayScope(), temp_focus.value(getDisplayScope()));
			if ( geometry != null ) {
				temp_focus = null;
				focusOn(geometry);
			}
		}
		if ( force ) {
			getOutput().setPaused(oldState);
		}

	}

	public boolean canBeUpdated() {
		return canBeUpdated /* && graphics != null */;
	}

	/**
	 * Method computeBoundsFrom()
	 * @see msi.gama.common.interfaces.IDisplaySurface#computeBoundsFrom(int, int)
	 */
	@Override
	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		if ( !manager.stayProportional() ) { return new int[] { vwidth, vheight }; }
		final int[] dim = new int[2];
		double widthHeightConstraint = getEnvHeight() / getEnvWidth();
		if ( widthHeightConstraint < 1 ) {
			dim[1] = Math.min(vheight, (int) Math.round(vwidth * widthHeightConstraint));
			dim[0] = Math.min(vwidth, (int) Math.round(dim[1] / widthHeightConstraint));
		} else {
			dim[0] = Math.min(vwidth, (int) Math.round(vheight / widthHeightConstraint));
			dim[1] = Math.min(vheight, (int) Math.round(dim[0] * widthHeightConstraint));
		}
		return dim;
	}

	/**
	 * Method resizeImage()
	 * @see msi.gama.common.interfaces.IDisplaySurface#resizeImage(int, int, boolean)
	 */
	@Override
	public boolean resizeImage(final int x, final int y, final boolean force) {
		if ( !force && x == viewPort.width && y == viewPort.height ) { return true; }
		if ( getWidth() <= 0 && getHeight() <= 0 ) { return false; }
		canBeUpdated(false);
		int[] point = computeBoundsFrom(x, y);
		int imageWidth = Math.max(1, point[0]);
		int imageHeight = Math.max(1, point[1]);
		createNewImage(imageWidth, imageHeight);
		canBeUpdated(true);
		setSize(x, y);
		return true;
	}

	/**
	 * @param imageWidth
	 * @param imageHeight
	 */
	private void createNewImage(final int width, final int height) {
		setDisplayHeight(height);
		setDisplayWidth(width);
	}

	@Override
	public int getDisplayWidth() {
		return viewPort.width;
	}

	protected void setDisplayWidth(final int displayWidth) {
		viewPort.width = displayWidth;
	}

	@Override
	public int getDisplayHeight() {
		return viewPort.height;
	}

	protected void setDisplayHeight(final int displayHeight) {
		viewPort.height = displayHeight;
	}

	/**
	 * Method zoomIn()
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomIn()
	 */
	@Override
	public void zoomIn() {
		// if ( alreadyZooming ) { return; }
		// alreadyZooming = true;
		renderer.camera.zoom(true);
		// alreadyZooming = false;
	}

	/**
	 * Method zoomOut()
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomOut()
	 */
	@Override
	public void zoomOut() {
		// if ( alreadyZooming ) { return; }
		// alreadyZooming = true;
		renderer.camera.zoom(false);
		// alreadyZooming = false;
	}

	/**
	 * Method zoomFit()
	 * @see msi.gama.common.interfaces.IDisplaySurface#zoomFit()
	 */
	@Override
	public void zoomFit() {
		resizeImage(getWidth(), getHeight(), false);
		renderer.frame = 0;
		renderer.camera.zeroVelocity();
		renderer.camera.resetCamera(getEnvWidth(), getEnvHeight(), output.getData().isOutput3D());
		newZoomLevel(1d);
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
	 * Method canBeUpdated()
	 * @see msi.gama.common.interfaces.IDisplaySurface#canBeUpdated(boolean)
	 */
	@Override
	public void canBeUpdated(final boolean ok) {
		this.canBeUpdated = ok;
	}

	/**
	 * Method waitForUpdateAndRun()
	 * @see msi.gama.common.interfaces.IDisplaySurface#waitForUpdateAndRun(java.lang.Runnable)
	 */
	@Override
	public void waitForUpdateAndRun(final Runnable r) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!canBeUpdated()) {
					try {
						Thread.sleep(10);
					} catch (final InterruptedException e) {}
				}
				GuiUtils.run(r);
			}
		}).start();
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
		String snapshotFile =
			FileUtils.constructAbsoluteFilePath(scope, SNAPSHOT_FOLDER_NAME + "/" + GAMA.getModel().getName() +
				"_display_" + output.getName(), false);

		String file =
			snapshotFile + "_size_" + image.getWidth() + "x" + image.getHeight() + "_cycle_" +
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
		return size.x;
	}

	/**
	 * Method getHeight()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getHeight()
	 */
	@Override
	public int getHeight() {
		return size.y;
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

		resizeImage(getWidth(), getHeight(), true);
		if ( zoomFit ) {
			zoomFit();
		}
	}

	/**
	 * Method addMouseListener()
	 * @see msi.gama.common.interfaces.IDisplaySurface#addMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void addMouseListener(final ILayerMouseListener listener) {

		if ( listeners.containsKey(listener) ) { return; }
		MouseListener l = new MouseAdapter() {

			int down_x, down_y;

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

		};
		listeners.put(listener, l);
		renderer.canvas.addMouseListener(l);

	}

	/**
	 * Method removeMouseListener()
	 * @see msi.gama.common.interfaces.IDisplaySurface#removeMouseListener(java.awt.event.MouseListener)
	 */
	@Override
	public void removeMouseListener(final ILayerMouseListener listener) {
		MouseListener l = listeners.get(listener);
		if ( l == null ) { return; }
		listeners.remove(listener);
		renderer.canvas.removeMouseListener(l);
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

	/**
	 * Method setZoomListener()
	 * @see msi.gama.common.interfaces.IDisplaySurface#setZoomListener(msi.gama.common.interfaces.IDisplaySurface.IZoomListener)
	 */
	@Override
	public void setZoomListener(final IZoomListener listener) {
		zoomListener = listener;
	}

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
		Set<IAgent> agents = null;
		IScope s = GAMA.obtainNewScope();
		try {
			agents =
				(Set<IAgent>) GAMA
					.getSimulation()
					.getPopulation()
					.getTopology()
					.getNeighboursOf(s, new GamaPoint(pp.getX(), pp.getY()), renderer.getMaxEnvDim() / 100,
						Different.with());
		} finally {
			GAMA.releaseScope(s);
		}
		return agents;
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
				GuiUtils.asyncRun(new Runnable() {

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
		if ( zoomLevel == null ) {
			zoomLevel = computeInitialZoomLevel();
		}
		return zoomLevel;
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
	public IDisplayOutput getOutput() {
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
	public void selectAgents(final IAgent agent) {
		menuManager.buildMenu(renderer.camera.getMousePosition().x, renderer.camera.getMousePosition().y, agent);
	}

	/**
	 * Method newZoomLevel()
	 * @see msi.gama.common.interfaces.IDisplaySurface.IZoomListener#newZoomLevel(double)
	 */
	@Override
	public void newZoomLevel(final double newZoomLevel) {
		zoomLevel = newZoomLevel;
		if ( zoomListener != null ) {
			zoomListener.newZoomLevel(zoomLevel);
		}
		// animator.getRenderer().initFor(this);
	}

	org.eclipse.swt.widgets.Menu menu;

	/**
	 * Method selectSeveralAgents()
	 * @see msi.gama.common.interfaces.IDisplaySurface.OpenGL#selectSeveralAgents(java.util.Collection, int)
	 */
	@Override
	public void selectSeveralAgents(final Collection<IAgent> agents, final int i) {
		try {
			// animator.pause();
			GuiUtils.asyncRun(new Runnable() {

				@Override
				public void run() {
					if ( menu != null && !menu.isDisposed() ) {
						menu.dispose();
					}
					Control swtControl = renderer.getCanvas();
					DisplayedAgentsMenu menuBuilder = new DisplayedAgentsMenu();
					menu =
						menuBuilder.getMenu(SWTOpenGLDisplaySurface.this, swtControl, true, false, agents,
							getModelCoordinates(), true);
					menu.setData(IKeyword.USER_LOCATION, getModelCoordinates());
					menu.setLocation(swtControl.toDisplay(renderer.camera.getMousePosition().x,
						renderer.camera.getMousePosition().y));
					menu.addMenuListener(new MenuListener() {

						@Override
						public void menuHidden(final MenuEvent e) {
							// animator.resume();
						}

						@Override
						public void menuShown(final MenuEvent e) {
							// animator.pause();
						}
					});
					menu.setVisible(true);

					// AD 3/10/13: Fix for Issue 669 on Linux GTK setup. See :
					// http://www.eclipse.org/forums/index.php/t/208284/
					// retryVisible(menu, MAX_RETRIES);
				}
			});

		} finally {
			// animator.resume();
		}
	}

	protected void setDisplayScope(final IScope scope) {
		if ( this.scope != null ) {
			GAMA.releaseScope(this.scope);
		}
		this.scope = scope;
	}

	@Override
	public void dispose() {
		if ( isDisposed() ) { return; }
		if ( manager != null ) {
			manager.dispose();
		}
		stop();
		renderer.getDrawable().removeGLEventListener(this);
		renderer.dispose();
		GAMA.releaseScope(getDisplayScope());
		setDisplayScope(null);
		// super.dispose();
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
	public void changed(final int property, final boolean value) {
		switch (property) {
			case LayeredDisplayData.CHANGE_CAMERA:
				renderer.switchCamera();
				break;
			case LayeredDisplayData.SPLIT_LAYER:
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
			case LayeredDisplayData.THREED_VIEW:
				// FIXME What is this ???
				break;
			case LayeredDisplayData.CAMERA_POS:
				renderer.updateCameraPosition();

		}

	}

	/**
	 * Method setSize()
	 * @see msi.gama.common.interfaces.IDisplaySurface#setSize(int, int)
	 */
	@Override
	public void setSize(final int x, final int y) {
		size = new Point(x, y);

	}

	/**
	 * Method isDisposed()
	 * @see msi.gama.common.interfaces.IDisplaySurface#isDisposed()
	 */
	@Override
	public boolean isDisposed() {
		return false;
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

	private SWTGLAnimator createAnimator() {
		// System.out.println("Creating animator");
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities cap = new GLCapabilities(profile);
		cap.setStencilBits(8);
		cap.setDoubleBuffered(true);
		cap.setHardwareAccelerated(true);
		GLAutoDrawable drawable = renderer.createDrawable(cap, this);
		SWTGLAnimator animator = new SWTGLAnimator(FRAME_PER_SECOND);
		animator.add(drawable);
		drawable.addGLEventListener(this);
		return animator;
	}

	/**
	 * Method init()
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void init(final GLAutoDrawable drawable) {
		renderer.init(drawable);
	}

	/**
	 * Method dispose()
	 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void dispose(final GLAutoDrawable drawable) {
		renderer.dispose();
	}

	/**
	 * Method display()
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void display(final GLAutoDrawable drawable) {
		// fail fast
		if ( GAMA.getSimulation() == null ) { return; }
		if ( animator != null && animator.isPaused() ) { return; }
		// if ( surface.getOutput().isPaused() ) { return; }
		// if ( GAMA.isPaused() ) { return; }
		// if ( !drawable.isRealized() ) { return; }
		renderer.display(drawable);

	}

	/**
	 * Method reshape()
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	@Override
	public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width, final int height) {
		// fail fast
		if ( width <= 0 || height <= 0 ) { return; }
		renderer.reshape(drawable, x, y, width, height);
	}

	public void start() {
		// System.out.println("ANimator asked to start");
		if ( !animator.isStarted() ) {
			animator.start();
		}
	}

	public void stop() {
		// System.out.println("ANimator asked to stop");
		if ( animator.isStarted() ) {
			animator.stop();
		}
	}

	public void pause() {
		// System.out.println("ANimator asked to pause");
		if ( animator.isAnimating() ) {
			animator.pause();
		}
	}

	public void resume() {
		// System.out.println("ANimator asked to resume");
		if ( !animator.isStarted() ) {
			start();
		} else if ( animator.isPaused() ) {
			animator.resume();

		}
	}

}
