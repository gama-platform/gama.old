/*******************************************************************************************************
 *
 * msi.gama.common.geometry.Matrix3D.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.geometry;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

public class Matrix3D implements Transformation3D {

	@FunctionalInterface
	public static interface TransformationVisitor {
		void visit(Transformation3D t);
	}

	List<Transformation3D> transformations = new ArrayList<Transformation3D>();

	@Override
	public void filter(final Coordinate coord) {
		for (final Transformation3D t : transformations) {
			t.filter(coord);
		}

	}

	public void add(final Transformation3D transformation) {
		transformations.add(transformation);
	}

	public void visit(final TransformationVisitor visitor) {
		for (final Transformation3D t : transformations)
			visitor.visit(t);
	}

}
