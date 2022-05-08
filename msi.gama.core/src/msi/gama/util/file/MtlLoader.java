/*******************************************************************************************************
 *
 * MtlLoader.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class MtlLoader.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class MtlLoader {

	/** The Materials. */
	public ArrayList Materials = new ArrayList<>();

	/**
	 * The Class mtl.
	 */
	public class mtl {
		
		/** The name. */
		public String name;
		
		/** The mtlnum. */
		public int mtlnum;
		
		/** The d. */
		public float d = 1f;
		
		/** The Ka. */
		public float[] Ka = new float[3];
		
		/** The Kd. */
		public float[] Kd = new float[3];
		
		/** The Ks. */
		public float[] Ks = new float[3];
		
		/** The map kd. */
		public String map_Kd;
		
		/** The map ka. */
		public String map_Ka;
		
		/** The map d. */
		public String map_d;

	}

	/**
	 * Instantiates a new mtl loader.
	 *
	 * @param ref the ref
	 * @param pathtoimages the pathtoimages
	 */
	public MtlLoader(final BufferedReader ref, final String pathtoimages) {

		loadobject(ref, pathtoimages);
		cleanup();
	}

	/**
	 * Cleanup.
	 */
	private void cleanup() {}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		return Materials.size();
	}

	/**
	 * Gets the d.
	 *
	 * @param namepass the namepass
	 * @return the d
	 */
	public float getd(final String namepass) {
		final float returnfloat = 1f;
		for (int i = 0; i < Materials.size(); i++) {
			final mtl tempmtl = (mtl) Materials.get(i);
			if (tempmtl.name.matches(namepass)) {
				// returnfloat = tempmtl.d;
				return tempmtl.d;
			}
		}
		return returnfloat;
	}

	/**
	 * Gets the kd.
	 *
	 * @param namepass the namepass
	 * @return the kd
	 */
	public float[] getKd(final String namepass) {
		final float[] returnfloat = new float[3];
		for (int i = 0; i < Materials.size(); i++) {
			final mtl tempmtl = (mtl) Materials.get(i);
			if (tempmtl.name.matches(namepass)) {
				// returnfloat = tempmtl.Kd;
				return tempmtl.Kd;
			}
		}
		return returnfloat;
	}

	/**
	 * Gets the map ka.
	 *
	 * @param namepass the namepass
	 * @return the map ka
	 */
	public String getMapKa(final String namepass) {
		for (int i = 0; i < Materials.size(); i++) {
			final mtl tempmtl = (mtl) Materials.get(i);
			if (tempmtl.name.matches(namepass)) { return tempmtl.map_Ka; }
		}
		return null;
	}

	/**
	 * Gets the map kd.
	 *
	 * @param namepass the namepass
	 * @return the map kd
	 */
	public String getMapKd(final String namepass) {
		for (int i = 0; i < Materials.size(); i++) {
			final mtl tempmtl = (mtl) Materials.get(i);
			if (tempmtl.name.matches(namepass)) { return tempmtl.map_Kd; }
		}
		return null;
	}

	/**
	 * Gets the mapd.
	 *
	 * @param namepass the namepass
	 * @return the mapd
	 */
	public String getMapd(final String namepass) {
		for (int i = 0; i < Materials.size(); i++) {
			final mtl tempmtl = (mtl) Materials.get(i);
			if (tempmtl.name.matches(namepass)) { return tempmtl.map_d; }
		}
		return null;
	}

	/**
	 * Loadobject.
	 *
	 * @param br the br
	 * @param pathtoimages the pathtoimages
	 */
	private void loadobject(final BufferedReader br, final String pathtoimages) {
		int linecounter = 0;
		try {

			String newline;
			boolean firstpass = true;
			mtl matset = new mtl();
			int mtlcounter = 0;

			while ((newline = br.readLine()) != null) {
				linecounter++;
				newline = newline.trim();
				if (newline.length() > 0) {
					if (newline.charAt(0) == 'n' && newline.charAt(1) == 'e' && newline.charAt(2) == 'w') {
						if (firstpass) {
							firstpass = false;
						} else {
							Materials.add(matset);
							matset = new mtl();
						}
						String[] coordstext = new String[2];
						coordstext = newline.split("\\s+");
						matset.name = coordstext[1];
						matset.mtlnum = mtlcounter;
						mtlcounter++;
					} else if (newline.charAt(0) == 'K' && newline.charAt(1) == 'a') {
						final float[] coords = new float[3];
						String[] coordstext = new String[4];
						coordstext = newline.split("\\s+");
						for (int i = 1; i < coordstext.length; i++) {
							coords[i - 1] = Float.valueOf(coordstext[i]).floatValue();
						}
						matset.Ka = coords;
					} else if (newline.charAt(0) == 'K' && newline.charAt(1) == 'd') {
						final float[] coords = new float[3];
						String[] coordstext = new String[4];
						coordstext = newline.split("\\s+");
						for (int i = 1; i < coordstext.length; i++) {
							coords[i - 1] = Float.valueOf(coordstext[i]).floatValue();
						}
						matset.Kd = coords;
					} else if (newline.charAt(0) == 'K' && newline.charAt(1) == 's') {
						final float[] coords = new float[3];
						String[] coordstext = new String[4];
						coordstext = newline.split("\\s+");
						for (int i = 1; i < coordstext.length; i++) {
							coords[i - 1] = Float.valueOf(coordstext[i]).floatValue();
						}
						matset.Ks = coords;
					} else if (newline.charAt(0) == 'd') {
						final String[] coordstext = newline.split("\\s+");
						matset.d = Float.valueOf(coordstext[1]).floatValue();
					} else if (newline.contains("map_Ka")) {
						String texture = newline.replace("map_Ka ", "");
						while (texture.startsWith(" ")) {
							texture = texture.replaceFirst(" ", "");
						}
						matset.map_Ka = texture;
					} else if (newline.contains("map_Kd")) {
						String texture = newline.replace("map_Kd ", "");
						while (texture.startsWith(" ")) {
							texture = texture.replaceFirst(" ", "");
						}
						matset.map_Kd = texture;
					} else if (newline.contains("map_d")) {
						String texture = newline.replace("map_d ", "");
						while (texture.startsWith(" ")) {
							texture = texture.replaceFirst(" ", "");
						}
						matset.map_d = texture;
					}
				}
			}
			Materials.add(matset);

		} catch (final IOException e) {
			DEBUG.ERR("Failed to read file: " + br.toString());
			e.printStackTrace();
		} catch (final NumberFormatException e) {
			DEBUG.ERR("Malformed MTL (on line " + linecounter + "): " + br.toString() + "\r \r" + e.getMessage());
		} catch (final StringIndexOutOfBoundsException e) {
			DEBUG.ERR("Malformed MTL (on line " + linecounter + "): " + br.toString() + "\r \r" + e.getMessage());
		}
	}
}
