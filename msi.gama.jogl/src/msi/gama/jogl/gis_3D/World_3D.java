package msi.gama.jogl.gis_3D;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.*;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;

import msi.gama.jogl.utils.MyGLEventListener;
import msi.gama.jogl.utils.MyListener;

import com.sun.opengl.util.FPSAnimator;

/**
 * World 3D : Loading And Moving Through A 3D World
 */
public class World_3D extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int REFRESH_FPS = 10; // Display refresh frames per

	public final FPSAnimator animator; // Used to drive display()
	GLCanvas canvas;

	// The polygon
	Sector sectorRef;

	// The csv data for the lighttrap
	Sector sectorLightTrap;
	CsvLightTrapReader csvReader;
	int iteration = 0;

	// Draw in 3D or not.
	public boolean z_Enabled = false;

	// Draw agent or not.
	public boolean drawAgent = false;

	// Draw Point value
	public boolean drawPointValue = false;

	// Draw each sector with a different z value.
	public boolean drawMultiLayer = false;

	// fill the polygon
	public boolean isFilledOn = true;

	// Draw a sector made of poygon as polygon or as only the contour of the
	// polygon.
	public boolean drawAsPolygon = false;

	// Lighting
	public static boolean isLightOn;

	public boolean blendingEnabled; // Blending ON/OFF

	// Camera
	private Camera camera;
	private float zoom = 10.0f;
	private float cameraXPosition = 0.0f;
	private float cameraYPosition = 0.0f;
	public float cameraZPosition = 10.0f;

	private float cameraLXPosition = cameraXPosition;
	private float cameraLYPosition = cameraYPosition;
	public float cameraLZPosition = cameraZPosition - zoom;
	// Listener
	public MyListener myListener;

	// list of all the geometires that we want to handle (Polygons or lines).
	SimpleFeatureCollection myCollection;

	// list of all the different sectorin the world.
	List<Sector> mySectors;

	// list of all the agent
	List<Agent> myAgents;
	int nbAgents;

	// Coordinates of the reference layer (the first opened).
	float myBoundRefCenter_x;
	float myBoundRefCenter_y;
	float mymaxBoundRefDimension;

	// Constructor
	public World_3D() throws IOException {

		myCollection = GetInitialeShapeFile();
		mySectors = new ArrayList<Sector>(10);
		nbAgents = 100;
		myAgents = new ArrayList<Agent>(nbAgents);

		// Initialize the user camera
		camera = new Camera();
		camera.InitParam();

		canvas = new GLCanvas();
		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);
		MyGLEventListener glEventListener = new MyGLEventListener(this, camera);
		myListener = new MyListener(this, camera);
		canvas.addGLEventListener(glEventListener);
		canvas.addKeyListener(myListener);
		canvas.addMouseListener(myListener);
		canvas.addMouseMotionListener(myListener);
		canvas.addMouseWheelListener(myListener);
		canvas.setFocusable(true); // To receive key event
		canvas.requestFocus();

		// Run the animation loop using the fixed-rate Frame-per-second
		// animator,
		// which calls back display() at this fixed-rate (FPS).
		animator = new FPSAnimator(canvas, REFRESH_FPS, true);
		this.setupWorld();
		//this.CreateGraph();

	}

	public SimpleFeatureCollection GetInitialeShapeFile() {
		// Read a shapeFile
		ShapeFileReader reader = new ShapeFileReader();

		ShapefileDataStore store = null;
		try {
			store = reader.GetDataStore();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return reader.getFeatureCollectionFromShapeFile(store);
	}

	
	public void CreateGraph(){
		System.out.println("Create Graph");
				
	}
	public void setupWorld() throws IOException {

		try {
			// Create GIS reference layer. The firt choosen file will be defined
			// as the refence layer.
			sectorRef = new Sector(myCollection.size());
			InitializeReferenceCoordinates(myCollection);

			// Setup Sector and add it in the sector list.
			sectorRef.SetupSector(myCollection, mymaxBoundRefDimension,
					myBoundRefCenter_x, myBoundRefCenter_y);
			mySectors.add(sectorRef);

			// Setup Agent with ;
			for (int i = 0; i < nbAgents; i++) {
				Agent curAgent = new Agent(0, 0, 0);
				myAgents.add(curAgent);
			}

		} finally {

		}
	}

	/**
	 * In order to center the openGL scene on the right layer we define the
	 * reference bound characteristics.
	 * 
	 * @param collection
	 *            Reference collection
	 */
	public void InitializeReferenceCoordinates(
			SimpleFeatureCollection collection) {

		// Get the center coordinates of the bounds
		myBoundRefCenter_x = (float) collection.getBounds().centre().x;
		myBoundRefCenter_y = (float) collection.getBounds().centre().y;

		// Get the largest diameter of the bound
		float boundWidth = (float) collection.getBounds().getWidth();
		float boundHeight = (float) collection.getBounds().getHeight();

		if (boundHeight < boundWidth) {
			mymaxBoundRefDimension = boundWidth;
		} else {
			mymaxBoundRefDimension = boundHeight;
		}
	}

	/**
	 * Draw all the sectors in the sector lists.
	 * 
	 * @param gl
	 * @param glu
	 * @param drawMultilayer
	 *            : display or not layer on the same z plan.
	 * @param drawAsPolygon
	 *            : display polygon as polygon or only as its contour.
	 */
	public void DrawSectors(GL gl, GLU glu, boolean drawMultiLayer,
			boolean drawAsPolygon) {

		Iterator<Sector> iterator = mySectors.iterator();
		int curSector = 0;

		while (iterator.hasNext()) {
			Sector tmpSector = iterator.next();
			// FIXME: need to change the cost of this if (every fps it's tested
			// where as we don't need to set the z value each time but only if
			// the boolean value change.
			if (drawMultiLayer) {
				tmpSector.SetZValue(-5.0f + (float) curSector);
				curSector++;
			} else {
				tmpSector.SetZValue(-5.0f);
				curSector++;

			}
			if (drawAsPolygon) {
				tmpSector.SetDrawAsPoygon(true);

			} else {
				tmpSector.SetDrawAsPoygon(false);

			}

			tmpSector.draw(gl, glu, z_Enabled);
		}

		if (sectorRef != null) {

			// Draw Agent inside a given sector
			if (drawAgent) { // press S
				DrawAgents(myAgents, gl, glu);
			}
			if (drawPointValue) { // press V
				try {
					AddLightTrapData();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// If sectorLightTrap has been initialized successfully, display it
		// (wether in 2 or 3D).
		if (sectorLightTrap != null) {
			sectorLightTrap.DrawPointsFromValue(gl, glu,
					csvReader.lightTrapsDatas, iteration, z_Enabled);
		}
		iteration++;
	}

	/**
	 * Draw every agents on a random point on the reference sector. Each gent
	 * position is updated and then each agent is drawn.
	 * 
	 * @param agents
	 * @param gl
	 * @param glu
	 */
	public void DrawAgents(List<Agent> agents, GL gl, GLU glu) {

		Iterator<Agent> iterator = agents.iterator();
		Agent curAgent;

		while (iterator.hasNext()) {
			curAgent = iterator.next();
			int rand1 = (int) (Math.random() * sectorRef.myGeometries.length);
			int rand2 = (int) (Math.random() * sectorRef.myGeometries[rand1].vertices.length);

			curAgent.updatePosition(
					sectorRef.myGeometries[rand1].vertices[rand2].x,
					sectorRef.myGeometries[rand1].vertices[rand2].y,
					sectorRef.myGeometries[rand1].vertices[rand2].z);
			curAgent.draw(gl, glu);
		}

	}

	/**
	 * Read a shapefile defining the lighttrap position create a sector from it.
	 * Ask for the csv file containing lightrap data.
	 * 
	 * @throws Exception
	 */
	public void AddLightTrapData() throws Exception {

		// read the shp file
		ShapeFileReader reader = new ShapeFileReader();
		ShapefileDataStore store = reader.GetDataStore();

		SimpleFeatureCollection newCollection = reader
				.getFeatureCollectionFromShapeFile(store);
		sectorLightTrap = new Sector(newCollection.size());
		sectorLightTrap.SetupSector(newCollection, mymaxBoundRefDimension,
				myBoundRefCenter_x, myBoundRefCenter_y);
		mySectors.add(sectorLightTrap);

		// Read a csv file
		csvReader = new CsvLightTrapReader();
		csvReader.ReadLightTrapsDatas();

		drawPointValue = false;

	}

	/**
	 * Add a new sector layer from a .shp file in the sector list.
	 * 
	 * @throws IOException
	 */
	public void AddNewLayer() throws IOException {

		ShapeFileReader reader = new ShapeFileReader();
		ShapefileDataStore store = reader.GetDataStore();

		SimpleFeatureCollection newCollection = reader
				.getFeatureCollectionFromShapeFile(store);

		// FIXME: this is done just for trap, assuming that we open a lighttrap
		// file
		Sector newSector = new Sector(newCollection.size());
		newSector = new Sector(newCollection.size());
		newSector.SetupSector(newCollection, mymaxBoundRefDimension,
				myBoundRefCenter_x, myBoundRefCenter_y);
		mySectors.add(newSector);
	}

	/**
	 * Remove the last sector of the list.
	 * 
	 * @throws IOException
	 */
	public void DeleteLayer() throws IOException {

		if (!mySectors.isEmpty()) {
			// Remove the last sector of the list
			mySectors.remove(mySectors.size() - 1);

		} else {
			System.out.println("No more layer");
		}

	}

	public void update(GL gl, GLU glu) {
		DrawSectors(gl, glu, drawMultiLayer, drawAsPolygon);
	}

}
