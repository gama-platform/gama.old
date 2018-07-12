package msi.gama.common.util;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class GISUtils {

	//ugly method to manage Google CRS.... hoping that it is better managed by the next versions of Geotools
	public static CoordinateReferenceSystem manageGoogleCRS(URL url)  {
		CoordinateReferenceSystem crs = null;
		try {
			String path = new File(url.toURI()).getAbsolutePath().replace(".shp", ".prj");
			if (Files.exists(Paths.get(path))) {
				byte[] encoded = Files.readAllBytes(Paths.get(path));
				String content = new String(encoded, StandardCharsets.UTF_8);
				if (content.contains("WGS 84 / Pseudo-Mercator") || content.contains("WGS_1984_Web_Mercator_Auxiliary_Sphere"))
				{
					crs = CRS.decode("EPSG:3857");
				}
			}
		} catch (Exception e) {}
		return crs;
	}
}
