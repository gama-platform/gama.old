
package msi.gama.ext.kml;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.annotations.Obvious;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BasicLinkType", propOrder = {
    "href",
    "basicLinkSimpleExtension",
    "basicLinkObjectExtension"
})
@XmlSeeAlso({
    Icon.class,
    Link.class
})
@XmlRootElement(name = "BasicLink", namespace = "http://www.opengis.net/kml/2.2")
public class BasicLink
    extends AbstractObject
    implements Cloneable
{

    protected String href;
    @XmlElement(name = "BasicLinkSimpleExtensionGroup")
    protected List<Object> basicLinkSimpleExtension;
    @XmlElement(name = "BasicLinkObjectExtensionGroup")
    protected List<AbstractObject> basicLinkObjectExtension;

    public BasicLink() {
        super();
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getHref() {
        return href;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * 
     */
    public List<Object> getBasicLinkSimpleExtension() {
        if (basicLinkSimpleExtension == null) {
            basicLinkSimpleExtension = new ArrayList<Object>();
        }
        return this.basicLinkSimpleExtension;
    }

    /**
     * 
     */
    public List<AbstractObject> getBasicLinkObjectExtension() {
        if (basicLinkObjectExtension == null) {
            basicLinkObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.basicLinkObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((href == null)? 0 :href.hashCode()));
        result = ((prime*result)+((basicLinkSimpleExtension == null)? 0 :basicLinkSimpleExtension.hashCode()));
        result = ((prime*result)+((basicLinkObjectExtension == null)? 0 :basicLinkObjectExtension.hashCode()));
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
        if ((obj instanceof BasicLink) == false) {
            return false;
        }
        BasicLink other = ((BasicLink) obj);
        if (href == null) {
            if (other.href!= null) {
                return false;
            }
        } else {
            if (href.equals(other.href) == false) {
                return false;
            }
        }
        if (basicLinkSimpleExtension == null) {
            if (other.basicLinkSimpleExtension!= null) {
                return false;
            }
        } else {
            if (basicLinkSimpleExtension.equals(other.basicLinkSimpleExtension) == false) {
                return false;
            }
        }
        if (basicLinkObjectExtension == null) {
            if (other.basicLinkObjectExtension!= null) {
                return false;
            }
        } else {
            if (basicLinkObjectExtension.equals(other.basicLinkObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the value of the basicLinkSimpleExtension property Objects of the following type(s) are allowed in the list List<Object>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withBasicLinkSimpleExtension} instead.
     * 
     * 
     * @param basicLinkSimpleExtension
     */
    public void setBasicLinkSimpleExtension(final List<Object> basicLinkSimpleExtension) {
        this.basicLinkSimpleExtension = basicLinkSimpleExtension;
    }

    /**
     * add a value to the basicLinkSimpleExtension property collection
     * 
     * @param basicLinkSimpleExtension
     *     Objects of the following type are allowed in the list: {@code <}{@link BigInteger}{@code>}{@link JAXBElement}{@code <}{@link BigInteger}{@code>}{@link JAXBElement}{@code <}{@link Object}{@code>}{@link JAXBElement}{@code <}{@link BigInteger}{@code>}{@link JAXBElement}{@code <}{@link BigInteger}{@code>}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public BasicLink addToBasicLinkSimpleExtension(final Object basicLinkSimpleExtension) {
        this.getBasicLinkSimpleExtension().add(basicLinkSimpleExtension);
        return this;
    }

    /**
     * Sets the value of the basicLinkObjectExtension property Objects of the following type(s) are allowed in the list List<AbstractObject>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withBasicLinkObjectExtension} instead.
     * 
     * 
     * @param basicLinkObjectExtension
     */
    public void setBasicLinkObjectExtension(final List<AbstractObject> basicLinkObjectExtension) {
        this.basicLinkObjectExtension = basicLinkObjectExtension;
    }

    /**
     * add a value to the basicLinkObjectExtension property collection
     * 
     * @param basicLinkObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public BasicLink addToBasicLinkObjectExtension(final AbstractObject basicLinkObjectExtension) {
        this.getBasicLinkObjectExtension().add(basicLinkObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public void setObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.setObjectSimpleExtension(objectSimpleExtension);
    }

    @Obvious
    @Override
    public BasicLink addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setHref(String)
     * 
     * @param href
     *     required parameter
     */
    public BasicLink withHref(final String href) {
        this.setHref(href);
        return this;
    }

    /**
     * fluent setter
     * @see #setBasicLinkSimpleExtension(List<Object>)
     * 
     * @param basicLinkSimpleExtension
     *     required parameter
     */
    public BasicLink withBasicLinkSimpleExtension(final List<Object> basicLinkSimpleExtension) {
        this.setBasicLinkSimpleExtension(basicLinkSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setBasicLinkObjectExtension(List<AbstractObject>)
     * 
     * @param basicLinkObjectExtension
     *     required parameter
     */
    public BasicLink withBasicLinkObjectExtension(final List<AbstractObject> basicLinkObjectExtension) {
        this.setBasicLinkObjectExtension(basicLinkObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public BasicLink withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public BasicLink withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public BasicLink withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Override
    public BasicLink clone() {
        BasicLink copy;
        copy = ((BasicLink) super.clone());
        copy.basicLinkSimpleExtension = new ArrayList<Object>((getBasicLinkSimpleExtension().size()));
        for (Object iter: basicLinkSimpleExtension) {
            copy.basicLinkSimpleExtension.add(iter);
        }
        copy.basicLinkObjectExtension = new ArrayList<AbstractObject>((getBasicLinkObjectExtension().size()));
        for (AbstractObject iter: basicLinkObjectExtension) {
            copy.basicLinkObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
