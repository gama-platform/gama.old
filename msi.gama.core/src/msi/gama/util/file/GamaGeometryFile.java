/*********************************************************************************************
 *
 *
 * 'GamaGeometryFile.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.util.file;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaPair;
import msi.gama.util.IList;
import msi.gaml.types.IContainerType;
import msi.gaml.types.Types;

/**
 * Class GamaGeometryFile. An abstract class that supports loading and saving
 * geometries in specific subclasses. The buffer is a GamaList of points
 * (GamaPoint) from which the GamaGeometry can be constructed (using
 * geometry(file("..."));)
 *
 * @author drogoul
 * @since 30 déc. 2013
 *
 */
public abstract class GamaGeometryFile extends GamaFile<IList<IShape>, IShape, Integer, IShape> {

	protected IShape geometry;

	public GamaGeometryFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	@Override
	public IContainerType getType() {
		return Types.FILE.of(Types.INT, Types.GEOMETRY);
	}

	/**
	 * Method computeEnvelope()
	 * 
	 * @see msi.gama.util.file.IGamaFile#computeEnvelope(msi.gama.runtime.IScope)
	 */
	@Override
	public Envelope computeEnvelope(final IScope scope) {
		return getGeometry(scope).getEnvelope();
	}

	public IShape getGeometry(final IScope scope) {
		fillBuffer(scope);
		if (geometry == null) {
			geometry = buildGeometry(scope);
		}
		return geometry;
	}

	protected abstract IShape buildGeometry(IScope scope);

	@Override
	public void invalidateContents() {
		super.invalidateContents();
		geometry = null;
	}

	public GamaPair<Double, GamaPoint> getInitRotation() {
		return null;
	}

}
