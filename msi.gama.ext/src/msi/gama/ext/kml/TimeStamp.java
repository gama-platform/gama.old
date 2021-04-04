
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
 * <TimeStamp>
 * <p>
 * Represents a single moment in time. This is a simple element and contains no children. 
 * Its value is a dateTime, specified in XML time (see XML Schema Part 2: Datatypes 
 * Second Edition). The precision of the TimeStamp is dictated by the dateTime value 
 * in the <when> element. 
 * </p>
 * 
 * Syntax: 
 * <pre><strong>&lt;TimeStamp id=ID&gt;</strong>
 *   &lt;when&gt;...&lt;/when&gt;      &lt;!-- kml:dateTime --&gt;
 * <strong>&lt;/TimeStamp&gt;</strong> </pre>
 * 
 * Extends: 
 * @see: <TimePrimitive>
 * 
 * Contained By: 
 * @see: <Feature>
 * @see: A copy of the <TimeSpan> and <TimeStamp> elements, in the extension namespace. This allows for the inclusion of time values in AbstractViews (<Camera> and <LookAt>). Time values are used to control historical imagery, sunlight, and visibility of time-stamped Features.
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeStampType", propOrder = {
    "when",
    "timeStampSimpleExtension",
    "timeStampObjectExtension"
})
@XmlRootElement(name = "TimeStamp", namespace = "http://www.opengis.net/kml/2.2")
public class TimeStamp
    extends TimePrimitive
    implements Cloneable
{

    /**
     * <when>
     * <p>
     * Specifies a single moment in time. The value is a dateTime, which can be one of 
     * the following: dateTime gives second resolution date gives day resolution gYearMonth 
     * gives month resolution gYear gives year resolution 
     * </p>
     * 
     * 
     * 
     */
    protected String when;
    @XmlElement(name = "TimeStampSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> timeStampSimpleExtension;
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
    @XmlElement(name = "TimeStampObjectExtensionGroup")
    protected List<AbstractObject> timeStampObjectExtension;

    public TimeStamp() {
        super();
    }

    /**
     * @see when
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getWhen() {
        return when;
    }

    /**
     * @see when
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setWhen(String value) {
        this.when = value;
    }

    /**
     * @see timeStampSimpleExtension
     * 
     */
    public List<Object> getTimeStampSimpleExtension() {
        if (timeStampSimpleExtension == null) {
            timeStampSimpleExtension = new ArrayList<Object>();
        }
        return this.timeStampSimpleExtension;
    }

    /**
     * @see timeStampObjectExtension
     * 
     */
    public List<AbstractObject> getTimeStampObjectExtension() {
        if (timeStampObjectExtension == null) {
            timeStampObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.timeStampObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((when == null)? 0 :when.hashCode()));
        result = ((prime*result)+((timeStampSimpleExtension == null)? 0 :timeStampSimpleExtension.hashCode()));
        result = ((prime*result)+((timeStampObjectExtension == null)? 0 :timeStampObjectExtension.hashCode()));
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
        if ((obj instanceof TimeStamp) == false) {
            return false;
        }
        TimeStamp other = ((TimeStamp) obj);
        if (when == null) {
            if (other.when!= null) {
                return false;
            }
        } else {
            if (when.equals(other.when) == false) {
                return false;
            }
        }
        if (timeStampSimpleExtension == null) {
            if (other.timeStampSimpleExtension!= null) {
                return false;
            }
        } else {
            if (timeStampSimpleExtension.equals(other.timeStampSimpleExtension) == false) {
                return false;
            }
        }
        if (timeStampObjectExtension == null) {
            if (other.timeStampObjectExtension!= null) {
                return false;
            }
        } else {
            if (timeStampObjectExtension.equals(other.timeStampObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see timeStampSimpleExtension
     * 
     * @param timeStampSimpleExtension
     */
    public void setTimeStampSimpleExtension(final List<Object> timeStampSimpleExtension) {
        this.timeStampSimpleExtension = timeStampSimpleExtension;
    }

    /**
     * add a value to the timeStampSimpleExtension property collection
     * 
     * @param timeStampSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public TimeStamp addToTimeStampSimpleExtension(final Object timeStampSimpleExtension) {
        this.getTimeStampSimpleExtension().add(timeStampSimpleExtension);
        return this;
    }

    /**
     * @see timeStampObjectExtension
     * 
     * @param timeStampObjectExtension
     */
    public void setTimeStampObjectExtension(final List<AbstractObject> timeStampObjectExtension) {
        this.timeStampObjectExtension = timeStampObjectExtension;
    }

    /**
     * add a value to the timeStampObjectExtension property collection
     * 
     * @param timeStampObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public TimeStamp addToTimeStampObjectExtension(final AbstractObject timeStampObjectExtension) {
        this.getTimeStampObjectExtension().add(timeStampObjectExtension);
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
    public TimeStamp addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * @see timePrimitiveSimpleExtension
     * 
     */
    @Obvious
    @Override
    public void setTimePrimitiveSimpleExtension(final List<Object> timePrimitiveSimpleExtension) {
        super.setTimePrimitiveSimpleExtension(timePrimitiveSimpleExtension);
    }

    @Obvious
    @Override
    public TimeStamp addToTimePrimitiveSimpleExtension(final Object timePrimitiveSimpleExtension) {
        super.getTimePrimitiveSimpleExtension().add(timePrimitiveSimpleExtension);
        return this;
    }

    /**
     * @see timePrimitiveObjectExtension
     * 
     */
    @Obvious
    @Override
    public void setTimePrimitiveObjectExtension(final List<AbstractObject> timePrimitiveObjectExtension) {
        super.setTimePrimitiveObjectExtension(timePrimitiveObjectExtension);
    }

    @Obvious
    @Override
    public TimeStamp addToTimePrimitiveObjectExtension(final AbstractObject timePrimitiveObjectExtension) {
        super.getTimePrimitiveObjectExtension().add(timePrimitiveObjectExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setWhen(String)
     * 
     * @param when
     *     required parameter
     */
    public TimeStamp withWhen(final String when) {
        this.setWhen(when);
        return this;
    }

    /**
     * fluent setter
     * @see #setTimeStampSimpleExtension(List<Object>)
     * 
     * @param timeStampSimpleExtension
     *     required parameter
     */
    public TimeStamp withTimeStampSimpleExtension(final List<Object> timeStampSimpleExtension) {
        this.setTimeStampSimpleExtension(timeStampSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setTimeStampObjectExtension(List<AbstractObject>)
     * 
     * @param timeStampObjectExtension
     *     required parameter
     */
    public TimeStamp withTimeStampObjectExtension(final List<AbstractObject> timeStampObjectExtension) {
        this.setTimeStampObjectExtension(timeStampObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public TimeStamp withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public TimeStamp withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public TimeStamp withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Obvious
    @Override
    public TimeStamp withTimePrimitiveSimpleExtension(final List<Object> timePrimitiveSimpleExtension) {
        super.withTimePrimitiveSimpleExtension(timePrimitiveSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public TimeStamp withTimePrimitiveObjectExtension(final List<AbstractObject> timePrimitiveObjectExtension) {
        super.withTimePrimitiveObjectExtension(timePrimitiveObjectExtension);
        return this;
    }

    @Override
    public TimeStamp clone() {
        TimeStamp copy;
        copy = ((TimeStamp) super.clone());
        copy.timeStampSimpleExtension = new ArrayList<Object>((getTimeStampSimpleExtension().size()));
        for (Object iter: timeStampSimpleExtension) {
            copy.timeStampSimpleExtension.add(iter);
        }
        copy.timeStampObjectExtension = new ArrayList<AbstractObject>((getTimeStampObjectExtension().size()));
        for (AbstractObject iter: timeStampObjectExtension) {
            copy.timeStampObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
