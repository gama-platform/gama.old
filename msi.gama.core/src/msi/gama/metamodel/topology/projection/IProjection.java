/**
 * Created by drogoul, 17 déc. 2013
 * 
 */
package msi.gama.metamodel.topology.projection;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import com.vividsolutions.jts.geom.*;

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

	public abstract CoordinateReferenceSystem getInitialCRS();

	public abstract CoordinateReferenceSystem getTargetCRS();

	public abstract Envelope getProjectedEnvelope();

	/**
	 * @param geom
	 */
	public abstract void translate(Geometry geom);

	public abstract void inverseTranslate(Geometry geom);

}