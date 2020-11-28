/*********************************************************************************************
 *
 * 'Gama3DSFile.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.files;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.file.Gama3DGeometryFile;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 *
 * Class Gama3DSFile. A basic loader (only loads vertices and faces).
 *
 * @author drogoul
 * @since 31 déc. 2013
 *
 */

@file (
		name = "threeds",
		extensions = { "3ds", "max" },
		buffer_type = IType.LIST,
		buffer_content = IType.GEOMETRY)
@doc ("Autodesk 3DS Max file format: https://en.wikipedia.org/wiki/.3ds")
public class Gama3DSFile extends Gama3DGeometryFile {

	class Chunk {

		public int id = 0;
		public int length = 0;
		public int bytesRead = 0;
	}

	class Obj {

		public GamaPoint verts[] = null;
		public List<Geometry> faces;
	}

	List<Obj> objects = new ArrayList<>();

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
	@doc (
			value = "This file constructor allows to read a 3DS Max file. Only loads vertices and faces",
			examples = { @example (
					value = "threeds_file f <- threeds_file(\"file\");",
					isExecutable = false)

			})
	public Gama3DSFile(final IScope scope, final String fileName) {
		super(scope, fileName);
	}

	// Verified
	@Override
	public void fillBuffer(final IScope scope) {
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		try {
			final FileInputStream fileInputStream = new FileInputStream(getFile(scope));
			dataInputStream = new DataInputStream(fileInputStream);
			readChunkHeader(currentChunk);
			if (currentChunk.id != PRIMARY) {
				DEBUG.ERR("Unable to load PRIMARY chunk from file " + getPath(scope));
			}
			processNextChunk(currentChunk);
			dataInputStream.close();
			fileInputStream.close();
		} catch (final IOException e) {
			DEBUG.ERR("Error:  File IO error in: Closing File");
		}
		for (final Obj obj : objects) {
			final Geometry g = GeometryUtils.GEOMETRY_FACTORY.buildGeometry(obj.faces);
			getBuffer().add(new GamaShape(g));
		}

	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO are there attributes ?
		return GamaListFactory.create(Types.STRING);
	}

	// Verified
	void processNextChunk(final Chunk previousChunk) {
		// final int version = 0;
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
						final Chunk tempChunk = new Chunk();
						readChunkHeader(tempChunk);
						buffer = new byte[tempChunk.length - tempChunk.bytesRead];
						tempChunk.bytesRead += dataInputStream.read(buffer, 0, tempChunk.length - tempChunk.bytesRead);
						currentChunk.bytesRead += tempChunk.bytesRead;
						processNextChunk(currentChunk);
						break;

					case OBJECT:
						final Obj obj = new Obj();
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
		} catch (final IOException e) {
			DEBUG.ERR("Error:  File IO error in: Process Next Chunk");
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

		} catch (final IOException e) {
			DEBUG.ERR("Error:  File IO error in: Read Chunk Header");
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
		} catch (final IOException e) {
			DEBUG.ERR("Error:  File IO error in: Process Next Object Chunk");
			return;
		}
		currentChunk = previousChunk;
	}

	// Verified
	private void readVertices(final Obj object, final Chunk previousChunk) {
		try {
			final int numOfVerts = swap(dataInputStream.readShort());
			previousChunk.bytesRead += 2;

			object.verts = new GamaPoint[numOfVerts];
			for (int i = 0; i < numOfVerts; i++) {
				object.verts[i] = new GamaPoint(swap(dataInputStream.readFloat()), swap(dataInputStream.readFloat()),
						swap(dataInputStream.readFloat()));

				previousChunk.bytesRead += 12;
			}
		} catch (final IOException e) {
			DEBUG.ERR("Error: File IO error in: Read Vertices");
			return;
		}
	}

	// Verified
	private void readFaceList(final Obj object, final Chunk previousChunk) {
		try {
			final int numOfFaces = swap(dataInputStream.readShort());
			previousChunk.bytesRead += 2;

			object.faces = new ArrayList<>(numOfFaces);
			for (int i = 0; i < numOfFaces; i++) {
				final List<IShape> points = new ArrayList<>();
				points.add(object.verts[swap(dataInputStream.readShort())]);
				points.add(object.verts[swap(dataInputStream.readShort())]);
				points.add(object.verts[swap(dataInputStream.readShort())]);
				final IShape face = GamaGeometryType.buildPolygon(points);
				object.faces.add(face.getInnerGeometry());

				// Read in the extra face info
				dataInputStream.readShort();

				// Account for how much data was read in (4 * 2bytes)
				previousChunk.bytesRead += 8;
			}
		} catch (final IOException e) {
			DEBUG.ERR("Error: File IO error in: Read Face List");
			return;
		}
	}

	private static short swap(final short value) {
		final int b1 = value & 0xff;
		final int b2 = value >> 8 & 0xff;
		return (short) (b1 << 8 | b2 << 0);
	}

	private static int swap(final int value) {
		final int b1 = value >> 0 & 0xff;
		final int b2 = value >> 8 & 0xff;
		final int b3 = value >> 16 & 0xff;
		final int b4 = value >> 24 & 0xff;
		return b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0;
	}

	private static float swap(final float value) {
		int intValue = Float.floatToIntBits(value);
		intValue = swap(intValue);
		return Float.intBitsToFloat(intValue);
	}

}
