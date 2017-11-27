/*********************************************************************************************
 *
 * 'GamaObjFile.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaPair;
import msi.gama.util.IList;
import msi.gama.util.file.Gama3DGeometryFile;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.opengl.scene.OpenGL;

/**
 * Class GamaObjFile.
 *
 * @author drogoul
 * @since 30 déc. 2013
 *
 */
@file (
		name = "obj",
		extensions = { "obj", "OBJ" },
		buffer_type = IType.LIST,
		buffer_content = IType.GEOMETRY,
		doc = @doc ("'.obj' files are files containing 3D geometries. The internal representation is a list of one geometry"))
public class GamaObjFile extends Gama3DGeometryFile {

	public final ArrayList<double[]> setOfVertex = new ArrayList<>();
	private final ArrayList<double[]> setOfVertexNormals = new ArrayList<>();
	private final ArrayList<double[]> setOfVertexTextures = new ArrayList<>();
	private final ArrayList<int[]> faces = new ArrayList<>();
	private final ArrayList<int[]> facesTexs = new ArrayList<>();
	private final ArrayList<int[]> facesNorms = new ArrayList<>();
	private final ArrayList<String[]> matTimings = new ArrayList<>();
	private MtlLoader materials;
	// private int objectList;
	// private int numPolys = 0;
	public double toppoint = 0f;
	public double bottompoint = 0f;
	public double leftpoint = 0f;
	public double rightpoint = 0f;
	public double farpoint = 0f;
	public double nearpoint = 0f;
	private final String mtlPath;
	boolean loaded = false;

