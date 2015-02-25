package idees.gaml.operators;

import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gaml.types.Types;

public class SpatialInterpolations {

	@operator(value = { "IDW" }, category = { IOperatorCategory.SPATIAL })
	@doc(value = "Inverse Distance Weighting (IDW) is a type of deterministic method for multivariate "
		+ "interpolation with a known scattered set of points. The assigned values to each geometry are calculated with a weighted average of the values available at the known points. See: http://en.wikipedia.org/wiki/Inverse_distance_weighting "
		+ "Usage: IDW (list of geometries, map of points (key: point, value: value), power parameter)",
		examples = { @example(value = "IDW([ag1, ag2, ag3, ag4, ag5],[{10,10}::25.0, {10,80}::10.0, {100,10}::15.0], 2)",
			equals = "for example, can return [ag1::12.0, ag2::23.0,ag3::12.0,ag4::14.0,ag5::17.0]",
			isExecutable = false) })
	public static
		GamaMap<IShape, Double> primIDW(final IScope scope, final IContainer<?, ? extends IShape> geometries,
			final GamaMap<GamaPoint, Double> points, final int power) {
		GamaMap<IShape, Double> results = GamaMapFactory.create(Types.GEOMETRY, Types.FLOAT);
		if ( points == null || points.isEmpty() ) { return null; }
		if ( geometries == null || geometries.isEmpty(scope) ) { return results; }
		for ( IShape geom : geometries.iterable(scope) ) {
			double sum = 0;
			double weight = 0;
			double sumNull = 0;
			int nbNull = 0;
			for ( GamaPoint pt : points.keySet() ) {
				double dist = geom.euclidianDistanceTo(pt);
				if ( dist == 0 ) {
					nbNull++;
					sumNull += points.get(pt);
				}
				if ( nbNull == 0 ) {
					double w = 1 / Math.pow(dist, power);
					weight += w;
					sum += w * points.get(pt);
				}
			}
			if ( nbNull > 0 ) {
				results.put(geom, sumNull / nbNull);
			} else {
				results.put(geom, sum / weight);
			}

		}
		return results;
	}

}
