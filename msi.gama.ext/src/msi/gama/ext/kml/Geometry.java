
package msi.gama.ext.kml;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.annotations.Obvious;
import msi.gama.ext.kml.gx.MultiTrack;
import msi.gama.ext.kml.gx.Track;


/**
 * <Geometry>
 * <p>
 * This is an abstract element and cannot be used directly in a KML file. It provides 
 * a placeholder object for all derived Geometry objects. 
 * </p>
 * 
 * Syntax: 
 * <pre>&lt;!-- abstract element; do not create --&gt;
 * <strong>&lt;!<em>-- Geometry</em> id="ID" --&gt;                 &lt;!-- Point,LineString,LinearRing,
 *                                                Polygon,MultiGeometry,Model --&gt;</strong>
 * <strong>&lt;!-- /<em>Geometry --</em>&gt;</strong></pre>
 * 
 * Extends: 
 * @see: <Object>
 * 
 * Extended By: 
 * @see: <LineString>
 * @see: <LinearRing>
 * @see: <Model>
 * @see: <Point>
 * @see: MultiGeometry
 * @see: Polygon
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractGeometryType", propOrder = {
    "geometrySimpleExtension",
    "geometryObjectExtension"
})
@XmlSeeAlso({
    MultiTrack.class,
    Track.class,
    LinearRing.class,
    Point.class,
    Model.class,
    MultiGeometry.class,
    LineString.class,
    Polygon.class
})
public abstract class Geometry
    extends AbstractObject
    implements Cloneable
{

    /**
     * <Object>
     * <p>
     * This is an abstract base class and cannot be used directly in a KML file. It provides 
     * the id attribute, which allows unique identification of a KML element, and the targetId 
     * attribute, which is used to reference objects that have already been loaded into 
     * Google Earth. The id attribute must be assigned if the <Update> mechanism is to 
     * be used. 
     * </p>
     * 
     * Syntax: 
     * <pre>&lt;!-- abstract element; do not create --&gt;<strong>
     * &lt;!-- <em>Object</em> id="ID" targetId="NCName" --&gt;
     * &lt;!-- /<em>Object</em>&gt; --&gt;</strong></pre>
     * 
     * 
     * 
     */
    @XmlElement(name = "AbstractGeometrySimpleExtensionGroup")
    protected List<Object> geometrySimpleExtension;
    /**
     * <Object>
     * <p>
     * This is an abstract base class and cannot be used directly in a KML file. It provides 
     * the id attribute, which allows unique identification of a KML element, and the targetId 
     * attribute, which is used to reference objects that have already been loaded into 
     * Google Earth. The id attribute must be assigned if the <Update> mechanism is to 
     * be used. 
     * </p>
     * 
     * Syntax: 
     * <pre>&lt;!-- abstract element; do not create --&gt;<strong>
     * &lt;!-- <em>Object</em> id="ID" targetId="NCName" --&gt;
     * &lt;!-- /<em>Object</em>&gt; --&gt;</strong></pre>
     * 
     * 
     * 
     */
    @XmlElement(name = "AbstractGeometryObjectExtensionGroup")
    protected List<AbstractObject> geometryObjectExtension;

    public Geometry() {
        super();
    }

    /**
     * @see geometrySimpleExtension
     * 
     */
    public List<Object> getGeometrySimpleExtension() {
        if (geometrySimpleExtension == null) {
            geometrySimpleExtension = new ArrayList<Object>();
        }
        return this.geometrySimpleExtension;
    }

    /**
     * @see geometryObjectExtension
     * 
     */
    public List<AbstractObject> getGeometryObjectExtension() {
        if (geometryObjectExtension == null) {
            geometryObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.geometryObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((geometrySimpleExtension == null)? 0 :geometrySimpleExtension.hashCode()));
        result = ((prime*result)+((geometryObjectExtension == null)? 0 :geometryObjectExtension.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (super.equals(obj) == false) {
            return false;
        }
        if ((obj instanceof Geometry) == false) {
            return false;
        }
        Geometry other = ((Geometry) obj);
        if (geometrySimpleExtension == null) {
            if (other.geometrySimpleExtension!= null) {
                return false;
            }
        } else {
            if (geometrySimpleExtension.equals(other.geometrySimpleExtension) == false) {
                return false;
            }
        }
        if (geometryObjectExtension == null) {
            if (other.geometryObjectExtension!= null) {
                return false;
            }
        } else {
            if (geometryObjectExtension.equals(other.geometryObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see geometrySimpleExtension
     * 
     * @param geometrySimpleExtension
     */
    public void setGeometrySimpleExtension(final List<Object> geometrySimpleExtension) {
        this.geometrySimpleExtension = geometrySimpleExtension;
    }

    /**
     * add a value to the geometrySimpleExtension property collection
     * 
     * @param geometrySimpleExtension
     *     Objects of the following type are allowed in the list: {@code <}{@link Object}{@code>}{@link JAXBElement}{@code <}{@link BigInteger}{@code>}{@link JAXBElement}{@code <}{@link Double}{@code>}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Geometry addToGeometrySimpleExtension(final Object geometrySimpleExtension) {
        this.getGeometrySimpleExtension().add(geometrySimpleExtension);
        return this;
    }

    /**
     * @see geometryObjectExtension
     * 
     * @param geometryObjectExtension
     */
    public void setGeometryObjectExtension(final List<AbstractObject> geometryObjectExtension) {
        this.geometryObjectExtension = geometryObjectExtension;
    }

    /**
     * add a value to the geometryObjectExtension property collection
     * 
     * @param geometryObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Geometry addToGeometryObjectExtension(final AbstractObject geometryObjectExtension) {
        this.getGeometryObjectExtension().add(geometryObjectExtension);
        return this;
    }

    /**
     * @see objectSimpleExtension
     * 
     */
    @Obvious
    @Override
    public void setObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.setObjectSimpleExtension(objectSimpleExtension);
    }

    @Obvious
    @Override
    public Geometry addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setGeometrySimpleExtension(List<Object>)
     * 
     * @param geometrySimpleExtension
     *     required parameter
     */
    public Geometry withGeometrySimpleExtension(final List<Object> geometrySimpleExtension) {
        this.setGeometrySimpleExtension(geometrySimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setGeometryObjectExtension(List<AbstractObject>)
     * 
     * @param geometryObjectExtension
     *     required parameter
     */
    public Geometry withGeometryObjectExtension(final List<AbstractObject> geometryObjectExtension) {
        this.setGeometryObjectExtension(geometryObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public Geometry withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public Geometry withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public Geometry withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Override
    public Geometry clone() {
        Geometry copy;
        copy = ((Geometry) super.clone());
        copy.geometrySimpleExtension = new ArrayList<Object>((getGeometrySimpleExtension().size()));
        for (Object iter: geometrySimpleExtension) {
            copy.geometrySimpleExtension.add(iter);
        }
        copy.geometryObjectExtension = new ArrayList<AbstractObject>((getGeometryObjectExtension().size()));
        for (AbstractObject iter: geometryObjectExtension) {
            copy.geometryObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
