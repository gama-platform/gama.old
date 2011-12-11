/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.internal.types;

import java.util.List;
import msi.gama.environment.GeometricFunctions;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.util.*;
import com.vividsolutions.jts.geom.*;

/**
 * Written by drogoul Modified on 1 août 2010
 * 
 * @todo Description
 * 
 */
@type(value = IType.GEOM_STR, id = IType.GEOMETRY, wraps = { GamaGeometry.class })
public class GamaGeometryType extends GamaType<GamaGeometry> {

	@Override
	public GamaGeometry cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	public static GamaGeometry staticCast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		if ( obj instanceof IGeometry ) { return ((IGeometry) obj).getGeometry(); }
		if ( obj instanceof GamaPoint ) { return GamaGeometry.createPoint((GamaPoint) obj); }
		if ( obj instanceof ISpecies ) {
			GamaList<IAgent> agents =
				scope.getAgentScope().getPopulationFor((ISpecies) obj).getAgentsList();
			return geometriesToGeometry(agents);
		}
		if ( obj instanceof GamaPair ) { return pairToGeometry(scope, (GamaPair) obj); }
		if ( obj instanceof IGamaContainer ) {
			if ( isPoints((IGamaContainer) obj) ) { return pointsToGeometry(scope,
				(IGamaContainer<?, GamaPoint>) obj); }
			return geometriesToGeometry((IGamaContainer) obj);
		}

		// Faire ici tous les casts nécessaires pour construire des géométries (liste, string, etc.)
		return null;
	}

	/**
	 * @param obj
	 * @return
	 */
	private static boolean isPoints(final IGamaContainer obj) {
		for ( Object o : obj ) {
			if ( !(o instanceof GamaPoint) ) { return false; }
		}
		return true;
	}

	@Override
	public GamaGeometry getDefault() {
		return null; // Retourner un point; ?
	}

	public static GamaGeometry geometriesToGeometry(final IGamaContainer<?, ? extends IGeometry> ags)
		throws GamaRuntimeException {
		if ( ags == null || ags.isEmpty() ) { return null; }
		Geometry geom = ((IGeometry) ags.first()).getInnerGeometry();
		for ( IGeometry ent : ags ) {
			try {
				geom = geom.union(ent.getInnerGeometry());
			} catch (TopologyException e) {
				geom = geom.buffer(0.0).union(ent.getInnerGeometry().buffer(0.0));
			}
		}
		if ( geom != null && geom.isValid() ) { return new GamaGeometry(geom); }
		return null;
	}

	public static GamaGeometry pointsToGeometry(final IScope scope,
		final IGamaContainer<?, GamaPoint> coordinates) throws GamaRuntimeException {
		if ( coordinates != null && !coordinates.isEmpty() ) {
			List<List<GamaPoint>> geoSimp = new GamaList();
			geoSimp.add(coordinates.listValue(scope));
			List<List<List<GamaPoint>>> geomG = new GamaList();
			geomG.add(geoSimp);
			Geometry geom = GeometricFunctions.buildGeometryJTS(geomG);
			return new GamaGeometry(geom);
		}
		return null;
	}

	public static GamaGeometry pairToGeometry(final IScope scope, final GamaPair p)
		throws GamaRuntimeException {
		GamaGeometry first = staticCast(scope, p.first(), null);
		if ( first == null ) { return null; }
		GamaGeometry second = staticCast(scope, p.last(), null);
		if ( second == null ) { return null; }
		return new GamaDynamicLink(first, second);
	}

}
