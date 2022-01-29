/*******************************************************************************************************
 *
 * Operators.java, in simtools.gaml.extensions.traffic, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package simtools.gaml.extensions.traffic.publictransport;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;

/**
 * The Class Operators.
 */
public class Operators {
	// TODO: find out why this is necessary in ESCAPE
	/**
	 * Closest to.
	 *
	 * @param scope the scope
	 * @param list the list
	 * @param shape the shape
	 * @return the i shape
	 */
	// If I use the built-in `closest_to`, ESCAPE would throw an error
	@operator(value = "closest_tob", content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1, category = { IOperatorCategory.SPATIAL }, concept = {})
	@doc(value = "Return the closest agent from a point !!", usages = {
			@usage(value = "Return the closest agent from a point !!") }, examples = {
					@example(value = "closest_tob(population, point) or list closest_tob geometry", equals = "Return the closest agent from a point !!", test = false) })
	@no_test
	public static IShape closest_to(final IScope scope, final IContainer<?, ? extends IShape> list, final IShape shape) {
		return closest(list.iterable(scope), shape);
	}
	
	/**
	 * Closest.
	 *
	 * @param collection the collection
	 * @param shape the shape
	 * @return the i shape
	 */
	public static IShape closest(final Iterable< ? extends IShape> collection, final IShape shape) {
		IShape closest1 = null;
		double min = Double.MAX_VALUE; //356 -> 373ms
		
		GamaPoint point = shape.getLocation();
		for(IShape s:collection) {
			double ax = s.getLocation().getX();
			double ay = s.getLocation().getY();
			double dist = (ax - point.getX())*(ax - point.getX())+(ay - point.getY())*(ay - point.getY());
			if (min > dist) {
				closest1 = s;
				min = dist;
			}
		}
		
		return closest1;		
	}
}
