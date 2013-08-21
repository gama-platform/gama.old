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
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.awt.AbstractSWTDisplaySurface;
import msi.gama.gui.displays.layers.LayerManager;
import msi.gama.jogl.scene.ModelSceneSWT;
import msi.gama.jogl.utils.JOGLSWTGLRenderer;
import msi.gama.jogl.utils.Camera.AbstractCamera;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.ShapeFileReader;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.*;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gaml.compilation.ISymbol;
import msi.gaml.species.ISpecies;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.geotools.data.simple.SimpleFeatureCollection;
import collada.Output3DSWT;
import com.vividsolutions.jts.geom.Envelope;

@display("swt")
public final class JOGLSWTDisplaySurface extends AbstractSWTDisplaySurface implements IDisplaySurface.OpenGL {

	public Menu agentsMenu;

	protected Point mousePosition;
	private SelectionListener menuListener;
	private SelectionListener focusListener;
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

	// Used to switch between cameras (if true switch to FreeFlyCamera)
	public boolean switchCamera = false;

	// Use to draw .shp file
	final String[] shapeFileName = new String[1];

	// private (return the renderer of the openGLGraphics)
	public JOGLSWTGLRenderer renderer;

	// private: the class of the Output3D manager
	Output3DSWT output3DManager;

	public IDisplayOutput output;

	public JOGLSWTDisplaySurface(final Object ... args) {
		this((Composite) args[0], (Integer) args[1]);
	}

	public JOGLSWTDisplaySurface(final Composite parent, final int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
		displayBlock = new Runnable() {

			// Remove all the already existing entity in openGLGraphics and redraw the existing ones.
			@Override
			public void run() {
				if ( !canBeUpdated() ) { return; }
				canBeUpdated(false);
				ModelSceneSWT s = renderer.getScene();
				if ( s != null ) {
					s.wipe(renderer);

					// renderer.setPolygonTriangulated(false);
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
					paintingNeeded.release();
					canBeUpdated(true);
					if ( output3D ) {
						output3DManager.updateOutput3D(renderer);
					}

					// Toolkit.getDefaultToolkit().sync();
				}
			}
		};
	}

