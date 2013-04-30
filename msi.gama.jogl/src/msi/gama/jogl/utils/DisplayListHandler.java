package msi.gama.jogl.utils;

import static javax.media.opengl.GL2.GL_COMPILE;
import java.util.*;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import msi.gama.jogl.utils.GraphicDataType.*;

public class DisplayListHandler {

	// OpenGL member
	private final GL2 myGl;
	private final GLU myGlu;

	// need to have the GLRenderer to enable texture mapping.
	public JOGLAWTGLRenderer myGLRender;

	public BasicOpenGlDrawer basicDrawer;

	float alpha = 1.0f;

	// Display List Id
	private int listId;
	private int firstList;

	private int listShapeId;
	private int firstShapeList;

	public DisplayListHandler(final GL2 gl, final GLU glu, final JOGLAWTGLRenderer gLRender) {
		myGl = gl;
		myGlu = glu;
		myGLRender = gLRender;
		basicDrawer = new BasicOpenGlDrawer(myGLRender);
	}

	/**
	 * Create the display list for each JTS geometries (one list per geometry)
	 * 
	 * @param myJTSGeometries
	 * @param size
	 */
	public void buildDisplayLists(List<MyJTSGeometry> list) {
		// Build n lists, and returns handle for the first list
		firstList = myGl.glGenLists(list.size());
		listId = firstList;
		Iterator<MyJTSGeometry> it = list.iterator();
		while (it.hasNext()) {
			MyJTSGeometry curGeometry = it.next();
			myGl.glNewList(listId, GL_COMPILE);
			basicDrawer.DrawJTSGeometry(curGeometry);
			myGl.glEndList();
			listId = listId + 1;
		}
	}

	public void DrawDisplayList(int nbDisplayList) {
		// System.out.println("draw" + nbDisplayList+ "list");
		for ( int i = 1; i <= nbDisplayList; i++ ) {
			myGl.glColor3f((float) Math.random(), (float) Math.random(), (float) Math.random());
			myGl.glCallList(i);
		}
	}

	public void DeleteDisplayLists(int nbDisplayList) {
		myGl.glDeleteLists(firstList, nbDisplayList);
		listId = 1;
	}

	/**
	 * Create the display list for each Image
	 * 
	 * @param myJTSGeometries
	 * @param size
	 */
	public void buildImageDisplayLists(List<MyImage> images) {

		// Build n lists, and returns handle for the first list
		firstList = myGl.glGenLists(images.size());
		listId = firstList;
		Iterator<MyImage> it = images.iterator();

		while (it.hasNext()) {
			MyImage curImage = it.next();
			myGl.glNewList(listId, GL_COMPILE);
			myGLRender.DrawTexture(curImage);
			myGl.glEndList();
			listId = listId + 1;
		}
	}

	public void DrawImageDisplayList(int nbDisplayList) {
		for ( int i = 0; i <= nbDisplayList; i++ ) {
			myGl.glCallList(i);
		}
	}

	/**
	 * Create the display list for a Collection
	 * 
	 * @param myJTSGeometries
	 * @param size
	 */
	public void buildCollectionDisplayLists(List<MyCollection> collections) {

		// Build n lists, and returns handle for the first list
		firstShapeList = myGl.glGenLists(collections.size());
		listShapeId = firstShapeList;
		Iterator<MyCollection> it = collections.iterator();

		System.out.println("in build list " + collections.size());
		while (it.hasNext()) {
			MyCollection curCol = it.next();
			myGl.glNewList(listShapeId, GL_COMPILE);
			basicDrawer.drawSimpleFeatureCollection(curCol);
			myGl.glEndList();
			listShapeId = listShapeId + 1;
		}
	}

	public void drawCollectionDisplayList(int nbDisplayList) {
		for ( int i = 0; i <= nbDisplayList; i++ ) {
			myGl.glCallList(i);
		}
	}

	public void DeleteCollectionDisplayLists(int nbDisplayList) {
		myGl.glDeleteLists(firstShapeList, nbDisplayList);
		listShapeId = 1;
	}

}
