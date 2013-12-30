/**
 * Created by drogoul, 30 déc. 2013
 * 
 */
package msi.gama.jogl.files;

import java.io.*;
import java.util.StringTokenizer;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.GamaGeometryFile.Gama3DGeometryFile;
import msi.gaml.types.GamaGeometryType;

/**
 * Class GamaObjFile.
 * 
 * @author drogoul
 * @since 30 déc. 2013
 * 
 */
@file(name = "obj", extensions = ".obj")
public class GamaObjFile extends Gama3DGeometryFile {

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
		try {
			buffer = new GamaList();
			final BufferedReader br = new BufferedReader(new FileReader(getFile()));
			IList<IShape> vertices = new GamaList();
			String newline;
			while ((newline = br.readLine()) != null) {
				if ( newline.length() > 0 ) {
					newline = newline.trim();
					// LOADS VERTEX COORDINATES
					if ( newline.startsWith("v ") ) {
						float coords[] = new float[4];
						newline = newline.substring(2, newline.length());
						StringTokenizer st = new StringTokenizer(newline, " ");
						for ( int i = 0; st.hasMoreTokens(); i++ ) {
							coords[i] = Float.parseFloat(st.nextToken());
						}
						vertices.add(new GamaPoint(coords[0], coords[1], coords[2])); // w ignored
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
						GamaList<IShape> points = new GamaList();
						for ( int i = 0; i < v.length; i++ ) {
							points.add(vertices.get(v[i])); // Correct only if all the vertices have been loaded before
						}
						((IList) buffer).add(GamaGeometryType.buildPolygon(points));
					}
				}
			}
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e);
		}

	}

	/**
	 * Method flushBuffer()
	 * @see msi.gama.util.file.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {}

}
