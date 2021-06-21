
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
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "addressLine",
    "administrativeAreaName",
    "subAdministrativeArea",
    "locality",
    "postOffice",
    "postalCode",
    "any"
})
@XmlRootElement(name = "AdministrativeArea")
public class AdministrativeArea implements Cloneable
{

    @XmlElement(name = "AddressLine")
    protected List<AddressLine> addressLine;
    @XmlElement(name = "AdministrativeAreaName")
    protected List<AdministrativeArea.AdministrativeAreaName> administrativeAreaName;
    @XmlElement(name = "SubAdministrativeArea")
    protected AdministrativeArea.SubAdministrativeArea subAdministrativeArea;
    @XmlElement(name = "Locality")
    protected Locality locality;
    @XmlElement(name = "PostOffice")
    protected PostOffice postOffice;
    @XmlElement(name = "PostalCode")
    protected PostalCode postalCode;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anySimpleType")
    protected String underscore;
    @XmlAttribute(name = "UsageType")
    @XmlSchemaType(name = "anySimpleType")
    protected String usage;
    @XmlAttribute(name = "Indicator")
    @XmlSchemaType(name = "anySimpleType")
    protected String indicator;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Value constructor with only mandatory fields
     * 
     * @param postalCode
     *     required parameter
     * @param locality
     *     required parameter
     * @param postOffice
     *     required parameter
     */
    public AdministrativeArea(final Locality locality, final PostOffice postOffice, final PostalCode postalCode) {
        super();
        this.locality = locality;
        this.postOffice = postOffice;
        this.postalCode = postalCode;
    }

