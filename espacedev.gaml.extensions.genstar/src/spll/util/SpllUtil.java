package spll.util;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.locationtech.jts.precision.EnhancedPrecisionOp;
import org.locationtech.jts.precision.GeometryPrecisionReducer;
import org.locationtech.jts.util.AssertionFailedException;

public class SpllUtil {

	/**
	 * Return a string representation of a 2D matrix
	 * 
	 * @param matrix
	 * @return
	 */
	public static String getStringMatrix(float[][] matrix){
		String string = "";
		for(int x = 0; x < matrix.length; x++){
			for(int y = 0; y < matrix[x].length; y++){
				string += "["+matrix[x][y]+"]";
			}
			string += "\n";
		}
		return string;
	}
	
	public static Geometry difference(Geometry g1, Geometry g2) {
		try {
			return g1.difference(g2);
		} catch (AssertionFailedException | TopologyException e) {
			try {
				final PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING_SINGLE);
				return GeometryPrecisionReducer.reducePointwise(g1, pm)
						.difference(GeometryPrecisionReducer.reducePointwise(g2, pm));
			} catch (final RuntimeException e1) {
				try {
					return g1.buffer(0, 10, BufferParameters.CAP_FLAT)
							.difference(g2.buffer(0, 10, BufferParameters.CAP_FLAT));
				} catch (final TopologyException e2) {
					try {
						final PrecisionModel pm = new PrecisionModel(100000d);
						return GeometryPrecisionReducer.reduce(g1, pm)
								.difference(GeometryPrecisionReducer.reduce(g2, pm));
					} catch (final RuntimeException e3) {
						try {
							return EnhancedPrecisionOp.difference(g1, g2);
						} catch (final RuntimeException last) {
							return null; // return g1; ??
						}
					}
				}
			}
		}
	}
	
	/**
	 * Retrieve a CRS from a WKT encoding. If Geotools fails to initialize
	 * a CRS factory, then null is return together with a stack trace of
	 * failure
	 * 
	 * @param wktCRS
	 * @return
	 */
	public static CoordinateReferenceSystem getCRSfromWKT(String wktCRS) {
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.parseWKT(wktCRS);
		} catch (FactoryException e) {
			
			e.printStackTrace();
		}
		return crs;
	}

}
