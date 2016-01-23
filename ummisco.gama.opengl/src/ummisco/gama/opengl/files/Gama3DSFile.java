/*********************************************************************************************
 * 
 * 
 * 'Gama3DSFile.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.opengl.files;

import java.io.*;
import java.util.*;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.Gama3DGeometryFile;
import msi.gaml.types.*;
import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * Class Gama3DSFile. A basic loader (only loads vertices and faces).
 * 
 * @author drogoul
 * @since 31 déc. 2013
 * 
 */

@file(name = "threeds", extensions = { "3ds", "max" }, buffer_type = IType.LIST, buffer_content = IType.GEOMETRY)
public class Gama3DSFile extends Gama3DGeometryFile {

	class Chunk {

		public int id = 0;
		public int length = 0;
		public int bytesRead = 0;
	}

	class Obj {

		public String strName = null;
		public GamaPoint verts[] = null;
		public List<Geometry> faces;
	}

	List<Obj> objects = new ArrayList();

	// Primary Chunk, at the beginning of each file
	private static final int PRIMARY = 0x4D4D;
	private static final int VERSION = 0x0002;

	// Main Chunks
	private static final int EDITOR = 0x3D3D;
	private static final int OBJECT = 0x4000;
	private static final int OBJECT_MESH = 0x4100;

	// Sub defines of OBJECT_MESH
	private static final int OBJECT_VERTICES = 0x4110;
	private static final int OBJECT_FACES = 0x4120;

	// File reader
	private DataInputStream dataInputStream;

	// Global chunks
	private Chunk currentChunk = new Chunk();

	// Constructor
	public Gama3DSFile(final IScope scope, final String fileName) {
		super(scope, fileName);
	}

	// Verified
	@Override
	public void fillBuffer(final IScope scope) {
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		try {
			FileInputStream fileInputStream = new FileInputStream(getFile());
			dataInputStream = new DataInputStream(fileInputStream);
			readChunkHeader(currentChunk);
			if ( currentChunk.id != PRIMARY ) {
				System.err.println("Unable to load PRIMARY chunk from file " + getPath());
			}
			processNextChunk(currentChunk);
			dataInputStream.close();
			fileInputStream.close();
		} catch (IOException e) {
			System.err.println("Error:  File IO error in: Closing File");
		}
		for ( Obj obj : objects ) {
			Geometry g = GeometryUtils.FACTORY.buildGeometry(obj.faces);
			((GamaList) getBuffer()).add(new GamaShape(g));
		}

	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO are there attributes ?
		return GamaListFactory.EMPTY_LIST;
	}

	// Verified
	void processNextChunk(final Chunk previousChunk) {
		int version = 0;
		byte buffer[] = null;
		currentChunk = new Chunk();
		try {
			while (previousChunk.bytesRead < previousChunk.length) {
				readChunkHeader(currentChunk);
				switch (currentChunk.id) {
					case VERSION:
						currentChunk.bytesRead += 4;
						break;

					case EDITOR:
						Chunk tempChunk = new Chunk();
						readChunkHeader(tempChunk);
						buffer = new byte[tempChunk.length - tempChunk.bytesRead];
						tempChunk.bytesRead += dataInputStream.read(buffer, 0, tempChunk.length - tempChunk.bytesRead);
						currentChunk.bytesRead += tempChunk.bytesRead;
						processNextChunk(currentChunk);
						break;

					case OBJECT:
						Obj obj = new Obj();
						obj.strName = getString(currentChunk);
						objects.add(obj);
						processNextObjectChunk(obj, currentChunk);
						break;

					default:
						buffer = new byte[currentChunk.length - currentChunk.bytesRead];
						currentChunk.bytesRead +=
							dataInputStream.read(buffer, 0, currentChunk.length - currentChunk.bytesRead);
						break;
				}
				previousChunk.bytesRead += currentChunk.bytesRead;
			}
		} catch (IOException e) {
			System.err.println("Error:  File IO error in: Process Next Chunk");
			return;
		}
		currentChunk = previousChunk;
	}

