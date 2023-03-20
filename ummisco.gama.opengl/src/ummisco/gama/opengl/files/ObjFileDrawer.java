/*******************************************************************************************************
 *
 * ObjFileDrawer.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.files;

import java.io.File;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.util.texture.Texture;

import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.file.GamaImageFile;
import msi.gama.util.file.GamaObjFile;
import ummisco.gama.opengl.OpenGL;

/**
 * The Class ObjFileDrawer.
 */
public class ObjFileDrawer {

	/**
	 * Draw to open GL.
	 *
	 * @param file
	 *            the file
	 * @param gl
	 *            the gl
	 */
	public static void drawToOpenGL(final GamaObjFile file, final OpenGL gl) {

		int nextmat = -1;
		int matcount = 0;
		final int totalmats = file.matTimings.size();
		String[] nextmatnamearray = null;
		String nextmatname = null;

		if (totalmats > 0 && file.materials != null) {
			nextmatnamearray = file.matTimings.get(matcount);
			nextmatname = nextmatnamearray[0];
			nextmat = Integer.parseInt(nextmatnamearray[1]);
		}
		Texture texture = null;
		final GamaPoint tex = new GamaPoint();
		final GamaPoint normal = new GamaPoint();
		final GamaPoint vertex = new GamaPoint();
		for (int i = 0; i < file.faces.size(); i++) {
			if (i == nextmat) {
				if (texture != null) {
					texture.destroy(gl.getGL());
					texture = null;
				}
				// gl.getGL().glEnable(GL2.GL_COLOR_MATERIAL);
				gl.setCurrentColor(file.materials.getKd(nextmatname)[0], file.materials.getKd(nextmatname)[1],
						file.materials.getKd(nextmatname)[2], file.materials.getd(nextmatname));

				final String mapKa = file.materials.getMapKa(nextmatname);
				final String mapKd = file.materials.getMapKd(nextmatname);
				final String mapd = file.materials.getMapd(nextmatname);
				if (mapKa != null || mapKd != null || mapd != null) {
					File f = new File(file.mtlPath);
					StringBuilder path = new StringBuilder().append(f.getAbsolutePath().replace(f.getName(), ""));
					if (mapd != null) {
						path.append(mapd);
					} else if (mapKa != null) {
						path.append(mapKa);
					} else if (mapKd != null) { path.append(mapKd); }
					GamaImageFile asset = new GamaImageFile(null, path.toString());
					if (asset.exists(null)) {
						// Solves Issue #1951. Asynchronous loading of textures
						// was not possible when displaying the file
						texture = gl.getTexture(asset, false, true);
						gl.setCurrentTextures(texture.getTextureObject(), texture.getTextureObject());
						texture.setTexParameteri(gl.getGL(), GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
						texture.setTexParameteri(gl.getGL(), GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
					}

				}
				matcount++;
				if (matcount < totalmats) {
					nextmatnamearray = file.matTimings.get(matcount);
					nextmatname = nextmatnamearray[0];
					nextmat = Integer.parseInt(nextmatnamearray[1]);
				}
			}

			final int[] tempfaces = file.faces.get(i);
			final int[] norms = file.facesNorms.get(i);
			final int[] texs = file.facesTexs.get(i);

			//// Quad Begin Header ////
			final int polytype =
					tempfaces.length == 3 ? GL.GL_TRIANGLES : tempfaces.length == 4 ? GL2ES3.GL_QUADS : GL2.GL_POLYGON;
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
				final double[] ordinates = file.setOfVertex.get(tempfaces[w] - 1);
				for (int k = 0; k < 3; k++) { arrayOfVertices[w * 3 + k] = ordinates[k]; }
			}
			final ICoordinates coords = ICoordinates.ofLength(tempfaces.length + 1);
			coords.setTo(arrayOfVertices);
			coords.completeRing();

			if (!hasNormals) { gl.setNormal(coords, !coords.isClockwise()); }
			for (int w = 0; w < tempfaces.length; w++) {
				if (tempfaces[w] == 0) { continue; }
				final boolean hasNormal = norms[w] != 0;
				final boolean hasTex = texs[w] != 0;
				if (hasNormal) {
					final double[] temp_coords = file.setOfVertexNormals.get(norms[w] - 1);
					normal.setLocation(temp_coords[0], temp_coords[1], temp_coords[2]);
				}
				if (hasTex) {
					final double[] ordinates = file.setOfVertexTextures.get(texs[w] - 1);
					tex.setLocation(ordinates[0], ordinates[1], ordinates[2]);
					if (1d >= tex.y && -tex.y <= 0) {
						tex.y = 1d - tex.y;
					} else {
						tex.y = Math.abs(tex.y);
					}
				}
				final double[] temp_coords = file.setOfVertex.get(tempfaces[w] - 1);
				vertex.setLocation(temp_coords[0], temp_coords[1], temp_coords[2]);
				gl.drawVertex(vertex, hasNormal ? normal : null, hasTex ? tex : null);
			}

			//// Quad End Footer /////
			gl.endDrawing();
		}

		if (texture != null) {
			gl.disableTextures();
			texture.destroy(gl.getGL());
			texture = null;
		}
		// gl.glEndList();

	}

}
