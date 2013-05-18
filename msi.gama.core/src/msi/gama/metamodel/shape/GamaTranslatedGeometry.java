/**
 * Created by drogoul, 18 mai 2013
 * 
 */
package msi.gama.metamodel.shape;

import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import com.vividsolutions.jts.geom.*;

/**
 * Class GamaTranslatedGeometry. A geometry that possesses a link to a reference IShape and a translation. All the
 * operations are transmitted to the reference geometry, but take this translation into account.
 * 
 * This class does not allow any other transformation to the geometry than translation (no scaling, no rotation, etc.).
 * TODO This might come later when rotatedBy() and scaledBy() will be redefined outside GamaShape.
 * 
 * @author drogoul
 * @since 18 mai 2013
 * 
 */
public class GamaTranslatedGeometry implements IShape {

	ILocation absoluteLocation;
	double dx, dy, dz;
	final IShape reference;
	IAgent agent;

	public GamaTranslatedGeometry(final IShape reference, final ILocation loc) {
		this.reference = reference;
		setLocation(loc);
	}

	/**
	 * Method setLocation()
	 * @see msi.gama.common.interfaces.ILocated#setLocation(msi.gama.metamodel.shape.ILocation)
	 */
	@Override
	public void setLocation(final ILocation loc) {
		absoluteLocation = loc;
		dx = absoluteLocation.getX() - reference.getLocation().getX();
		dy = absoluteLocation.getY() - reference.getLocation().getY();
		dz = absoluteLocation.getZ() - reference.getLocation().getZ();
	}

	/**
	 * Method getLocation()
	 * @see msi.gama.common.interfaces.ILocated#getLocation()
	 */
	@Override
	public ILocation getLocation() {
		return absoluteLocation;
	}

	/**
	 * Method stringValue()
	 * @see msi.gama.common.interfaces.IValue#stringValue(msi.gama.runtime.IScope)
	 */
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return reference.stringValue(scope) + " translated to " + absoluteLocation.stringValue(scope);
	}

	/**
	 * Method copy()
	 * @see msi.gama.common.interfaces.IValue#copy(msi.gama.runtime.IScope)
	 */
	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		return new GamaTranslatedGeometry((IShape) reference.copy(scope), absoluteLocation.copy(scope));
	}

	/**
	 * Method toGaml()
	 * @see msi.gama.common.interfaces.IGamlable#toGaml()
	 */
	@Override
	public String toGaml() {
		return reference.toGaml() + " at_location " + absoluteLocation.toGaml();
	}

	/**
	 * Method getAttributes(). The attributes are shared by all the translated geometries. Another option would be to
	 * maintain a map of attributes in each translated shape, but it is costly.
	 * @see msi.gama.common.interfaces.IAttributed#getAttributes()
	 */
	@Override
	public GamaMap getAttributes() {
		return reference.getAttributes();
	}

	/**
	 * Method getOrCreateAttributes()
	 * @see msi.gama.common.interfaces.IAttributed#getOrCreateAttributes()
	 */
	@Override
	public GamaMap getOrCreateAttributes() {
		return reference.getOrCreateAttributes();
	}

	/**
	 * Method getAttribute()
	 * @see msi.gama.common.interfaces.IAttributed#getAttribute(java.lang.Object)
	 */
	@Override
	public Object getAttribute(final Object key) {
		return reference.getAttribute(key);
	}

	/**
	 * Method setAttribute()
	 * @see msi.gama.common.interfaces.IAttributed#setAttribute(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setAttribute(final Object key, final Object value) {
		reference.setAttribute(key, value);
	}

	/**
	 * Method hasAttribute()
	 * @see msi.gama.common.interfaces.IAttributed#hasAttribute(java.lang.Object)
	 */
	@Override
	public boolean hasAttribute(final Object key) {
		return reference.hasAttribute(key);
	}

	/**
	 * Method getAgent()
	 * @see msi.gama.metamodel.shape.IShape#getAgent()
	 */
	@Override
	public IAgent getAgent() {
		return agent;
	}

	/**
	 * Method setAgent()
	 * @see msi.gama.metamodel.shape.IShape#setAgent(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void setAgent(final IAgent agent) {
		this.agent = agent;
	}

	/**
	 * Method getGeometry()
	 * @see msi.gama.metamodel.shape.IShape#getGeometry()
	 */
	@Override
	public IShape getGeometry() {
		return this; // or the translated geometry ??
	}

	/**
	 * Method setGeometry()
	 * @see msi.gama.metamodel.shape.IShape#setGeometry(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public void setGeometry(final IShape g) {
		// Not allowed. The reference geometry is final
	}

	/**
	 * Method isPoint()
	 * @see msi.gama.metamodel.shape.IShape#isPoint()
	 */
	@Override
	public boolean isPoint() {
		return reference.isPoint();
	}

	/**
	 * Method getInnerGeometry()
	 * @see msi.gama.metamodel.shape.IShape#getInnerGeometry()
	 */
	@Override
	public Geometry getInnerGeometry() {
		final Geometry copy = (Geometry) reference.getInnerGeometry().clone();
		copy.apply(GamaShape.translation.by(dx, dy, dz));
		copy.geometryChanged();
		return copy;
	}

	/**
	 * Method getEnvelope()
	 * @see msi.gama.metamodel.shape.IShape#getEnvelope()
	 */
	@Override
	public Envelope getEnvelope() {
		final Envelope copy = new Envelope(reference.getEnvelope());
		copy.translate(dx, dy);
		return copy;
	}

	/**
	 * Method covers()
	 * @see msi.gama.metamodel.shape.IShape#covers(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public boolean covers(final IShape g) {
		// TODO Use prepared geometries like in GamaShape ?
		return getInnerGeometry().covers(g.getInnerGeometry());
	}

	/**
	 * Method crosses()
	 * @see msi.gama.metamodel.shape.IShape#crosses(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public boolean crosses(final IShape g) {
		return getInnerGeometry().crosses(g.getInnerGeometry());
	}

	/**
	 * Method euclidianDistanceTo()
	 * @see msi.gama.metamodel.shape.IShape#euclidianDistanceTo(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		return g.euclidianDistanceTo(this);
	}

	/**
	 * Method euclidianDistanceTo()
	 * @see msi.gama.metamodel.shape.IShape#euclidianDistanceTo(msi.gama.metamodel.shape.ILocation)
	 */
	@Override
	public double euclidianDistanceTo(final ILocation g) {
		if ( g.isPoint() ) { return GamaShape.Operations.getDistance(getInnerGeometry(), (Coordinate) g); }
		return g.euclidianDistanceTo(this);
	}

	/**
	 * Method intersects()
	 * @see msi.gama.metamodel.shape.IShape#intersects(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public boolean intersects(final IShape g) {
		return getInnerGeometry().intersects(g.getInnerGeometry());
	}

	/**
	 * Method getPerimeter()
	 * @see msi.gama.metamodel.shape.IShape#getPerimeter()
	 */
	@Override
	public double getPerimeter() {
		return reference.getPerimeter();
	}

	/**
	 * Method setInnerGeometry()
	 * @see msi.gama.metamodel.shape.IShape#setInnerGeometry(com.vividsolutions.jts.geom.Geometry)
	 */
	@Override
	public void setInnerGeometry(final Geometry intersection) {}

	/**
	 * Method dispose()
	 * @see msi.gama.metamodel.shape.IShape#dispose()
	 */
	@Override
	public void dispose() {}

}