    /**
     * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
     * 
     */
    @Deprecated
    private AdministrativeArea() {
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
    public List<AdministrativeArea.AdministrativeAreaName> getAdministrativeAreaName() {
        if (administrativeAreaName == null) {
            administrativeAreaName = new ArrayList<AdministrativeArea.AdministrativeAreaName>();
        }
        return this.administrativeAreaName;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link AdministrativeArea.SubAdministrativeArea}
     *     
     */
    public AdministrativeArea.SubAdministrativeArea getSubAdministrativeArea() {
        return subAdministrativeArea;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link AdministrativeArea.SubAdministrativeArea}
     *     
     */
    public void setSubAdministrativeArea(AdministrativeArea.SubAdministrativeArea value) {
        this.subAdministrativeArea = value;
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
     *     {@link PostOffice}
     *     
     */
    public PostOffice getPostOffice() {
        return postOffice;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link PostOffice}
     *     
     */
    public void setPostOffice(PostOffice value) {
        this.postOffice = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link PostalCode}
     *     
     */
    public PostalCode getPostalCode() {
        return postalCode;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link PostalCode}
     *     
     */
    public void setPostalCode(PostalCode value) {
        this.postalCode = value;
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
     *     possible object is
     *     {@link String}
     *     
     */
    public String getUsage() {
        return usage;
    }

    /**
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
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getIndicator() {
        return indicator;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setIndicator(String value) {
        this.indicator = value;
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
        result = ((prime*result)+((administrativeAreaName == null)? 0 :administrativeAreaName.hashCode()));
        result = ((prime*result)+((subAdministrativeArea == null)? 0 :subAdministrativeArea.hashCode()));
        result = ((prime*result)+((locality == null)? 0 :locality.hashCode()));
        result = ((prime*result)+((postOffice == null)? 0 :postOffice.hashCode()));
        result = ((prime*result)+((postalCode == null)? 0 :postalCode.hashCode()));
        result = ((prime*result)+((any == null)? 0 :any.hashCode()));
        result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
        result = ((prime*result)+((usage == null)? 0 :usage.hashCode()));
        result = ((prime*result)+((indicator == null)? 0 :indicator.hashCode()));
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
        if ((obj instanceof AdministrativeArea) == false) {
            return false;
        }
        AdministrativeArea other = ((AdministrativeArea) obj);
        if (addressLine == null) {
            if (other.addressLine!= null) {
                return false;
            }
        } else {
            if (addressLine.equals(other.addressLine) == false) {
                return false;
            }
        }
        if (administrativeAreaName == null) {
            if (other.administrativeAreaName!= null) {
                return false;
            }
        } else {
            if (administrativeAreaName.equals(other.administrativeAreaName) == false) {
                return false;
            }
        }
        if (subAdministrativeArea == null) {
            if (other.subAdministrativeArea!= null) {
                return false;
            }
        } else {
            if (subAdministrativeArea.equals(other.subAdministrativeArea) == false) {
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
        if (postOffice == null) {
            if (other.postOffice!= null) {
                return false;
            }
        } else {
            if (postOffice.equals(other.postOffice) == false) {
                return false;
            }
        }
        if (postalCode == null) {
            if (other.postalCode!= null) {
                return false;
            }
        } else {
            if (postalCode.equals(other.postalCode) == false) {
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
        if (usage == null) {
            if (other.usage!= null) {
                return false;
            }
        } else {
            if (usage.equals(other.usage) == false) {
                return false;
            }
        }
        if (indicator == null) {
            if (other.indicator!= null) {
                return false;
            }
        } else {
            if (indicator.equals(other.indicator) == false) {
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
     * Creates a new instance of {@link AdministrativeArea.AdministrativeAreaName} and adds it to administrativeAreaName.
     * This method is a short version for:
     * <code>
     * AdministrativeAreaName administrativeAreaName = new AdministrativeAreaName();
     * this.getAdministrativeAreaName().add(administrativeAreaName); </code>
     * 
     * 
     */
    public AdministrativeArea.AdministrativeAreaName createAndAddAdministrativeAreaName() {
        AdministrativeArea.AdministrativeAreaName newValue = new AdministrativeArea.AdministrativeAreaName();
        this.getAdministrativeAreaName().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link AdministrativeArea.SubAdministrativeArea} and set it to subAdministrativeArea.
     * 
     * This method is a short version for:
     * <code>
     * SubAdministrativeArea subAdministrativeArea = new SubAdministrativeArea();
     * this.setSubAdministrativeArea(subAdministrativeArea); </code>
     * 
     * 
     * @param postalCode
     *     required parameter
     * @param locality
     *     required parameter
     * @param postOffice
     *     required parameter
     */
    public AdministrativeArea.SubAdministrativeArea createAndSetSubAdministrativeArea(final Locality locality, final PostOffice postOffice, final PostalCode postalCode) {
        AdministrativeArea.SubAdministrativeArea newValue = new AdministrativeArea.SubAdministrativeArea(locality, postOffice, postalCode);
        this.setSubAdministrativeArea(newValue);
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
     * Creates a new instance of {@link PostOffice} and set it to postOffice.
     * 
     * This method is a short version for:
     * <code>
     * PostOffice postOffice = new PostOffice();
     * this.setPostOffice(postOffice); </code>
     * 
     * 
     */
    public PostOffice createAndSetPostOffice() {
        PostOffice newValue = new PostOffice();
        this.setPostOffice(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PostalCode} and set it to postalCode.
     * 
     * This method is a short version for:
     * <code>
     * PostalCode postalCode = new PostalCode();
     * this.setPostalCode(postalCode); </code>
     * 
     * 
     */
    public PostalCode createAndSetPostalCode() {
        PostalCode newValue = new PostalCode();
        this.setPostalCode(newValue);
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
    public AdministrativeArea addToAddressLine(final AddressLine addressLine) {
        this.getAddressLine().add(addressLine);
        return this;
    }

    /**
     * Sets the value of the administrativeAreaName property Objects of the following type(s) are allowed in the list List<AdministrativeAreaName>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withAdministrativeAreaName} instead.
     * 
     * 
     * @param administrativeAreaName
     */
    public void setAdministrativeAreaName(final List<AdministrativeArea.AdministrativeAreaName> administrativeAreaName) {
        this.administrativeAreaName = administrativeAreaName;
    }

    /**
     * add a value to the administrativeAreaName property collection
     * 
     * @param administrativeAreaName
     *     Objects of the following type are allowed in the list: {@link AdministrativeArea.AdministrativeAreaName}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public AdministrativeArea addToAdministrativeAreaName(final AdministrativeArea.AdministrativeAreaName administrativeAreaName) {
        this.getAdministrativeAreaName().add(administrativeAreaName);
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
    public AdministrativeArea addToAny(final Object any) {
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
    public AdministrativeArea withAddressLine(final List<AddressLine> addressLine) {
        this.setAddressLine(addressLine);
        return this;
    }

    /**
     * fluent setter
     * @see #setAdministrativeAreaName(List<AdministrativeAreaName>)
     * 
     * @param administrativeAreaName
     *     required parameter
     */
    public AdministrativeArea withAdministrativeAreaName(final List<AdministrativeArea.AdministrativeAreaName> administrativeAreaName) {
        this.setAdministrativeAreaName(administrativeAreaName);
        return this;
    }

    /**
     * fluent setter
     * @see #setSubAdministrativeArea(SubAdministrativeArea)
     * 
     * @param subAdministrativeArea
     *     required parameter
     */
    public AdministrativeArea withSubAdministrativeArea(final AdministrativeArea.SubAdministrativeArea subAdministrativeArea) {
        this.setSubAdministrativeArea(subAdministrativeArea);
        return this;
    }

    /**
     * fluent setter
     * @see #setAny(List<Object>)
     * 
     * @param any
     *     required parameter
     */
    public AdministrativeArea withAny(final List<Object> any) {
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
    public AdministrativeArea withUnderscore(final String underscore) {
        this.setUnderscore(underscore);
        return this;
    }

    /**
     * fluent setter
     * @see #setUsage(String)
     * 
     * @param usage
     *     required parameter
     */
    public AdministrativeArea withUsage(final String usage) {
        this.setUsage(usage);
        return this;
    }

    /**
     * fluent setter
     * @see #setIndicator(String)
     * 
     * @param indicator
     *     required parameter
     */
    public AdministrativeArea withIndicator(final String indicator) {
        this.setIndicator(indicator);
        return this;
    }

    @Override
    public AdministrativeArea clone() {
        AdministrativeArea copy;
        try {
            copy = ((AdministrativeArea) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
        for (AddressLine iter: addressLine) {
            copy.addressLine.add(iter.clone());
        }
        copy.administrativeAreaName = new ArrayList<AdministrativeArea.AdministrativeAreaName>((getAdministrativeAreaName().size()));
        for (AdministrativeArea.AdministrativeAreaName iter: administrativeAreaName) {
            copy.administrativeAreaName.add(iter.clone());
        }
        copy.subAdministrativeArea = ((subAdministrativeArea == null)?null:((AdministrativeArea.SubAdministrativeArea) subAdministrativeArea.clone()));
        copy.locality = ((locality == null)?null:((Locality) locality.clone()));
        copy.postOffice = ((postOffice == null)?null:((PostOffice) postOffice.clone()));
        copy.postalCode = ((postalCode == null)?null:((PostalCode) postalCode.clone()));
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
    @XmlRootElement(name = "AdministrativeAreaName", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class AdministrativeAreaName implements Cloneable
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

        public AdministrativeAreaName() {
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
            if ((obj instanceof AdministrativeArea.AdministrativeAreaName) == false) {
                return false;
            }
            AdministrativeArea.AdministrativeAreaName other = ((AdministrativeArea.AdministrativeAreaName) obj);
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
        public AdministrativeArea.AdministrativeAreaName withContent(final String content) {
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
        public AdministrativeArea.AdministrativeAreaName withUnderscore(final String underscore) {
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
        public AdministrativeArea.AdministrativeAreaName withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public AdministrativeArea.AdministrativeAreaName clone() {
            AdministrativeArea.AdministrativeAreaName copy;
            try {
                copy = ((AdministrativeArea.AdministrativeAreaName) super.clone());
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
        "subAdministrativeAreaName",
        "locality",
        "postOffice",
        "postalCode",
        "any"
    })
    @XmlRootElement(name = "SubAdministrativeArea", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class SubAdministrativeArea implements Cloneable
    {

        @XmlElement(name = "AddressLine")
        protected List<AddressLine> addressLine;
        @XmlElement(name = "SubAdministrativeAreaName")
        protected List<AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName> subAdministrativeAreaName;
        @XmlElement(name = "Locality")
        protected Locality locality;
        @XmlElement(name = "PostOffice")
        protected PostOffice postOffice;
        @XmlElement(name = "PostalCode")
        protected PostalCode postalCode;
        @XmlAnyElement(lax = true)
        protected List<Object> any;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        protected String underscore;
        @XmlAttribute(name = "UsageType")
        @XmlSchemaType(name = "anySimpleType")
        protected String usage;
        @XmlAttribute(name = "Indicator")
        @XmlSchemaType(name = "anySimpleType")
        protected String indicator;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        /**
         * Value constructor with only mandatory fields
         * 
         * @param postalCode
         *     required parameter
         * @param locality
         *     required parameter
         * @param postOffice
         *     required parameter
         */
        public SubAdministrativeArea(final Locality locality, final PostOffice postOffice, final PostalCode postalCode) {
            super();
            this.locality = locality;
            this.postOffice = postOffice;
            this.postalCode = postalCode;
        }

        /**
         * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
         * 
         */
        @Deprecated
        private SubAdministrativeArea() {
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
        public List<AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName> getSubAdministrativeAreaName() {
            if (subAdministrativeAreaName == null) {
                subAdministrativeAreaName = new ArrayList<AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName>();
            }
            return this.subAdministrativeAreaName;
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
         *     {@link PostOffice}
         *     
         */
        public PostOffice getPostOffice() {
            return postOffice;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link PostOffice}
         *     
         */
        public void setPostOffice(PostOffice value) {
            this.postOffice = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link PostalCode}
         *     
         */
        public PostalCode getPostalCode() {
            return postalCode;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link PostalCode}
         *     
         */
        public void setPostalCode(PostalCode value) {
            this.postalCode = value;
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
         *     possible object is
         *     {@link String}
         *     
         */
        public String getUsage() {
            return usage;
        }

        /**
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
         * 
         * @return
         *     possible object is
         *     {@link String}
         *     
         */
        public String getIndicator() {
            return indicator;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setIndicator(String value) {
            this.indicator = value;
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
            result = ((prime*result)+((subAdministrativeAreaName == null)? 0 :subAdministrativeAreaName.hashCode()));
            result = ((prime*result)+((locality == null)? 0 :locality.hashCode()));
            result = ((prime*result)+((postOffice == null)? 0 :postOffice.hashCode()));
            result = ((prime*result)+((postalCode == null)? 0 :postalCode.hashCode()));
            result = ((prime*result)+((any == null)? 0 :any.hashCode()));
            result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
            result = ((prime*result)+((usage == null)? 0 :usage.hashCode()));
            result = ((prime*result)+((indicator == null)? 0 :indicator.hashCode()));
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
            if ((obj instanceof AdministrativeArea.SubAdministrativeArea) == false) {
                return false;
            }
            AdministrativeArea.SubAdministrativeArea other = ((AdministrativeArea.SubAdministrativeArea) obj);
            if (addressLine == null) {
                if (other.addressLine!= null) {
                    return false;
                }
            } else {
                if (addressLine.equals(other.addressLine) == false) {
                    return false;
                }
            }
            if (subAdministrativeAreaName == null) {
                if (other.subAdministrativeAreaName!= null) {
                    return false;
                }
            } else {
                if (subAdministrativeAreaName.equals(other.subAdministrativeAreaName) == false) {
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
            if (postOffice == null) {
                if (other.postOffice!= null) {
                    return false;
                }
            } else {
                if (postOffice.equals(other.postOffice) == false) {
                    return false;
                }
            }
            if (postalCode == null) {
                if (other.postalCode!= null) {
                    return false;
                }
            } else {
                if (postalCode.equals(other.postalCode) == false) {
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
            if (usage == null) {
                if (other.usage!= null) {
                    return false;
                }
            } else {
                if (usage.equals(other.usage) == false) {
                    return false;
                }
            }
            if (indicator == null) {
                if (other.indicator!= null) {
                    return false;
                }
            } else {
                if (indicator.equals(other.indicator) == false) {
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
         * Creates a new instance of {@link AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName} and adds it to subAdministrativeAreaName.
         * This method is a short version for:
         * <code>
         * SubAdministrativeAreaName subAdministrativeAreaName = new SubAdministrativeAreaName();
         * this.getSubAdministrativeAreaName().add(subAdministrativeAreaName); </code>
         * 
         * 
         */
        public AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName createAndAddSubAdministrativeAreaName() {
            AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName newValue = new AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName();
            this.getSubAdministrativeAreaName().add(newValue);
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
         * Creates a new instance of {@link PostOffice} and set it to postOffice.
         * 
         * This method is a short version for:
         * <code>
         * PostOffice postOffice = new PostOffice();
         * this.setPostOffice(postOffice); </code>
         * 
         * 
         */
        public PostOffice createAndSetPostOffice() {
            PostOffice newValue = new PostOffice();
            this.setPostOffice(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link PostalCode} and set it to postalCode.
         * 
         * This method is a short version for:
         * <code>
         * PostalCode postalCode = new PostalCode();
         * this.setPostalCode(postalCode); </code>
         * 
         * 
         */
        public PostalCode createAndSetPostalCode() {
            PostalCode newValue = new PostalCode();
            this.setPostalCode(newValue);
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
        public AdministrativeArea.SubAdministrativeArea addToAddressLine(final AddressLine addressLine) {
            this.getAddressLine().add(addressLine);
            return this;
        }

        /**
         * Sets the value of the subAdministrativeAreaName property Objects of the following type(s) are allowed in the list List<SubAdministrativeAreaName>.
         * <p>Note:
         * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withSubAdministrativeAreaName} instead.
         * 
         * 
         * @param subAdministrativeAreaName
         */
        public void setSubAdministrativeAreaName(final List<AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName> subAdministrativeAreaName) {
            this.subAdministrativeAreaName = subAdministrativeAreaName;
        }

        /**
         * add a value to the subAdministrativeAreaName property collection
         * 
         * @param subAdministrativeAreaName
         *     Objects of the following type are allowed in the list: {@link AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName}
         * @return
         *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
         */
        public AdministrativeArea.SubAdministrativeArea addToSubAdministrativeAreaName(final AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName subAdministrativeAreaName) {
            this.getSubAdministrativeAreaName().add(subAdministrativeAreaName);
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
        public AdministrativeArea.SubAdministrativeArea addToAny(final Object any) {
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
        public AdministrativeArea.SubAdministrativeArea withAddressLine(final List<AddressLine> addressLine) {
            this.setAddressLine(addressLine);
            return this;
        }

        /**
         * fluent setter
         * @see #setSubAdministrativeAreaName(List<SubAdministrativeAreaName>)
         * 
         * @param subAdministrativeAreaName
         *     required parameter
         */
        public AdministrativeArea.SubAdministrativeArea withSubAdministrativeAreaName(final List<AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName> subAdministrativeAreaName) {
            this.setSubAdministrativeAreaName(subAdministrativeAreaName);
            return this;
        }

        /**
         * fluent setter
         * @see #setAny(List<Object>)
         * 
         * @param any
         *     required parameter
         */
        public AdministrativeArea.SubAdministrativeArea withAny(final List<Object> any) {
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
        public AdministrativeArea.SubAdministrativeArea withUnderscore(final String underscore) {
            this.setUnderscore(underscore);
            return this;
        }

        /**
         * fluent setter
         * @see #setUsage(String)
         * 
         * @param usage
         *     required parameter
         */
        public AdministrativeArea.SubAdministrativeArea withUsage(final String usage) {
            this.setUsage(usage);
            return this;
        }

        /**
         * fluent setter
         * @see #setIndicator(String)
         * 
         * @param indicator
         *     required parameter
         */
        public AdministrativeArea.SubAdministrativeArea withIndicator(final String indicator) {
            this.setIndicator(indicator);
            return this;
        }

        @Override
        public AdministrativeArea.SubAdministrativeArea clone() {
            AdministrativeArea.SubAdministrativeArea copy;
            try {
                copy = ((AdministrativeArea.SubAdministrativeArea) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
            for (AddressLine iter: addressLine) {
                copy.addressLine.add(iter.clone());
            }
            copy.subAdministrativeAreaName = new ArrayList<AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName>((getSubAdministrativeAreaName().size()));
            for (AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName iter: subAdministrativeAreaName) {
                copy.subAdministrativeAreaName.add(iter.clone());
            }
            copy.locality = ((locality == null)?null:((Locality) locality.clone()));
            copy.postOffice = ((postOffice == null)?null:((PostOffice) postOffice.clone()));
            copy.postalCode = ((postalCode == null)?null:((PostalCode) postalCode.clone()));
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
        @XmlRootElement(name = "SubAdministrativeAreaName", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class SubAdministrativeAreaName implements Cloneable
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

            public SubAdministrativeAreaName() {
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
                if ((obj instanceof AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName) == false) {
                    return false;
                }
                AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName other = ((AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName) obj);
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
            public AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName withContent(final String content) {
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
            public AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName withUnderscore(final String underscore) {
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
            public AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName clone() {
                AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName copy;
                try {
                    copy = ((AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                return copy;
            }

        }

    }

}
