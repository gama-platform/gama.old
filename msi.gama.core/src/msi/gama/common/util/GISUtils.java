/*******************************************************************************************************
 *
 * GISUtils.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * The Class GISUtils.
 */
public class GISUtils {

	/**
	 * Manage google CRS.
	 *
	 * @param url
	 *            the url
	 * @return the coordinate reference system
	 */
	// ugly method to manage Google CRS.... hoping that it is better managed by the next versions of Geotools
	public static CoordinateReferenceSystem manageGoogleCRS(final URL url) {
		CoordinateReferenceSystem crs = null;
		try {
			final String path = new File(url.toURI()).getAbsolutePath().replace(".shp", ".prj");
			if (Files.exists(Paths.get(path))) {
				final byte[] encoded = Files.readAllBytes(Paths.get(path));
				final String content = new String(encoded, StandardCharsets.UTF_8);
				if (content.contains("WGS 84 / Pseudo-Mercator")
						|| content.contains("WGS_1984_Web_Mercator_Auxiliary_Sphere")) {
					crs = CRS.decode("EPSG:3857");
				}
			}
		} catch (final IOException | URISyntaxException | FactoryException e) {}
		return crs;
	}
}
