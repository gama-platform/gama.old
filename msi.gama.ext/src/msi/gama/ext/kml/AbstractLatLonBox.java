
package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.annotations.Obvious;


/**
 * <latlonbox>
 * <p>
 * Specifies where the top, bottom, right, and left sides of a bounding box for the 
 * ground overlay are aligned. <north> Specifies the latitude of the north edge of 
 * the bounding box, in decimal degrees from 0 to ±90. <south> Specifies the latitude 
 * of the south edge of the bounding box, in decimal degrees from 0 to ±90. <east> 
 * Specifies the longitude of the east edge of the bounding box, in decimal degrees 
 * from 0 to ±180. (For overlays that overlap the meridian of 180° longitude, values 
 * can extend beyond that range.) <west> Specifies the longitude of the west edge of 
 * the bounding box, in decimal degrees from 0 to ±180. (For overlays that overlap 
 * the meridian of 180° longitude, values can extend beyond that range.) <rotation> 
 * Specifies a rotation of the overlay about its center, in degrees. Values can be 
 * ±180. The default is 0 (north). Rotations are specified in a counterclockwise direction. 
 * <LatLonBox> <north>48.25475939255556</north> <south>48.25207367852141</south> <east>-90.86591508839973</east> 
 * <west>-90.8714285289695</west> <rotation>39.37878630116985</rotation> </LatLonBox> 
 * </p>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractLatLonBoxType", propOrder = {
    "north",
    "south",
    "east",
    "west",
    "abstractLatLonBoxSimpleExtension",
    "abstractLatLonBoxObjectExtension"
})
@XmlSeeAlso({
    LatLonAltBox.class,
    LatLonBox.class
})
public abstract class AbstractLatLonBox
    extends AbstractObject
    implements Cloneable
{

    /**
     * <north> (required)
     * <p>
     * Specifies the latitude of the north edge of the bounding box, in decimal degrees 
     * from 0 to ±90. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "180.0")
    protected double north;
    /**
     * <south> (required)
     * <p>
     * Specifies the latitude of the south edge of the bounding box, in decimal degrees 
     * from 0 to ±90. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "-180.0")
    protected double south;
    /**
     * <east> (required)
     * 
     * 
     */
    @XmlElement(defaultValue = "180.0")
    protected double east;
    /**
     * <west> (required)
     * <p>
     * Specifies the longitude of the west edge of the bounding box, in decimal degrees 
     * from 0 to ±180. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "-180.0")
    protected double west;
    @XmlElement(name = "AbstractLatLonBoxSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> abstractLatLonBoxSimpleExtension;
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
    @XmlElement(name = "AbstractLatLonBoxObjectExtensionGroup")
    protected List<AbstractObject> abstractLatLonBoxObjectExtension;

    public AbstractLatLonBox() {
        super();
    }

    /**
     * @see north
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getNorth() {
        return north;
    }

    /**
     * @see north
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setNorth(double value) {
        this.north = value;
    }

    /**
     * @see south
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getSouth() {
        return south;
    }

    /**
     * @see south
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setSouth(double value) {
        this.south = value;
    }

    /**
     * @see east
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getEast() {
        return east;
    }

    /**
     * @see east
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setEast(double value) {
        this.east = value;
    }

    /**
     * @see west
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getWest() {
        return west;
    }

    /**
     * @see west
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setWest(double value) {
        this.west = value;
    }

    /**
     * @see abstractLatLonBoxSimpleExtension
     * 
     */
    public List<Object> getAbstractLatLonBoxSimpleExtension() {
        if (abstractLatLonBoxSimpleExtension == null) {
            abstractLatLonBoxSimpleExtension = new ArrayList<Object>();
        }
        return this.abstractLatLonBoxSimpleExtension;
    }

    /**
     * @see abstractLatLonBoxObjectExtension
     * 
     */
    public List<AbstractObject> getAbstractLatLonBoxObjectExtension() {
        if (abstractLatLonBoxObjectExtension == null) {
            abstractLatLonBoxObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.abstractLatLonBoxObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(north);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        temp = Double.doubleToLongBits(south);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        temp = Double.doubleToLongBits(east);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        temp = Double.doubleToLongBits(west);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        result = ((prime*result)+((abstractLatLonBoxSimpleExtension == null)? 0 :abstractLatLonBoxSimpleExtension.hashCode()));
        result = ((prime*result)+((abstractLatLonBoxObjectExtension == null)? 0 :abstractLatLonBoxObjectExtension.hashCode()));
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
        if ((obj instanceof AbstractLatLonBox) == false) {
            return false;
        }
        AbstractLatLonBox other = ((AbstractLatLonBox) obj);
        if (north!= other.north) {
            return false;
        }
        if (south!= other.south) {
            return false;
        }
        if (east!= other.east) {
            return false;
        }
        if (west!= other.west) {
            return false;
        }
        if (abstractLatLonBoxSimpleExtension == null) {
            if (other.abstractLatLonBoxSimpleExtension!= null) {
                return false;
            }
        } else {
            if (abstractLatLonBoxSimpleExtension.equals(other.abstractLatLonBoxSimpleExtension) == false) {
                return false;
            }
        }
        if (abstractLatLonBoxObjectExtension == null) {
            if (other.abstractLatLonBoxObjectExtension!= null) {
                return false;
            }
        } else {
            if (abstractLatLonBoxObjectExtension.equals(other.abstractLatLonBoxObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see abstractLatLonBoxSimpleExtension
     * 
     * @param abstractLatLonBoxSimpleExtension
     */
    public void setAbstractLatLonBoxSimpleExtension(final List<Object> abstractLatLonBoxSimpleExtension) {
        this.abstractLatLonBoxSimpleExtension = abstractLatLonBoxSimpleExtension;
    }

    /**
     * add a value to the abstractLatLonBoxSimpleExtension property collection
     * 
     * @param abstractLatLonBoxSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public AbstractLatLonBox addToAbstractLatLonBoxSimpleExtension(final Object abstractLatLonBoxSimpleExtension) {
        this.getAbstractLatLonBoxSimpleExtension().add(abstractLatLonBoxSimpleExtension);
        return this;
    }

    /**
     * @see abstractLatLonBoxObjectExtension
     * 
     * @param abstractLatLonBoxObjectExtension
     */
    public void setAbstractLatLonBoxObjectExtension(final List<AbstractObject> abstractLatLonBoxObjectExtension) {
        this.abstractLatLonBoxObjectExtension = abstractLatLonBoxObjectExtension;
    }

    /**
     * add a value to the abstractLatLonBoxObjectExtension property collection
     * 
     * @param abstractLatLonBoxObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public AbstractLatLonBox addToAbstractLatLonBoxObjectExtension(final AbstractObject abstractLatLonBoxObjectExtension) {
        this.getAbstractLatLonBoxObjectExtension().add(abstractLatLonBoxObjectExtension);
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
    public AbstractLatLonBox addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setNorth(double)
     * 
     * @param north
     *     required parameter
     */
    public AbstractLatLonBox withNorth(final double north) {
        this.setNorth(north);
        return this;
    }

    /**
     * fluent setter
     * @see #setSouth(double)
     * 
     * @param south
     *     required parameter
     */
    public AbstractLatLonBox withSouth(final double south) {
        this.setSouth(south);
        return this;
    }

    /**
     * fluent setter
     * @see #setEast(double)
     * 
     * @param east
     *     required parameter
     */
    public AbstractLatLonBox withEast(final double east) {
        this.setEast(east);
        return this;
    }

    /**
     * fluent setter
     * @see #setWest(double)
     * 
     * @param west
     *     required parameter
     */
    public AbstractLatLonBox withWest(final double west) {
        this.setWest(west);
        return this;
    }

    /**
     * fluent setter
     * @see #setAbstractLatLonBoxSimpleExtension(List<Object>)
     * 
     * @param abstractLatLonBoxSimpleExtension
     *     required parameter
     */
    public AbstractLatLonBox withAbstractLatLonBoxSimpleExtension(final List<Object> abstractLatLonBoxSimpleExtension) {
        this.setAbstractLatLonBoxSimpleExtension(abstractLatLonBoxSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setAbstractLatLonBoxObjectExtension(List<AbstractObject>)
     * 
     * @param abstractLatLonBoxObjectExtension
     *     required parameter
     */
    public AbstractLatLonBox withAbstractLatLonBoxObjectExtension(final List<AbstractObject> abstractLatLonBoxObjectExtension) {
        this.setAbstractLatLonBoxObjectExtension(abstractLatLonBoxObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public AbstractLatLonBox withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public AbstractLatLonBox withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public AbstractLatLonBox withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Override
    public AbstractLatLonBox clone() {
        AbstractLatLonBox copy;
        copy = ((AbstractLatLonBox) super.clone());
        copy.abstractLatLonBoxSimpleExtension = new ArrayList<Object>((getAbstractLatLonBoxSimpleExtension().size()));
        for (Object iter: abstractLatLonBoxSimpleExtension) {
            copy.abstractLatLonBoxSimpleExtension.add(iter);
        }
        copy.abstractLatLonBoxObjectExtension = new ArrayList<AbstractObject>((getAbstractLatLonBoxObjectExtension().size()));
        for (AbstractObject iter: abstractLatLonBoxObjectExtension) {
            copy.abstractLatLonBoxObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
