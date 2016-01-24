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

public abstract class Gama3DGeometryFile extends GamaGeometryFile {

	protected GamaPair initRotation;
	protected Envelope3D envelope;
	
	public Gama3DGeometryFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}
	public Gama3DGeometryFile(final IScope scope, final String pathName,final GamaPair initRotation) throws GamaRuntimeException {
		super(scope, pathName);
		this.initRotation = initRotation;
	}
	

	@Override
	protected IShape buildGeometry(final IScope scope) {
		List<Geometry> faces = new ArrayList();
		for ( IShape shape : getBuffer().iterable(scope) ) {
			faces.add(shape.getInnerGeometry());
		}
		return new GamaShape(GeometryUtils.FACTORY.buildGeometry(faces));
	}
	public GamaPair getInitRotation() {
		return initRotation;
	}
	public void setInitRotation(GamaPair initRotation) {
		this.initRotation = initRotation;
	}
	@Override
	public Envelope computeEnvelope(final IScope scope) {
		if (envelope == null) fillBuffer(scope);
		return envelope;
	}

}