	// Verified
	private void readChunkHeader(final Chunk chunk) {
		// byte buffer[] = new byte[2];

		try {
			chunk.id = swap(dataInputStream.readShort());
			chunk.id &= 0x0000FFFF;
			chunk.bytesRead = 2;
			chunk.length = swap(dataInputStream.readInt());
			chunk.bytesRead += 4;

		} catch (IOException e) {
			System.err.println("Error:  File IO error in: Read Chunk Header");
			return;
		}
	}

	// Verified
	private void processNextObjectChunk(final Obj object, final Chunk previousChunk) {
		byte buffer[] = null;

		currentChunk = new Chunk();

		try {
			while (previousChunk.bytesRead < previousChunk.length) {
				readChunkHeader(currentChunk);

				switch (currentChunk.id) {
					case OBJECT_MESH:
						processNextObjectChunk(object, currentChunk);
						break;

					case OBJECT_VERTICES:
						readVertices(object, currentChunk);
						break;

					case OBJECT_FACES:
						readFaceList(object, currentChunk);
						break;

					default:
						buffer = new byte[currentChunk.length - currentChunk.bytesRead];
						currentChunk.bytesRead +=
							dataInputStream.read(buffer, 0, currentChunk.length - currentChunk.bytesRead);
						break;
				}
				previousChunk.bytesRead += currentChunk.bytesRead;
			}
		} catch (IOException e) {
			System.err.println("Error:  File IO error in: Process Next Object Chunk");
			return;
		}
		currentChunk = previousChunk;
	}

	// Verified
	private void readVertices(final Obj object, final Chunk previousChunk) {
		try {
			int numOfVerts = swap(dataInputStream.readShort());
			previousChunk.bytesRead += 2;

			object.verts = new GamaPoint[numOfVerts];
			for ( int i = 0; i < numOfVerts; i++ ) {
				object.verts[i] =
					new GamaPoint(swap(dataInputStream.readFloat()), swap(dataInputStream.readFloat()),
						swap(dataInputStream.readFloat()));

				previousChunk.bytesRead += 12;
			}
		} catch (IOException e) {
			System.err.println("Error: File IO error in: Read Vertices");
			return;
		}
	}

	// Verified
	private void readFaceList(final Obj object, final Chunk previousChunk) {
		try {
			int numOfFaces = swap(dataInputStream.readShort());
			previousChunk.bytesRead += 2;

			object.faces = new ArrayList(numOfFaces);
			for ( int i = 0; i < numOfFaces; i++ ) {
				List<IShape> points = new ArrayList();
				points.add(object.verts[swap(dataInputStream.readShort())]);
				points.add(object.verts[swap(dataInputStream.readShort())]);
				points.add(object.verts[swap(dataInputStream.readShort())]);
				IShape face = GamaGeometryType.buildPolygon(points);
				object.faces.add(face.getInnerGeometry());

				// Read in the extra face info
				dataInputStream.readShort();

				// Account for how much data was read in (4 * 2bytes)
				previousChunk.bytesRead += 8;
			}
		} catch (IOException e) {
			System.err.println("Error: File IO error in: Read Face List");
			return;
		}
	}

	private String getString(final Chunk chunk) {
		int index = 0, bytesRead = 0;
		boolean read = true;
		byte buffer[] = new byte[256];

		try {
			while (read) {
				bytesRead += dataInputStream.read(buffer, index, 1);
				if ( buffer[index] == 0x00 ) {
					break;
				}
				index++;
			}
		} catch (IOException e) {
			System.err.println("Error: File IO error in: Get String");
			return "";
		}
		chunk.bytesRead += bytesRead;
		return new String(buffer).trim();
	}

	private static short swap(final short value) {
		int b1 = value & 0xff;
		int b2 = value >> 8 & 0xff;
		return (short) (b1 << 8 | b2 << 0);
	}

	private static int swap(final int value) {
		int b1 = value >> 0 & 0xff;
		int b2 = value >> 8 & 0xff;
		int b3 = value >> 16 & 0xff;
		int b4 = value >> 24 & 0xff;
		return b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0;
	}

	private static float swap(final float value) {
		int intValue = Float.floatToIntBits(value);
		intValue = swap(intValue);
		return Float.intBitsToFloat(intValue);
	}

	/**
	 * Method flushBuffer()
	 * @see msi.gama.util.file.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {}

}