	@Override
	public void initialize(final double env_width, final double env_height, final LayeredDisplayOutput out) {
		super.initialize(env_width, env_height, out);
		agentsMenu = new Menu(this);
		renderer = new JOGLSWTGLRenderer(this);
		renderer.setTessellation(getOutput().getTesselation());
		renderer.setAmbientLightValue(getOutput().getAmbientLightColor());
		renderer.setPolygonMode(getOutput().getPolygonMode());
		renderer.setCameraPosition(getOutput().getCameraPos());
		renderer.setCameraLookPosition(getOutput().getCameraLookPos());
		renderer.setCameraUpVector(getOutput().getCameraUpVector());

		this.output = out;

		// add(renderer.canvas, BorderLayout.CENTER);
		// openGLGraphicsGLRender.animator.start();
		// zoomFit();
		// new way
		// createIGraphics();

		renderer.canvas.addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				GuiUtils.debug("JOGLSWTDisplaySurface.componentResized: " + out.getId());
				// if ( buffImage == null ) {
				// // zoomFit();
				// if ( resizeImage(getWidth(), getHeight()) ) {
				// centerImage();
				// }
				// } else {
				// if ( isFullImageInPanel() ) {
				// centerImage();
				// } else if ( isImageEdgeInPanel() ) {
				// scaleOrigin();
				// }
				// }
				if ( zoomFit ) {
					zoomFit();
				}
				resizeImage(getWidth(), getHeight());
				initOutput3D(out.getOutput3D(), out.getOutput3DNbCycles());
				updateDisplay();
				previousPanelSize = new Dimension(getSize().x, getSize().y);
			}

		});
	}

	@Override
	protected void createIGraphics() {
		if ( iGraphics == null ) {
			iGraphics = new JOGLSWTDisplayGraphics(this, renderer);
			// renderer = g.getMyGLRender();
			// add(renderer.canvas, BorderLayout.CENTER);
			// zoomFit();
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

	static class AgentMenuItem extends MenuItem {

		private final IAgent agent;
		private final ILayer display;

		AgentMenuItem(final Menu parentMenu, final String name, final IAgent agent, final ILayer display,
			final int style) {
			super(parentMenu, style);
			this.agent = agent;
			this.display = display;
		}

		IAgent getAgent() {
			return agent;
		}

		ILayer getDisplay2() {
			return this.display;
		}

		@Override
		protected void checkSubclass() {}
	}

	public class SelectedAgent {

		IAgent macro;
		Map<ISpecies, List<SelectedAgent>> micros;

		void buildMenuItems(final Menu parentMenu, final ILayer display, final MenuItem mItem) {
			MenuItem macroMenuHeader = new MenuItem(parentMenu, SWT.CASCADE);
			macroMenuHeader.setText(macro.getName());

			Menu macroMenu = new Menu(macroMenuHeader);
			macroMenu.setOrientation(SWT.LEFT_TO_RIGHT);

			MenuItem inspectItem = new AgentMenuItem(macroMenu, "Inspect", macro, display, SWT.PUSH);
			inspectItem.setText("Inspect");
			inspectItem.addSelectionListener(menuListener);

			MenuItem focusItem = new AgentMenuItem(macroMenu, "Focus", macro, display, SWT.PUSH);
			focusItem.setText("Focus");
			focusItem.addSelectionListener(focusListener);

			macroMenuHeader.setMenu(macroMenu);
			if ( micros != null && !micros.isEmpty() ) {
				MenuItem microsMenuHeader = new MenuItem(macroMenu, SWT.CASCADE);
				microsMenuHeader.setText("Micro agents");

				Menu microsMenu = new Menu(microsMenuHeader);
				microsMenu.setOrientation(SWT.LEFT_TO_RIGHT);

				MenuItem microSpecMenuHeader;
				for ( ISpecies microSpec : micros.keySet() ) {
					microSpecMenuHeader = new MenuItem(microsMenu, SWT.CASCADE);
					microSpecMenuHeader.setText("Species " + microSpec.getName());

					Menu microSpecMenu = new Menu(microSpecMenuHeader);

					for ( SelectedAgent micro : micros.get(microSpec) ) {
						micro.buildMenuItems(microSpecMenu, display, mItem);
					}
				}
			}
		}
	}

	@Override
	public void outputChanged(final double env_width, final double env_height, final LayeredDisplayOutput output) {
		setBackgroundColor(output.getBackgroundColor());
		this.setBackgroundColor(getBgColor());
		setEnvWidth(env_width);
		setEnvHeight(env_height);
		widthHeightConstraint = env_height / env_width;
		menuListener = new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				AgentMenuItem source = (AgentMenuItem) e.getSource();
				IAgent a = source.getAgent();
				if ( a != null ) {
					GuiUtils.setSelectedAgent(a);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub
				AgentMenuItem source = (AgentMenuItem) e.getSource();
				IAgent a = source.getAgent();
				if ( a != null ) {
					GuiUtils.setSelectedAgent(a);
				}
			}
		};

		focusListener = new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				AgentMenuItem source = (AgentMenuItem) e.getSource();
				IAgent a = source.getAgent();
				if ( a != null ) {
					focusOn(a.getGeometry(), source.getDisplay2());
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub
				AgentMenuItem source = (AgentMenuItem) e.getSource();
				IAgent a = source.getAgent();
				if ( a != null ) {
					focusOn(a.getGeometry(), source.getDisplay2());
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
		paintingNeeded.release();
	}

	@Override
	public int[] computeBoundsFrom(final int vwidth, final int vheight) {
		// we take the smallest dimension as a guide
		int[] dim = new int[2];
		dim[0] = vwidth > vheight ? (int) (vheight / widthHeightConstraint) : vwidth;
		dim[1] = vwidth <= vheight ? (int) (vwidth * widthHeightConstraint) : vheight;
		return dim;
	}

	public void selectAgents(final int x, final int y, final IAgent agent, final int layerId) {

		Runnable r = new Runnable() {

			@Override
			public void run() {
				agentsMenu.setOrientation(SWT.LEFT_TO_RIGHT);
				Menu m = new Menu(agentsMenu);
				m.setOrientation(SWT.LEFT_TO_RIGHT);
				MenuItem mHeader = new MenuItem(agentsMenu, SWT.CASCADE);
				mHeader.setText(manager.getItems().get(layerId).getName());
				mHeader.setMenu(m);
				SelectedAgent sa = new SelectedAgent();
				sa.macro = agent;
				sa.buildMenuItems(m, manager.getItems().get(layerId), mHeader);
				agentsMenu.setVisible(true);
				while (!agentsMenu.isDisposed() && agentsMenu.isVisible()) {
					if ( !Display.getDefault().readAndDispatch() ) {
						Display.getDefault().sleep();
					}
				}
			}
		};

		if ( Display.getCurrent() != null ) {
			r.run();
		} else {
			Display.getDefault().asyncExec(r);
		}

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
	public void dispose() {
		// GuiUtils.debug("JOGLAWTDisplaySurface.dispose: " + getOutputName());
		// SwingUtilities.invokeLater(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// }
		// });
		// remove(renderer.canvas);
		renderer.dispose();
		if ( manager != null ) {
			manager.dispose();
		}
		if ( navigator == null || navigator.isDisposed() ) { return; }
		navigator.dispose();

	}

	@Override
	public BufferedImage getImage() {
		BufferedImage buffImage = renderer.getScreenShot();
		return buffImage;
	}

	@Override
	public void zoomIn() {
		float incrementalZoomStep;
		// Check if Z is not equal to 0 (avoid being block on z=0)
		if ( renderer.camera.getPosition().getZ() != 0 ) {
			incrementalZoomStep = (float) renderer.camera.getPosition().getZ() / 10;
		} else {
			incrementalZoomStep = 0.1f;
		}
		renderer.camera.getPosition().setZ(renderer.camera.getPosition().getZ() - incrementalZoomStep);
		renderer.camera.getTarget().setZ(renderer.camera.getTarget().getZ() - incrementalZoomStep);
		setZoomLevel(renderer.camera.getMaxDim() * AbstractCamera.INIT_Z_FACTOR / renderer.camera.getPosition().getZ());
		// FIXME Approximate
		resizeImage((int) (getWidth() * zoomLevel), (int) (getHeight() * zoomLevel));
		// setZoomLevel(zoomLevel + zoomLevel * 0.1);
		// updateDisplay();
		zoomFit = false;
	}

	@Override
	public void zoomOut() {
		float incrementalZoomStep;
		// Check if Z is not equal to 0 (avoid being block on z=0)
		if ( renderer.camera.getPosition().getZ() != 0 ) {
			incrementalZoomStep = (float) renderer.camera.getPosition().getZ() / 10;
		} else {
			incrementalZoomStep = 0.1f;
		}
		renderer.camera.getPosition().setZ(renderer.camera.getPosition().getZ() + incrementalZoomStep);
		renderer.camera.getTarget().setZ(renderer.camera.getTarget().getZ() + incrementalZoomStep);
		setZoomLevel(renderer.camera.getMaxDim() * AbstractCamera.INIT_Z_FACTOR / renderer.camera.getPosition().getZ());
		// FIXME Approximate
		resizeImage((int) (getWidth() * zoomLevel), (int) (getHeight() * zoomLevel));
		// updateDisplay();
		zoomFit = false;
	}

	@Override
	public void zoomFit() {
		resizeImage(getWidth(), getHeight());
		if ( renderer != null ) {
			super.zoomFit();
			if ( threeD ) {
				renderer.camera.initialize3DCamera(getEnvWidth(), getEnvHeight());
				renderer.reset();
			} else {
				renderer.camera.initializeCamera(getEnvWidth(), getEnvHeight());
				renderer.reset();
			}
		}
		// updateDisplay();
	}

	@Override
	public void toggleView() {
		threeD = !threeD;
		zoomFit();
	}

	@Override
	public void togglePicking() {

		if ( picking == false ) {
			threeD = false;
			zoomFit();
		}
		// FIXME: need to change the status of the button
		if ( threeD == true ) {
			threeD = false;
		}
		picking = !picking;

	}

	@Override
	public void toggleArcball() {
		arcball = !arcball;
		/*
		 * if(Arcball == true){
		 * ((JOGLAWTDisplayGraphics)openGLGraphics).graphicsGLUtils.DrawArcBall();
		 * }
		 */
	}

	@Override
	public void toggleSelectRectangle() {
		selectRectangle = !selectRectangle;

		/*
		 * if(Arcball == true){
		 * ((JOGLAWTDisplayGraphics)openGLGraphics).graphicsGLUtils.DrawArcBall();
		 * }
		 */
	}

	@Override
	public void toggleTriangulation() {
		triangulation = !triangulation;
	}

	@Override
	public void toggleSplitLayer() {

		splitLayer = !splitLayer;
		int nbLayers = this.getManager().getItems().size();
		int i = 0;
		Iterator<ILayer> it = this.getManager().getItems().iterator();
		while (it.hasNext()) {
			ILayer curLayer = it.next();
			if ( splitLayer ) {// Split layer
				curLayer.setElevation((double) i / nbLayers);
			} else {// put all the layer at zero
				curLayer.setElevation(0.0);
			}
			i++;
		}
		this.updateDisplay();
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

						Shell shell = new Shell(Display.getDefault());
						FileDialog dialog = new FileDialog(shell, SWT.OPEN);

						dialog.setText("Browse for a .shp file");

						dialog.setFilterPath(System.getProperty(GAMA.getModel().getProjectPath()));

						dialog.setFilterExtensions(new String[] { "*.shp" });

						if ( dialog.open() != null ) {

							String path = dialog.getFilterPath();

							String[] names = dialog.getFileNames();

							for ( int i = 0; i < names.length; i++ ) {
								shapeFileName[i] = path + "/" + names[i];
								System.out.println(shapeFileName[i]);
							}

						}

						renderer.myShapeFileReader = new ShapeFileReader(shapeFileName[0]);
						SimpleFeatureCollection myCollection =
							renderer.myShapeFileReader
								.getFeatureCollectionFromShapeFile(renderer.myShapeFileReader.store);
						Color color =
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

		Envelope env = geometry.getEnvelope();

		double xPos = geometry.getLocation().getX() - this.getEnvWidth() / 2;
		double yPos = -(geometry.getLocation().getY() - this.getEnvHeight() / 2);

		// FIXME: Need to compute the depth of the shape to adjust ZPos value.
		// FIXME: Problem when the geometry is a point how to determine the maxExtent of the shape?
		double zPos = env.maxExtent() * 2 + geometry.getLocation().getZ();
		double zLPos = -(env.maxExtent() * 2);

		this.renderer.camera.updatePosition(xPos, yPos, zPos);
		this.renderer.camera.lookPosition(xPos, yPos, zLPos);
	}

	@Override
	public void initOutput3D(final boolean yes, final ILocation output3DNbCycles) {
		output3D = yes;
		if ( output3D ) {
			output3DManager = new Output3DSWT(output3DNbCycles, renderer);
			// (new Output3D()).to3DGLGEModel(((JOGLAWTDisplayGraphics) openGLGraphics).myJTSGeometries,
			// renderer);
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

	public Color getBgColor() {
		return bgColor;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addMouseListener(final MouseListener e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void toggleRotation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void toggleCamera() {
		// TODO Auto-generated method stub

	}

	/**
	 * Method getModelCoordinates()
	 * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinates()
	 */
	@Override
	public GamaPoint getModelCoordinates() {
		return null;
	}

}
