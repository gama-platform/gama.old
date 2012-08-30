	package msi.gama.jogl.utils;

import static javax.media.opengl.GL.GL_COMPILE;
import java.util.ArrayList;
import java.util.Iterator;


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.geotools.data.simple.SimpleFeatureCollection;

import msi.gama.jogl.utils.GraphicDataType.MyCollection;
import msi.gama.jogl.utils.GraphicDataType.MyImage;
import msi.gama.jogl.utils.GraphicDataType.MyJTSGeometry;


public class DisplayListHandler {

	// OpenGL member
	private GL myGl;
	private GLU myGlu;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer myGLRender;
	
	public BasicOpenGlDrawer basicDrawer;

	float alpha = 1.0f;

	// Display List Id
	private int listId;
	private int firstList;


	public DisplayListHandler(final GL gl, final GLU glu,
			final JOGLAWTGLRenderer gLRender) {
		myGl = gl;
		myGlu = glu;
		myGLRender = gLRender;
		basicDrawer= new BasicOpenGlDrawer(myGl, myGlu, myGLRender);
	}


	/**
	 * Create the display list for each JTS geometries (one list per geometry)
	 * 
	 * @param myJTSGeometries
	 * @param size
	 */
	public void buildDisplayLists(ArrayList<MyJTSGeometry> myJTSGeometries) {
		// Build n lists, and returns handle for the first list
		firstList = myGl.glGenLists(myJTSGeometries.size());
		listId = firstList;
		Iterator<MyJTSGeometry> it = myJTSGeometries.iterator();
		while (it.hasNext()) {
			MyJTSGeometry curGeometry = it.next();
			myGl.glNewList(listId, GL_COMPILE);
			basicDrawer.DrawJTSGeometry(curGeometry);
			myGl.glEndList();
			listId = listId + 1;
		}
	}

	public void DrawDisplayList(int nbDisplayList) {
		//System.out.println("draw" + nbDisplayList+ "list");
		for (int i = 1; i <= nbDisplayList; i++) {
			myGl.glColor3f((float) Math.random(), (float) Math.random(),
					(float) Math.random());
			myGl.glCallList(i);
		}
	}

	public void DeleteDisplayLists(int nbDisplayList) {
		myGl.glDeleteLists(firstList, nbDisplayList);
		listId=1;
	}

	

	/**
	 * Create the display list for each Image
	 * 
	 * @param myJTSGeometries
	 * @param size
	 */
	public void buildImageDisplayLists(ArrayList<MyImage> myImages) {

		// Build n lists, and returns handle for the first list
		firstList = myGl.glGenLists(myImages.size());
		listId = firstList;
		Iterator<MyImage> it = myImages.iterator();

		while (it.hasNext()) {
			MyImage curImage = it.next();
			myGl.glNewList(listId, GL_COMPILE);
			myGLRender.DrawTexture(curImage);
			myGl.glEndList();
			listId = listId + 1;
		}
	}

	public void DrawImageDisplayList(int nbDisplayList) {
		for (int i = 0; i <= nbDisplayList; i++) {
			myGl.glCallList(i);
		}
	}

	/**
	 * Create the display list for a Collection
	 * 
	 * @param myJTSGeometries
	 * @param size
	 */
	public void buildCollectionDisplayLists(ArrayList<MyCollection> myCollections) {
	
		// Build n lists, and returns handle for the first list
		firstList = myGl.glGenLists(myCollections.size());
		listId = firstList;
		Iterator<MyCollection> it = myCollections.iterator();

		while (it.hasNext()) {
			MyCollection curCol = it.next();
			myGl.glNewList(listId, GL_COMPILE);
			basicDrawer.DrawSimpleFeatureCollection(curCol);
			myGl.glEndList();
			listId = listId + 1;
		}		
	}

	public void DrawCollectionDisplayList(int nbDisplayList) {
		for (int i = 0; i <= nbDisplayList; i++) {
			myGl.glCallList(i);
		}
	}
	
}
