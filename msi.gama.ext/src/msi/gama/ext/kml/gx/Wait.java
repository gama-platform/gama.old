
package msi.gama.ext.kml.gx;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.annotations.Obvious;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WaitType", propOrder = {
    "duration"
})
@XmlRootElement(name = "Wait", namespace = "http://www.google.com/kml/ext/2.2")
public class Wait
    extends TourPrimitive
    implements Cloneable
{

    @XmlElement(defaultValue = "0.0")
    protected double duration;

    public Wait() {
        super();
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getDuration() {
        return duration;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setDuration(double value) {
        this.duration = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(duration);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
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
        if ((obj instanceof Wait) == false) {
            return false;
        }
        Wait other = ((Wait) obj);
        if (duration!= other.duration) {
            return false;
        }
        return true;
    }

    /**
     * fluent setter
     * @see #setDuration(double)
     * 
     * @param duration
     *     required parameter
     */
    public Wait withDuration(final double duration) {
        this.setDuration(duration);
        return this;
    }

    @Obvious
    @Override
    public Wait withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public Wait withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public Wait withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Override
    public Wait clone() {
        Wait copy;
        copy = ((Wait) super.clone());
        return copy;
    }

}
