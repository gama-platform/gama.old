
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
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;


/**
 * <xal:addressdetails>
 * <p>
 * A structured address, formatted as xAL, or eXtensible Address Language, an international 
 * standard for address formatting. <xal:AddressDetails> is used by KML for geocoding 
 * in Google Maps only. For details, see the Google Maps API documentation. Currently, 
 * Google Earth does not use this element; use <address> instead. Be sure to include 
 * the namespace for this element in any KML file that uses it: xmlns:xal="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0" 
 * </p>
 * <p>
 * A structured address, formatted as xAL, or eXtensible Address Language, an international 
 * standard for address formatting. <xal:AddressDetails> is used by KML for geocoding 
 * in Google Maps only. For details, see the Google Maps API documentation. Currently, 
 * Google Earth does not use this element; use <address> instead. Be sure to include 
 * the namespace for this element in any KML file that uses it: xmlns:xal="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0" 
 * </p>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddressDetails", propOrder = {
    "postalServiceElements",
    "xalAddress",
    "addressLines",
    "country",
    "administrativeArea",
    "locality",
    "thoroughfare",
    "any"
})
@XmlRootElement(name = "AddressDetails", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
public class AddressDetails implements Cloneable
{

    @XmlElement(name = "PostalServiceElements")
    protected AddressDetails.PostalServiceElements postalServiceElements;
    /**
     * <address>
     * <p>
     * A string value representing an unstructured address written as a standard street, 
     * city, state address, and/or as a postal code. You can use the <address> tag to specify 
     * the location of a point instead of using latitude and longitude coordinates. (However, 
     * if a <Point> is provided, it takes precedence over the <address>.) To find out which 
     * locales are supported for this tag in Google Earth, go to the Google Maps Help. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(name = "Address")
    protected AddressDetails.Address xalAddress;
    @XmlElement(name = "AddressLines")
    protected AddressLines addressLines;
    @XmlElement(name = "Country")
    protected AddressDetails.Country country;
    @XmlElement(name = "AdministrativeArea")
    protected AdministrativeArea administrativeArea;
    @XmlElement(name = "Locality")
    protected Locality locality;
    @XmlElement(name = "Thoroughfare")
    protected Thoroughfare thoroughfare;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    /**
     * <address>
     * <p>
     * A string value representing an unstructured address written as a standard street, 
     * city, state address, and/or as a postal code. You can use the <address> tag to specify 
     * the location of a point instead of using latitude and longitude coordinates. (However, 
     * if a <Point> is provided, it takes precedence over the <address>.) To find out which 
     * locales are supported for this tag in Google Earth, go to the Google Maps Help. 
     * </p>
     * 
     * 
     * 
     */
    @XmlAttribute(name = "AddressType")
    @XmlSchemaType(name = "anySimpleType")
    protected String address;
    @XmlAttribute(name = "CurrentStatus")
    @XmlSchemaType(name = "anySimpleType")
    protected String currentStatus;
    @XmlAttribute(name = "ValidFromDate")
    @XmlSchemaType(name = "anySimpleType")
    protected String validFromDate;
    @XmlAttribute(name = "ValidToDate")
    @XmlSchemaType(name = "anySimpleType")
    protected String validToDate;
    @XmlAttribute(name = "Usage")
    @XmlSchemaType(name = "anySimpleType")
    protected String usage;
    @XmlAttribute(name = "AddressDetailsKey")
    @XmlSchemaType(name = "anySimpleType")
    protected String addressDetailsKey;
    @XmlAttribute(name = "Code")
    @XmlSchemaType(name = "anySimpleType")
    protected String code;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Value constructor with only mandatory fields
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
    public AddressDetails(final AddressDetails.Address xalAddress, final AddressLines addressLines, final AddressDetails.Country country, final AdministrativeArea administrativeArea, final Locality locality, final Thoroughfare thoroughfare) {
        super();
        this.xalAddress = xalAddress;
        this.addressLines = addressLines;
        this.country = country;
        this.administrativeArea = administrativeArea;
        this.locality = locality;
        this.thoroughfare = thoroughfare;
    }

    /**
     * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
     * 
     */
    @Deprecated
    private AddressDetails() {
        super();
    }

    /**
     * @see postalServiceElements
     * 
     * @return
     *     possible object is
     *     {@link AddressDetails.PostalServiceElements}
     *     
     */
    public AddressDetails.PostalServiceElements getPostalServiceElements() {
        return postalServiceElements;
    }

    /**
     * @see postalServiceElements
     * 
     * @param value
     *     allowed object is
     *     {@link AddressDetails.PostalServiceElements}
     *     
     */
    public void setPostalServiceElements(AddressDetails.PostalServiceElements value) {
        this.postalServiceElements = value;
    }

    /**
     * @see xalAddress
     * 
     * @return
     *     possible object is
     *     {@link AddressDetails.Address}
     *     
     */
    public AddressDetails.Address getXalAddress() {
        return xalAddress;
    }

    /**
     * @see xalAddress
     * 
     * @param value
     *     allowed object is
     *     {@link AddressDetails.Address}
     *     
     */
    public void setXalAddress(AddressDetails.Address value) {
        this.xalAddress = value;
    }

    /**
     * @see addressLines
     * 
     * @return
     *     possible object is
     *     {@link AddressLines}
     *     
     */
    public AddressLines getAddressLines() {
        return addressLines;
    }

    /**
     * @see addressLines
     * 
     * @param value
     *     allowed object is
     *     {@link AddressLines}
     *     
     */
    public void setAddressLines(AddressLines value) {
        this.addressLines = value;
    }

    /**
     * @see country
     * 
     * @return
     *     possible object is
     *     {@link AddressDetails.Country}
     *     
     */
    public AddressDetails.Country getCountry() {
        return country;
    }

    /**
     * @see country
     * 
     * @param value
     *     allowed object is
     *     {@link AddressDetails.Country}
     *     
     */
    public void setCountry(AddressDetails.Country value) {
        this.country = value;
    }

    /**
     * @see administrativeArea
     * 
     * @return
     *     possible object is
     *     {@link AdministrativeArea}
     *     
     */
    public AdministrativeArea getAdministrativeArea() {
        return administrativeArea;
    }

    /**
     * @see administrativeArea
     * 
     * @param value
     *     allowed object is
     *     {@link AdministrativeArea}
     *     
     */
    public void setAdministrativeArea(AdministrativeArea value) {
        this.administrativeArea = value;
    }

    /**
     * @see locality
     * 
     * @return
     *     possible object is
     *     {@link Locality}
     *     
     */
    public Locality getLocality() {
        return locality;
    }

    /**
     * @see locality
     * 
     * @param value
     *     allowed object is
     *     {@link Locality}
     *     
     */
    public void setLocality(Locality value) {
        this.locality = value;
    }

    /**
     * @see thoroughfare
     * 
     * @return
     *     possible object is
     *     {@link Thoroughfare}
     *     
     */
    public Thoroughfare getThoroughfare() {
        return thoroughfare;
    }

    /**
     * @see thoroughfare
     * 
     * @param value
     *     allowed object is
     *     {@link Thoroughfare}
     *     
     */
    public void setThoroughfare(Thoroughfare value) {
        this.thoroughfare = value;
    }

    /**
     * @see any
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * @see address
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * @see address
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * @see currentStatus
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getCurrentStatus() {
        return currentStatus;
    }

    /**
     * @see currentStatus
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setCurrentStatus(String value) {
        this.currentStatus = value;
    }

    /**
     * @see validFromDate
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getValidFromDate() {
        return validFromDate;
    }

    /**
     * @see validFromDate
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setValidFromDate(String value) {
        this.validFromDate = value;
    }

    /**
     * @see validToDate
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getValidToDate() {
        return validToDate;
    }

    /**
     * @see validToDate
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setValidToDate(String value) {
        this.validToDate = value;
    }

    /**
     * @see usage
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getUsage() {
        return usage;
    }

    /**
     * @see usage
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setUsage(String value) {
        this.usage = value;
    }

    /**
     * @see addressDetailsKey
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getAddressDetailsKey() {
        return addressDetailsKey;
    }

    /**
     * @see addressDetailsKey
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setAddressDetailsKey(String value) {
        this.addressDetailsKey = value;
    }

    /**
     * @see code
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * @see code
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * @see otherAttributes
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
        result = ((prime*result)+((postalServiceElements == null)? 0 :postalServiceElements.hashCode()));
        result = ((prime*result)+((xalAddress == null)? 0 :xalAddress.hashCode()));
        result = ((prime*result)+((addressLines == null)? 0 :addressLines.hashCode()));
        result = ((prime*result)+((country == null)? 0 :country.hashCode()));
        result = ((prime*result)+((administrativeArea == null)? 0 :administrativeArea.hashCode()));
        result = ((prime*result)+((locality == null)? 0 :locality.hashCode()));
        result = ((prime*result)+((thoroughfare == null)? 0 :thoroughfare.hashCode()));
        result = ((prime*result)+((any == null)? 0 :any.hashCode()));
        result = ((prime*result)+((address == null)? 0 :address.hashCode()));
        result = ((prime*result)+((currentStatus == null)? 0 :currentStatus.hashCode()));
        result = ((prime*result)+((validFromDate == null)? 0 :validFromDate.hashCode()));
        result = ((prime*result)+((validToDate == null)? 0 :validToDate.hashCode()));
        result = ((prime*result)+((usage == null)? 0 :usage.hashCode()));
        result = ((prime*result)+((addressDetailsKey == null)? 0 :addressDetailsKey.hashCode()));
        result = ((prime*result)+((code == null)? 0 :code.hashCode()));
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
        if ((obj instanceof AddressDetails) == false) {
            return false;
        }
        AddressDetails other = ((AddressDetails) obj);
        if (postalServiceElements == null) {
            if (other.postalServiceElements!= null) {
                return false;
            }
        } else {
            if (postalServiceElements.equals(other.postalServiceElements) == false) {
                return false;
            }
        }
        if (xalAddress == null) {
            if (other.xalAddress!= null) {
                return false;
            }
        } else {
            if (xalAddress.equals(other.xalAddress) == false) {
                return false;
            }
        }
        if (addressLines == null) {
            if (other.addressLines!= null) {
                return false;
            }
        } else {
            if (addressLines.equals(other.addressLines) == false) {
                return false;
            }
        }
        if (country == null) {
            if (other.country!= null) {
                return false;
            }
        } else {
            if (country.equals(other.country) == false) {
                return false;
            }
        }
        if (administrativeArea == null) {
            if (other.administrativeArea!= null) {
                return false;
            }
        } else {
            if (administrativeArea.equals(other.administrativeArea) == false) {
                return false;
            }
        }
        if (locality == null) {
            if (other.locality!= null) {
                return false;
            }
        } else {
            if (locality.equals(other.locality) == false) {
                return false;
            }
        }
        if (thoroughfare == null) {
            if (other.thoroughfare!= null) {
                return false;
            }
        } else {
            if (thoroughfare.equals(other.thoroughfare) == false) {
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
        if (address == null) {
            if (other.address!= null) {
                return false;
            }
        } else {
            if (address.equals(other.address) == false) {
                return false;
            }
        }
        if (currentStatus == null) {
            if (other.currentStatus!= null) {
                return false;
            }
        } else {
            if (currentStatus.equals(other.currentStatus) == false) {
                return false;
            }
        }
        if (validFromDate == null) {
            if (other.validFromDate!= null) {
                return false;
            }
        } else {
            if (validFromDate.equals(other.validFromDate) == false) {
                return false;
            }
        }
        if (validToDate == null) {
            if (other.validToDate!= null) {
                return false;
            }
        } else {
            if (validToDate.equals(other.validToDate) == false) {
                return false;
            }
        }
        if (usage == null) {
            if (other.usage!= null) {
                return false;
            }
        } else {
            if (usage.equals(other.usage) == false) {
                return false;
            }
        }
        if (addressDetailsKey == null) {
            if (other.addressDetailsKey!= null) {
                return false;
            }
        } else {
            if (addressDetailsKey.equals(other.addressDetailsKey) == false) {
                return false;
            }
        }
        if (code == null) {
            if (other.code!= null) {
                return false;
            }
        } else {
            if (code.equals(other.code) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link AddressDetails.PostalServiceElements} and set it to postalServiceElements.
     * 
     * This method is a short version for:
     * <code>
     * PostalServiceElements postalServiceElements = new PostalServiceElements();
     * this.setPostalServiceElements(postalServiceElements); </code>
     * 
     * 
     */
    public AddressDetails.PostalServiceElements createAndSetPostalServiceElements() {
        AddressDetails.PostalServiceElements newValue = new AddressDetails.PostalServiceElements();
        this.setPostalServiceElements(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link AddressDetails.Address} and set it to xalAddress.
     * 
     * This method is a short version for:
     * <code>
     * Address address = new Address();
     * this.setXalAddress(address); </code>
     * 
     * 
     */
    public AddressDetails.Address createAndSetXalAddress() {
        AddressDetails.Address newValue = new AddressDetails.Address();
        this.setXalAddress(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link AddressLines} and set it to addressLines.
     * 
     * This method is a short version for:
     * <code>
     * AddressLines addressLines = new AddressLines();
     * this.setAddressLines(addressLines); </code>
     * 
     * 
     * @param addressLine
     *     required parameter
     */
    public AddressLines createAndSetAddressLines(final List<AddressLine> addressLine) {
        AddressLines newValue = new AddressLines(addressLine);
        this.setAddressLines(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link AddressDetails.Country} and set it to country.
     * 
     * This method is a short version for:
     * <code>
     * Country country = new Country();
     * this.setCountry(country); </code>
     * 
     * 
     * @param locality
     *     required parameter
     * @param administrativeArea
     *     required parameter
     * @param thoroughfare
     *     required parameter
     */
    public AddressDetails.Country createAndSetCountry(final AdministrativeArea administrativeArea, final Locality locality, final Thoroughfare thoroughfare) {
        AddressDetails.Country newValue = new AddressDetails.Country(administrativeArea, locality, thoroughfare);
        this.setCountry(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link AdministrativeArea} and set it to administrativeArea.
     * 
     * This method is a short version for:
     * <code>
     * AdministrativeArea administrativeArea = new AdministrativeArea();
     * this.setAdministrativeArea(administrativeArea); </code>
     * 
     * 
     * @param postalCode
     *     required parameter
     * @param locality
     *     required parameter
     * @param postOffice
     *     required parameter
     */
    public AdministrativeArea createAndSetAdministrativeArea(final Locality locality, final PostOffice postOffice, final PostalCode postalCode) {
        AdministrativeArea newValue = new AdministrativeArea(locality, postOffice, postalCode);
        this.setAdministrativeArea(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Locality} and set it to locality.
     * 
     * This method is a short version for:
     * <code>
     * Locality locality = new Locality();
     * this.setLocality(locality); </code>
     * 
     * 
     * @param postBox
     *     required parameter
     * @param postOffice
     *     required parameter
     * @param postalRoute
     *     required parameter
     * @param largeMailUser
     *     required parameter
     */
    public Locality createAndSetLocality(final PostBox postBox, final LargeMailUser largeMailUser, final PostOffice postOffice, final PostalRoute postalRoute) {
        Locality newValue = new Locality(postBox, largeMailUser, postOffice, postalRoute);
        this.setLocality(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Thoroughfare} and set it to thoroughfare.
     * 
     * This method is a short version for:
     * <code>
     * Thoroughfare thoroughfare = new Thoroughfare();
     * this.setThoroughfare(thoroughfare); </code>
     * 
     * 
     * @param postalCode
     *     required parameter
     * @param premise
     *     required parameter
     * @param firm
     *     required parameter
     * @param dependentLocality
     *     required parameter
     */
    public Thoroughfare createAndSetThoroughfare(final DependentLocality dependentLocality, final Premise premise, final Firm firm, final PostalCode postalCode) {
        Thoroughfare newValue = new Thoroughfare(dependentLocality, premise, firm, postalCode);
        this.setThoroughfare(newValue);
        return newValue;
    }

    /**
     * @see any
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
    public AddressDetails addToAny(final Object any) {
        this.getAny().add(any);
        return this;
    }

    /**
     * fluent setter
     * @see #setPostalServiceElements(PostalServiceElements)
     * 
     * @param postalServiceElements
     *     required parameter
     */
    public AddressDetails withPostalServiceElements(final AddressDetails.PostalServiceElements postalServiceElements) {
        this.setPostalServiceElements(postalServiceElements);
        return this;
    }

    /**
     * fluent setter
     * @see #setAny(List<Object>)
     * 
     * @param any
     *     required parameter
     */
    public AddressDetails withAny(final List<Object> any) {
        this.setAny(any);
        return this;
    }

    /**
     * fluent setter
     * @see #setAddress(String)
     * 
     * @param address
     *     required parameter
     */
    public AddressDetails withAddress(final String address) {
        this.setAddress(address);
        return this;
    }

    /**
     * fluent setter
     * @see #setCurrentStatus(String)
     * 
     * @param currentStatus
     *     required parameter
     */
    public AddressDetails withCurrentStatus(final String currentStatus) {
        this.setCurrentStatus(currentStatus);
        return this;
    }

    /**
     * fluent setter
     * @see #setValidFromDate(String)
     * 
     * @param validFromDate
     *     required parameter
     */
    public AddressDetails withValidFromDate(final String validFromDate) {
        this.setValidFromDate(validFromDate);
        return this;
    }

    /**
     * fluent setter
     * @see #setValidToDate(String)
     * 
     * @param validToDate
     *     required parameter
     */
    public AddressDetails withValidToDate(final String validToDate) {
        this.setValidToDate(validToDate);
        return this;
    }

    /**
     * fluent setter
     * @see #setUsage(String)
     * 
     * @param usage
     *     required parameter
     */
    public AddressDetails withUsage(final String usage) {
        this.setUsage(usage);
        return this;
    }

    /**
     * fluent setter
     * @see #setAddressDetailsKey(String)
     * 
     * @param addressDetailsKey
     *     required parameter
     */
    public AddressDetails withAddressDetailsKey(final String addressDetailsKey) {
        this.setAddressDetailsKey(addressDetailsKey);
        return this;
    }

    /**
     * fluent setter
     * @see #setCode(String)
     * 
     * @param code
     *     required parameter
     */
    public AddressDetails withCode(final String code) {
        this.setCode(code);
        return this;
    }

    @Override
    public AddressDetails clone() {
        AddressDetails copy;
        try {
            copy = ((AddressDetails) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        copy.postalServiceElements = ((postalServiceElements == null)?null:((AddressDetails.PostalServiceElements) postalServiceElements.clone()));
        copy.xalAddress = ((xalAddress == null)?null:((AddressDetails.Address) xalAddress.clone()));
        copy.addressLines = ((addressLines == null)?null:((AddressLines) addressLines.clone()));
        copy.country = ((country == null)?null:((AddressDetails.Country) country.clone()));
        copy.administrativeArea = ((administrativeArea == null)?null:((AdministrativeArea) administrativeArea.clone()));
        copy.locality = ((locality == null)?null:((Locality) locality.clone()));
        copy.thoroughfare = ((thoroughfare == null)?null:((Thoroughfare) thoroughfare.clone()));
        copy.any = new ArrayList<Object>((getAny().size()));
        for (Object iter: any) {
            copy.any.add(iter);
        }
        return copy;
    }


    /**
     * <address>
     * <p>
     * A string value representing an unstructured address written as a standard street, 
     * city, state address, and/or as a postal code. You can use the <address> tag to specify 
     * the location of a point instead of using latitude and longitude coordinates. (However, 
     * if a <Point> is provided, it takes precedence over the <address>.) To find out which 
     * locales are supported for this tag in Google Earth, go to the Google Maps Help. 
     * </p>
     * 
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "content"
    })
    @XmlRootElement(name = "Address", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class Address implements Cloneable
    {

        @XmlValue
        protected String content;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        protected String underscore;
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        protected String code;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        public Address() {
            super();
        }

        /**
         * @see content
         * 
         * @return
         *     possible object is
         *     {@link String}
         *     
         */
        public String getContent() {
            return content;
        }

        /**
         * @see content
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setContent(String value) {
            this.content = value;
        }

        /**
         * @see underscore
         * 
         * @return
         *     possible object is
         *     {@link String}
         *     
         */
        public String getUnderscore() {
            return underscore;
        }

        /**
         * @see underscore
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setUnderscore(String value) {
            this.underscore = value;
        }

        /**
         * @see code
         * 
         * @return
         *     possible object is
         *     {@link String}
         *     
         */
        public String getCode() {
            return code;
        }

        /**
         * @see code
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setCode(String value) {
            this.code = value;
        }

        /**
         * @see otherAttributes
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
            result = ((prime*result)+((content == null)? 0 :content.hashCode()));
            result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
            result = ((prime*result)+((code == null)? 0 :code.hashCode()));
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
            if ((obj instanceof AddressDetails.Address) == false) {
                return false;
            }
            AddressDetails.Address other = ((AddressDetails.Address) obj);
            if (content == null) {
                if (other.content!= null) {
                    return false;
                }
            } else {
                if (content.equals(other.content) == false) {
                    return false;
                }
            }
            if (underscore == null) {
                if (other.underscore!= null) {
                    return false;
                }
            } else {
                if (underscore.equals(other.underscore) == false) {
                    return false;
                }
            }
            if (code == null) {
                if (other.code!= null) {
                    return false;
                }
            } else {
                if (code.equals(other.code) == false) {
                    return false;
                }
            }
            return true;
        }

        /**
         * fluent setter
         * @see #setContent(String)
         * 
         * @param content
         *     required parameter
         */
        public AddressDetails.Address withContent(final String content) {
            this.setContent(content);
            return this;
        }

        /**
         * fluent setter
         * @see #setUnderscore(String)
         * 
         * @param underscore
         *     required parameter
         */
        public AddressDetails.Address withUnderscore(final String underscore) {
            this.setUnderscore(underscore);
            return this;
        }

        /**
         * fluent setter
         * @see #setCode(String)
         * 
         * @param code
         *     required parameter
         */
        public AddressDetails.Address withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public AddressDetails.Address clone() {
            AddressDetails.Address copy;
            try {
                copy = ((AddressDetails.Address) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            return copy;
        }

    }


    /**
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "addressLine",
        "countryNameCode",
        "countryName",
        "administrativeArea",
        "locality",
        "thoroughfare",
        "any"
    })
    @XmlRootElement(name = "Country", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class Country implements Cloneable
    {

        @XmlElement(name = "AddressLine")
        protected List<AddressLine> addressLine;
        @XmlElement(name = "CountryNameCode")
        protected List<AddressDetails.Country.CountryNameCode> countryNameCode;
        @XmlElement(name = "CountryName")
        protected List<CountryName> countryName;
        @XmlElement(name = "AdministrativeArea")
        protected AdministrativeArea administrativeArea;
        @XmlElement(name = "Locality")
        protected Locality locality;
        @XmlElement(name = "Thoroughfare")
        protected Thoroughfare thoroughfare;
        @XmlAnyElement(lax = true)
        protected List<Object> any;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        /**
         * Value constructor with only mandatory fields
         * 
         * @param locality
         *     required parameter
         * @param administrativeArea
         *     required parameter
         * @param thoroughfare
         *     required parameter
         */
        public Country(final AdministrativeArea administrativeArea, final Locality locality, final Thoroughfare thoroughfare) {
            super();
            this.administrativeArea = administrativeArea;
            this.locality = locality;
            this.thoroughfare = thoroughfare;
        }

        /**
         * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
         * 
         */
        @Deprecated
        private Country() {
            super();
        }

        /**
         * 
         */
        public List<AddressLine> getAddressLine() {
            if (addressLine == null) {
                addressLine = new ArrayList<AddressLine>();
            }
            return this.addressLine;
        }

        /**
         * 
         */
        public List<AddressDetails.Country.CountryNameCode> getCountryNameCode() {
            if (countryNameCode == null) {
                countryNameCode = new ArrayList<AddressDetails.Country.CountryNameCode>();
            }
            return this.countryNameCode;
        }

        /**
         * 
         */
        public List<CountryName> getCountryName() {
            if (countryName == null) {
                countryName = new ArrayList<CountryName>();
            }
            return this.countryName;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link AdministrativeArea}
         *     
         */
        public AdministrativeArea getAdministrativeArea() {
            return administrativeArea;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link AdministrativeArea}
         *     
         */
        public void setAdministrativeArea(AdministrativeArea value) {
            this.administrativeArea = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link Locality}
         *     
         */
        public Locality getLocality() {
            return locality;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link Locality}
         *     
         */
        public void setLocality(Locality value) {
            this.locality = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link Thoroughfare}
         *     
         */
        public Thoroughfare getThoroughfare() {
            return thoroughfare;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link Thoroughfare}
         *     
         */
        public void setThoroughfare(Thoroughfare value) {
            this.thoroughfare = value;
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
         *     always non-null
         */
        public Map<QName, String> getOtherAttributes() {
            return otherAttributes;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = ((prime*result)+((addressLine == null)? 0 :addressLine.hashCode()));
            result = ((prime*result)+((countryNameCode == null)? 0 :countryNameCode.hashCode()));
            result = ((prime*result)+((countryName == null)? 0 :countryName.hashCode()));
            result = ((prime*result)+((administrativeArea == null)? 0 :administrativeArea.hashCode()));
            result = ((prime*result)+((locality == null)? 0 :locality.hashCode()));
            result = ((prime*result)+((thoroughfare == null)? 0 :thoroughfare.hashCode()));
            result = ((prime*result)+((any == null)? 0 :any.hashCode()));
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
            if ((obj instanceof AddressDetails.Country) == false) {
                return false;
            }
            AddressDetails.Country other = ((AddressDetails.Country) obj);
            if (addressLine == null) {
                if (other.addressLine!= null) {
                    return false;
                }
            } else {
                if (addressLine.equals(other.addressLine) == false) {
                    return false;
                }
            }
            if (countryNameCode == null) {
                if (other.countryNameCode!= null) {
                    return false;
                }
            } else {
                if (countryNameCode.equals(other.countryNameCode) == false) {
                    return false;
                }
            }
            if (countryName == null) {
                if (other.countryName!= null) {
                    return false;
                }
            } else {
                if (countryName.equals(other.countryName) == false) {
                    return false;
                }
            }
            if (administrativeArea == null) {
                if (other.administrativeArea!= null) {
                    return false;
                }
            } else {
                if (administrativeArea.equals(other.administrativeArea) == false) {
                    return false;
                }
            }
            if (locality == null) {
                if (other.locality!= null) {
                    return false;
                }
            } else {
                if (locality.equals(other.locality) == false) {
                    return false;
                }
            }
            if (thoroughfare == null) {
                if (other.thoroughfare!= null) {
                    return false;
                }
            } else {
                if (thoroughfare.equals(other.thoroughfare) == false) {
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
            return true;
        }

        /**
         * Creates a new instance of {@link AddressLine} and adds it to addressLine.
         * This method is a short version for:
         * <code>
         * AddressLine addressLine = new AddressLine();
         * this.getAddressLine().add(addressLine); </code>
         * 
         * 
         */
        public AddressLine createAndAddAddressLine() {
            AddressLine newValue = new AddressLine();
            this.getAddressLine().add(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link AddressDetails.Country.CountryNameCode} and adds it to countryNameCode.
         * This method is a short version for:
         * <code>
         * CountryNameCode countryNameCode = new CountryNameCode();
         * this.getCountryNameCode().add(countryNameCode); </code>
         * 
         * 
         */
        public AddressDetails.Country.CountryNameCode createAndAddCountryNameCode() {
            AddressDetails.Country.CountryNameCode newValue = new AddressDetails.Country.CountryNameCode();
            this.getCountryNameCode().add(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link CountryName} and adds it to countryName.
         * This method is a short version for:
         * <code>
         * CountryName countryName = new CountryName();
         * this.getCountryName().add(countryName); </code>
         * 
         * 
         */
        public CountryName createAndAddCountryName() {
            CountryName newValue = new CountryName();
            this.getCountryName().add(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link AdministrativeArea} and set it to administrativeArea.
         * 
         * This method is a short version for:
         * <code>
         * AdministrativeArea administrativeArea = new AdministrativeArea();
         * this.setAdministrativeArea(administrativeArea); </code>
         * 
         * 
         * @param postalCode
         *     required parameter
         * @param locality
         *     required parameter
         * @param postOffice
         *     required parameter
         */
        public AdministrativeArea createAndSetAdministrativeArea(final Locality locality, final PostOffice postOffice, final PostalCode postalCode) {
            AdministrativeArea newValue = new AdministrativeArea(locality, postOffice, postalCode);
            this.setAdministrativeArea(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link Locality} and set it to locality.
         * 
         * This method is a short version for:
         * <code>
         * Locality locality = new Locality();
         * this.setLocality(locality); </code>
         * 
         * 
         * @param postBox
         *     required parameter
         * @param postOffice
         *     required parameter
         * @param postalRoute
         *     required parameter
         * @param largeMailUser
         *     required parameter
         */
        public Locality createAndSetLocality(final PostBox postBox, final LargeMailUser largeMailUser, final PostOffice postOffice, final PostalRoute postalRoute) {
            Locality newValue = new Locality(postBox, largeMailUser, postOffice, postalRoute);
            this.setLocality(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link Thoroughfare} and set it to thoroughfare.
         * 
         * This method is a short version for:
         * <code>
         * Thoroughfare thoroughfare = new Thoroughfare();
         * this.setThoroughfare(thoroughfare); </code>
         * 
         * 
         * @param postalCode
         *     required parameter
         * @param premise
         *     required parameter
         * @param firm
         *     required parameter
         * @param dependentLocality
         *     required parameter
         */
        public Thoroughfare createAndSetThoroughfare(final DependentLocality dependentLocality, final Premise premise, final Firm firm, final PostalCode postalCode) {
            Thoroughfare newValue = new Thoroughfare(dependentLocality, premise, firm, postalCode);
            this.setThoroughfare(newValue);
            return newValue;
        }

        /**
         * Sets the value of the addressLine property Objects of the following type(s) are allowed in the list List<AddressLine>.
         * <p>Note:
         * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withAddressLine} instead.
         * 
         * 
         * @param addressLine
         */
        public void setAddressLine(final List<AddressLine> addressLine) {
            this.addressLine = addressLine;
        }

        /**
         * add a value to the addressLine property collection
         * 
         * @param addressLine
         *     Objects of the following type are allowed in the list: {@link AddressLine}
         * @return
         *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
         */
        public AddressDetails.Country addToAddressLine(final AddressLine addressLine) {
            this.getAddressLine().add(addressLine);
            return this;
        }

        /**
         * Sets the value of the countryNameCode property Objects of the following type(s) are allowed in the list List<CountryNameCode>.
         * <p>Note:
         * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withCountryNameCode} instead.
         * 
         * 
         * @param countryNameCode
         */
        public void setCountryNameCode(final List<AddressDetails.Country.CountryNameCode> countryNameCode) {
            this.countryNameCode = countryNameCode;
        }

        /**
         * add a value to the countryNameCode property collection
         * 
         * @param countryNameCode
         *     Objects of the following type are allowed in the list: {@link AddressDetails.Country.CountryNameCode}
         * @return
         *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
         */
        public AddressDetails.Country addToCountryNameCode(final AddressDetails.Country.CountryNameCode countryNameCode) {
            this.getCountryNameCode().add(countryNameCode);
            return this;
        }

        /**
         * Sets the value of the countryName property Objects of the following type(s) are allowed in the list List<CountryName>.
         * <p>Note:
         * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withCountryName} instead.
         * 
         * 
         * @param countryName
         */
        public void setCountryName(final List<CountryName> countryName) {
            this.countryName = countryName;
        }

        /**
         * add a value to the countryName property collection
         * 
         * @param countryName
         *     Objects of the following type are allowed in the list: {@link CountryName}
         * @return
         *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
         */
        public AddressDetails.Country addToCountryName(final CountryName countryName) {
            this.getCountryName().add(countryName);
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
        public AddressDetails.Country addToAny(final Object any) {
            this.getAny().add(any);
            return this;
        }

        /**
         * fluent setter
         * @see #setAddressLine(List<AddressLine>)
         * 
         * @param addressLine
         *     required parameter
         */
        public AddressDetails.Country withAddressLine(final List<AddressLine> addressLine) {
            this.setAddressLine(addressLine);
            return this;
        }

        /**
         * fluent setter
         * @see #setCountryNameCode(List<CountryNameCode>)
         * 
         * @param countryNameCode
         *     required parameter
         */
        public AddressDetails.Country withCountryNameCode(final List<AddressDetails.Country.CountryNameCode> countryNameCode) {
            this.setCountryNameCode(countryNameCode);
            return this;
        }

        /**
         * fluent setter
         * @see #setCountryName(List<CountryName>)
         * 
         * @param countryName
         *     required parameter
         */
        public AddressDetails.Country withCountryName(final List<CountryName> countryName) {
            this.setCountryName(countryName);
            return this;
        }

        /**
         * fluent setter
         * @see #setAny(List<Object>)
         * 
         * @param any
         *     required parameter
         */
        public AddressDetails.Country withAny(final List<Object> any) {
            this.setAny(any);
            return this;
        }

        @Override
        public AddressDetails.Country clone() {
            AddressDetails.Country copy;
            try {
                copy = ((AddressDetails.Country) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
            for (AddressLine iter: addressLine) {
                copy.addressLine.add(iter.clone());
            }
            copy.countryNameCode = new ArrayList<AddressDetails.Country.CountryNameCode>((getCountryNameCode().size()));
            for (AddressDetails.Country.CountryNameCode iter: countryNameCode) {
                copy.countryNameCode.add(iter.clone());
            }
            copy.countryName = new ArrayList<CountryName>((getCountryName().size()));
            for (CountryName iter: countryName) {
                copy.countryName.add(iter.clone());
            }
            copy.administrativeArea = ((administrativeArea == null)?null:((AdministrativeArea) administrativeArea.clone()));
            copy.locality = ((locality == null)?null:((Locality) locality.clone()));
            copy.thoroughfare = ((thoroughfare == null)?null:((Thoroughfare) thoroughfare.clone()));
            copy.any = new ArrayList<Object>((getAny().size()));
            for (Object iter: any) {
                copy.any.add(iter);
            }
            return copy;
        }


        /**
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        @XmlRootElement(name = "CountryNameCode", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class CountryNameCode implements Cloneable
        {

            @XmlValue
            protected String content;
            @XmlAttribute(name = "Scheme")
            @XmlSchemaType(name = "anySimpleType")
            protected String scheme;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            protected String code;
            @XmlAnyAttribute
            private Map<QName, String> otherAttributes = new HashMap<QName, String>();

            public CountryNameCode() {
                super();
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getContent() {
                return content;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setContent(String value) {
                this.content = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getScheme() {
                return scheme;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setScheme(String value) {
                this.scheme = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getCode() {
                return code;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setCode(String value) {
                this.code = value;
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
                result = ((prime*result)+((content == null)? 0 :content.hashCode()));
                result = ((prime*result)+((scheme == null)? 0 :scheme.hashCode()));
                result = ((prime*result)+((code == null)? 0 :code.hashCode()));
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
                if ((obj instanceof AddressDetails.Country.CountryNameCode) == false) {
                    return false;
                }
                AddressDetails.Country.CountryNameCode other = ((AddressDetails.Country.CountryNameCode) obj);
                if (content == null) {
                    if (other.content!= null) {
                        return false;
                    }
                } else {
                    if (content.equals(other.content) == false) {
                        return false;
                    }
                }
                if (scheme == null) {
                    if (other.scheme!= null) {
                        return false;
                    }
                } else {
                    if (scheme.equals(other.scheme) == false) {
                        return false;
                    }
                }
                if (code == null) {
                    if (other.code!= null) {
                        return false;
                    }
                } else {
                    if (code.equals(other.code) == false) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * fluent setter
             * @see #setContent(String)
             * 
             * @param content
             *     required parameter
             */
            public AddressDetails.Country.CountryNameCode withContent(final String content) {
                this.setContent(content);
                return this;
            }

            /**
             * fluent setter
             * @see #setScheme(String)
             * 
             * @param scheme
             *     required parameter
             */
            public AddressDetails.Country.CountryNameCode withScheme(final String scheme) {
                this.setScheme(scheme);
                return this;
            }

            /**
             * fluent setter
             * @see #setCode(String)
             * 
             * @param code
             *     required parameter
             */
            public AddressDetails.Country.CountryNameCode withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public AddressDetails.Country.CountryNameCode clone() {
                AddressDetails.Country.CountryNameCode copy;
                try {
                    copy = ((AddressDetails.Country.CountryNameCode) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                return copy;
            }

        }

    }


    /**
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "addressIdentifier",
        "endorsementLineCode",
        "keyLineCode",
        "barcode",
        "sortingCode",
        "addressLatitude",
        "addressLatitudeDirection",
        "addressLongitude",
        "addressLongitudeDirection",
        "supplementaryPostalServiceData",
        "any"
    })
    @XmlRootElement(name = "PostalServiceElements", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class PostalServiceElements implements Cloneable
    {

        @XmlElement(name = "AddressIdentifier")
        protected List<AddressDetails.PostalServiceElements.AddressIdentifier> addressIdentifier;
        @XmlElement(name = "EndorsementLineCode")
        protected AddressDetails.PostalServiceElements.EndorsementLineCode endorsementLineCode;
        @XmlElement(name = "KeyLineCode")
        protected AddressDetails.PostalServiceElements.KeyLineCode keyLineCode;
        @XmlElement(name = "Barcode")
        protected AddressDetails.PostalServiceElements.Barcode barcode;
        @XmlElement(name = "SortingCode")
        protected AddressDetails.PostalServiceElements.SortingCode sortingCode;
        @XmlElement(name = "AddressLatitude")
        protected AddressDetails.PostalServiceElements.AddressLatitude addressLatitude;
        @XmlElement(name = "AddressLatitudeDirection")
        protected AddressDetails.PostalServiceElements.AddressLatitudeDirection addressLatitudeDirection;
        @XmlElement(name = "AddressLongitude")
        protected AddressDetails.PostalServiceElements.AddressLongitude addressLongitude;
        @XmlElement(name = "AddressLongitudeDirection")
        protected AddressDetails.PostalServiceElements.AddressLongitudeDirection addressLongitudeDirection;
        @XmlElement(name = "SupplementaryPostalServiceData")
        protected List<AddressDetails.PostalServiceElements.SupplementaryPostalServiceData> supplementaryPostalServiceData;
        @XmlAnyElement(lax = true)
        protected List<Object> any;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        protected String underscore;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        public PostalServiceElements() {
            super();
        }

        /**
         * 
         */
        public List<AddressDetails.PostalServiceElements.AddressIdentifier> getAddressIdentifier() {
            if (addressIdentifier == null) {
                addressIdentifier = new ArrayList<AddressDetails.PostalServiceElements.AddressIdentifier>();
            }
            return this.addressIdentifier;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.EndorsementLineCode}
         *     
         */
        public AddressDetails.PostalServiceElements.EndorsementLineCode getEndorsementLineCode() {
            return endorsementLineCode;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.EndorsementLineCode}
         *     
         */
        public void setEndorsementLineCode(AddressDetails.PostalServiceElements.EndorsementLineCode value) {
            this.endorsementLineCode = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.KeyLineCode}
         *     
         */
        public AddressDetails.PostalServiceElements.KeyLineCode getKeyLineCode() {
            return keyLineCode;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.KeyLineCode}
         *     
         */
        public void setKeyLineCode(AddressDetails.PostalServiceElements.KeyLineCode value) {
            this.keyLineCode = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.Barcode}
         *     
         */
        public AddressDetails.PostalServiceElements.Barcode getBarcode() {
            return barcode;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.Barcode}
         *     
         */
        public void setBarcode(AddressDetails.PostalServiceElements.Barcode value) {
            this.barcode = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.SortingCode}
         *     
         */
        public AddressDetails.PostalServiceElements.SortingCode getSortingCode() {
            return sortingCode;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.SortingCode}
         *     
         */
        public void setSortingCode(AddressDetails.PostalServiceElements.SortingCode value) {
            this.sortingCode = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.AddressLatitude}
         *     
         */
        public AddressDetails.PostalServiceElements.AddressLatitude getAddressLatitude() {
            return addressLatitude;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.AddressLatitude}
         *     
         */
        public void setAddressLatitude(AddressDetails.PostalServiceElements.AddressLatitude value) {
            this.addressLatitude = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.AddressLatitudeDirection}
         *     
         */
        public AddressDetails.PostalServiceElements.AddressLatitudeDirection getAddressLatitudeDirection() {
            return addressLatitudeDirection;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.AddressLatitudeDirection}
         *     
         */
        public void setAddressLatitudeDirection(AddressDetails.PostalServiceElements.AddressLatitudeDirection value) {
            this.addressLatitudeDirection = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.AddressLongitude}
         *     
         */
        public AddressDetails.PostalServiceElements.AddressLongitude getAddressLongitude() {
            return addressLongitude;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.AddressLongitude}
         *     
         */
        public void setAddressLongitude(AddressDetails.PostalServiceElements.AddressLongitude value) {
            this.addressLongitude = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.AddressLongitudeDirection}
         *     
         */
        public AddressDetails.PostalServiceElements.AddressLongitudeDirection getAddressLongitudeDirection() {
            return addressLongitudeDirection;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.AddressLongitudeDirection}
         *     
         */
        public void setAddressLongitudeDirection(AddressDetails.PostalServiceElements.AddressLongitudeDirection value) {
            this.addressLongitudeDirection = value;
        }

        /**
         * 
         */
        public List<AddressDetails.PostalServiceElements.SupplementaryPostalServiceData> getSupplementaryPostalServiceData() {
            if (supplementaryPostalServiceData == null) {
                supplementaryPostalServiceData = new ArrayList<AddressDetails.PostalServiceElements.SupplementaryPostalServiceData>();
            }
            return this.supplementaryPostalServiceData;
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
        public String getUnderscore() {
            return underscore;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setUnderscore(String value) {
            this.underscore = value;
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
            result = ((prime*result)+((addressIdentifier == null)? 0 :addressIdentifier.hashCode()));
            result = ((prime*result)+((endorsementLineCode == null)? 0 :endorsementLineCode.hashCode()));
            result = ((prime*result)+((keyLineCode == null)? 0 :keyLineCode.hashCode()));
            result = ((prime*result)+((barcode == null)? 0 :barcode.hashCode()));
            result = ((prime*result)+((sortingCode == null)? 0 :sortingCode.hashCode()));
            result = ((prime*result)+((addressLatitude == null)? 0 :addressLatitude.hashCode()));
            result = ((prime*result)+((addressLatitudeDirection == null)? 0 :addressLatitudeDirection.hashCode()));
            result = ((prime*result)+((addressLongitude == null)? 0 :addressLongitude.hashCode()));
            result = ((prime*result)+((addressLongitudeDirection == null)? 0 :addressLongitudeDirection.hashCode()));
            result = ((prime*result)+((supplementaryPostalServiceData == null)? 0 :supplementaryPostalServiceData.hashCode()));
            result = ((prime*result)+((any == null)? 0 :any.hashCode()));
            result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
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
            if ((obj instanceof AddressDetails.PostalServiceElements) == false) {
                return false;
            }
            AddressDetails.PostalServiceElements other = ((AddressDetails.PostalServiceElements) obj);
            if (addressIdentifier == null) {
                if (other.addressIdentifier!= null) {
                    return false;
                }
            } else {
                if (addressIdentifier.equals(other.addressIdentifier) == false) {
                    return false;
                }
            }
            if (endorsementLineCode == null) {
                if (other.endorsementLineCode!= null) {
                    return false;
                }
            } else {
                if (endorsementLineCode.equals(other.endorsementLineCode) == false) {
                    return false;
                }
            }
            if (keyLineCode == null) {
                if (other.keyLineCode!= null) {
                    return false;
                }
            } else {
                if (keyLineCode.equals(other.keyLineCode) == false) {
                    return false;
                }
            }
            if (barcode == null) {
                if (other.barcode!= null) {
                    return false;
                }
            } else {
                if (barcode.equals(other.barcode) == false) {
                    return false;
                }
            }
            if (sortingCode == null) {
                if (other.sortingCode!= null) {
                    return false;
                }
            } else {
                if (sortingCode.equals(other.sortingCode) == false) {
                    return false;
                }
            }
            if (addressLatitude == null) {
                if (other.addressLatitude!= null) {
                    return false;
                }
            } else {
                if (addressLatitude.equals(other.addressLatitude) == false) {
                    return false;
                }
            }
            if (addressLatitudeDirection == null) {
                if (other.addressLatitudeDirection!= null) {
                    return false;
                }
            } else {
                if (addressLatitudeDirection.equals(other.addressLatitudeDirection) == false) {
                    return false;
                }
            }
            if (addressLongitude == null) {
                if (other.addressLongitude!= null) {
                    return false;
                }
            } else {
                if (addressLongitude.equals(other.addressLongitude) == false) {
                    return false;
                }
            }
            if (addressLongitudeDirection == null) {
                if (other.addressLongitudeDirection!= null) {
                    return false;
                }
            } else {
                if (addressLongitudeDirection.equals(other.addressLongitudeDirection) == false) {
                    return false;
                }
            }
            if (supplementaryPostalServiceData == null) {
                if (other.supplementaryPostalServiceData!= null) {
                    return false;
                }
            } else {
                if (supplementaryPostalServiceData.equals(other.supplementaryPostalServiceData) == false) {
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
            if (underscore == null) {
                if (other.underscore!= null) {
                    return false;
                }
            } else {
                if (underscore.equals(other.underscore) == false) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Creates a new instance of {@link AddressDetails.PostalServiceElements.AddressIdentifier} and adds it to addressIdentifier.
         * This method is a short version for:
         * <code>
         * AddressIdentifier addressIdentifier = new AddressIdentifier();
         * this.getAddressIdentifier().add(addressIdentifier); </code>
         * 
         * 
         */
        public AddressDetails.PostalServiceElements.AddressIdentifier createAndAddAddressIdentifier() {
            AddressDetails.PostalServiceElements.AddressIdentifier newValue = new AddressDetails.PostalServiceElements.AddressIdentifier();
            this.getAddressIdentifier().add(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link AddressDetails.PostalServiceElements.EndorsementLineCode} and set it to endorsementLineCode.
         * 
         * This method is a short version for:
         * <code>
         * EndorsementLineCode endorsementLineCode = new EndorsementLineCode();
         * this.setEndorsementLineCode(endorsementLineCode); </code>
         * 
         * 
         */
        public AddressDetails.PostalServiceElements.EndorsementLineCode createAndSetEndorsementLineCode() {
            AddressDetails.PostalServiceElements.EndorsementLineCode newValue = new AddressDetails.PostalServiceElements.EndorsementLineCode();
            this.setEndorsementLineCode(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link AddressDetails.PostalServiceElements.KeyLineCode} and set it to keyLineCode.
         * 
         * This method is a short version for:
         * <code>
         * KeyLineCode keyLineCode = new KeyLineCode();
         * this.setKeyLineCode(keyLineCode); </code>
         * 
         * 
         */
        public AddressDetails.PostalServiceElements.KeyLineCode createAndSetKeyLineCode() {
            AddressDetails.PostalServiceElements.KeyLineCode newValue = new AddressDetails.PostalServiceElements.KeyLineCode();
            this.setKeyLineCode(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link AddressDetails.PostalServiceElements.Barcode} and set it to barcode.
         * 
         * This method is a short version for:
         * <code>
         * Barcode barcode = new Barcode();
         * this.setBarcode(barcode); </code>
         * 
         * 
         */
        public AddressDetails.PostalServiceElements.Barcode createAndSetBarcode() {
            AddressDetails.PostalServiceElements.Barcode newValue = new AddressDetails.PostalServiceElements.Barcode();
            this.setBarcode(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link AddressDetails.PostalServiceElements.SortingCode} and set it to sortingCode.
         * 
         * This method is a short version for:
         * <code>
         * SortingCode sortingCode = new SortingCode();
         * this.setSortingCode(sortingCode); </code>
         * 
         * 
         */
        public AddressDetails.PostalServiceElements.SortingCode createAndSetSortingCode() {
            AddressDetails.PostalServiceElements.SortingCode newValue = new AddressDetails.PostalServiceElements.SortingCode();
            this.setSortingCode(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link AddressDetails.PostalServiceElements.AddressLatitude} and set it to addressLatitude.
         * 
         * This method is a short version for:
         * <code>
         * AddressLatitude addressLatitude = new AddressLatitude();
         * this.setAddressLatitude(addressLatitude); </code>
         * 
         * 
         */
        public AddressDetails.PostalServiceElements.AddressLatitude createAndSetAddressLatitude() {
            AddressDetails.PostalServiceElements.AddressLatitude newValue = new AddressDetails.PostalServiceElements.AddressLatitude();
            this.setAddressLatitude(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link AddressDetails.PostalServiceElements.AddressLatitudeDirection} and set it to addressLatitudeDirection.
         * 
         * This method is a short version for:
         * <code>
         * AddressLatitudeDirection addressLatitudeDirection = new AddressLatitudeDirection();
         * this.setAddressLatitudeDirection(addressLatitudeDirection); </code>
         * 
         * 
         */
        public AddressDetails.PostalServiceElements.AddressLatitudeDirection createAndSetAddressLatitudeDirection() {
            AddressDetails.PostalServiceElements.AddressLatitudeDirection newValue = new AddressDetails.PostalServiceElements.AddressLatitudeDirection();
            this.setAddressLatitudeDirection(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link AddressDetails.PostalServiceElements.AddressLongitude} and set it to addressLongitude.
         * 
         * This method is a short version for:
         * <code>
         * AddressLongitude addressLongitude = new AddressLongitude();
         * this.setAddressLongitude(addressLongitude); </code>
         * 
         * 
         */
        public AddressDetails.PostalServiceElements.AddressLongitude createAndSetAddressLongitude() {
            AddressDetails.PostalServiceElements.AddressLongitude newValue = new AddressDetails.PostalServiceElements.AddressLongitude();
            this.setAddressLongitude(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link AddressDetails.PostalServiceElements.AddressLongitudeDirection} and set it to addressLongitudeDirection.
         * 
         * This method is a short version for:
         * <code>
         * AddressLongitudeDirection addressLongitudeDirection = new AddressLongitudeDirection();
         * this.setAddressLongitudeDirection(addressLongitudeDirection); </code>
         * 
         * 
         */
        public AddressDetails.PostalServiceElements.AddressLongitudeDirection createAndSetAddressLongitudeDirection() {
            AddressDetails.PostalServiceElements.AddressLongitudeDirection newValue = new AddressDetails.PostalServiceElements.AddressLongitudeDirection();
            this.setAddressLongitudeDirection(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link AddressDetails.PostalServiceElements.SupplementaryPostalServiceData} and adds it to supplementaryPostalServiceData.
         * This method is a short version for:
         * <code>
         * SupplementaryPostalServiceData supplementaryPostalServiceData = new SupplementaryPostalServiceData();
         * this.getSupplementaryPostalServiceData().add(supplementaryPostalServiceData); </code>
         * 
         * 
         */
        public AddressDetails.PostalServiceElements.SupplementaryPostalServiceData createAndAddSupplementaryPostalServiceData() {
            AddressDetails.PostalServiceElements.SupplementaryPostalServiceData newValue = new AddressDetails.PostalServiceElements.SupplementaryPostalServiceData();
            this.getSupplementaryPostalServiceData().add(newValue);
            return newValue;
        }

        /**
         * Sets the value of the addressIdentifier property Objects of the following type(s) are allowed in the list List<AddressIdentifier>.
         * <p>Note:
         * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withAddressIdentifier} instead.
         * 
         * 
         * @param addressIdentifier
         */
        public void setAddressIdentifier(final List<AddressDetails.PostalServiceElements.AddressIdentifier> addressIdentifier) {
            this.addressIdentifier = addressIdentifier;
        }

        /**
         * add a value to the addressIdentifier property collection
         * 
         * @param addressIdentifier
         *     Objects of the following type are allowed in the list: {@link AddressDetails.PostalServiceElements.AddressIdentifier}
         * @return
         *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
         */
        public AddressDetails.PostalServiceElements addToAddressIdentifier(final AddressDetails.PostalServiceElements.AddressIdentifier addressIdentifier) {
            this.getAddressIdentifier().add(addressIdentifier);
            return this;
        }

        /**
         * Sets the value of the supplementaryPostalServiceData property Objects of the following type(s) are allowed in the list List<SupplementaryPostalServiceData>.
         * <p>Note:
         * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withSupplementaryPostalServiceData} instead.
         * 
         * 
         * @param supplementaryPostalServiceData
         */
        public void setSupplementaryPostalServiceData(final List<AddressDetails.PostalServiceElements.SupplementaryPostalServiceData> supplementaryPostalServiceData) {
            this.supplementaryPostalServiceData = supplementaryPostalServiceData;
        }

        /**
         * add a value to the supplementaryPostalServiceData property collection
         * 
         * @param supplementaryPostalServiceData
         *     Objects of the following type are allowed in the list: {@link AddressDetails.PostalServiceElements.SupplementaryPostalServiceData}
         * @return
         *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
         */
        public AddressDetails.PostalServiceElements addToSupplementaryPostalServiceData(final AddressDetails.PostalServiceElements.SupplementaryPostalServiceData supplementaryPostalServiceData) {
            this.getSupplementaryPostalServiceData().add(supplementaryPostalServiceData);
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
        public AddressDetails.PostalServiceElements addToAny(final Object any) {
            this.getAny().add(any);
            return this;
        }

        /**
         * fluent setter
         * @see #setAddressIdentifier(List<AddressIdentifier>)
         * 
         * @param addressIdentifier
         *     required parameter
         */
        public AddressDetails.PostalServiceElements withAddressIdentifier(final List<AddressDetails.PostalServiceElements.AddressIdentifier> addressIdentifier) {
            this.setAddressIdentifier(addressIdentifier);
            return this;
        }

        /**
         * fluent setter
         * @see #setEndorsementLineCode(EndorsementLineCode)
         * 
         * @param endorsementLineCode
         *     required parameter
         */
        public AddressDetails.PostalServiceElements withEndorsementLineCode(final AddressDetails.PostalServiceElements.EndorsementLineCode endorsementLineCode) {
            this.setEndorsementLineCode(endorsementLineCode);
            return this;
        }

        /**
         * fluent setter
         * @see #setKeyLineCode(KeyLineCode)
         * 
         * @param keyLineCode
         *     required parameter
         */
        public AddressDetails.PostalServiceElements withKeyLineCode(final AddressDetails.PostalServiceElements.KeyLineCode keyLineCode) {
            this.setKeyLineCode(keyLineCode);
            return this;
        }

        /**
         * fluent setter
         * @see #setBarcode(Barcode)
         * 
         * @param barcode
         *     required parameter
         */
        public AddressDetails.PostalServiceElements withBarcode(final AddressDetails.PostalServiceElements.Barcode barcode) {
            this.setBarcode(barcode);
            return this;
        }

        /**
         * fluent setter
         * @see #setSortingCode(SortingCode)
         * 
         * @param sortingCode
         *     required parameter
         */
        public AddressDetails.PostalServiceElements withSortingCode(final AddressDetails.PostalServiceElements.SortingCode sortingCode) {
            this.setSortingCode(sortingCode);
            return this;
        }

        /**
         * fluent setter
         * @see #setAddressLatitude(AddressLatitude)
         * 
         * @param addressLatitude
         *     required parameter
         */
        public AddressDetails.PostalServiceElements withAddressLatitude(final AddressDetails.PostalServiceElements.AddressLatitude addressLatitude) {
            this.setAddressLatitude(addressLatitude);
            return this;
        }

        /**
         * fluent setter
         * @see #setAddressLatitudeDirection(AddressLatitudeDirection)
         * 
         * @param addressLatitudeDirection
         *     required parameter
         */
        public AddressDetails.PostalServiceElements withAddressLatitudeDirection(final AddressDetails.PostalServiceElements.AddressLatitudeDirection addressLatitudeDirection) {
            this.setAddressLatitudeDirection(addressLatitudeDirection);
            return this;
        }

        /**
         * fluent setter
         * @see #setAddressLongitude(AddressLongitude)
         * 
         * @param addressLongitude
         *     required parameter
         */
        public AddressDetails.PostalServiceElements withAddressLongitude(final AddressDetails.PostalServiceElements.AddressLongitude addressLongitude) {
            this.setAddressLongitude(addressLongitude);
            return this;
        }

        /**
         * fluent setter
         * @see #setAddressLongitudeDirection(AddressLongitudeDirection)
         * 
         * @param addressLongitudeDirection
         *     required parameter
         */
        public AddressDetails.PostalServiceElements withAddressLongitudeDirection(final AddressDetails.PostalServiceElements.AddressLongitudeDirection addressLongitudeDirection) {
            this.setAddressLongitudeDirection(addressLongitudeDirection);
            return this;
        }

        /**
         * fluent setter
         * @see #setSupplementaryPostalServiceData(List<SupplementaryPostalServiceData>)
         * 
         * @param supplementaryPostalServiceData
         *     required parameter
         */
        public AddressDetails.PostalServiceElements withSupplementaryPostalServiceData(final List<AddressDetails.PostalServiceElements.SupplementaryPostalServiceData> supplementaryPostalServiceData) {
            this.setSupplementaryPostalServiceData(supplementaryPostalServiceData);
            return this;
        }

        /**
         * fluent setter
         * @see #setAny(List<Object>)
         * 
         * @param any
         *     required parameter
         */
        public AddressDetails.PostalServiceElements withAny(final List<Object> any) {
            this.setAny(any);
            return this;
        }

        /**
         * fluent setter
         * @see #setUnderscore(String)
         * 
         * @param underscore
         *     required parameter
         */
        public AddressDetails.PostalServiceElements withUnderscore(final String underscore) {
            this.setUnderscore(underscore);
            return this;
        }

        @Override
        public AddressDetails.PostalServiceElements clone() {
            AddressDetails.PostalServiceElements copy;
            try {
                copy = ((AddressDetails.PostalServiceElements) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            copy.addressIdentifier = new ArrayList<AddressDetails.PostalServiceElements.AddressIdentifier>((getAddressIdentifier().size()));
            for (AddressDetails.PostalServiceElements.AddressIdentifier iter: addressIdentifier) {
                copy.addressIdentifier.add(iter.clone());
            }
            copy.endorsementLineCode = ((endorsementLineCode == null)?null:((AddressDetails.PostalServiceElements.EndorsementLineCode) endorsementLineCode.clone()));
            copy.keyLineCode = ((keyLineCode == null)?null:((AddressDetails.PostalServiceElements.KeyLineCode) keyLineCode.clone()));
            copy.barcode = ((barcode == null)?null:((AddressDetails.PostalServiceElements.Barcode) barcode.clone()));
            copy.sortingCode = ((sortingCode == null)?null:((AddressDetails.PostalServiceElements.SortingCode) sortingCode.clone()));
            copy.addressLatitude = ((addressLatitude == null)?null:((AddressDetails.PostalServiceElements.AddressLatitude) addressLatitude.clone()));
            copy.addressLatitudeDirection = ((addressLatitudeDirection == null)?null:((AddressDetails.PostalServiceElements.AddressLatitudeDirection) addressLatitudeDirection.clone()));
            copy.addressLongitude = ((addressLongitude == null)?null:((AddressDetails.PostalServiceElements.AddressLongitude) addressLongitude.clone()));
            copy.addressLongitudeDirection = ((addressLongitudeDirection == null)?null:((AddressDetails.PostalServiceElements.AddressLongitudeDirection) addressLongitudeDirection.clone()));
            copy.supplementaryPostalServiceData = new ArrayList<AddressDetails.PostalServiceElements.SupplementaryPostalServiceData>((getSupplementaryPostalServiceData().size()));
            for (AddressDetails.PostalServiceElements.SupplementaryPostalServiceData iter: supplementaryPostalServiceData) {
                copy.supplementaryPostalServiceData.add(iter.clone());
            }
            copy.any = new ArrayList<Object>((getAny().size()));
            for (Object iter: any) {
                copy.any.add(iter);
            }
            return copy;
        }


        /**
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        @XmlRootElement(name = "AddressIdentifier", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class AddressIdentifier implements Cloneable
        {

            @XmlValue
            protected String content;
            @XmlAttribute(name = "IdentifierType")
            @XmlSchemaType(name = "anySimpleType")
            protected String identifier;
            @XmlAttribute(name = "Type")
            @XmlSchemaType(name = "anySimpleType")
            protected String underscore;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            protected String code;
            @XmlAnyAttribute
            private Map<QName, String> otherAttributes = new HashMap<QName, String>();

            public AddressIdentifier() {
                super();
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getContent() {
                return content;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setContent(String value) {
                this.content = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getIdentifier() {
                return identifier;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setIdentifier(String value) {
                this.identifier = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getUnderscore() {
                return underscore;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setUnderscore(String value) {
                this.underscore = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getCode() {
                return code;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setCode(String value) {
                this.code = value;
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
                result = ((prime*result)+((content == null)? 0 :content.hashCode()));
                result = ((prime*result)+((identifier == null)? 0 :identifier.hashCode()));
                result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
                result = ((prime*result)+((code == null)? 0 :code.hashCode()));
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
                if ((obj instanceof AddressDetails.PostalServiceElements.AddressIdentifier) == false) {
                    return false;
                }
                AddressDetails.PostalServiceElements.AddressIdentifier other = ((AddressDetails.PostalServiceElements.AddressIdentifier) obj);
                if (content == null) {
                    if (other.content!= null) {
                        return false;
                    }
                } else {
                    if (content.equals(other.content) == false) {
                        return false;
                    }
                }
                if (identifier == null) {
                    if (other.identifier!= null) {
                        return false;
                    }
                } else {
                    if (identifier.equals(other.identifier) == false) {
                        return false;
                    }
                }
                if (underscore == null) {
                    if (other.underscore!= null) {
                        return false;
                    }
                } else {
                    if (underscore.equals(other.underscore) == false) {
                        return false;
                    }
                }
                if (code == null) {
                    if (other.code!= null) {
                        return false;
                    }
                } else {
                    if (code.equals(other.code) == false) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * fluent setter
             * @see #setContent(String)
             * 
             * @param content
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressIdentifier withContent(final String content) {
                this.setContent(content);
                return this;
            }

            /**
             * fluent setter
             * @see #setIdentifier(String)
             * 
             * @param identifier
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressIdentifier withIdentifier(final String identifier) {
                this.setIdentifier(identifier);
                return this;
            }

            /**
             * fluent setter
             * @see #setUnderscore(String)
             * 
             * @param underscore
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressIdentifier withUnderscore(final String underscore) {
                this.setUnderscore(underscore);
                return this;
            }

            /**
             * fluent setter
             * @see #setCode(String)
             * 
             * @param code
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressIdentifier withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public AddressDetails.PostalServiceElements.AddressIdentifier clone() {
                AddressDetails.PostalServiceElements.AddressIdentifier copy;
                try {
                    copy = ((AddressDetails.PostalServiceElements.AddressIdentifier) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                return copy;
            }

        }


        /**
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        @XmlRootElement(name = "AddressLatitude", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class AddressLatitude implements Cloneable
        {

            @XmlValue
            protected String content;
            @XmlAttribute(name = "Type")
            @XmlSchemaType(name = "anySimpleType")
            protected String underscore;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            protected String code;
            @XmlAnyAttribute
            private Map<QName, String> otherAttributes = new HashMap<QName, String>();

            public AddressLatitude() {
                super();
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getContent() {
                return content;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setContent(String value) {
                this.content = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getUnderscore() {
                return underscore;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setUnderscore(String value) {
                this.underscore = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getCode() {
                return code;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setCode(String value) {
                this.code = value;
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
                result = ((prime*result)+((content == null)? 0 :content.hashCode()));
                result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
                result = ((prime*result)+((code == null)? 0 :code.hashCode()));
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
                if ((obj instanceof AddressDetails.PostalServiceElements.AddressLatitude) == false) {
                    return false;
                }
                AddressDetails.PostalServiceElements.AddressLatitude other = ((AddressDetails.PostalServiceElements.AddressLatitude) obj);
                if (content == null) {
                    if (other.content!= null) {
                        return false;
                    }
                } else {
                    if (content.equals(other.content) == false) {
                        return false;
                    }
                }
                if (underscore == null) {
                    if (other.underscore!= null) {
                        return false;
                    }
                } else {
                    if (underscore.equals(other.underscore) == false) {
                        return false;
                    }
                }
                if (code == null) {
                    if (other.code!= null) {
                        return false;
                    }
                } else {
                    if (code.equals(other.code) == false) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * fluent setter
             * @see #setContent(String)
             * 
             * @param content
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressLatitude withContent(final String content) {
                this.setContent(content);
                return this;
            }

            /**
             * fluent setter
             * @see #setUnderscore(String)
             * 
             * @param underscore
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressLatitude withUnderscore(final String underscore) {
                this.setUnderscore(underscore);
                return this;
            }

            /**
             * fluent setter
             * @see #setCode(String)
             * 
             * @param code
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressLatitude withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public AddressDetails.PostalServiceElements.AddressLatitude clone() {
                AddressDetails.PostalServiceElements.AddressLatitude copy;
                try {
                    copy = ((AddressDetails.PostalServiceElements.AddressLatitude) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                return copy;
            }

        }


        /**
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        @XmlRootElement(name = "AddressLatitudeDirection", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class AddressLatitudeDirection implements Cloneable
        {

            @XmlValue
            protected String content;
            @XmlAttribute(name = "Type")
            @XmlSchemaType(name = "anySimpleType")
            protected String underscore;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            protected String code;
            @XmlAnyAttribute
            private Map<QName, String> otherAttributes = new HashMap<QName, String>();

            public AddressLatitudeDirection() {
                super();
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getContent() {
                return content;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setContent(String value) {
                this.content = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getUnderscore() {
                return underscore;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setUnderscore(String value) {
                this.underscore = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getCode() {
                return code;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setCode(String value) {
                this.code = value;
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
                result = ((prime*result)+((content == null)? 0 :content.hashCode()));
                result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
                result = ((prime*result)+((code == null)? 0 :code.hashCode()));
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
                if ((obj instanceof AddressDetails.PostalServiceElements.AddressLatitudeDirection) == false) {
                    return false;
                }
                AddressDetails.PostalServiceElements.AddressLatitudeDirection other = ((AddressDetails.PostalServiceElements.AddressLatitudeDirection) obj);
                if (content == null) {
                    if (other.content!= null) {
                        return false;
                    }
                } else {
                    if (content.equals(other.content) == false) {
                        return false;
                    }
                }
                if (underscore == null) {
                    if (other.underscore!= null) {
                        return false;
                    }
                } else {
                    if (underscore.equals(other.underscore) == false) {
                        return false;
                    }
                }
                if (code == null) {
                    if (other.code!= null) {
                        return false;
                    }
                } else {
                    if (code.equals(other.code) == false) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * fluent setter
             * @see #setContent(String)
             * 
             * @param content
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressLatitudeDirection withContent(final String content) {
                this.setContent(content);
                return this;
            }

            /**
             * fluent setter
             * @see #setUnderscore(String)
             * 
             * @param underscore
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressLatitudeDirection withUnderscore(final String underscore) {
                this.setUnderscore(underscore);
                return this;
            }

            /**
             * fluent setter
             * @see #setCode(String)
             * 
             * @param code
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressLatitudeDirection withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public AddressDetails.PostalServiceElements.AddressLatitudeDirection clone() {
                AddressDetails.PostalServiceElements.AddressLatitudeDirection copy;
                try {
                    copy = ((AddressDetails.PostalServiceElements.AddressLatitudeDirection) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                return copy;
            }

        }


        /**
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        @XmlRootElement(name = "AddressLongitude", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class AddressLongitude implements Cloneable
        {

            @XmlValue
            protected String content;
            @XmlAttribute(name = "Type")
            @XmlSchemaType(name = "anySimpleType")
            protected String underscore;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            protected String code;
            @XmlAnyAttribute
            private Map<QName, String> otherAttributes = new HashMap<QName, String>();

            public AddressLongitude() {
                super();
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getContent() {
                return content;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setContent(String value) {
                this.content = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getUnderscore() {
                return underscore;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setUnderscore(String value) {
                this.underscore = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getCode() {
                return code;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setCode(String value) {
                this.code = value;
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
                result = ((prime*result)+((content == null)? 0 :content.hashCode()));
                result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
                result = ((prime*result)+((code == null)? 0 :code.hashCode()));
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
                if ((obj instanceof AddressDetails.PostalServiceElements.AddressLongitude) == false) {
                    return false;
                }
                AddressDetails.PostalServiceElements.AddressLongitude other = ((AddressDetails.PostalServiceElements.AddressLongitude) obj);
                if (content == null) {
                    if (other.content!= null) {
                        return false;
                    }
                } else {
                    if (content.equals(other.content) == false) {
                        return false;
                    }
                }
                if (underscore == null) {
                    if (other.underscore!= null) {
                        return false;
                    }
                } else {
                    if (underscore.equals(other.underscore) == false) {
                        return false;
                    }
                }
                if (code == null) {
                    if (other.code!= null) {
                        return false;
                    }
                } else {
                    if (code.equals(other.code) == false) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * fluent setter
             * @see #setContent(String)
             * 
             * @param content
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressLongitude withContent(final String content) {
                this.setContent(content);
                return this;
            }

            /**
             * fluent setter
             * @see #setUnderscore(String)
             * 
             * @param underscore
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressLongitude withUnderscore(final String underscore) {
                this.setUnderscore(underscore);
                return this;
            }

            /**
             * fluent setter
             * @see #setCode(String)
             * 
             * @param code
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressLongitude withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public AddressDetails.PostalServiceElements.AddressLongitude clone() {
                AddressDetails.PostalServiceElements.AddressLongitude copy;
                try {
                    copy = ((AddressDetails.PostalServiceElements.AddressLongitude) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                return copy;
            }

        }


        /**
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        @XmlRootElement(name = "AddressLongitudeDirection", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class AddressLongitudeDirection implements Cloneable
        {

            @XmlValue
            protected String content;
            @XmlAttribute(name = "Type")
            @XmlSchemaType(name = "anySimpleType")
            protected String underscore;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            protected String code;
            @XmlAnyAttribute
            private Map<QName, String> otherAttributes = new HashMap<QName, String>();

            public AddressLongitudeDirection() {
                super();
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getContent() {
                return content;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setContent(String value) {
                this.content = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getUnderscore() {
                return underscore;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setUnderscore(String value) {
                this.underscore = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getCode() {
                return code;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setCode(String value) {
                this.code = value;
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
                result = ((prime*result)+((content == null)? 0 :content.hashCode()));
                result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
                result = ((prime*result)+((code == null)? 0 :code.hashCode()));
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
                if ((obj instanceof AddressDetails.PostalServiceElements.AddressLongitudeDirection) == false) {
                    return false;
                }
                AddressDetails.PostalServiceElements.AddressLongitudeDirection other = ((AddressDetails.PostalServiceElements.AddressLongitudeDirection) obj);
                if (content == null) {
                    if (other.content!= null) {
                        return false;
                    }
                } else {
                    if (content.equals(other.content) == false) {
                        return false;
                    }
                }
                if (underscore == null) {
                    if (other.underscore!= null) {
                        return false;
                    }
                } else {
                    if (underscore.equals(other.underscore) == false) {
                        return false;
                    }
                }
                if (code == null) {
                    if (other.code!= null) {
                        return false;
                    }
                } else {
                    if (code.equals(other.code) == false) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * fluent setter
             * @see #setContent(String)
             * 
             * @param content
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressLongitudeDirection withContent(final String content) {
                this.setContent(content);
                return this;
            }

            /**
             * fluent setter
             * @see #setUnderscore(String)
             * 
             * @param underscore
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressLongitudeDirection withUnderscore(final String underscore) {
                this.setUnderscore(underscore);
                return this;
            }

            /**
             * fluent setter
             * @see #setCode(String)
             * 
             * @param code
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.AddressLongitudeDirection withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public AddressDetails.PostalServiceElements.AddressLongitudeDirection clone() {
                AddressDetails.PostalServiceElements.AddressLongitudeDirection copy;
                try {
                    copy = ((AddressDetails.PostalServiceElements.AddressLongitudeDirection) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                return copy;
            }

        }


        /**
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        @XmlRootElement(name = "Barcode", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class Barcode implements Cloneable
        {

            @XmlValue
            protected String content;
            @XmlAttribute(name = "Type")
            @XmlSchemaType(name = "anySimpleType")
            protected String underscore;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            protected String code;
            @XmlAnyAttribute
            private Map<QName, String> otherAttributes = new HashMap<QName, String>();

            public Barcode() {
                super();
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getContent() {
                return content;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setContent(String value) {
                this.content = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getUnderscore() {
                return underscore;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setUnderscore(String value) {
                this.underscore = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getCode() {
                return code;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setCode(String value) {
                this.code = value;
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
                result = ((prime*result)+((content == null)? 0 :content.hashCode()));
                result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
                result = ((prime*result)+((code == null)? 0 :code.hashCode()));
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
                if ((obj instanceof AddressDetails.PostalServiceElements.Barcode) == false) {
                    return false;
                }
                AddressDetails.PostalServiceElements.Barcode other = ((AddressDetails.PostalServiceElements.Barcode) obj);
                if (content == null) {
                    if (other.content!= null) {
                        return false;
                    }
                } else {
                    if (content.equals(other.content) == false) {
                        return false;
                    }
                }
                if (underscore == null) {
                    if (other.underscore!= null) {
                        return false;
                    }
                } else {
                    if (underscore.equals(other.underscore) == false) {
                        return false;
                    }
                }
                if (code == null) {
                    if (other.code!= null) {
                        return false;
                    }
                } else {
                    if (code.equals(other.code) == false) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * fluent setter
             * @see #setContent(String)
             * 
             * @param content
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.Barcode withContent(final String content) {
                this.setContent(content);
                return this;
            }

            /**
             * fluent setter
             * @see #setUnderscore(String)
             * 
             * @param underscore
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.Barcode withUnderscore(final String underscore) {
                this.setUnderscore(underscore);
                return this;
            }

            /**
             * fluent setter
             * @see #setCode(String)
             * 
             * @param code
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.Barcode withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public AddressDetails.PostalServiceElements.Barcode clone() {
                AddressDetails.PostalServiceElements.Barcode copy;
                try {
                    copy = ((AddressDetails.PostalServiceElements.Barcode) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                return copy;
            }

        }


        /**
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        @XmlRootElement(name = "EndorsementLineCode", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class EndorsementLineCode implements Cloneable
        {

            @XmlValue
            protected String content;
            @XmlAttribute(name = "Type")
            @XmlSchemaType(name = "anySimpleType")
            protected String underscore;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            protected String code;
            @XmlAnyAttribute
            private Map<QName, String> otherAttributes = new HashMap<QName, String>();

            public EndorsementLineCode() {
                super();
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getContent() {
                return content;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setContent(String value) {
                this.content = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getUnderscore() {
                return underscore;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setUnderscore(String value) {
                this.underscore = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getCode() {
                return code;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setCode(String value) {
                this.code = value;
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
                result = ((prime*result)+((content == null)? 0 :content.hashCode()));
                result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
                result = ((prime*result)+((code == null)? 0 :code.hashCode()));
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
                if ((obj instanceof AddressDetails.PostalServiceElements.EndorsementLineCode) == false) {
                    return false;
                }
                AddressDetails.PostalServiceElements.EndorsementLineCode other = ((AddressDetails.PostalServiceElements.EndorsementLineCode) obj);
                if (content == null) {
                    if (other.content!= null) {
                        return false;
                    }
                } else {
                    if (content.equals(other.content) == false) {
                        return false;
                    }
                }
                if (underscore == null) {
                    if (other.underscore!= null) {
                        return false;
                    }
                } else {
                    if (underscore.equals(other.underscore) == false) {
                        return false;
                    }
                }
                if (code == null) {
                    if (other.code!= null) {
                        return false;
                    }
                } else {
                    if (code.equals(other.code) == false) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * fluent setter
             * @see #setContent(String)
             * 
             * @param content
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.EndorsementLineCode withContent(final String content) {
                this.setContent(content);
                return this;
            }

            /**
             * fluent setter
             * @see #setUnderscore(String)
             * 
             * @param underscore
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.EndorsementLineCode withUnderscore(final String underscore) {
                this.setUnderscore(underscore);
                return this;
            }

            /**
             * fluent setter
             * @see #setCode(String)
             * 
             * @param code
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.EndorsementLineCode withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public AddressDetails.PostalServiceElements.EndorsementLineCode clone() {
                AddressDetails.PostalServiceElements.EndorsementLineCode copy;
                try {
                    copy = ((AddressDetails.PostalServiceElements.EndorsementLineCode) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                return copy;
            }

        }


        /**
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        @XmlRootElement(name = "KeyLineCode", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class KeyLineCode implements Cloneable
        {

            @XmlValue
            protected String content;
            @XmlAttribute(name = "Type")
            @XmlSchemaType(name = "anySimpleType")
            protected String underscore;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            protected String code;
            @XmlAnyAttribute
            private Map<QName, String> otherAttributes = new HashMap<QName, String>();

            public KeyLineCode() {
                super();
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getContent() {
                return content;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setContent(String value) {
                this.content = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getUnderscore() {
                return underscore;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setUnderscore(String value) {
                this.underscore = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getCode() {
                return code;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setCode(String value) {
                this.code = value;
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
                result = ((prime*result)+((content == null)? 0 :content.hashCode()));
                result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
                result = ((prime*result)+((code == null)? 0 :code.hashCode()));
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
                if ((obj instanceof AddressDetails.PostalServiceElements.KeyLineCode) == false) {
                    return false;
                }
                AddressDetails.PostalServiceElements.KeyLineCode other = ((AddressDetails.PostalServiceElements.KeyLineCode) obj);
                if (content == null) {
                    if (other.content!= null) {
                        return false;
                    }
                } else {
                    if (content.equals(other.content) == false) {
                        return false;
                    }
                }
                if (underscore == null) {
                    if (other.underscore!= null) {
                        return false;
                    }
                } else {
                    if (underscore.equals(other.underscore) == false) {
                        return false;
                    }
                }
                if (code == null) {
                    if (other.code!= null) {
                        return false;
                    }
                } else {
                    if (code.equals(other.code) == false) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * fluent setter
             * @see #setContent(String)
             * 
             * @param content
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.KeyLineCode withContent(final String content) {
                this.setContent(content);
                return this;
            }

            /**
             * fluent setter
             * @see #setUnderscore(String)
             * 
             * @param underscore
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.KeyLineCode withUnderscore(final String underscore) {
                this.setUnderscore(underscore);
                return this;
            }

            /**
             * fluent setter
             * @see #setCode(String)
             * 
             * @param code
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.KeyLineCode withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public AddressDetails.PostalServiceElements.KeyLineCode clone() {
                AddressDetails.PostalServiceElements.KeyLineCode copy;
                try {
                    copy = ((AddressDetails.PostalServiceElements.KeyLineCode) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                return copy;
            }

        }


        /**
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        @XmlRootElement(name = "SortingCode", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class SortingCode implements Cloneable
        {

            @XmlAttribute(name = "Type")
            @XmlSchemaType(name = "anySimpleType")
            protected String underscore;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            protected String code;

            public SortingCode() {
                super();
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getUnderscore() {
                return underscore;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setUnderscore(String value) {
                this.underscore = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getCode() {
                return code;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setCode(String value) {
                this.code = value;
            }

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
                result = ((prime*result)+((code == null)? 0 :code.hashCode()));
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
                if ((obj instanceof AddressDetails.PostalServiceElements.SortingCode) == false) {
                    return false;
                }
                AddressDetails.PostalServiceElements.SortingCode other = ((AddressDetails.PostalServiceElements.SortingCode) obj);
                if (underscore == null) {
                    if (other.underscore!= null) {
                        return false;
                    }
                } else {
                    if (underscore.equals(other.underscore) == false) {
                        return false;
                    }
                }
                if (code == null) {
                    if (other.code!= null) {
                        return false;
                    }
                } else {
                    if (code.equals(other.code) == false) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * fluent setter
             * @see #setUnderscore(String)
             * 
             * @param underscore
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.SortingCode withUnderscore(final String underscore) {
                this.setUnderscore(underscore);
                return this;
            }

            /**
             * fluent setter
             * @see #setCode(String)
             * 
             * @param code
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.SortingCode withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public AddressDetails.PostalServiceElements.SortingCode clone() {
                AddressDetails.PostalServiceElements.SortingCode copy;
                try {
                    copy = ((AddressDetails.PostalServiceElements.SortingCode) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                return copy;
            }

        }


        /**
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        @XmlRootElement(name = "SupplementaryPostalServiceData", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class SupplementaryPostalServiceData implements Cloneable
        {

            @XmlValue
            protected String content;
            @XmlAttribute(name = "Type")
            @XmlSchemaType(name = "anySimpleType")
            protected String underscore;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            protected String code;
            @XmlAnyAttribute
            private Map<QName, String> otherAttributes = new HashMap<QName, String>();

            public SupplementaryPostalServiceData() {
                super();
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getContent() {
                return content;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setContent(String value) {
                this.content = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getUnderscore() {
                return underscore;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setUnderscore(String value) {
                this.underscore = value;
            }

            /**
             * 
             * @return
             *     possible object is
             *     {@link String}
             *     
             */
            public String getCode() {
                return code;
            }

            /**
             * 
             * @param value
             *     allowed object is
             *     {@link String}
             *     
             */
            public void setCode(String value) {
                this.code = value;
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
                result = ((prime*result)+((content == null)? 0 :content.hashCode()));
                result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
                result = ((prime*result)+((code == null)? 0 :code.hashCode()));
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
                if ((obj instanceof AddressDetails.PostalServiceElements.SupplementaryPostalServiceData) == false) {
                    return false;
                }
                AddressDetails.PostalServiceElements.SupplementaryPostalServiceData other = ((AddressDetails.PostalServiceElements.SupplementaryPostalServiceData) obj);
                if (content == null) {
                    if (other.content!= null) {
                        return false;
                    }
                } else {
                    if (content.equals(other.content) == false) {
                        return false;
                    }
                }
                if (underscore == null) {
                    if (other.underscore!= null) {
                        return false;
                    }
                } else {
                    if (underscore.equals(other.underscore) == false) {
                        return false;
                    }
                }
                if (code == null) {
                    if (other.code!= null) {
                        return false;
                    }
                } else {
                    if (code.equals(other.code) == false) {
                        return false;
                    }
                }
                return true;
            }

            /**
             * fluent setter
             * @see #setContent(String)
             * 
             * @param content
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.SupplementaryPostalServiceData withContent(final String content) {
                this.setContent(content);
                return this;
            }

            /**
             * fluent setter
             * @see #setUnderscore(String)
             * 
             * @param underscore
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.SupplementaryPostalServiceData withUnderscore(final String underscore) {
                this.setUnderscore(underscore);
                return this;
            }

            /**
             * fluent setter
             * @see #setCode(String)
             * 
             * @param code
             *     required parameter
             */
            public AddressDetails.PostalServiceElements.SupplementaryPostalServiceData withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public AddressDetails.PostalServiceElements.SupplementaryPostalServiceData clone() {
                AddressDetails.PostalServiceElements.SupplementaryPostalServiceData copy;
                try {
                    copy = ((AddressDetails.PostalServiceElements.SupplementaryPostalServiceData) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                return copy;
            }

        }

    }

}
