/*********************************************************************************************
 *
 *
 * 'GamaObjFile.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.files;

import java.io.*;
import java.util.StringTokenizer;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.GamaGeometryFile.Gama3DGeometryFile;
import msi.gaml.types.*;

/**
 * Class GamaObjFile.
 *
 * @author drogoul
 * @since 30 déc. 2013
 *
 */
@file(name = "obj", extensions = "obj", buffer_type = IType.LIST, buffer_content = IType.GEOMETRY)
public class GamaObjFile extends Gama3DGeometryFile {

	protected String mtl_path;

	/**
	 * @param scope
	 * @param pathName
	 * @throws GamaRuntimeException
	 */
	public GamaObjFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	/**
	 * Method fillBuffer(). Fills the buffer with the polygons built from the .obj vertices + faces
	 * @see msi.gama.util.file.GamaFile#fillBuffer(msi.gama.runtime.IScope)
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		BufferedReader br = null;
		try {
			setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
			br = new BufferedReader(new FileReader(getFile()));
			IList<IShape> vertices = GamaListFactory.create(Types.GEOMETRY);
			String newline;
			while ((newline = br.readLine()) != null) {
				if ( newline.length() > 0 ) {
					newline = newline.trim();
					// LOADS MATERIALS
					if ( newline.charAt(0) == 'm' && newline.charAt(1) == 't' && newline.charAt(2) == 'l' &&
						newline.charAt(3) == 'l' && newline.charAt(4) == 'i' && newline.charAt(5) == 'b' ) {
						String[] coordstext = new String[3];
						coordstext = newline.split("\\s+");
						if ( mtl_path != null ) {
							loadmaterials();
						}
					}

					// LOADS VERTEX COORDINATES
					if ( newline.startsWith("v ") ) {
						float coords[] = new float[4];
						newline = newline.substring(2, newline.length());
						StringTokenizer st = new StringTokenizer(newline, " ");
						for ( int i = 0; st.hasMoreTokens(); i++ ) {
							coords[i] = Float.parseFloat(st.nextToken());
						}
						vertices.add(new GamaPoint(coords[0], -coords[1], coords[2])); // w ignored
					} else

					// LOADS FACES COORDINATES
					if ( newline.startsWith("f ") ) {
						newline = newline.substring(2, newline.length());
						StringTokenizer st = new StringTokenizer(newline, " ");
						int count = st.countTokens();
						int v[] = new int[count];
						for ( int i = 0; i < count; i++ ) {
							char chars[] = st.nextToken().toCharArray();
							StringBuffer sb = new StringBuffer();
							char lc = 'x';
							for ( int k = 0; k < chars.length; k++ ) {
								if ( chars[k] == '/' && lc == '/' ) {
									sb.append('0');
								}
								lc = chars[k];
								sb.append(lc);
							}
							StringTokenizer st2 = new StringTokenizer(sb.toString(), "/");
							v[i] = Integer.parseInt(st2.nextToken());
						}
						IList<IShape> face = GamaListFactory.<IShape> create(Types.GEOMETRY);
						for ( int i = 0; i < v.length; i++ ) {
							face.add(vertices.get(v[i] - 1)); // Correct only if all the vertices have been loaded
																// before
						}
						((IList) getBuffer()).add(GamaGeometryType.buildPolygon(face));
					}
				}
			}
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			if ( br != null ) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO what to return ?
		return GamaListFactory.EMPTY_LIST;
	}

	private void loadmaterials() throws IOException {
		FileReader frm = null;
		BufferedReader brm = null;
		String refm = mtl_path;

		try {
			frm = new FileReader(refm);
			brm = new BufferedReader(frm);
			// materials = new MtlLoader(brm,mtl_path);

		} catch (IOException e) {
			System.out.println("Could not open file: " + refm);
			// materials = null;
		} finally {
			if ( brm != null ) {
				brm.close();
			}
			if ( frm != null ) {
				frm.close();
			}
		}
	}

	/**
	 * Method flushBuffer()
	 * @see msi.gama.util.file.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {}

}
