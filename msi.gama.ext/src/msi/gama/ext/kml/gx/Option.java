
package msi.gama.ext.kml.gx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "optionType")
@XmlRootElement(name = "Option", namespace = "http://www.google.com/kml/ext/2.2")
public class Option implements Cloneable
{

    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "enabled")
    protected boolean enabled;

    public Option() {
        super();
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * 
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 
     */
    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((prime*result)+((name == null)? 0 :name.hashCode()));
        result = ((prime*result)+(new Boolean(enabled).hashCode()));
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
        if ((obj instanceof Option) == false) {
            return false;
        }
        Option other = ((Option) obj);
        if (name == null) {
            if (other.name!= null) {
                return false;
            }
        } else {
            if (name.equals(other.name) == false) {
                return false;
            }
        }
        if (enabled!= other.enabled) {
            return false;
        }
        return true;
    }

    /**
     * fluent setter
     * @see #setName(String)
     * 
     * @param name
     *     required parameter
     */
    public Option withName(final String name) {
        this.setName(name);
        return this;
    }

    /**
     * fluent setter
     * @see #setEnabled(boolean)
     * 
     * @param enabled
     *     required parameter
     */
    public Option withEnabled(final boolean enabled) {
        this.setEnabled(enabled);
        return this;
    }

    @Override
    public Option clone() {
        Option copy;
        try {
            copy = ((Option) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        return copy;
    }

}
