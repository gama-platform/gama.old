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
 * - Benoit Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.jogl;

import java.awt.*;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.awt.AbstractAWTDisplaySurface;
import msi.gama.gui.displays.layers.LayerManager;
import msi.gama.jogl.scene.ModelScene;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.Camera.AbstractCamera;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.ShapeFileReader;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gaml.compilation.ISymbol;
import msi.gaml.species.ISpecies;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.FileDialog;
import org.geotools.data.simple.SimpleFeatureCollection;
import collada.Output3D;
import com.vividsolutions.jts.geom.Envelope;

@display("opengl")
public final class JOGLAWTDisplaySurface extends AbstractAWTDisplaySurface implements IDisplaySurface.OpenGL {

	private static final long serialVersionUID = 1L;
	public PopupMenu agentsMenu = new PopupMenu();

	protected Point mousePosition;
	private ActionListener menuListener;
	private ActionListener focusListener;
	private ActionListener followListener;
	private boolean output3D = false;
	// Environment properties useful to set the camera position.

	// Use to toggle the 3D view.
	public boolean threeD = false; // true; //false;

	// Use to toggle the Picking mode
	public boolean picking = false;

	// Use to toggle the Arcball view
	public boolean arcball = false;

	// Use to toggle the selectRectangle tool
	public boolean selectRectangle = false;

	// Use to toggle the Triangulation view
	public boolean triangulation = false;

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

	public JOGLAWTDisplaySurface(final Object ... args) {
		displayBlock = new Runnable() {

			// Remove all the already existing entity in openGLGraphics and redraw the existing ones.
			@Override
			public void run() {
				// if ( !canBeUpdated() ) { return; }
				canBeUpdated(false);
				final ModelScene s = renderer.getScene();
				if ( s != null ) {
					s.wipe(renderer);

					renderer.setTessellation(getOutput().getTesselation());
					renderer.setAmbientLightValue(getOutput().getAmbientLightColor());
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
	public void initialize(final double env_width, final double env_height, final LayeredDisplayOutput out) {
		GuiUtils.debug("JOGLAWTDisplaySurface1.1.initialize");
		super.initialize(env_width, env_height, out);
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		// Call sun.awt.noerasebackground to reduce the flickering when creating a popup menu,
		// due to AWT erasing the GLCanvas every time before jogl repaint.
		System.setProperty("sun.awt.noerasebackground", "true");

		agentsMenu = new PopupMenu();
		add(agentsMenu);
		renderer = new JOGLAWTGLRenderer(this);
		// renderer.setPolygonTriangulated(false);
		renderer.setTessellation(getOutput().getTesselation());
		renderer.setAmbientLightValue(getOutput().getAmbientLightColor());
		renderer.setPolygonMode(getOutput().getPolygonMode());
		renderer.setCameraPosition(getOutput().getCameraPos());
		renderer.setCameraLookPosition(getOutput().getCameraLookPos());
		renderer.setCameraUpVector(getOutput().getCameraUpVector());

		add(renderer.canvas, BorderLayout.CENTER);
		// openGLGraphicsGLRender.animator.start();
		zoomFit();
		// new way
		// createIGraphics();
		this.setVisible(true);

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				resizeImage(getWidth(), getHeight());
				initOutput3D(out.getOutput3D(), out.getOutput3DNbCycles());
				updateDisplay();
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

	public static class AgentMenuItem extends MenuItem {

		private final IAgent agent;
		private final ILayer display;

		AgentMenuItem(final String name, final IAgent agent, final ILayer display) {
			super(name);
			this.agent = agent;
			this.display = display;
		}

		IAgent getAgent() {
			return agent;
		}

		ILayer getDisplay() {
			return display;
		}
	}

	public class SelectedAgent {

		IAgent macro;
		Map<ISpecies, List<SelectedAgent>> micros;

		void buildMenuItems(final Menu parentMenu, final ILayer display) {
			final Menu macroMenu = new Menu(macro.getName());
			parentMenu.add(macroMenu);

			final MenuItem inspectItem = new AgentMenuItem("Inspect", macro, display);
			inspectItem.addActionListener(menuListener);
			macroMenu.add(inspectItem);

			final MenuItem focusItem = new AgentMenuItem("Focus", macro, display);
			focusItem.addActionListener(focusListener);
			macroMenu.add(focusItem);

			MenuItem followItem = new AgentMenuItem("Follow", macro, display);
			followItem.addActionListener(followListener);
			macroMenu.add(followItem);

			if ( micros != null && !micros.isEmpty() ) {
				final Menu microsMenu = new Menu("Micro agents");
				macroMenu.add(microsMenu);

				Menu microSpecMenu;
				for ( final ISpecies microSpec : micros.keySet() ) {
					microSpecMenu = new Menu("Species " + microSpec.getName());
					microsMenu.add(microSpecMenu);

					for ( final SelectedAgent micro : micros.get(microSpec) ) {
						micro.buildMenuItems(microSpecMenu, display);
					}
				}
			}
		}
	}

	@Override
	public void outputChanged(final double env_width, final double env_height, final LayeredDisplayOutput output) {
		setBackgroundColor(output.getBackgroundColor());
		this.setBackground(getBgColor());
		setEnvWidth(env_width);
		setEnvHeight(env_height);
		widthHeightConstraint = env_height / env_width;
		menuListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final AgentMenuItem source = (AgentMenuItem) e.getSource();
				final IAgent a = source.getAgent();
				if ( a != null ) {
					GuiUtils.setSelectedAgent(a);
				}
			}

		};

		focusListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final AgentMenuItem source = (AgentMenuItem) e.getSource();
				final IAgent a = source.getAgent();
				if ( a != null ) {
					focusOn(a.getGeometry(), source.getDisplay());
				}
			}

		};

		followListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				AgentMenuItem source = (AgentMenuItem) e.getSource();
				IAgent a = source.getAgent();
				if ( a != null ) {
					agent = a;
					followAgent = !followAgent;
					// followAgent(a);
				}
			}

		};

