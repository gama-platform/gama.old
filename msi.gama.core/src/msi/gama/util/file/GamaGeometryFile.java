/**
 * Created by drogoul, 30 déc. 2013
 * 
 */
package msi.gama.util.file;

import java.util.*;
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import com.vividsolutions.jts.geom.*;

/**
 * Class GamaGeometryFile. An abstract class that supports loading and saving geometries in specific subclasses.
 * The buffer is a GamaList of points (GamaPoint) from which the GamaGeometry can be constructed (using
 * geometry(file("..."));)
 * 
 * @author drogoul
 * @since 30 déc. 2013
 * 
 */
public abstract class GamaGeometryFile extends GamaFile<Integer, IShape> {

	protected IShape geometry;

	public static abstract class Gama3DGeometryFile extends GamaGeometryFile {

		public Gama3DGeometryFile(final IScope scope, final String pathName) throws GamaRuntimeException {
			super(scope, pathName);
		}

		@Override
		protected IShape buildGeometry(final IScope scope) {
			List<Geometry> faces = new ArrayList();
			for ( IShape shape : buffer.iterable(scope) ) {
				faces.add(shape.getInnerGeometry());
			}
			return new GamaShape(GeometryUtils.FACTORY.buildGeometry(faces));
		}

	}

	public GamaGeometryFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	/**
	 * Method computeEnvelope()
	 * @see msi.gama.util.file.IGamaFile#computeEnvelope(msi.gama.runtime.IScope)
	 */
	@Override
	public Envelope computeEnvelope(final IScope scope) {
		return getGeometry(scope).getEnvelope();
	}

	public IShape getGeometry(final IScope scope) {
		fillBuffer(scope);
		if ( geometry == null ) {
			geometry = buildGeometry(scope);
		}
		return geometry;
	}

	/**
	 * @param scope
	 * @return
	 */
	protected abstract IShape buildGeometry(IScope scope);

}
