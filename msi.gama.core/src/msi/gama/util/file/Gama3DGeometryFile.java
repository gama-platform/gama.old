/*********************************************************************************************
 *
 * 'Gama3DGeometryFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.file;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaPair;
import msi.gaml.operators.Cast;

public abstract class Gama3DGeometryFile extends GamaGeometryFile {

	protected AxisAngle initRotation;
	protected Envelope3D envelope;

	public Gama3DGeometryFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	public Gama3DGeometryFile(final IScope scope, final String pathName, final GamaPair<Double, GamaPoint> initRotation)
			throws GamaRuntimeException {
		super(scope, pathName);
		if (initRotation != null) {
			final Double angle = Cast.asFloat(null, initRotation.key);
			final GamaPoint axis = initRotation.value;
			this.initRotation = new AxisAngle(axis, angle);
		} else {
			this.initRotation = null;
		}
	}

	@Override
	protected IShape buildGeometry(final IScope scope) {
		final List<Geometry> faces = new ArrayList<>();
		for (final IShape shape : getBuffer().iterable(scope)) {
			faces.add(shape.getInnerGeometry());
		}
		return new GamaShape(GeometryUtils.GEOMETRY_FACTORY.buildGeometry(faces));
	}

	@Override
	public AxisAngle getInitRotation() {
		return initRotation;
	}

	public void setInitRotation(final AxisAngle initRotation) {
		this.initRotation = initRotation;
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		if (envelope == null) {
			fillBuffer(scope);
		}
		return envelope;
	}

	@Override
	public boolean is2D() {
		return false;
	}

}