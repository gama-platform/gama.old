
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
 * <TimePrimitive>
 * <p>
 * Associates this Feature with a period of time (<TimeSpan>) or a point in time (<TimeStamp>). 
 * </p>
 * <p>
 * This is an abstract element and cannot be used directly in a KML file. This element 
 * is extended by the <TimeSpan> and <TimeStamp> elements. 
 * </p>
 * 
 * Syntax: 
 * <pre>&lt;!-- abstract element; do not create --&gt;
 * <strong>&lt;!-- TimePrimitive id="ID" --&gt;            &lt;!-- TimeSpan,TimeStamp --&gt;               
 *  </strong> &lt;!-- extends Object --&gt;<strong>
 * &lt;!-- /TimePrimitive --&gt;</strong></pre>
 * 
 * Extends: 
 * @see: <Object>
 * 
 * Extended By: 
 * @see: <TimeSpan>
 * @see: <TimeStamp>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractTimePrimitiveType", propOrder = {
    "timePrimitiveSimpleExtension",
    "timePrimitiveObjectExtension"
})
@XmlSeeAlso({
    TimeSpan.class,
    TimeStamp.class
})
public abstract class TimePrimitive
    extends AbstractObject
    implements Cloneable
{

    @XmlElement(name = "AbstractTimePrimitiveSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> timePrimitiveSimpleExtension;
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
    @XmlElement(name = "AbstractTimePrimitiveObjectExtensionGroup")
    protected List<AbstractObject> timePrimitiveObjectExtension;

    public TimePrimitive() {
        super();
    }

    /**
     * @see timePrimitiveSimpleExtension
     * 
     */
    public List<Object> getTimePrimitiveSimpleExtension() {
        if (timePrimitiveSimpleExtension == null) {
            timePrimitiveSimpleExtension = new ArrayList<Object>();
        }
        return this.timePrimitiveSimpleExtension;
    }

    /**
     * @see timePrimitiveObjectExtension
     * 
     */
    public List<AbstractObject> getTimePrimitiveObjectExtension() {
        if (timePrimitiveObjectExtension == null) {
            timePrimitiveObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.timePrimitiveObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((timePrimitiveSimpleExtension == null)? 0 :timePrimitiveSimpleExtension.hashCode()));
        result = ((prime*result)+((timePrimitiveObjectExtension == null)? 0 :timePrimitiveObjectExtension.hashCode()));
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
        if ((obj instanceof TimePrimitive) == false) {
            return false;
        }
        TimePrimitive other = ((TimePrimitive) obj);
        if (timePrimitiveSimpleExtension == null) {
            if (other.timePrimitiveSimpleExtension!= null) {
                return false;
            }
        } else {
            if (timePrimitiveSimpleExtension.equals(other.timePrimitiveSimpleExtension) == false) {
                return false;
            }
        }
        if (timePrimitiveObjectExtension == null) {
            if (other.timePrimitiveObjectExtension!= null) {
                return false;
            }
        } else {
            if (timePrimitiveObjectExtension.equals(other.timePrimitiveObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see timePrimitiveSimpleExtension
     * 
     * @param timePrimitiveSimpleExtension
     */
    public void setTimePrimitiveSimpleExtension(final List<Object> timePrimitiveSimpleExtension) {
        this.timePrimitiveSimpleExtension = timePrimitiveSimpleExtension;
    }

    /**
     * add a value to the timePrimitiveSimpleExtension property collection
     * 
     * @param timePrimitiveSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public TimePrimitive addToTimePrimitiveSimpleExtension(final Object timePrimitiveSimpleExtension) {
        this.getTimePrimitiveSimpleExtension().add(timePrimitiveSimpleExtension);
        return this;
    }

    /**
     * @see timePrimitiveObjectExtension
     * 
     * @param timePrimitiveObjectExtension
     */
    public void setTimePrimitiveObjectExtension(final List<AbstractObject> timePrimitiveObjectExtension) {
        this.timePrimitiveObjectExtension = timePrimitiveObjectExtension;
    }

    /**
     * add a value to the timePrimitiveObjectExtension property collection
     * 
     * @param timePrimitiveObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public TimePrimitive addToTimePrimitiveObjectExtension(final AbstractObject timePrimitiveObjectExtension) {
        this.getTimePrimitiveObjectExtension().add(timePrimitiveObjectExtension);
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
    public TimePrimitive addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setTimePrimitiveSimpleExtension(List<Object>)
     * 
     * @param timePrimitiveSimpleExtension
     *     required parameter
     */
    public TimePrimitive withTimePrimitiveSimpleExtension(final List<Object> timePrimitiveSimpleExtension) {
        this.setTimePrimitiveSimpleExtension(timePrimitiveSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setTimePrimitiveObjectExtension(List<AbstractObject>)
     * 
     * @param timePrimitiveObjectExtension
     *     required parameter
     */
    public TimePrimitive withTimePrimitiveObjectExtension(final List<AbstractObject> timePrimitiveObjectExtension) {
        this.setTimePrimitiveObjectExtension(timePrimitiveObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public TimePrimitive withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public TimePrimitive withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public TimePrimitive withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Override
    public TimePrimitive clone() {
        TimePrimitive copy;
        copy = ((TimePrimitive) super.clone());
        copy.timePrimitiveSimpleExtension = new ArrayList<Object>((getTimePrimitiveSimpleExtension().size()));
        for (Object iter: timePrimitiveSimpleExtension) {
            copy.timePrimitiveSimpleExtension.add(iter);
        }
        copy.timePrimitiveObjectExtension = new ArrayList<AbstractObject>((getTimePrimitiveObjectExtension().size()));
        for (AbstractObject iter: timePrimitiveObjectExtension) {
            copy.timePrimitiveObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
