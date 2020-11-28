/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.projection.IProjection.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.topology.projection;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.runtime.IScope;

/**
 * Class IProjection.
 * 
 * @author drogoul
 * @since 17 déc. 2013
 * 
 */
public interface IProjection {

	public abstract void createTransformation(final MathTransform t);

	public abstract Geometry transform(final Geometry g);

	public abstract Geometry inverseTransform(final Geometry g);

	public abstract CoordinateReferenceSystem getInitialCRS(IScope scope);

	public abstract CoordinateReferenceSystem getTargetCRS(IScope scope);

	public abstract Envelope3D getProjectedEnvelope();

	/**
	 * @param geom
	 */
	public abstract void translate(Geometry geom);

	public abstract void inverseTranslate(Geometry geom);
	

	public abstract void convertUnit(Geometry geom);

	public abstract void inverseConvertUnit(Geometry geom);

}