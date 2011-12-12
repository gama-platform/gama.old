/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2011
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
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
@type(value = IType.GEOM_STR, id = IType.GEOMETRY, wraps = { GamaGeometry.class, IGeometry.class })
public class GamaGeometryType extends GamaType<IGeometry> {

	public static GeometryFactory factory = new GeometryFactory();

	@Override
	public IGeometry cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	public static IGeometry staticCast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		if ( obj instanceof IGeometry ) { return ((IGeometry) obj).getGeometry(); }
		if ( obj instanceof GamaPoint ) { return GamaGeometry.createPoint((GamaPoint) obj); }
		if ( obj instanceof ISpecies ) {
			GamaList<IAgent> agents =
				scope.getAgentScope().getPopulationFor((ISpecies) obj).getAgentsList();
			return geometriesToGeometry(agents);
		}
		if ( obj instanceof GamaPair ) { return pairToGeometry(scope, (GamaPair) obj); }
		if ( obj instanceof IContainer ) {
			if ( isPoints((IContainer) obj) ) { return pointsToGeometry(scope,
				(IContainer<?, GamaPoint>) obj); }
			return geometriesToGeometry((IContainer) obj);
		}

		// Faire ici tous les casts nécessaires pour construire des géométries (liste, string, etc.)
		return null;
	}

	/**
	 * @param obj
	 * @return
	 */
	private static boolean isPoints(final IContainer obj) {
		for ( Object o : obj ) {
			if ( !(o instanceof GamaPoint) ) { return false; }
		}
		return true;
	}

	@Override
	public GamaGeometry getDefault() {
		return null; // Retourner un point; ?
	}

	public static GeometryFactory getFactory() {
		return factory;
	}

	public static GamaGeometry geometriesToGeometry(final IContainer<?, ? extends IGeometry> ags)
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
		final IContainer<?, GamaPoint> coordinates) throws GamaRuntimeException {
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

	public static IGeometry pairToGeometry(final IScope scope, final GamaPair p)
		throws GamaRuntimeException {
		IGeometry first = staticCast(scope, p.first(), null);
		if ( first == null ) { return null; }
		IGeometry second = staticCast(scope, p.last(), null);
		if ( second == null ) { return null; }
		return new GamaDynamicLink(first, second);
	}

}
