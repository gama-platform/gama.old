
package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.annotations.Obvious;


/**
 * <lod>
 * <p>
 * <minLodPixels> (required) Measurement in screen pixels that represents the minimum 
 * limit of the visibility range for a given Region. Google Earth calculates the size 
 * of the Region when projected onto screen space. Then it computes the square root 
 * of the Region's area (if, for example, the Region is square and the viewpoint is 
 * directly above the Region, and the Region is not tilted, this measurement is equal 
 * to the width of the projected Region). If this measurement falls within the limits 
 * defined by <minLodPixels> and <maxLodPixels> (and if the <LatLonAltBox> is in view), 
 * the Region is active. If this limit is not reached, the associated geometry is considered 
 * to be too far from the user's viewpoint to be drawn. <maxLodPixels> Measurement 
 * in screen pixels that represents the maximum limit of the visibility range for a 
 * given Region. A value of −1, the default, indicates "active to infinite size." <minFadeExtent> 
 * Distance over which the geometry fades, from fully opaque to fully transparent. 
 * This ramp value, expressed in screen pixels, is applied at the minimum end of the 
 * LOD (visibility) limits. <maxFadeExtent> Distance over which the geometry fades, 
 * from fully transparent to fully opaque. This ramp value, expressed in screen pixels, 
 * is applied at the maximum end of the LOD (visibility) limits. 
 * </p>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LodType", propOrder = {
    "minLodPixels",
    "maxLodPixels",
    "minFadeExtent",
    "maxFadeExtent",
    "lodSimpleExtension",
    "lodObjectExtension"
})
@XmlRootElement(name = "Lod", namespace = "http://www.opengis.net/kml/2.2")
public class Lod
    extends AbstractObject
    implements Cloneable
{

    /**
     * <minlodpixels> (required)
     * <p>
     * Measurement in screen pixels that represents the minimum limit of the visibility 
     * range for a given Region. Google Earth calculates the size of the Region when projected 
     * onto screen space. Then it computes the square root of the Region's area (if, for 
     * example, the Region is square and the viewpoint is directly above the Region, and 
     * the Region is not tilted, this measurement is equal to the width of the projected 
     * Region). If this measurement falls within the limits defined by <minLodPixels> and 
     * <maxLodPixels> (and if the <LatLonAltBox> is in view), the Region is active. If 
     * this limit is not reached, the associated geometry is considered to be too far from 
     * the user's viewpoint to be drawn. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "0.0")
    protected double minLodPixels;
    /**
     * <maxlodpixels>
     * <p>
     * Measurement in screen pixels that represents the maximum limit of the visibility 
     * range for a given Region. A value of −1, the default, indicates "active to infinite 
     * size." 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "-1.0")
    protected double maxLodPixels;
    /**
     * <minfadeextent>
     * <p>
     * Distance over which the geometry fades, from fully opaque to fully transparent. 
     * This ramp value, expressed in screen pixels, is applied at the minimum end of the 
     * LOD (visibility) limits. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "0.0")
    protected double minFadeExtent;
    /**
     * <maxfadeextent>
     * <p>
     * Distance over which the geometry fades, from fully transparent to fully opaque. 
     * This ramp value, expressed in screen pixels, is applied at the maximum end of the 
     * LOD (visibility) limits. 
     * </p>
     * <p>
     * In the following diagram, if P=the calculated projected pixel size, the circled 
     * numbers indicate the following: if (P < minLodPixels) opacity=0 //#1 in diagram 
     * else if(P < minLodPixels + minFadeExtent) opacity=(P - minLodPixels)/minFadeExtent 
     * //#2 in diagram else if (P < maxLodPixels - maxFadeExtent) opacity=1 //#3 in diagram 
     * else if (P < maxLodPixels) opacity=(maxLodPixels-P)/maxFadeExtent //#4 in diagram 
     * else opacity=0 //#5 in diagram 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "0.0")
    protected double maxFadeExtent;
    @XmlElement(name = "LodSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> lodSimpleExtension;
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
    @XmlElement(name = "LodObjectExtensionGroup")
    protected List<AbstractObject> lodObjectExtension;

    public Lod() {
        super();
    }

    /**
     * @see minLodPixels
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getMinLodPixels() {
        return minLodPixels;
    }

    /**
     * @see minLodPixels
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setMinLodPixels(double value) {
        this.minLodPixels = value;
    }

    /**
     * @see maxLodPixels
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getMaxLodPixels() {
        return maxLodPixels;
    }

    /**
     * @see maxLodPixels
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setMaxLodPixels(double value) {
        this.maxLodPixels = value;
    }

    /**
     * @see minFadeExtent
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getMinFadeExtent() {
        return minFadeExtent;
    }

    /**
     * @see minFadeExtent
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setMinFadeExtent(double value) {
        this.minFadeExtent = value;
    }

    /**
     * @see maxFadeExtent
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getMaxFadeExtent() {
        return maxFadeExtent;
    }

    /**
     * @see maxFadeExtent
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setMaxFadeExtent(double value) {
        this.maxFadeExtent = value;
    }

    /**
     * @see lodSimpleExtension
     * 
     */
    public List<Object> getLodSimpleExtension() {
        if (lodSimpleExtension == null) {
            lodSimpleExtension = new ArrayList<Object>();
        }
        return this.lodSimpleExtension;
    }

    /**
     * @see lodObjectExtension
     * 
     */
    public List<AbstractObject> getLodObjectExtension() {
        if (lodObjectExtension == null) {
            lodObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.lodObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(minLodPixels);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        temp = Double.doubleToLongBits(maxLodPixels);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        temp = Double.doubleToLongBits(minFadeExtent);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        temp = Double.doubleToLongBits(maxFadeExtent);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        result = ((prime*result)+((lodSimpleExtension == null)? 0 :lodSimpleExtension.hashCode()));
        result = ((prime*result)+((lodObjectExtension == null)? 0 :lodObjectExtension.hashCode()));
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
        if ((obj instanceof Lod) == false) {
            return false;
        }
        Lod other = ((Lod) obj);
        if (minLodPixels!= other.minLodPixels) {
            return false;
        }
        if (maxLodPixels!= other.maxLodPixels) {
            return false;
        }
        if (minFadeExtent!= other.minFadeExtent) {
            return false;
        }
        if (maxFadeExtent!= other.maxFadeExtent) {
            return false;
        }
        if (lodSimpleExtension == null) {
            if (other.lodSimpleExtension!= null) {
                return false;
            }
        } else {
            if (lodSimpleExtension.equals(other.lodSimpleExtension) == false) {
                return false;
            }
        }
        if (lodObjectExtension == null) {
            if (other.lodObjectExtension!= null) {
                return false;
            }
        } else {
            if (lodObjectExtension.equals(other.lodObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see lodSimpleExtension
     * 
     * @param lodSimpleExtension
     */
    public void setLodSimpleExtension(final List<Object> lodSimpleExtension) {
        this.lodSimpleExtension = lodSimpleExtension;
    }

    /**
     * add a value to the lodSimpleExtension property collection
     * 
     * @param lodSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Lod addToLodSimpleExtension(final Object lodSimpleExtension) {
        this.getLodSimpleExtension().add(lodSimpleExtension);
        return this;
    }

    /**
     * @see lodObjectExtension
     * 
     * @param lodObjectExtension
     */
    public void setLodObjectExtension(final List<AbstractObject> lodObjectExtension) {
        this.lodObjectExtension = lodObjectExtension;
    }

    /**
     * add a value to the lodObjectExtension property collection
     * 
     * @param lodObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Lod addToLodObjectExtension(final AbstractObject lodObjectExtension) {
        this.getLodObjectExtension().add(lodObjectExtension);
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
    public Lod addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setMinLodPixels(double)
     * 
     * @param minLodPixels
     *     required parameter
     */
    public Lod withMinLodPixels(final double minLodPixels) {
        this.setMinLodPixels(minLodPixels);
        return this;
    }

    /**
     * fluent setter
     * @see #setMaxLodPixels(double)
     * 
     * @param maxLodPixels
     *     required parameter
     */
    public Lod withMaxLodPixels(final double maxLodPixels) {
        this.setMaxLodPixels(maxLodPixels);
        return this;
    }

    /**
     * fluent setter
     * @see #setMinFadeExtent(double)
     * 
     * @param minFadeExtent
     *     required parameter
     */
    public Lod withMinFadeExtent(final double minFadeExtent) {
        this.setMinFadeExtent(minFadeExtent);
        return this;
    }

    /**
     * fluent setter
     * @see #setMaxFadeExtent(double)
     * 
     * @param maxFadeExtent
     *     required parameter
     */
    public Lod withMaxFadeExtent(final double maxFadeExtent) {
        this.setMaxFadeExtent(maxFadeExtent);
        return this;
    }

    /**
     * fluent setter
     * @see #setLodSimpleExtension(List<Object>)
     * 
     * @param lodSimpleExtension
     *     required parameter
     */
    public Lod withLodSimpleExtension(final List<Object> lodSimpleExtension) {
        this.setLodSimpleExtension(lodSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setLodObjectExtension(List<AbstractObject>)
     * 
     * @param lodObjectExtension
     *     required parameter
     */
    public Lod withLodObjectExtension(final List<AbstractObject> lodObjectExtension) {
        this.setLodObjectExtension(lodObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public Lod withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public Lod withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public Lod withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Override
    public Lod clone() {
        Lod copy;
        copy = ((Lod) super.clone());
        copy.lodSimpleExtension = new ArrayList<Object>((getLodSimpleExtension().size()));
        for (Object iter: lodSimpleExtension) {
            copy.lodSimpleExtension.add(iter);
        }
        copy.lodObjectExtension = new ArrayList<AbstractObject>((getLodObjectExtension().size()));
        for (AbstractObject iter: lodObjectExtension) {
            copy.lodObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