		if ( manager == null ) {
			manager = new LayerManager(this);
			final List<? extends ISymbol> layers = output.getChildren();
			for ( final ISymbol layer : layers ) {
				// IDisplay d =
				manager.addLayer(LayerManager.createLayer((ILayerStatement) layer, env_width, env_height, iGraphics));
				// d.initMenuItems(this);
			}

		} else {
			manager.outputChanged();
		}
		// paintingNeeded.release();
	}

	@Override
	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		// we take the smallest dimension as a guide
		final int[] dim = new int[2];
		dim[0] = vwidth > vheight ? (int) (vheight / widthHeightConstraint) : vwidth;
		dim[1] = vwidth <= vheight ? (int) (vwidth * widthHeightConstraint) : vheight;
		return dim;
	}

	public void selectAgents(final int x, final int y, final IAgent agent, final int layerId) {

		agentsMenu.removeAll();
		agentsMenu.setLabel("Layers");

		final java.awt.Menu m = new java.awt.Menu(manager.getItems().get(layerId).getName());
		final SelectedAgent sa = new SelectedAgent();
		sa.macro = agent;
		sa.buildMenuItems(m, manager.getItems().get(layerId));

		agentsMenu.add(m);
		agentsMenu.show(this, renderer.camera.mousePosition.x, renderer.camera.mousePosition.y);

	}

	public void showAgentMonitor(final IAgent agent) {

	}

	@Override
	public void forceUpdateDisplay() {
		updateDisplay();
	}

	public void drawDisplaysWithoutRepainting() {
		if ( iGraphics == null ) { return; }
		ex[0] = null;
		manager.drawLayersOn(iGraphics);
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		redrawNavigator();
	}

	@Override
	public void dispose() {
		renderer.dispose();
		if ( manager != null ) {
			manager.dispose();
		}
		if ( navigator == null || navigator.isDisposed() ) { return; }
		navigator.dispose();

	}

	@Override
	public BufferedImage getImage() {
		final BufferedImage buffImage = renderer.getScreenShot();
		return buffImage;
	}

	@Override
	public void zoomIn() {
		float incrementalZoomStep;
		// Check if Z is not equal to 0 (avoid being block on z=0)
		// renderer.camera.getPosition().setZ(renderer.camera.getPosition().getZ() - incrementalZoomStep);
		// // renderer.camera.getTarget().setZ(renderer.camera.getTarget().getZ() - incrementalZoomStep);
		// renderer.camera.setRadius(renderer.camera.getPosition().getZ() - incrementalZoomStep);
		// // FIXME Approximate
		// resizeImage((int) (getWidth() * zoomLevel), (int) (getHeight() * zoomLevel));
		// // setZoomLevel(zoomLevel + zoomLevel * 0.1);
		// // updateDisplay();
		// zoomFit = false;
		if ( !this.switchCamera ) {
			if ( renderer.camera.getPosition().getZ() != 0 ) {
				incrementalZoomStep = (float) renderer.camera.getRadius() / 10;
			} else {
				incrementalZoomStep = 0.1f;
			}
			renderer.camera.setRadius(renderer.camera.getRadius() - incrementalZoomStep);
			renderer.camera.rotation();
			setZoomLevel(renderer.camera.getMaxDim() * AbstractCamera.INIT_Z_FACTOR / renderer.camera.getRadius());

		} else {
			if ( renderer.camera.getPosition().getZ() != 0 ) {
				incrementalZoomStep = (float) renderer.camera.getPosition().getZ() / 10;
			} else {
				incrementalZoomStep = 0.1f;
			}
			renderer.camera.setPosition(renderer.camera.getPosition().add(
				renderer.camera.getForward().scalarMultiply(renderer.camera.getSpeed() * 800))); // on recule
			renderer.camera.setTarget(renderer.camera.getForward().add(renderer.camera.getPosition().getX(),
				renderer.camera.getPosition().getY(), renderer.camera.getPosition().getZ()));
			setZoomLevel(renderer.camera.getMaxDim() * AbstractCamera.INIT_Z_FACTOR /
				renderer.camera.getPosition().getZ());
		}

	}

	@Override
	public void zoomOut() {
		float incrementalZoomStep;
		// Check if Z is not equal to 0 (avoid being block on z=0)
		// renderer.camera.getPosition().setZ(renderer.camera.getPosition().getZ() + incrementalZoomStep);
		// renderer.camera.getTarget().setZ(renderer.camera.getTarget().getZ() + incrementalZoomStep);
		// // FIXME Approximate
		// resizeImage((int) (getWidth() * zoomLevel), (int) (getHeight() * zoomLevel));
		// // updateDisplay();
		// zoomFit = false;
		if ( !this.switchCamera ) {
			if ( renderer.camera.getPosition().getZ() != 0 ) {
				incrementalZoomStep = (float) renderer.camera.getRadius() / 10;
			} else {
				incrementalZoomStep = 0.1f;
			}
			renderer.camera.setRadius(renderer.camera.getRadius() + incrementalZoomStep);
			renderer.camera.rotation();
			setZoomLevel(renderer.camera.getMaxDim() * AbstractCamera.INIT_Z_FACTOR / renderer.camera.getRadius());

		} else {
			if ( renderer.camera.getPosition().getZ() != 0 ) {
				incrementalZoomStep = (float) renderer.camera.getPosition().getZ() / 10;
			} else {
				incrementalZoomStep = 0.1f;
			}
			renderer.camera.setPosition(renderer.camera.getPosition().subtract(
				renderer.camera.getForward().scalarMultiply(renderer.camera.getSpeed() * 800))); // on recule
			renderer.camera.setTarget(renderer.camera.getForward().add(renderer.camera.getPosition().getX(),
				renderer.camera.getPosition().getY(), renderer.camera.getPosition().getZ()));
			setZoomLevel(renderer.camera.getMaxDim() * AbstractCamera.INIT_Z_FACTOR /
				renderer.camera.getPosition().getZ());

		}
	}

	@Override
	public void zoomFit() {
		resizeImage(getWidth(), getHeight());
		renderer.frame = 0;
		if ( renderer != null ) {
			super.zoomFit();
			if ( threeD ) {
				renderer.camera.initialize3DCamera(getEnvWidth(), getEnvHeight());

			} else {
				renderer.camera.initializeCamera(getEnvWidth(), getEnvHeight());
			}
		}
	}

	@Override
	public void toggleView() {
		threeD = !threeD;
		zoomFit();
		// followAgent = !followAgent;
		updateDisplay();
	}

	@Override
	public void togglePicking() {
		picking = !picking;
		if(!picking){
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		else{
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}

	}

	@Override
	public void toggleArcball() {
		arcball = !arcball;
	}

	@Override
	public void toggleSelectRectangle() {
		selectRectangle = !selectRectangle;
		if ( selectRectangle && !renderer.camera.IsViewIn2DPlan() ) {
			zoomFit();
		}

	}

	@Override
	public void toggleTriangulation() {
		triangulation = !triangulation;
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

	/**
	 * Add a simple feature collection from a .Shp file.
	 */
	@Override
	public void addShapeFile() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						final Shell shell = new Shell(Display.getDefault());
						final FileDialog dialog = new FileDialog(shell, SWT.OPEN);

						dialog.setText("Browse for a .shp file");

						dialog.setFilterPath(System.getProperty(GAMA.getModel().getProjectPath()));

						dialog.setFilterExtensions(new String[] { "*.shp" });

						if ( dialog.open() != null ) {

							final String path = dialog.getFilterPath();

							final String[] names = dialog.getFileNames();

							for ( int i = 0; i < names.length; i++ ) {
								shapeFileName[i] = path + "/" + names[i];
								System.out.println(shapeFileName[i]);
							}

						}

						renderer.myShapeFileReader = new ShapeFileReader(shapeFileName[0]);
						final SimpleFeatureCollection myCollection =
							renderer.myShapeFileReader
								.getFeatureCollectionFromShapeFile(renderer.myShapeFileReader.store);
						final Color color =
							new Color((int) (Math.random() * 255), (int) (Math.random() * 255),
								(int) (Math.random() * 255));
						renderer.getScene().addCollections(myCollection, color);
						// FIXME: Need to reinitialise th displaylist

					}
				});
			}
		}).start();

	}

	//
	// @Override
	// public IGraphics.OpenGL getIGraphics() {
	// return (IGraphics.OpenGL) super.getIGraphics();
	// }

	@Override
	public void focusOn(final IShape geometry, final ILayer display) {

		// this.openGLGraphicsGLRender.camera.PrintParam();

		final Envelope env = geometry.getEnvelope();

		final double xPos = geometry.getLocation().getX();
		final double yPos = -(geometry.getLocation().getY());

		// FIXME: Need to compute the depth of the shape to adjust ZPos value.
		// FIXME: Problem when the geometry is a point how to determine the maxExtent of the shape?
		final double zPos = env.maxExtent() * 2 + geometry.getLocation().getZ() + this.renderer.env_width/100;
		final double zLPos = -(env.maxExtent() * 2);
		if ( !this.switchCamera ) {
			renderer.camera.setRadius(zPos);
			renderer.camera.rotation();
		}
		this.renderer.camera.updatePosition(xPos, yPos, zPos);
		this.renderer.camera.lookPosition(xPos, yPos, zLPos);
	}

	public void followAgent(final IAgent a) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						ILocation l = agent.getGeometry().getLocation();
						Envelope env = agent.getGeometry().getEnvelope();

						double xPos = l.getX() - getEnvWidth() / 2;
						double yPos = -(l.getY() - getEnvHeight() / 2);

						double zPos = env.maxExtent() * 2 + l.getZ();
						double zLPos = -(env.maxExtent() * 2);

						renderer.camera.updatePosition(xPos, yPos, zPos);
						renderer.camera.lookPosition(xPos, yPos, zLPos);
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
	public void snapshot() {
		GAMA.run(new InScope.Void() {

			@Override
			public void process(final IScope scope) {
				save(scope, getImage());
			}
		});

	}

	@Override
	public synchronized void addMouseListener(final MouseListener e) {
		renderer.canvas.addMouseListener(e);
	}

	public Color getBgColor() {
		return bgColor;
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
	public final boolean resizeImage(final int x, final int y) {
		super.resizeImage(x, y);
		setSize(x, y);
		return true;
	}

}
