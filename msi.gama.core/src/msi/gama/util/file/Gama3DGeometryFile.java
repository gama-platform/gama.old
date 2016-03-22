/**
 * Created by drogoul, 23 janv. 2016
 *
 */
package msi.gama.util.file;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaPair;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types;

public abstract class Gama3DGeometryFile extends GamaGeometryFile {

	protected GamaPair<Double, GamaPoint> initRotation;
	protected Envelope3D envelope;

	public Gama3DGeometryFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	public Gama3DGeometryFile(final IScope scope, final String pathName, final GamaPair<Double, GamaPoint> initRotation)
		throws GamaRuntimeException {
		super(scope, pathName);
		if (initRotation != null) {
			this.initRotation = new GamaPair(Cast.asFloat(null, initRotation.key), Cast.asPoint(null, initRotation.value), Types.FLOAT,Types.POINT);
		} else {
			this.initRotation = null;
		}
	}

	@Override
	protected IShape buildGeometry(final IScope scope) {
		List<Geometry> faces = new ArrayList();
		for ( IShape shape : getBuffer().iterable(scope) ) {
			faces.add(shape.getInnerGeometry());
		}
		return new GamaShape(GeometryUtils.FACTORY.buildGeometry(faces));
	}

	public GamaPair<Double, GamaPoint> getInitRotation() {
		return initRotation;
	}

	public void setInitRotation(final GamaPair initRotation) {
		this.initRotation = initRotation;
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		if ( envelope == null ) {
			fillBuffer(scope);
		}
		return envelope;
	}

}