	/**
	 * @param scope
	 * @param pathName
	 * @throws GamaRuntimeException
	 */
	public GamaObjFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		this(scope, pathName, (GamaPair<Double, GamaPoint>) null);
	}

	public GamaObjFile(final IScope scope, final String pathName, final GamaPair<Double, GamaPoint> initRotation)
			throws GamaRuntimeException {
		this(scope, pathName, pathName.replace(".obj", ".mtl"), initRotation);
	}

	public GamaObjFile(final IScope scope, final String pathName, final String mtlPath) {
		this(scope, pathName, mtlPath, null);
	}

	public GamaObjFile(final IScope scope, final String pathName, final String mtlPath,
			final GamaPair<Double, GamaPoint> initRotation) {
		super(scope, pathName, initRotation);
		if (mtlPath != null) {
			this.mtlPath = FileUtils.constructAbsoluteFilePath(scope, mtlPath, false);
		} else {
			this.mtlPath = null;
		}

	}

	private void centerit() {
		final double xshift = (rightpoint - leftpoint) / 2.0d;
		final double yshift = (toppoint - bottompoint) / 2.0d;
		final double zshift = (nearpoint - farpoint) / 2.0d;
		for (int i = 0; i < setOfVertex.size(); i++) {
			final double coords[] = new double[4];
			coords[0] = setOfVertex.get(i)[0] - leftpoint - xshift;
			coords[1] = setOfVertex.get(i)[1] - bottompoint - yshift;
			coords[2] = setOfVertex.get(i)[2] - farpoint - zshift;
			setOfVertex.set(i, coords);
		}

	}

	public void loadObject(final IScope scope, final boolean forDrawing) {

		try (BufferedReader br = new BufferedReader(new FileReader(getFile(scope)))) {
			loadObject(br);
		} catch (final IOException e) {
			System.out.println("Failed to read file: " /* + br.toString() */);
		} catch (final NumberFormatException e) {
			System.out.println("Malformed OBJ file: "/* + br.toString() */ + "\r \r" + e.getMessage());
		}

	}

	public void loadObject(final BufferedReader br) throws IOException {
		if (loaded) { return; }
		int facecounter = 0;
		boolean firstpass = true;
		String newline;
		while ((newline = br.readLine()) != null) {
			if (newline.length() > 0) {
				newline = newline.trim();

				// LOADS VERTEX COORDINATES
				if (newline.startsWith("v ")) {
					newline = newline.substring(2, newline.length());
					final StringTokenizer st = new StringTokenizer(newline, " ");
					final double coords[] = new double[st.countTokens()];
					for (int i = 0; st.hasMoreTokens(); i++) {
						coords[i] = Double.parseDouble(st.nextToken());
					}

					if (firstpass) {
						rightpoint = coords[0];
						leftpoint = coords[0];
						toppoint = coords[1];
						bottompoint = coords[1];
						nearpoint = coords[2];
						farpoint = coords[2];
						firstpass = false;
					}
					if (coords[0] > rightpoint) {
						rightpoint = coords[0];
					}
					if (coords[0] < leftpoint) {
						leftpoint = coords[0];
					}
					if (coords[1] > toppoint) {
						toppoint = coords[1];
					}
					if (coords[1] < bottompoint) {
						bottompoint = coords[1];
					}
					if (coords[2] > nearpoint) {
						nearpoint = coords[2];
					}
					if (coords[2] < farpoint) {
						farpoint = coords[2];
					}
					setOfVertex.add(coords);
				} else

				// LOADS VERTEX TEXTURE COORDINATES
				if (newline.startsWith("vt")) {
					final double coords[] = new double[4];
					// final String coordstext[] = new String[4];
					newline = newline.substring(3, newline.length());
					final StringTokenizer st = new StringTokenizer(newline, " ");
					for (int i = 0; st.hasMoreTokens(); i++) {
						coords[i] = Double.parseDouble(st.nextToken());
					}

					setOfVertexTextures.add(coords);
				} else

				// LOADS VERTEX NORMALS COORDINATES
				if (newline.startsWith("vn")) {
					final double coords[] = new double[4];
					// final String coordstext[] = new String[4];
					newline = newline.substring(3, newline.length());
					final StringTokenizer st = new StringTokenizer(newline, " ");
					for (int i = 0; st.hasMoreTokens(); i++) {
						coords[i] = Double.parseDouble(st.nextToken());
					}

					setOfVertexNormals.add(coords);
				} else

				// LOADS FACES COORDINATES
				if (newline.startsWith("f ")) {
					facecounter++;
					newline = newline.substring(2, newline.length());
					final StringTokenizer st = new StringTokenizer(newline, " ");
					final int count = st.countTokens();
					final int v[] = new int[count];
					final int vt[] = new int[count];
					final int vn[] = new int[count];
					for (int i = 0; i < count; i++) {
						final char chars[] = st.nextToken().toCharArray();
						final StringBuffer sb = new StringBuffer();
						char lc = 'x';
						for (int k = 0; k < chars.length; k++) {
							if (chars[k] == '/' && lc == '/') {
								sb.append('0');
							}
							lc = chars[k];
							sb.append(lc);
						}

						final StringTokenizer st2 = new StringTokenizer(sb.toString(), "/");
						final int num = st2.countTokens();
						v[i] = Integer.parseInt(st2.nextToken());
						if (num > 1) {
							vt[i] = Integer.parseInt(st2.nextToken());
						} else {
							vt[i] = 0;
						}
						if (num > 2) {
							vn[i] = Integer.parseInt(st2.nextToken());
						} else {
							vn[i] = 0;
						}
					}

					faces.add(v);
					facesTexs.add(vt);
					facesNorms.add(vn);
				} else

				// LOADS MATERIALS
				if (newline.charAt(0) == 'm' && newline.charAt(1) == 't' && newline.charAt(2) == 'l'
						&& newline.charAt(3) == 'l' && newline.charAt(4) == 'i' && newline.charAt(5) == 'b') {
					// String[] coordstext = new String[3];
					// coordstext = newline.split("\\s+");
					if (mtlPath != null) {
						loadMaterials();
					}
				} else

				// USES MATERIALS
				if (newline.charAt(0) == 'u' && newline.charAt(1) == 's' && newline.charAt(2) == 'e'
						&& newline.charAt(3) == 'm' && newline.charAt(4) == 't' && newline.charAt(5) == 'l') {
					final String[] coords = new String[2];
					final String[] coordstext = newline.split("\\s+");
					coords[0] = coordstext[1];
					coords[1] = facecounter + "";
					matTimings.add(coords);
				}
			}
		}
		centerit();
		// numPolys = faces.size();
		loaded = true;
	}

	/**
	 * Method fillBuffer(). Fills the buffer with the polygons built from the .obj vertices + faces
	 * 
	 * @see msi.gama.util.file.GamaFile#fillBuffer(msi.gama.runtime.IScope)
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		loadObject(scope, false);
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		final IList<IShape> vertices = GamaListFactory.create(Types.POINT);
		for (final double[] coords : setOfVertex) {
			final GamaPoint pt = new GamaPoint(coords[0], -coords[1], coords[2]);
			vertices.add(pt);
		}
		for (final int[] vertexRefs : faces) {
			final IList<IShape> face = GamaListFactory.<IShape> create(Types.POINT);
			for (final int vertex : vertexRefs) {
				face.add(vertices.get(vertex - 1));
				getBuffer().add(GamaGeometryType.buildPolygon(face));
			}
		}
		envelope = new Envelope3D(leftpoint, rightpoint, bottompoint, toppoint, nearpoint, farpoint);

	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO what to return ?
		return GamaListFactory.create();
	}

	private void loadMaterials() {
		FileReader frm;
		final String refm = mtlPath;

		try {
			frm = new FileReader(refm);
			final BufferedReader brm = new BufferedReader(frm);
			materials = new MtlLoader(brm, mtlPath);
			frm.close();
		} catch (final IOException e) {
			System.out.println("Could not open file: " + refm);
			materials = null;
		}
	}

	/**
	 * Method flushBuffer()
	 * 
	 * @see msi.gama.util.file.GamaFile#flushBuffer() //
	 */
	// @Override
	// protected void flushBuffer() throws GamaRuntimeException {
	// }

	public void drawToOpenGL(final OpenGL gl) {
		int nextmat = -1;
		int matcount = 0;
		final int totalmats = matTimings.size();
		String[] nextmatnamearray = null;
		String nextmatname = null;

		if (totalmats > 0 && materials != null) {
			nextmatnamearray = matTimings.get(matcount);
			nextmatname = nextmatnamearray[0];
			nextmat = Integer.parseInt(nextmatnamearray[1]);
		}
		Texture texture = null;
		final GamaPoint tex = new GamaPoint();
		final GamaPoint normal = new GamaPoint();
		final GamaPoint vertex = new GamaPoint();
		for (int i = 0; i < faces.size(); i++) {
			if (i == nextmat) {
				if (texture != null) {
					gl.deleteTexture(texture);
					texture = null;
				}
				// gl.getGL().glEnable(GL2.GL_COLOR_MATERIAL);
				gl.setCurrentColor(materials.getKd(nextmatname)[0], materials.getKd(nextmatname)[1],
						materials.getKd(nextmatname)[2], materials.getd(nextmatname));

				final String mapKa = materials.getMapKa(nextmatname);
				final String mapKd = materials.getMapKd(nextmatname);
				final String mapd = materials.getMapd(nextmatname);
				if (mapKa != null || mapKd != null || mapd != null) {
					File f = new File(mtlPath);
					String path = f.getAbsolutePath().replace(f.getName(), "");
					if (mapd != null) {
						path += mapd;
					} else if (mapKa != null) {
						path += mapKa;
					} else if (mapKd != null) {
						path += mapKd;
					}
					f = new File(path);
					if (f.exists()) {
						// Solves Issue #1951. Asynchronous loading of textures
						// was not possible when displaying the file
						texture = gl.getTexture(f, false, true);
						gl.setCurrentTextures(texture.getTextureObject(), texture.getTextureObject());
						texture.setTexParameteri(gl.getGL(), GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
						texture.setTexParameteri(gl.getGL(), GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
					}

				}
				matcount++;
				if (matcount < totalmats) {
					nextmatnamearray = matTimings.get(matcount);
					nextmatname = nextmatnamearray[0];
					nextmat = Integer.parseInt(nextmatnamearray[1]);
				}
			}

			final int[] tempfaces = faces.get(i);
			final int[] norms = facesNorms.get(i);
			final int[] texs = facesTexs.get(i);

			//// Quad Begin Header ////
			final int polytype =
					tempfaces.length == 3 ? GL.GL_TRIANGLES : tempfaces.length == 4 ? GL2.GL_QUADS : GL2.GL_POLYGON;
			gl.beginDrawing(polytype);
			////////////////////////////

			boolean hasNormals = true;
			for (int w = 0; w < tempfaces.length; w++) {
				if (norms[w] == 0) {
					hasNormals = false;
					break;
				}
			}

			final double[] arrayOfVertices = new double[tempfaces.length * 3];
			for (int w = 0; w < tempfaces.length; w++) {
				final double[] ordinates = setOfVertex.get(tempfaces[w] - 1);
				for (int k = 0; k < 3; k++)
					arrayOfVertices[w * 3 + k] = ordinates[k];
			}
			final ICoordinates coords = ICoordinates.ofLength(tempfaces.length + 1);
			coords.setTo(arrayOfVertices);
			coords.completeRing();

			if (!hasNormals) {
				gl.setNormal(coords, !coords.isClockwise());
			}
			for (int w = 0; w < tempfaces.length; w++) {
				if (tempfaces[w] == 0)
					continue;
				final boolean hasNormal = norms[w] != 0;
				final boolean hasTex = texs[w] != 0;
				if (hasNormal)
					normal.setLocation(setOfVertexNormals.get(norms[w] - 1));
				if (hasTex) {
					tex.setLocation(setOfVertexTextures.get(texs[w] - 1));
					if (1d >= tex.y && -tex.y <= 0) {
						tex.y = 1d - tex.y;
					} else
						tex.y = Math.abs(tex.y);
				}
				vertex.setLocation(setOfVertex.get(tempfaces[w] - 1));
				gl.drawVertex(vertex, hasNormal ? normal : null, hasTex ? tex : null);
			}

			//// Quad End Footer /////
			gl.endDrawing();
		}

		if (texture != null) {
			gl.disableTextures();
			;
			texture = null;
		}
		// gl.glEndList();
	}

}
