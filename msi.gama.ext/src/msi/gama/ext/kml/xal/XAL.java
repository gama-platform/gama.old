
package msi.gama.ext.kml.xal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xalAddressDetails",
    "any"
})
@XmlRootElement(name = "xAL")
public class XAL implements Cloneable
{

    @XmlElement(name = "AddressDetails", required = true)
    protected List<AddressDetails> xalAddressDetails;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "Version")
    @XmlSchemaType(name = "anySimpleType")
    protected String version;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Value constructor with only mandatory fields
     * 
     * @param xalAddressDetails
     *     required parameter
     */
    public XAL(final List<AddressDetails> xalAddressDetails) {
        super();
        this.xalAddressDetails = xalAddressDetails;
    }

    /**
     * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
     * 
     */
    @Deprecated
    private XAL() {
        super();
    }

    /**
     * 
     */
    public List<AddressDetails> getXalAddressDetails() {
        if (xalAddressDetails == null) {
            xalAddressDetails = new ArrayList<AddressDetails>();
        }
        return this.xalAddressDetails;
    }

    /**
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((prime*result)+((xalAddressDetails == null)? 0 :xalAddressDetails.hashCode()));
        result = ((prime*result)+((any == null)? 0 :any.hashCode()));
        result = ((prime*result)+((version == null)? 0 :version.hashCode()));
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
        if ((obj instanceof XAL) == false) {
            return false;
        }
        XAL other = ((XAL) obj);
        if (xalAddressDetails == null) {
            if (other.xalAddressDetails!= null) {
                return false;
            }
        } else {
            if (xalAddressDetails.equals(other.xalAddressDetails) == false) {
                return false;
            }
        }
        if (any == null) {
            if (other.any!= null) {
                return false;
            }
        } else {
            if (any.equals(other.any) == false) {
                return false;
            }
        }
        if (version == null) {
            if (other.version!= null) {
                return false;
            }
        } else {
            if (version.equals(other.version) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link AddressDetails} and adds it to xalAddressDetails.
     * This method is a short version for:
     * <code>
     * AddressDetails addressDetails = new AddressDetails();
     * this.getXalAddressDetails().add(addressDetails); </code>
     * 
     * 
     * @param xalAddress
     *     required parameter
     * @param addressLines
     *     required parameter
     * @param country
     *     required parameter
     * @param locality
     *     required parameter
     * @param administrativeArea
     *     required parameter
     * @param thoroughfare
     *     required parameter
     */
    public AddressDetails createAndAddXalAddressDetails(final AddressDetails.Address xalAddress, final AddressLines addressLines, final AddressDetails.Country country, final AdministrativeArea administrativeArea, final Locality locality, final Thoroughfare thoroughfare) {
        AddressDetails newValue = new AddressDetails(xalAddress, addressLines, country, administrativeArea, locality, thoroughfare);
        this.getXalAddressDetails().add(newValue);
        return newValue;
    }

    /**
     * Sets the value of the xalAddressDetails property Objects of the following type(s) are allowed in the list List<AddressDetails>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withXalAddressDetails} instead.
     * 
     * 
     * @param xalAddressDetails
     */
    public void setXalAddressDetails(final List<AddressDetails> xalAddressDetails) {
        this.xalAddressDetails = xalAddressDetails;
    }

    /**
     * add a value to the xalAddressDetails property collection
     * 
     * @param xalAddressDetails
     *     Objects of the following type are allowed in the list: {@link AddressDetails}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public XAL addToXalAddressDetails(final AddressDetails xalAddressDetails) {
        this.getXalAddressDetails().add(xalAddressDetails);
        return this;
    }

    /**
     * Sets the value of the any property Objects of the following type(s) are allowed in the list List<Object>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withAny} instead.
     * 
     * 
     * @param any
     */
    public void setAny(final List<Object> any) {
        this.any = any;
    }

    /**
     * add a value to the any property collection
     * 
     * @param any
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public XAL addToAny(final Object any) {
        this.getAny().add(any);
        return this;
    }

    /**
     * fluent setter
     * @see #setAny(List<Object>)
     * 
     * @param any
     *     required parameter
     */
    public XAL withAny(final List<Object> any) {
        this.setAny(any);
        return this;
    }

    /**
     * fluent setter
     * @see #setVersion(String)
     * 
     * @param version
     *     required parameter
     */
    public XAL withVersion(final String version) {
        this.setVersion(version);
        return this;
    }

    @Override
    public XAL clone() {
        XAL copy;
        try {
            copy = ((XAL) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        copy.xalAddressDetails = new ArrayList<AddressDetails>((getXalAddressDetails().size()));
        for (AddressDetails iter: xalAddressDetails) {
            copy.xalAddressDetails.add(iter.clone());
        }
        copy.any = new ArrayList<Object>((getAny().size()));
        for (Object iter: any) {
            copy.any.add(iter);
        }
        return copy;
    }

}
