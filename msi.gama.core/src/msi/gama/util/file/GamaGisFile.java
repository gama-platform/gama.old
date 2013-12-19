/**
 * Created by drogoul, 12 déc. 2013
 * 
 */
package msi.gama.util.file;

import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Class GamaGisFile.
 * 
 * @author drogoul
 * @since 12 déc. 2013
 * 
 */
public abstract class GamaGisFile extends GamaFile<Integer, GamaShape> {

	// The code to force reading the GIS data as already projected
	public static final int ALREADY_PROJECTED_CODE = 0;
	protected IProjection gis;
	protected Integer initialCRSCode = null;

	// Faire les tests sur ALREADY_PROJECTED ET LE PASSER AUSSI A GIS UTILS ???

	/**
	 * Returns the CRS defined with this file (in a ".prj" file or elsewhere)
	 * @return
	 */
	protected CoordinateReferenceSystem getExistingCRS(final IScope scope) {
		if ( initialCRSCode != null ) { return scope.getModel().getProjectionFactory().getCRS(initialCRSCode); }
		CoordinateReferenceSystem crs = getOwnCRS();
		if ( crs == null ) {
			crs = scope.getModel().getProjectionFactory().getDefaultInitialCRS();
		}
		return crs;
	}

	/**
	 * @return
	 */
	protected abstract CoordinateReferenceSystem getOwnCRS();

	protected void computeProjection(final IScope scope, final Envelope env) {
		CoordinateReferenceSystem crs = getExistingCRS(scope);
		gis = scope.getModel().getProjectionFactory().fromCRS(crs, env);
		// return gis.getInitialCRS();

		//
		// // If we have a forced EPSG code for the initial CRS, we use it (even if the .prj file is present).
		// if ( initialCRSCode != null ) {
		// gis = ProjectionFactory.fromEPSG(initialCRSCode, env);
		// return gis.getInitialCRS();
		// }
		// CoordinateReferenceSystem crs = getExistingCRS();
		// // If we have a .prj file or an existing CRS inside the project, we use it
		// if ( crs != null ) {
		// gis = ProjectionFactory.fromCRS(crs, env);
		// // If the user does not consider the data to be projected, take the default value in the
		// // preferences else pass the code for already projected data to GisUtils
		// } else if ( !GamaPreferences.LIB_PROJECTED.getValue() ) {
		// gis = ProjectionFactory.fromEPSG(GamaPreferences.LIB_INITIAL_CRS.getValue(), env);
		// } else {
		// gis = ProjectionFactory.fromEPSG(ALREADY_PROJECTED_CODE, env);
		// }
		// return gis.getInitialCRS();
	}

	public GamaGisFile(final IScope scope, final String pathName, final Integer code) {
		super(scope, pathName);
		initialCRSCode = code;
	}

	/**
	 * Method flushBuffer()
	 * @see msi.gama.util.file.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// Not yet done for GIS files
	}

	public IProjection getGis(final IScope scope) {
		if ( gis == null ) {
			fillBuffer(scope);
		}
		return gis;
	}

}
