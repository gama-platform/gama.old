
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
 * <gx:TimeSpan> and <gx:TimeStamp>
 * <p>
 * If <begin> or <end> is missing, then that end of the period is unbounded (see Example 
 * below). 
 * </p>
 * <p>
 * Represents an extent in time bounded by begin and end dateTimes. 
 * </p>
 * <p>
 * The dateTime is defined according to XML Schema time (see XML Schema Part 2: Datatypes 
 * Second Edition). The value can be expressed as yyyy-mm-ddThh:mm:sszzzzzz, where 
 * T is the separator between the date and the time, and the time zone is either Z 
 * (for UTC) or zzzzzz, which represents ±hh:mm in relation to UTC. Additionally, the 
 * value can be expressed as a date only. See <TimeStamp> for examples. 
 * </p>
 * 
 * Syntax: 
 * <pre><strong>&lt;TimeSpan id="ID"&gt;</strong>
 *   &lt;begin&gt;<em>...</em>&lt;/begin&gt;     &lt;!-- kml:dateTime --&gt;
 *   &lt;end&gt;<em>...</em>&lt;/end&gt;         &lt;!-- kml:dateTime --&gt;
 * <strong>&lt;/TimeSpan&gt;</strong></pre>
 * 
 * Extends: 
 * @see: <TimePrimitive>
 * 
 * Contained By: 
 * @see: <Feature>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeSpanType", propOrder = {
    "begin",
    "end",
    "timeSpanSimpleExtension",
    "timeSpanObjectExtension"
})
@XmlRootElement(name = "TimeSpan", namespace = "http://www.opengis.net/kml/2.2")
public class TimeSpan
    extends TimePrimitive
    implements Cloneable
{

    /**
     * <begin>
     * <p>
     * Describes the beginning instant of a time period. If absent, the beginning of the 
     * period is unbounded. 
     * </p>
     * 
     * 
     * 
     */
    protected String begin;
    /**
     * <end>
     * <p>
     * Describes the ending instant of a time period. If absent, the end of the period 
     * is unbounded. 
     * </p>
     * 
     * 
     * 
     */
    protected String end;
    @XmlElement(name = "TimeSpanSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> timeSpanSimpleExtension;
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
    @XmlElement(name = "TimeSpanObjectExtensionGroup")
    protected List<AbstractObject> timeSpanObjectExtension;

    public TimeSpan() {
        super();
    }

    /**
     * @see begin
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getBegin() {
        return begin;
    }

    /**
     * @see begin
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setBegin(String value) {
        this.begin = value;
    }

    /**
     * @see end
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getEnd() {
        return end;
    }

    /**
     * @see end
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setEnd(String value) {
        this.end = value;
    }

    /**
     * @see timeSpanSimpleExtension
     * 
     */
    public List<Object> getTimeSpanSimpleExtension() {
        if (timeSpanSimpleExtension == null) {
            timeSpanSimpleExtension = new ArrayList<Object>();
        }
        return this.timeSpanSimpleExtension;
    }

    /**
     * @see timeSpanObjectExtension
     * 
     */
    public List<AbstractObject> getTimeSpanObjectExtension() {
        if (timeSpanObjectExtension == null) {
            timeSpanObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.timeSpanObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((begin == null)? 0 :begin.hashCode()));
        result = ((prime*result)+((end == null)? 0 :end.hashCode()));
        result = ((prime*result)+((timeSpanSimpleExtension == null)? 0 :timeSpanSimpleExtension.hashCode()));
        result = ((prime*result)+((timeSpanObjectExtension == null)? 0 :timeSpanObjectExtension.hashCode()));
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
        if ((obj instanceof TimeSpan) == false) {
            return false;
        }
        TimeSpan other = ((TimeSpan) obj);
        if (begin == null) {
            if (other.begin!= null) {
                return false;
            }
        } else {
            if (begin.equals(other.begin) == false) {
                return false;
            }
        }
        if (end == null) {
            if (other.end!= null) {
                return false;
            }
        } else {
            if (end.equals(other.end) == false) {
                return false;
            }
        }
        if (timeSpanSimpleExtension == null) {
            if (other.timeSpanSimpleExtension!= null) {
                return false;
            }
        } else {
            if (timeSpanSimpleExtension.equals(other.timeSpanSimpleExtension) == false) {
                return false;
            }
        }
        if (timeSpanObjectExtension == null) {
            if (other.timeSpanObjectExtension!= null) {
                return false;
            }
        } else {
            if (timeSpanObjectExtension.equals(other.timeSpanObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see timeSpanSimpleExtension
     * 
     * @param timeSpanSimpleExtension
     */
    public void setTimeSpanSimpleExtension(final List<Object> timeSpanSimpleExtension) {
        this.timeSpanSimpleExtension = timeSpanSimpleExtension;
    }

    /**
     * add a value to the timeSpanSimpleExtension property collection
     * 
     * @param timeSpanSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public TimeSpan addToTimeSpanSimpleExtension(final Object timeSpanSimpleExtension) {
        this.getTimeSpanSimpleExtension().add(timeSpanSimpleExtension);
        return this;
    }

    /**
     * @see timeSpanObjectExtension
     * 
     * @param timeSpanObjectExtension
     */
    public void setTimeSpanObjectExtension(final List<AbstractObject> timeSpanObjectExtension) {
        this.timeSpanObjectExtension = timeSpanObjectExtension;
    }

    /**
     * add a value to the timeSpanObjectExtension property collection
     * 
     * @param timeSpanObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public TimeSpan addToTimeSpanObjectExtension(final AbstractObject timeSpanObjectExtension) {
        this.getTimeSpanObjectExtension().add(timeSpanObjectExtension);
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
    public TimeSpan addToObjectSimpleExtension(final Object objectSimpleExtension) {
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
    public TimeSpan addToTimePrimitiveSimpleExtension(final Object timePrimitiveSimpleExtension) {
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
    public TimeSpan addToTimePrimitiveObjectExtension(final AbstractObject timePrimitiveObjectExtension) {
        super.getTimePrimitiveObjectExtension().add(timePrimitiveObjectExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setBegin(String)
     * 
     * @param begin
     *     required parameter
     */
    public TimeSpan withBegin(final String begin) {
        this.setBegin(begin);
        return this;
    }

    /**
     * fluent setter
     * @see #setEnd(String)
     * 
     * @param end
     *     required parameter
     */
    public TimeSpan withEnd(final String end) {
        this.setEnd(end);
        return this;
    }

    /**
     * fluent setter
     * @see #setTimeSpanSimpleExtension(List<Object>)
     * 
     * @param timeSpanSimpleExtension
     *     required parameter
     */
    public TimeSpan withTimeSpanSimpleExtension(final List<Object> timeSpanSimpleExtension) {
        this.setTimeSpanSimpleExtension(timeSpanSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setTimeSpanObjectExtension(List<AbstractObject>)
     * 
     * @param timeSpanObjectExtension
     *     required parameter
     */
    public TimeSpan withTimeSpanObjectExtension(final List<AbstractObject> timeSpanObjectExtension) {
        this.setTimeSpanObjectExtension(timeSpanObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public TimeSpan withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public TimeSpan withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public TimeSpan withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Obvious
    @Override
    public TimeSpan withTimePrimitiveSimpleExtension(final List<Object> timePrimitiveSimpleExtension) {
        super.withTimePrimitiveSimpleExtension(timePrimitiveSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public TimeSpan withTimePrimitiveObjectExtension(final List<AbstractObject> timePrimitiveObjectExtension) {
        super.withTimePrimitiveObjectExtension(timePrimitiveObjectExtension);
        return this;
    }

    @Override
    public TimeSpan clone() {
        TimeSpan copy;
        copy = ((TimeSpan) super.clone());
        copy.timeSpanSimpleExtension = new ArrayList<Object>((getTimeSpanSimpleExtension().size()));
        for (Object iter: timeSpanSimpleExtension) {
            copy.timeSpanSimpleExtension.add(iter);
        }
        copy.timeSpanObjectExtension = new ArrayList<AbstractObject>((getTimeSpanObjectExtension().size()));
        for (AbstractObject iter: timeSpanObjectExtension) {
            copy.timeSpanObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
