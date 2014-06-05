/*********************************************************************************************
 * 
 * 
 * 'JOGLAWTDisplaySurface.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.awt.AbstractAWTDisplaySurface;
import msi.gama.jogl.scene.ModelScene;
import msi.gama.jogl.utils.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.runtime.*;
import msi.gama.util.*;
import msi.gaml.operators.Cast;
import collada.Output3D;
import com.vividsolutions.jts.geom.Envelope;

@display("opengl")
public final class JOGLAWTDisplaySurface extends AbstractAWTDisplaySurface implements IDisplaySurface.OpenGL {

	private static final long serialVersionUID = 1L;

	protected Point mousePosition;

	private boolean output3D = false;
	// Environment properties useful to set the camera position.

	// Use to toggle the 3D view.
	public boolean threeD = false; // true; //false;

	// Use to toggle the Picking mode
	// private boolean picking = false;

	// Use to toggle the Arcball view
	public boolean arcball = false;

	// Use to toggle the selectRectangle tool
	public boolean selectRectangle = false;

	// Use to toggle the SplitLayer view
	public boolean splitLayer = false;

	// Us toggle to switch cameras
	public boolean switchCamera = false;

	// Use to toggle the Rotation view
	public boolean rotation = false;

	// Used to follow an agent
	public boolean followAgent = false;
	public IAgent agent;

	// Use to draw .shp file
	final String[] shapeFileName = new String[1];

	// private (return the renderer of the openGLGraphics)
	private JOGLAWTGLRenderer renderer;

	// private: the class of the Output3D manager
	Output3D output3DManager;

	// USe to get the EventLayer mouselistener
	private MouseListener eventMouse;

	public JOGLAWTDisplaySurface(final Object ... args) {
		displayBlock = new Runnable() {

			// Remove all the already existing entity in openGLGraphics and redraw the existing ones.
			@Override
			public void run() {
				final ModelScene s = renderer.getScene();
				if ( s != null ) {
					s.wipe(getOutput().getTraceDisplay());
					// FIXME: Why setting this at each run??
					renderer.setTessellation(getOutput().getTesselation());
					renderer.setShowFPS(getOutput().getShowFPS());
					// renderer.setTraceDisplay(getOutput().getTraceDisplay());
					// renderer.setDrawEnv(getOutput().getDrawEnv());
					renderer.setAmbientLightValue(getOutput().getAmbientLightColor());
					renderer.setDiffuseLightValue(getOutput().getDiffuseLightColor());
					renderer.setDiffuseLightPosition(getOutput().getDiffuseLightPosition());
					renderer.setPolygonMode(getOutput().getPolygonMode());
					renderer.setCameraPosition(getOutput().getCameraPos());
					renderer.setCameraLookPosition(getOutput().getCameraLookPos());
					renderer.setCameraUpVector(getOutput().getCameraUpVector());
					if ( autosave ) {
						snapshot();
					}

					drawDisplaysWithoutRepainting();

					if ( output3D ) {
						output3DManager.updateOutput3D(renderer);
					}

				}
				canBeUpdated(true);
			}
		};

	}

	@Override
	public void updateDisplay() {
		super.updateDisplay();
		// EXPERIMENTAL

		if ( temp_focus != null ) {
			IShape geometry = GAMA.run(new GAMA.InScope<IShape>() {

				@Override
				public IShape run(final IScope scope) {
					return Cast.asGeometry(scope, temp_focus.value(scope));
				}
			});
			if ( geometry != null ) {
				temp_focus = null;
				canBeUpdated(true);
				focusOn(geometry);
			}

			// canBeUpdated(false);
		}

		// EXPERIMENTAL
	}

	@Override
	public void initialize(final IScope scope, final double env_width, final double env_height,
		final LayeredDisplayOutput out) {
		super.initialize(scope, env_width, env_height, out);

		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		// Call sun.awt.noerasebackground to reduce the flickering when creating a popup menu,
		// due to AWT erasing the GLCanvas every time before jogl repaint.
		System.setProperty("sun.awt.noerasebackground", "true");
		renderer = new JOGLAWTGLRenderer(this);
		renderer.setAntiAliasing(getQualityRendering());
		// renderer.setPolygonTriangulated(false);
		renderer.setTessellation(getOutput().getTesselation());
		renderer.setZFighting(getOutput().getZFighting());
		renderer.setDrawNorm(getOutput().getDrawNorm());
		renderer.setCubeDisplay(getOutput().getCubeDisplay());
		renderer.setOrtho(getOutput().getOrtho());
		renderer.setShowFPS(getOutput().getShowFPS());
		// renderer.setTraceDisplay(getOutput().getTraceDisplay());
		renderer.setDrawEnv(getOutput().getDrawEnv());
		renderer.setDrawDiffuseLight(getOutput().getDrawDiffuseLight());
		renderer.setIsLightOn(getOutput().getIsLightOn());
		renderer.setAmbientLightValue(getOutput().getAmbientLightColor());
		renderer.setDiffuseLightValue(getOutput().getDiffuseLightColor());
		renderer.setDiffuseLightPosition(getOutput().getDiffuseLightPosition());
		renderer.setPolygonMode(getOutput().getPolygonMode());
		renderer.setCameraPosition(getOutput().getCameraPos());
		renderer.setCameraLookPosition(getOutput().getCameraLookPos());
		renderer.setCameraUpVector(getOutput().getCameraUpVector());
		add(renderer.canvas, BorderLayout.CENTER);
		zoomFit();
		this.setVisible(true);

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				if ( renderer != null && renderer.canvas != null ) {
					renderer.canvas.setSize(getWidth(), getHeight());
				}
				initOutput3D(out.getOutput3D(), out.getOutput3DNbCycles());
				// updateDisplay();
				previousPanelSize = getSize();
			}
		});
		renderer.animator.start();
	}

	@Override
	protected void createIGraphics() {
		if ( iGraphics == null ) {
			iGraphics = new JOGLAWTDisplayGraphics(this, renderer);
		}
	}

	@Override
	public void setPaused(final boolean flag) {
		if ( flag == true ) {
			if ( renderer.animator.isAnimating() ) {
				renderer.animator.stop();
			}
		} else {
			if ( !renderer.animator.isAnimating() ) {
				renderer.animator.start();
			}
		}
		super.setPaused(flag);
	}

	@Override
	public void outputChanged(final IScope scope, final double env_width, final double env_height,
		final LayeredDisplayOutput output) {
		super.outputChanged(scope, env_width, env_height, output);
		setBackgroundColor(output.getBackgroundColor());
		this.setBackground(getBackgroundColor());
	}

	@Override
	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		// we take the smallest dimension as a guide
		final int[] dim = new int[2];
		dim[0] = vwidth > vheight ? (int) (vheight / widthHeightConstraint) : vwidth;
		dim[1] = vwidth <= vheight ? (int) (vwidth * widthHeightConstraint) : vheight;
		return dim;
	}

	public void selectAgents(final IAgent agent) {
		menuManager.buildMenu(renderer.camera.getMousePosition().x, renderer.camera.getMousePosition().y, agent);
	}

	public void selectSeveralAgents(final Collection<IAgent> agents, final int layerId) {
		menuManager.buildMenu(false, renderer.camera.getMousePosition().x, renderer.camera.getMousePosition().y,
			getModelCoordinates(), agents);
	}

	@Override
	public void forceUpdateDisplay() {
		updateDisplay();
	}

	public void drawDisplaysWithoutRepainting() {
		if ( iGraphics == null ) { return; }
		// ex[0] = null;
		manager.drawLayersOn(iGraphics);
	}

	@Override
	public void dispose() {
		renderer.dispose();
		if ( manager != null ) {
			manager.dispose();
		}
		GAMA.releaseScope(scope);
		scope = null;
	}

	@Override
	public void zoomIn() {
		renderer.camera.zoom(true);
	}

	@Override
	public void zoomOut() {
		renderer.camera.zoom(false);
	}

	@Override
	public void zoomFit() {
		resizeImage(getWidth(), getHeight());
		if ( renderer != null ) {
			renderer.frame = 0;
			renderer.camera.zeroVelocity();
			renderer.camera.resetCamera(getEnvWidth(), getEnvHeight(), threeD);
		}
		super.zoomFit();
	}

	@Override
	public void setZoomLevel(final Double newZoomLevel) {
		super.setZoomLevel(newZoomLevel);
		if ( iGraphics != null ) {
			((JOGLAWTDisplayGraphics) iGraphics).reinitFor(this);
		}
	}

	@Override
	public void toggleView() {
		threeD = !threeD;
		zoomFit();
		updateDisplay();
	}

	@Override
	public void togglePicking() {
		renderer.setPicking(!renderer.isPicking());
		renderer.camera.zeroVelocity();
		if ( !renderer.isPicking() ) {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		} else {
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
	}

	@Override
	public void toggleArcball() {
		arcball = !arcball;
	}

	@Override
	public void toggleInertia() {
		renderer.setInertia(!renderer.getInertia());
	}

	@Override
	public void toggleSelectRectangle() {
		selectRectangle = !selectRectangle;
		if ( selectRectangle && !renderer.camera.isViewIn2DPlan() ) {
			zoomFit();
		}

	}

	@Override
	public void toggleTriangulation() {
		renderer.triangulation = !renderer.triangulation;
		updateDisplay();
	}

	@Override
	public void toggleSplitLayer() {

		splitLayer = !splitLayer;
		final int nbLayers = this.getManager().getItems().size();
		int i = 0;
		final Iterator<ILayer> it = this.getManager().getItems().iterator();
		while (it.hasNext()) {
			final ILayer curLayer = it.next();
			if ( splitLayer ) {// Split layer
				curLayer.setElevation((double) i / nbLayers);
			} else {// put all the layer at zero
				curLayer.setElevation(0.0);
			}
			i++;
		}
		this.updateDisplay();
	}

	@Override
	public void toggleRotation() {
		rotation = !rotation;
	}

	@Override
	public void toggleCamera() {
		// TODO Auto-generated method stub
		switchCamera = !switchCamera;
		renderer.switchCamera();
		zoomFit();
		updateDisplay();
	}

	@Override
	public void focusOn(final IShape geometry) {
		// FIXME: Need to compute the depth of the shape to adjust ZPos value.
		// FIXME: Problem when the geometry is a point how to determine the maxExtent of the shape?
		// FIXME: Problem when an agent is placed on a layer with a z_value how to get this z_layer value to offset it?
		ILocation p = geometry.getLocation();
		renderer.camera.zoomFocus(p.getX(), p.getY(), p.getZ(), geometry.getEnvelope().maxExtent());
	}

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

	@Override
	public void initOutput3D(final boolean yes, final ILocation output3DNbCycles) {
		output3D = yes;
		if ( output3D ) {
			output3DManager = new Output3D(output3DNbCycles, renderer);
			// (new Output3D()).to3DGLGEModel(((JOGLAWTDisplayGraphics) openGLGraphics).myJTSGeometries,
			// openGLGraphicsGLRender);
		}
	}

	@Override
	public synchronized void addMouseListener(final MouseListener e) {
		setEventMouse(e);
	}

	@Override
	public synchronized void removeMouseListener(final MouseListener e) {
		renderer.canvas.removeMouseListener(e);
	}

	@Override
	public synchronized void addMouseMotionListener(final MouseMotionListener e) {
		renderer.canvas.addMouseMotionListener(e);
	}

	public Color getBackgroundColor() {
		return bgColor;
	}

	@Override
	public final boolean resizeImage(final int x, final int y) {
		super.resizeImage(x, y);
		// int[] point = computeBoundsFrom(x, y);
		// int imageWidth = Math.max(1, point[0]);
		// int imageHeight = Math.max(1, point[1]);
		// this.createNewImage(imageWidth, imageHeight);
		// createIGraphics();
		setSize(x, y);
		return true;
	}

	/**
	 * Method getModelCoordinates()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinates()
	 */
	@Override
	public GamaPoint getModelCoordinates() {
		Point mp = renderer.camera.getMousePosition();
		if ( mp == null ) { return null; }
		GamaPoint p = GLUtil.getRealWorldPointFromWindowPoint(renderer, mp);
		if ( p == null ) { return null; }
		return new GamaPoint(p.x, -p.y);
	}

	/**
	 * Method getCameraPosition()
	 * @see msi.gama.common.interfaces.IDisplaySurface.OpenGL#getCameraPosition()
	 */
	@Override
	public GamaPoint getCameraPosition() {
		if ( renderer == null && renderer.camera == null ) { return new GamaPoint(0, 0, 0); }
		return renderer.camera.getPosition();
	}

	/**
	 * Method computeInitialZoomLevel()
	 * @see msi.gama.gui.displays.awt.AbstractAWTDisplaySurface#computeInitialZoomLevel()
	 */
	@Override
	protected Double computeInitialZoomLevel() {
		if ( renderer == null && renderer.camera == null ) { return 1.0; }
		return renderer.camera.zoomLevel();
	}

	@Override
	public int getDisplayWidth() {
		return (int) (super.getDisplayWidth() * getZoomLevel());
	}

	@Override
	public int getDisplayHeight() {
		return (int) (super.getDisplayHeight() * getZoomLevel());
	}

	@Override
	public void setBackgroundColor(final Color c) {
		super.setBackgroundColor(c);
		if ( iGraphics != null ) {
			iGraphics.fillBackground(bgColor, 1);
		}
	}

	public MouseListener getEventMouse() {
		return eventMouse;
	}

	public void setEventMouse(final MouseListener eventMouse) {
		this.eventMouse = eventMouse;
	}

	@Override
	public GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final Point sizeInPixels,
		final Point positionInPixels) {
		// TODO Auto-generated method stub
		Point mp = new Point(xOnScreen, yOnScreen);
		GamaPoint p = GLUtil.getRealWorldPointFromWindowPoint(renderer, mp);
		return new GamaPoint(p.x, -p.y);

	}

	@Override
	public IList<IAgent> selectAgent(final int x, final int y) {
		final GamaPoint pp = getModelCoordinatesFrom(x, y, null, null);
		Set<IAgent> agents = null;
		IScope s = GAMA.obtainNewScope();
		try {
			agents =
				(Set<IAgent>) GAMA
					.getSimulation()
					.getPopulation()
					.getTopology()
					.getNeighboursOf(s, new GamaPoint(pp.x, pp.y), this.renderer.getMaxEnvDim() / 100, Different.with());
		} finally {
			GAMA.releaseScope(s);
		}
		return new GamaList<IAgent>(agents);
	}
}
